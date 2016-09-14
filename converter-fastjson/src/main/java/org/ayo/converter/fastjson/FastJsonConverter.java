package org.ayo.converter.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import org.ayo.http.converter.ResponseConverter;
import org.ayo.http.converter.TypeToken;

/**
 * Created by Administrator on 2016/8/16.
 */
public class FastJsonConverter<T> implements ResponseConverter<T> {


    @Override
    public T convert(String s, TypeToken<T> typeToken) {
        T t = JSON.parseObject(s, new TypeReference<T>(){});
        return t;
    }
}
