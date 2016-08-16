package org.ayo.converter.fastjson;

import org.ayo.http.converter.ResponseConverter;
import org.ayo.http.converter.TypeToken;

/**
 * Created by Administrator on 2016/8/16.
 */
public class FastJsonConverter<T> implements ResponseConverter<T> {


    @Override
    public T convert(String s, TypeToken<T> typeToken) {
        return JsonUtils.getBean(s, typeToken);
    }
}
