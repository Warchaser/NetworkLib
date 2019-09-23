package com.warchaser.networklib.download;

import com.warchaser.networklib.util.NLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * 下载UI回调
 * */
public abstract class DownloadProgressCallBack<T> {

    private final String mDestFileDir;
    private final String mDestFileName;

    private int mCurrentState = 0;

    /**
     * 更新下载进度的消息事件标志
     * */
    public static final int UPDATE_PROGRESS = 0x2001;

    /**
     * 开始下载的消息事件标志
     * */
    public static final int START_DOWNLOAD = 0x2002;

    /**
     * 完成下载的消息事件标志
     * */
    public static final int FINISH_DOWNLOAD = 0x2003;

    /**
     * 下载出错的消息事件标志
     * */
    public static final int DOWNLOAD_ERROR = 0x2004;

    /**
     * 开始发起下载请求的消息事件标志
     * */
    public static final int ON_DOWNLOAD_REQUEST = 0x2005;

    public DownloadProgressCallBack(String destFileDir, String destFileName){
        this.mDestFileDir = destFileDir;
        this.mDestFileName = destFileName;
    }

    /**
     * 开始下载请求(Optional)
     * */
    public void onRequest(){
        setCurrentState(ON_DOWNLOAD_REQUEST);
    }

    /**
     * 下载成功(Optional)
     * */
    public void onSuccess(T t){
        setCurrentState(FINISH_DOWNLOAD);
    }

    /**
     * 更新下载进度(Required)
     * */
    public abstract void onProgress(long progress, long total);

    /**
     * 更新下载进度(内部使用)
     * */
    private void progressPri(long progress, long total){
        setCurrentState(UPDATE_PROGRESS);
        onProgress(progress, total);
    }

    /**
     * 开始下载
     * 目前木有使用
     * 具体使用时，触发时机为开始写文件，较onRequest延后，不推荐
     * (Optional)
     * */
    public void onStart(){
        setCurrentState(START_DOWNLOAD);
    }

    /**
     * 下载完成(Required)
     * */
    public abstract void onCompleted();

    /**
     * 下载完成(内部使用)
     * */
    private void onCompletedPri(){
        setCurrentState(FINISH_DOWNLOAD);
        onCompleted();
    }

    /**
     * 下载异常(Required)
     * */
    public abstract void onError(Throwable e);

    /**
     * 下载异常(内部使用)
     * */
    private void onErrorPri(Throwable e){
        setCurrentState(DOWNLOAD_ERROR);
        onError(e);
    }

    /**
     * 写文件
     * */
    public void saveFile(ResponseBody body) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        long readLength = 0;
        FileOutputStream fos = null;
        try {
            is = body.byteStream();
            File dir = new File(mDestFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, mDestFileName);
            if(file.exists()){
                file.delete();
            }
            fos = new FileOutputStream(file);
            onStart();
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                readLength += len;
                progressPri(readLength, body.contentLength());
            }

            fos.flush();

            if(readLength != file.length()){
                onErrorPri(new IOException("Download Not Completed!"));
                return;
            }

            if(file.length() == 0){
                onErrorPri(new IOException("File is Empty!"));
                return;
            }

            onCompletedPri();
        } catch (FileNotFoundException e) {
            onError(e);
            onErrorPri(new IOException("Download Not Completed!"));
            NLog.printStackTrace("saveFile", e);
        } catch (IOException e) {
            onError(e);
            onErrorPri(new IOException("Download Not Completed!"));
            NLog.printStackTrace("saveFile", e);
        } finally {
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                onError(e);
                onErrorPri(new IOException("Download Not Completed!"));
                NLog.printStackTrace("saveFile", e);
            }
        }
    }

    public synchronized int getCurrentState() {
        return mCurrentState;
    }

    private synchronized void setCurrentState(int currentState){
        mCurrentState = currentState;
    }

    public boolean isDownloading(){
        return mCurrentState == DownloadProgressCallBack.UPDATE_PROGRESS
                || mCurrentState == DownloadProgressCallBack.ON_DOWNLOAD_REQUEST
                || mCurrentState == DownloadProgressCallBack.START_DOWNLOAD;
    }

}
