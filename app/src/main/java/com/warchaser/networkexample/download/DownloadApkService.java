package com.warchaser.networkexample.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.warchaser.commonuitls.FileUtils;
import com.warchaser.commonuitls.StatusBarUtil;
import com.warchaser.networkexample.BuildConfig;
import com.warchaser.networkexample.R;
import com.warchaser.networklib.download.DownloadProgressCallBack;
import com.warchaser.networklib.download.DownloadRequest;
import com.warchaser.networklib.download.DownloadValueBean;
import com.warchaser.networklib.download.SingleDownloadSubscriber;
import com.warchaser.networklib.util.NLog;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;

import okhttp3.ResponseBody;

/**
 * 下载用Service
 */
public final class DownloadApkService extends Service {

    /**
     * Notification的ChannelId
     * */
    private String mChannelId = "";

    /**
     * Notification的Id
     * */
    private final int NOTIFICATION_ID = 1001;

    /**
     * 开始下载的Command
     * 用于onStartCommand
     * */
    public static final int START_DOWNLOAD_COMMAND = 0x3001;

    /**
     * 开始下载的Action的Key
     * */
    public static final String START_DOWNLOAD_ACTION = "START_DOWNLOAD_ACTION";

    public static final String DOWNLOAD_URL = "DOWNLOAD_URL";

    /**
     * 完成下载并开始安装Apk的Action
     * */
    private final String START_INSTALL_ON_APK_DOWNLOAD_FINISHED_ACTION = "START_INSTALL_ON_APK_DOWNLOAD_FINISHED_ACTION";

    /**
     * 完成下载并开始安装Apk的Flag
     * */
    private static final int START_INSTALL_ON_APK_DOWNLOAD_FLAG = 0x3002;

    private MessageHandler mMessageHandler;
    private RemoteViews mNotificationRemoteView;
    private Notification mNotification;
    private NotificationManager mNotificationManager;

    private DownloadCallBack mDownloadCallBack;
    private SingleDownloadSubscriber mDownloadSubscriber;

    /**
     * 当前更新时间
     * 用于避免频繁刷新UI
     * */
    private long mCurrentTime = 0;

    private NumberFormat mNumberFormat;

    private IntentReceiver mIntentReceiver;

    /**
     * 下载Apk的绝对路径
     * */
    private String mApkPath = "";

    private static DownloadApkService mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mChannelId = getPackageName() + "download_notification";

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mMessageHandler = new MessageHandler(this);

        mCurrentTime = System.currentTimeMillis();

        mInstance = this;

        initializeReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String url = intent.getStringExtra(DOWNLOAD_URL);
            sendMessage(intent.getIntExtra(START_DOWNLOAD_ACTION, 0), url);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mNotificationManager != null) {
            mNotificationManager.cancel(NOTIFICATION_ID);
            mNotificationManager = null;
        }

        if(mDownloadSubscriber != null){
            mDownloadSubscriber.cancel();
            mDownloadSubscriber = null;
        }

        if(mMessageHandler != null){
            mMessageHandler.removeCallbacksAndMessages(null);
            mMessageHandler = null;
        }

        mDownloadCallBack = null;

        mNotificationRemoteView = null;

        mNotification = null;

        if(mIntentReceiver != null){
            unregisterReceiver(mIntentReceiver);
            mIntentReceiver = null;
        }

    }

    private void initializeReceiver() {
        mIntentReceiver = new IntentReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(START_INSTALL_ON_APK_DOWNLOAD_FINISHED_ACTION);

        registerReceiver(mIntentReceiver, intentFilter);
    }

    /**
     * Retrofit + RxJava下载请求
     * */
    private void startDownloadRequest(String url) {
        if(TextUtils.isEmpty(url)){
            NLog.e("DownloadApkService", "url is empty or null!");
            return;
        }

        final String destFolder = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()
                + File.separator
                + "apk"
                + File.separator;
        final String fileName = "update.apk";
//        final String url = "http://221.180.170.10/cache/ucan.25pp.com/Wandoujia_wap_app_article_download.apk?ich_args2=123-27132207020467_dc1f42d1ad560090e77efb1bd3f90c9e_10051001_9c896724d7c1f4d6903a518939a83798_922ca6a062a18499bc102c477543d688";

        mApkPath = destFolder + fileName;

        if(FileUtils.isFileExist(mApkPath)){
            installApk();
            return;
        }

        if (mDownloadCallBack == null) {
            mDownloadCallBack = new DownloadCallBack(destFolder, fileName);
        }

        if(mDownloadSubscriber == null){
            mDownloadSubscriber = new SingleDownloadSubscriber(mDownloadCallBack);
        }

        //正在下载就不要请求了
        if(isDownloading()){
            return;
        }

        DownloadRequest.getInstance().downloadFile(url, mDownloadCallBack, mDownloadSubscriber);
    }

    /**
     * 是否正在下载
     * */
    public boolean isDownloading(){
        return mInstance != null && mDownloadCallBack != null && mDownloadCallBack.isDownloading();
    }

    /**
     * 通知Android系统显示Notification
     * */
    private void showNotification() {
        if(mNotificationManager != null){
            mNotificationManager.notify(NOTIFICATION_ID, getNotification());
        }
    }

    /**
     * 创建Notification
     * */
    private Notification getNotification() {
        mNotificationRemoteView = new RemoteViews(this.getPackageName(), R.layout.notification_download_apk);

        mNotificationRemoteView.setViewVisibility(R.id.mLyProgress, View.VISIBLE);
        mNotificationRemoteView.setViewVisibility(R.id.mLyFinishWithState, View.GONE);
        mNotificationRemoteView.setTextViewText(R.id.mTvProgress, String.format(getResources().getString(R.string.format_download_apk_percent), "0"));
        mNotificationRemoteView.setProgressBar(R.id.mProgressBar, 0, 100, false);

        Intent installIntent = new Intent(START_INSTALL_ON_APK_DOWNLOAD_FINISHED_ACTION);
        installIntent.putExtra("FLAG", START_INSTALL_ON_APK_DOWNLOAD_FLAG);
        PendingIntent installPendingIntent = PendingIntent.getBroadcast(this, 0, installIntent, 0);
        mNotificationRemoteView.setOnClickPendingIntent(R.id.mLyRoot, installPendingIntent);

        if (mNotification == null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, mChannelId)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContent(mNotificationRemoteView)
                    .setSmallIcon(R.mipmap.ic_launcher);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(mChannelId, getClass().getName(), NotificationManager.IMPORTANCE_HIGH);
                channel.setSound(null, null);
                mNotificationManager.createNotificationChannel(channel);
            }

            builder.setOnlyAlertOnce(true);
            mNotification = builder.build();
        }

        mNotification.flags |= Notification.FLAG_NO_CLEAR;

        return mNotification;
    }

    /**
     * 通知Notification下载结束
     * */
    private void setProgressFinished() {
        if (mNotificationRemoteView != null) {
            mNotificationRemoteView.setViewVisibility(R.id.mLyProgress, View.GONE);
            mNotificationRemoteView.setViewVisibility(R.id.mLyFinishWithState, View.VISIBLE);
            mNotificationRemoteView.setTextViewText(R.id.mTvFinishState, getResources().getText(R.string.hint_apk_download_finished));
        }

        if(mNotification != null){
            mNotification.flags = Notification.FLAG_NO_CLEAR;
        }

        notifyNotification();
    }

    /**
     * 下载出错
     * 此事件在重试3次后触发
     * 此事件在写文件出错后触发
     * */
    private void setDownloadError(){

        if(mDownloadSubscriber != null){
            mDownloadSubscriber.cancel();
        }

        if (mNotificationRemoteView != null) {
            mNotificationRemoteView.setViewVisibility(R.id.mLyProgress, View.GONE);
            mNotificationRemoteView.setViewVisibility(R.id.mLyFinishWithState, View.VISIBLE);
            mNotificationRemoteView.setTextViewText(R.id.mTvFinishState, getResources().getText(R.string.hint_apk_download_error));
        }

        if(mNotification != null){
            mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        }

        notifyNotification();
    }

    /**
     * 更新Notification ProgressBar进度
     * */
    private synchronized void updateProgress(DownloadValueBean bean) {

        if (System.currentTimeMillis() - mCurrentTime < 1000) {
            return;
        }

        mCurrentTime = System.currentTimeMillis();

        if (mNotificationRemoteView != null && bean != null) {

            if(mNumberFormat == null){
                mNumberFormat = NumberFormat.getInstance();
                // 设置精确到小数点后2位
                mNumberFormat.setMaximumFractionDigits(2);
            }

            String percent = mNumberFormat.format((float) bean.getProgress() / (float) bean.getTotal() * 100);

            mNotificationRemoteView.setTextViewText(R.id.mTvProgress, String.format(getResources().getString(R.string.format_download_apk_percent), percent));
            mNotificationRemoteView.setProgressBar(R.id.mProgressBar, (int) bean.getTotal(), (int) bean.getProgress(), false);
        }

        notifyNotification();
    }

    private void cancelNotification(){
        stopForeground(true);
        if(mNotificationManager != null){
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    }

    /**
     * 通知Notification 视图更新
     * */
    private synchronized void notifyNotification(){
        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 处理通知栏主动控制的消息
     */
    private synchronized void handleIntentCommand(Intent intent) {

        if (intent == null) {
            return;
        }

        final String action = intent.getAction();
        if (START_INSTALL_ON_APK_DOWNLOAD_FINISHED_ACTION.equals(action)) {

            if (mDownloadCallBack != null && mDownloadCallBack.getCurrentState() != DownloadProgressCallBack.FINISH_DOWNLOAD) {
                return;
            }

            sendMessage(START_INSTALL_ON_APK_DOWNLOAD_FLAG, null);
        }

    }

    /**
     * 下载完成后
     * 安装Apk
     * */
    private void installApk() {

        try {
            File apkFile = new File(mApkPath);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(
                        getApplicationContext()
                        , BuildConfig.APPLICATION_ID + ".fileProvider"
                        , apkFile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            startActivity(intent);
            StatusBarUtil.collapseStatusBar(getApplicationContext());
        } catch (Exception | Error e) {
            NLog.printStackTrace("DownloadApkService", e);
        }
    }

    /**
     * 向MessageHandler发送消息
     * */
    private void sendMessage(int type, Object object) {
        if (mMessageHandler == null) {
            return;
        }

        mMessageHandler.obtainMessage(type, object).sendToTarget();
    }

    public static DownloadApkService getInstance(){
        return mInstance;
    }

    /**
     * 消息Handler
     * 集中在此Handler处理消息并调用Service中的方法
     * */
    private static class MessageHandler extends Handler {

        private WeakReference<DownloadApkService> mServiceWeakReference;

        MessageHandler(DownloadApkService service) {
            mServiceWeakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final DownloadApkService service = mServiceWeakReference.get();
            switch (msg.what) {
                case DownloadProgressCallBack.ON_DOWNLOAD_REQUEST:
                    service.showNotification();
                    break;
                case DownloadProgressCallBack.UPDATE_PROGRESS:
                    final DownloadValueBean bean = (DownloadValueBean) msg.obj;
                    service.updateProgress(bean);
                    break;
                case DownloadProgressCallBack.FINISH_DOWNLOAD:
                    service.setProgressFinished();
                    service.installApk();
                    break;
                case DownloadProgressCallBack.DOWNLOAD_ERROR:
//                    service.downloadError();
                    service.setDownloadError();
                    break;
                case START_DOWNLOAD_COMMAND:
                    String url = (String)msg.obj;
                    service.startDownloadRequest(url);
                    break;
                case START_INSTALL_ON_APK_DOWNLOAD_FLAG:
                    service.installApk();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 下载消息UI回调
     * */
    private class DownloadCallBack extends DownloadProgressCallBack<ResponseBody> {

        public DownloadCallBack(String destFileDir, String destFileName) {
            super(destFileDir, destFileName);
        }

        @Override
        public void onRequest() {
            super.onRequest();
            sendMessage(ON_DOWNLOAD_REQUEST, null);
            NLog.i("DownloadCallBack", "ON_DOWNLOAD_REQUEST");
        }

        @Override
        public void onProgress(long progress, long total) {
            sendMessage(UPDATE_PROGRESS, new DownloadValueBean(progress, total));
            NLog.i("DownloadCallBack", "UPDATE_PROGRESS onProgress: " + progress + " total: " + total);
        }

        @Override
        public void onCompleted() {
            sendMessage(FINISH_DOWNLOAD, null);
            NLog.i("DownloadCallBack", "FINISH_DOWNLOAD");
        }

        @Override
        public void onError(Throwable e) {
            sendMessage(DOWNLOAD_ERROR, null);
            NLog.printStackTrace("DownloadCallBack", e);
        }

    }

    /**
     * 处理Notification点击事件的Receiver
     * */
    private class IntentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            handleIntentCommand(intent);
        }
    }
}
