package org.ayo.http.sample;

import java.util.Map;

/**
 * Created by Administrator on 2016/8/16.
 */
public abstract class HttpIntercepter {

    abstract void beforeRequest();

    abstract void requestHeader(Map<String, String> header);

    abstract void beforeTopLevelConvert(String s);

}
