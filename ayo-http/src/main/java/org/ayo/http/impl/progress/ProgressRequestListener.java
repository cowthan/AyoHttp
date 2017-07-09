package org.ayo.http.impl.progress;

/**
 * Created by Administrator on 2017/7/9.
 */
public interface ProgressRequestListener {
    void onRequestProgress(long bytesWritten, long contentLength, boolean done);
}