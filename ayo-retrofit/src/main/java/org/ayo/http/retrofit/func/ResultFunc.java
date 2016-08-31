package org.ayo.http.retrofit.func;

import org.ayo.http.retrofit.JsonConvert;
import org.ayo.http.retrofit.ResponseWrapper;

import rx.functions.Func1;

/**
 * Created by _SOLID
 * Date:2016/7/28
 * Time:11:04
 */
public class ResultFunc<T> implements Func1<String, ResponseWrapper<T>> {
    @Override
    public ResponseWrapper<T> call(String result) {
        JsonConvert<ResponseWrapper<T>> convert = new JsonConvert<ResponseWrapper<T>>() {
        };
        return convert.parseData(result);
    }
}
