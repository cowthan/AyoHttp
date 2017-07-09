package org.ayo.http.impl.progress;

/**
 * Created by Administrator on 2017/7/9.
 */

public interface ProgressResponseListener {
    void onResponseProgress(long bytesRead, long contentLength, boolean done);
}