package com.warchaser.networklib.download;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 下载返回Body
 * */
final public class DownloadResponseBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private BufferedSource mBufferedSource;

    private DownloadProgressCallBack mCallBack;

    public DownloadResponseBody(ResponseBody responseBody){
        this.mResponseBody = responseBody;
    }

    public DownloadResponseBody(ResponseBody responseBody, DownloadProgressCallBack callBack){
        this.mResponseBody = responseBody;
        mCallBack = callBack;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {

        if(mBufferedSource == null){
            mBufferedSource = Okio.buffer(createSource(mResponseBody.source()));
        }

        return mBufferedSource;
    }

    private Source createSource(Source source){
        return new ForwardingSource(source) {

            long mBytesRead = 0;

            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {

                long bytesRead = super.read(sink, byteCount);

                mBytesRead += bytesRead == -1 ? 0 : bytesRead;

//                if(mCallBack != null){
//                    mCallBack.onProgress(mBytesRead, mResponseBody.contentLength());
//                }

                return bytesRead;
            }
        };
    }
}
