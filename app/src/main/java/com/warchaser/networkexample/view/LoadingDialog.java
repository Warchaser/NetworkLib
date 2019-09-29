package com.warchaser.networkexample.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.warchaser.networkexample.R;

import java.lang.ref.WeakReference;

/**
 * Author：Leon
 * Date: On 2018/9/20
 * Email：fangjianwei@ihmair.cn
 */
public class LoadingDialog extends Dialog {

    private Context mContext;
    private final int TIMEOUT = 45_000;

    private MessageHandler mMessageHandler;

    public LoadingDialog(Context context) {
        super(context, R.style.loading_dialog);
        this.mContext = context;
        mMessageHandler = new MessageHandler(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        // 获取视图
        View view = LayoutInflater.from(mContext).inflate(R.layout.loading_dialog_view, null);
        // 获取整个布局
        ImageView gifView = view.findViewById(R.id.mGifImg);
        Glide.with(mContext).load(R.mipmap.loading).into(gifView);

        setContentView(view);
        WindowManager windowManager = ((Activity) mContext).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth();
        lp.height = display.getHeight();
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);
    }

    private static class MessageHandler extends Handler {

        WeakReference<LoadingDialog> mWeakReference;

        MessageHandler(LoadingDialog dialog){
            mWeakReference = new WeakReference<>(dialog);
        }

        @Override
        public void handleMessage(Message msg){

            final LoadingDialog dialog = mWeakReference.get();

            if(msg.what == 1){
                dialog.dismiss();
            }
        }
    }

    @Override
    public void show() {
        super.show();
        mMessageHandler.sendEmptyMessageDelayed(1,TIMEOUT) ;
    }

    public void destroy(){
        if(mMessageHandler != null){
            mMessageHandler.removeCallbacksAndMessages(null);
            mMessageHandler = null;
        }
    }
}
