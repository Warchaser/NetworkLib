package com.warchaser.networklib.upload;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class UploadFileRequestBody extends RequestBody {

    private RequestBody mRequestBody;

    private UploadCallback<BaseUploadResp<UploadResponseBody>> mCallback;

    public UploadFileRequestBody(File file, UploadCallback<BaseUploadResp<UploadResponseBody>> callback){
        mRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        mCallback = callback;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        final CountingSink countingSink = new CountingSink(sink);
        final BufferedSink bufferedSink = Okio.buffer(countingSink);

        mRequestBody.writeTo(bufferedSink);

        bufferedSink.flush();
    }

    private final class CountingSink extends ForwardingSink{

        private long mBytesWritten = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);

            mBytesWritten += byteCount;
            if(mCallback != null){
                mCallback.onProgress(mBytesWritten, contentLength());
            }
        }
    }
}
