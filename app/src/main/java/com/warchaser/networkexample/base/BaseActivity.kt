package com.warchaser.networkexample.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.warchaser.commonuitls.AppManager
import com.warchaser.commonuitls.PackageUtil
import com.warchaser.commonuitls.ToastUtil
import com.warchaser.networkexample.view.LoadingDialog
import com.warchaser.networklib.util.NLog

open class BaseActivity : RxAppCompatActivity() {

    /**
     * 全屏LoadingDialog
     * */
    private var mLoadingDialog: LoadingDialog? = null
    protected lateinit var TAG : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = PackageUtil.getSimpleClassName(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }

        AppManager.getInstance().addActivity(this)

//        if(this is MainActivity){
//            setDarkStatusIcon(false)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.getInstance().removeActivity(this)

        dismissLoadingDialog()
        mLoadingDialog?.run {
            destroy()
        }
        mLoadingDialog = null
    }

    /**
     * 说明：Android 6.0+ 状态栏图标原生反色操作
     */
    fun setDarkStatusIcon(dark: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView ?: return

            var vis = decorView.systemUiVisibility
            vis = if (dark) {
                vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            decorView.systemUiVisibility = vis
        }
    }

    /**
     * 弹出toast的公共方法
     */
    fun showToast(message: String) {
        ToastUtil.showToast(message)
    }

    fun showToast(strResourceId:Int){
        ToastUtil.showToast(strResourceId)
    }

    fun showToastLong(message: String){
        ToastUtil.showToastLong(message)
    }

    fun showToastLong(strResourceId:Int){
        ToastUtil.showToastLong(strResourceId)
    }

    private fun getLoadingDialog() {
        mLoadingDialog = LoadingDialog(this)
        mLoadingDialog?.setCancelable(false)
        mLoadingDialog?.setCanceledOnTouchOutside(false)
    }

    /**
     * show loadingDialog
     * */
    @Synchronized
    fun showLoadingDialog() {

        try {
            if (mLoadingDialog == null) {
                getLoadingDialog()
            }

            mLoadingDialog?.run {
                if(!isShowing) show()
            }

        } catch (e: Exception) {
            NLog.printStackTrace(TAG, e)
        } catch (e: Error) {
            NLog.printStackTrace(TAG, e)
        }

    }

    /**
     * dismiss LoadingDialog
     * */
    @Synchronized
    fun dismissLoadingDialog() {
        try {
            mLoadingDialog?.run {
                if(isShowing) dismiss()
            }
        } catch (e: Exception) {
            NLog.printStackTrace(TAG, e)
        } catch (e: Error) {
            NLog.printStackTrace(TAG, e)
        }

    }


}
