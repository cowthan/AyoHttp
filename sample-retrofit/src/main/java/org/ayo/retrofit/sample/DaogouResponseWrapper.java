package org.ayo.retrofit.sample;

import org.ayo.http.retrofit.ResponseWrapper;

/**
 * Created by Administrator on 2016/8/18.
 */
public class DaogouResponseWrapper<T> extends ResponseWrapper<T>{

    public int code;
    public String message;
    public T result;


    @Override
    public boolean isOk() {
        return code == 0;
    }

    @Override
    public String getErrorMessage() {
        return message;
    }

    @Override
    public int getErrorCode() {
        return code;
    }

    @Override
    public T getResult() {
        return result;
    }
}
