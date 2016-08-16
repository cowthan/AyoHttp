package org.ayo.http;

import java.util.Map;

/**
 * Created by Administrator on 2016/8/16.
 */
public abstract class HttpIntercepter {

    public abstract void beforeRequest(AyoHttp req);

    public abstract void responseHeader(Map<String, String> header);

    public abstract void beforeTopLevelConvert(String s);

}
