package org.ayo.http;

import org.ayo.http.callback.BaseHttpCallback;

/**
 * Created by Administrator on 2016/8/16.
 */
public abstract class TopLevelConverter {

    public abstract String convert(String s, BaseHttpCallback callback);

}
