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

package com.android.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.OnNotModifyListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class BytesRequest extends Request<BytesRequest.ByteRequestModel> {
    private Listener<ByteRequestModel> mListener;

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public BytesRequest(int method, String url, Listener<ByteRequestModel> listener,
            ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    /**
     * Creates a new GET request.
     *
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public BytesRequest(String url, Listener<ByteRequestModel> listener, ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    public BytesRequest(int method, String url, Listener<ByteRequestModel> listener,
            Response.OnCacheListener<ByteRequestModel> onCacheListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        this.cacheListener = onCacheListener;
    }

    public BytesRequest(String url, Listener<ByteRequestModel> listener,
            Response.OnCacheListener<ByteRequestModel> onCacheListener, ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
        this.cacheListener = onCacheListener;
    }

    public BytesRequest(String url, Listener<ByteRequestModel> listener,
            OnNotModifyListener onNotModifyListener, ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
        this.notModifyListener = onNotModifyListener;

    }

    public BytesRequest(int method, String url, Listener<ByteRequestModel> listener,
            Response.OnCacheListener<ByteRequestModel> onCacheListener,
            OnNotModifyListener onNotModifyListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.cacheListener = onCacheListener;
        this.notModifyListener = onNotModifyListener;
    }

    public BytesRequest(String url, Listener<ByteRequestModel> listener,
            Response.OnCacheListener<ByteRequestModel> onCacheListener,
            OnNotModifyListener onNotModifyListener, ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
        this.cacheListener = onCacheListener;
        this.notModifyListener = onNotModifyListener;
    }

    @Override
    protected void deliverResponse(ByteRequestModel response) {
        if (null != mListener) {
            mListener.onResponse(response);
        }
    }

    @Override
    protected void deliverCacheResponse(ByteRequestModel response) {
        super.deliverCacheResponse(response);
    }

    @Override
    public void finish(String tag) {
        super.finish(tag);
        mListener = null;
    }

    @Override
    protected Response<ByteRequestModel> parseNetworkResponse(NetworkResponse response) {
        String mimetype = "";
        if (response.headers != null && response.headers.containsKey("Content-Type"))
            mimetype = response.headers.get("Content-Type");
        ByteRequestModel model = new ByteRequestModel(response.data, mimetype);
        return Response.success(model, HttpHeaderParser.parseCacheHeaders(response));
    }

    public static class ByteRequestModel {
        public byte[] bytes;

        public String mimetype;

        public ByteRequestModel(byte[] bytes, String mimetype) {
            this.bytes = bytes;
            this.mimetype = mimetype;
        }
    }
}
