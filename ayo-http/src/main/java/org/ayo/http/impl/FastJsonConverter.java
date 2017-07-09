package org.ayo.http.impl;

import com.alibaba.fastjson.JSON;

import org.ayo.http.converter.ResponseConverter;
import org.ayo.http.converter.TypeToken;

/**
 * Created by Administrator on 2016/8/16.
 */
public class FastJsonConverter<T> implements ResponseConverter<T> {

    @Override
    public T convert(String s, TypeToken<T> typeToken) {
        T t = JSON.parseObject(s, typeToken.getType());
        return t;
    }
}
