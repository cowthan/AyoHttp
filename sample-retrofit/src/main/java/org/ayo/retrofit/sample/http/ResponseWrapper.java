package org.ayo.retrofit.sample.http;

/**
 * Created by Administrator on 2016/8/18.
 */
public class ResponseWrapper<T> {

    public int code;
    public String message;
    public T result;
}
