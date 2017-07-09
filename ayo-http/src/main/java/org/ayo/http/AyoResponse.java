package org.ayo.http;

import org.ayo.http.callback.FailInfo;

/**
 * Created by Administrator on 2017/7/9.
 */

public class AyoResponse<T> {
    public T data;
    public FailInfo failInfo;
}
