package org.ayo.http.retrofit;

/**
 * Created by Administrator on 2016/8/18.
 */
public abstract class ResponseWrapper<T> {

    public abstract boolean isOk();
    public abstract String getErrorMessage();
    public abstract int getErrorCode();
    public abstract T getResult();

}
