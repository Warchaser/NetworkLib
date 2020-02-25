package com.warchaser.networkexample

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.gson.GsonBuilder

import com.google.gson.JsonObject
import com.warchaser.networkexample.base.BaseActivity
import com.warchaser.networkexample.bean.GetUsersByIndexResp
import com.warchaser.networkexample.network.NetworkRequest
import com.warchaser.networkexample.network.NormalSubscriber
import com.warchaser.networklib.common.base.BaseSubscriber
import com.warchaser.networklib.upload.BaseUploadResp
import com.warchaser.networklib.upload.UploadCallback
import com.warchaser.networklib.upload.UploadRequest
import com.warchaser.networklib.upload.UploadResponseBody
import com.warchaser.networklib.util.GsonUtil
import com.warchaser.networklib.util.NLog
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity() {

    private var mBtnGetUsers: Button? = null
    private var mBtnUpload : Button ? = null

    private var mGetUsersSubscriber : GetUsersSubscriber? = null

    private var mUploadFileCallback : FileUploadCallback ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()
    }

    override fun onDestroy() {
        super.onDestroy()

        mGetUsersSubscriber?.cancelSubscribe()
        mGetUsersSubscriber = null
    }

    private fun initialize() {
        mBtnGetUsers = findViewById(R.id.mBtnGetUsers)
        mBtnUpload = findViewById(R.id.mBtnUpload)

        mBtnGetUsers?.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                getUsersRequest()
            }
        })

        mBtnUpload?.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                uploadFile()
            }
        })

    }

    private fun getUsersRequest() {

        if(mGetUsersSubscriber == null){
            mGetUsersSubscriber = GetUsersSubscriber()
        }

        NetworkRequest.getInstance().getUsersByIndex("0", "10", mGetUsersSubscriber, this)
    }

    private fun uploadFile(){

        if(mUploadFileCallback == null){
            mUploadFileCallback = FileUploadCallback()
        }

        UploadRequest.getInstance().uploadFile("https://cloud.carautocloud.com/rcg/m85/analysis/behavior/upload", File("/storage/self/primary/Download/collect_data(1)(1).db"),"1000000074", "c3cf2837665d010ba44e0d105b031cca", mUploadFileCallback)
    }

    private inner class GetUsersSubscriber : NormalSubscriber<JsonObject>(true){

        override fun onINext(t: JsonObject?) {
            super.onINext(t)
            val gson = GsonBuilder().setPrettyPrinting().create()
//            gson.toJson(t?.toString())
            mTvResult.text = gson.toJson(t?.toString())

            val resultCode = t?.get(BaseSubscriber.RES_KEY_STATE)?.asInt
            if (resultCode == 0){
                val jsonObj = t.getAsJsonObject(BaseSubscriber.RES_KEY_DATA)
                val objStr = jsonObj.toString()
                val bean : GetUsersByIndexResp = GsonUtil.parseString2Object(objStr, GetUsersByIndexResp::class.java)
                NLog.i(TAG, "UserName: ${bean.array[0].name}")
            }
        }

        override fun onError(t: Throwable?) {
            super.onError(t)
        }

    }

    private inner class FileUploadCallback : UploadCallback<BaseUploadResp<UploadResponseBody>>() {

        override fun onRequest() {
            super.onRequest()
            NLog.e("Upload", "onRequest()!!!")
        }

        override fun onUploadSuccess(uploadResult : BaseUploadResp<UploadResponseBody>) {
            NLog.e("Upload", "OnUploadSuccess!!!")
            mTvUploadResult.text = uploadResult.data.toString()
        }

        override fun onUploadFailed(e: Throwable?) {
            NLog.printStackTrace("Upload", e)
        }

        override fun onProgress(bytesWritten: Long, contentLength: Long) {
            NLog.e("Upload", bytesWritten.toString())
        }

    }
}
