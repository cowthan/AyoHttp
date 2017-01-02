package org.ayo.http;

import org.ayo.http.callback.BaseHttpCallback;

/**
 */
public interface TopLevelConverter {

    String convert(String s, BaseHttpCallback callback);

}
