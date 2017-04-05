/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.error;

import com.android.volley.NetworkResponse;

/**
 * Exception style class encapsulating Volley errors
 */
@SuppressWarnings("serial")
public class VolleyError extends Exception {
    public final NetworkResponse networkResponse;

    public VolleyError() {
        networkResponse = null;
    }

    public VolleyError(NetworkResponse response) {
        networkResponse = response;
    }

    public VolleyError(String exceptionMessage) {
        super(exceptionMessage);
        networkResponse = null;
    }

    public VolleyError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
        networkResponse = null;
    }

    public VolleyError(Throwable cause) {
        super(cause);
        networkResponse = null;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        String line = "<br>";
        if (networkResponse != null) {
            sb.append("statusCode:" + networkResponse.statusCode + line);
            if (networkResponse.headers != null) {
                sb.append("headers" + line);
                for (String str : networkResponse.headers.keySet()) {
                    sb.append("     " + str + ":" + networkResponse.headers.get(str) + line);
                }
            }
            if (networkResponse.data != null)
                sb.append("data:" + new String(networkResponse.data) + line);
        } else
            sb.append("nothing");
        return sb.toString();
    }

    @Override
    public Throwable fillInStackTrace() {
        return super.fillInStackTrace();
    }
}
