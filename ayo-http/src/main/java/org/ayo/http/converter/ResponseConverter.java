package org.ayo.http.converter;

/**
 * Created by Administrator on 2016/8/16.
 */
public interface ResponseConverter<T> {

    T convert(String s, TypeToken<T> typeToken);

}
