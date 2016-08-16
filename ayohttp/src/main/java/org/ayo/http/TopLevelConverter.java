package org.ayo.http;

import org.ayo.http.callback.BaseHttpCallback;

/**
 * Created by Administrator on 2016/8/16.
 */
public interface TopLevelConverter {

    String convert(String s, BaseHttpCallback callback);

}
