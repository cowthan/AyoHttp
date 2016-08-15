package org.ayo.http.sample;

/**
 * Created by Administrator on 2016/8/16.
 */
public abstract class ResonseConverter  {

    abstract <T> String convert(String s, BaseHttpCallback<T> callback);

}
