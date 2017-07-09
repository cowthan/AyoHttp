package org.ayo.http;

import org.ayo.http.callback.BaseHttpCallback;
import org.ayo.http.converter.TypeToken;
import org.ayo.http.impl.progress.ProgressRequestListener;
import org.ayo.http.impl.progress.ProgressResponseListener;

/**
 * Created by Administrator on 2016/8/16.
 */
public abstract class HttpWorker {

    public abstract <T> void fire(AyoRequest req, BaseHttpCallback<T> callback);
    public abstract <T> AyoResponse<T> fireSync(AyoRequest req, TypeToken<T> typeToken, ProgressRequestListener progressRequestListener, ProgressResponseListener progressResponseListener);
    public abstract void cancelAll(Object tag);

}
