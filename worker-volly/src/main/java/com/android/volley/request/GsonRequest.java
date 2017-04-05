
package com.android.volley.request;

import com.alibaba.json.JSON;
import com.alibaba.json.JSONException;
import com.alibaba.json.TypeReference;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.Response.OnCacheListener;
import com.android.volley.Response.OnNotModifyListener;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.ParseError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Volley adapter for JSON requests that will be parsed into Java objects by
 * Gson.
 */
public class GsonRequest<T> extends Request<T> {
    /**
     * 304回调
     */
    public interface OnParseNetworkResponseListener<T> {
        Response<T> onParse(NetworkResponse response);
    }

    private final Class<T> clazz;

    private Map<String, String> headers;

    private final Map<String, String> params;

    private Listener<T> listener;

    private TypeReference<T> typeToken;

    private OnParseNetworkResponseListener<T> mParseListener;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
            Listener<T> listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = null;
        this.listener = listener;
    }

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
                       Listener<T> listener, ErrorListener errorListener,OnParseNetworkResponseListener parseNetworkResponseListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = null;
        this.listener = listener;
        this.mParseListener = parseNetworkResponseListener;
    }

    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(int type, String url, Class<T> clazz, Map<String, String> headers,
            Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {
        super(type, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = params;
        this.listener = listener;
    }

    public GsonRequest(int type, String url, Class<T> clazz, Map<String, String> headers,
            Map<String, String> params, Listener<T> listener, ErrorListener errorListener,
            OnParseNetworkResponseListener parseListener) {
        super(type, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = params;
        this.listener = listener;
        this.mParseListener = parseListener;
    }

    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
            Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = params;
        this.listener = listener;
    }

    public GsonRequest(int method, String url, TypeReference<T> typeToken, Listener<T> listener,
            ErrorListener errorListener) {
        super(method, url, errorListener);
        this.typeToken = typeToken;
        this.clazz = null;
        this.params = null;
        this.listener = listener;
    }

    public GsonRequest(int method, String url, TypeReference<T> typeToken, Listener<T> listener,
            ErrorListener errorListener, OnParseNetworkResponseListener<T> parseListener) {
        super(method, url, errorListener);
        this.typeToken = typeToken;
        this.clazz = null;
        this.params = null;
        this.listener = listener;
        this.mParseListener = parseListener;
    }

    public GsonRequest(String url, TypeReference<T> typeToken, Listener<T> listener,
            ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.typeToken = typeToken;
        this.params = null;
        this.clazz = null;
        this.listener = listener;
    }

    public GsonRequest(String url, TypeReference<T> typeToken, Map<String, String> headers,
            Listener<T> listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.typeToken = typeToken;
        this.params = null;
        this.clazz = null;
        this.listener = listener;
        this.headers = headers;
    }

    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
            Listener<T> listener, OnCacheListener<T> cacheListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = null;
        this.listener = listener;
        this.cacheListener = cacheListener;
    }

    public GsonRequest(int type, String url, Class<T> clazz, Map<String, String> headers,
            Map<String, String> params, Listener<T> listener, OnCacheListener<T> cacheListener,
            ErrorListener errorListener) {
        super(type, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = params;
        this.listener = listener;
        this.cacheListener = cacheListener;
    }

    public GsonRequest(int method, String url, TypeReference<T> typeToken, Listener<T> listener,
            OnCacheListener<T> cacheListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.typeToken = typeToken;
        this.clazz = null;
        this.params = null;
        this.listener = listener;
        this.cacheListener = cacheListener;
    }

    public GsonRequest(String url, TypeReference<T> typeToken, Listener<T> listener,
            OnCacheListener<T> cacheListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.typeToken = typeToken;
        this.params = null;
        this.clazz = null;
        this.listener = listener;
        this.cacheListener = cacheListener;
    }

    public GsonRequest(String url, TypeReference<T> typeToken, Map<String, String> headers,
            Listener<T> listener, OnCacheListener<T> cacheListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.typeToken = typeToken;
        this.params = null;
        this.clazz = null;
        this.listener = listener;
        this.cacheListener = cacheListener;
        this.headers = headers;
    }

    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
            Listener<T> listener, OnCacheListener<T> cacheListener,
            OnNotModifyListener notModifyListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = null;
        this.listener = listener;
        this.cacheListener = cacheListener;
        this.notModifyListener = notModifyListener;
    }

    public GsonRequest(int type, String url, Class<T> clazz, Map<String, String> headers,
            Map<String, String> params, Listener<T> listener, OnCacheListener<T> cacheListener,
            OnNotModifyListener notModifyListener, ErrorListener errorListener) {
        super(type, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = params;
        this.listener = listener;
        this.cacheListener = cacheListener;
        this.notModifyListener = notModifyListener;
    }

    public GsonRequest(int type, String url, Class<T> clazz, Map<String, String> headers,
            Map<String, String> params, Listener<T> listener, OnCacheListener<T> cacheListener,
            OnNotModifyListener notModifyListener, ErrorListener errorListener,
            OnParseNetworkResponseListener<T> onParseListener) {
        super(type, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = params;
        this.listener = listener;
        this.cacheListener = cacheListener;
        this.notModifyListener = notModifyListener;
        this.mParseListener = onParseListener;
    }

    public GsonRequest(int method, String url, TypeReference<T> typeToken, Listener<T> listener,
            OnCacheListener<T> cacheListener, OnNotModifyListener notModifyListener,
            ErrorListener errorListener) {
        super(method, url, errorListener);
        this.typeToken = typeToken;
        this.clazz = null;
        this.params = null;
        this.listener = listener;
        this.cacheListener = cacheListener;
        this.notModifyListener = notModifyListener;
    }

    public GsonRequest(String url, TypeReference<T> typeToken, Listener<T> listener,
            OnCacheListener<T> cacheListener, OnNotModifyListener notModifyListener,
            ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.typeToken = typeToken;
        this.params = null;
        this.clazz = null;
        this.listener = listener;
        this.cacheListener = cacheListener;
        this.notModifyListener = notModifyListener;
    }

    public GsonRequest(String url, TypeReference<T> typeToken, Listener<T> listener,
                       OnCacheListener<T> cacheListener, OnNotModifyListener notModifyListener,
                       ErrorListener errorListener, OnParseNetworkResponseListener parseListener) {
        super(Method.GET, url, errorListener);
        this.typeToken = typeToken;
        this.params = null;
        this.clazz = null;
        this.listener = listener;
        this.cacheListener = cacheListener;
        this.notModifyListener = notModifyListener;
        this.mParseListener = parseListener;
    }

    public GsonRequest(String url, TypeReference<T> typeToken, Map<String, String> headers,
            Listener<T> listener, OnCacheListener<T> cacheListener,
            OnNotModifyListener notModifyListener, ErrorListener errorListener,
            OnParseNetworkResponseListener parseListener) {
        super(Method.GET, url, errorListener);
        this.typeToken = typeToken;
        this.params = null;
        this.clazz = null;
        this.listener = listener;
        this.headers = headers;
        this.cacheListener = cacheListener;
        this.notModifyListener = notModifyListener;
        this.mParseListener = parseListener;
    }

    public GsonRequest(String url, TypeReference<T> typeToken, Map<String, String> headers,
            Listener<T> listener, OnCacheListener<T> cacheListener,
            OnNotModifyListener notModifyListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.typeToken = typeToken;
        this.params = null;
        this.clazz = null;
        this.listener = listener;
        this.cacheListener = cacheListener;
        this.headers = headers;
        this.notModifyListener = notModifyListener;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params != null ? params : super.getParams();
    }

    @Override
    public void addHeader(String key, String value) {
        if (headers == null)
            headers = new HashMap<String, String>();
        headers.put(key, value);
    }

    @Override
    protected void deliverResponse(T response) {
        if (null != listener) {
            listener.onResponse(response);
        }
    }

    @Override
    public void finish(String tag) {
        super.finish(tag);
        listener = null;
        mParseListener = null;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        if (mParseListener != null)
            return mParseListener.onParse(response);
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if (typeToken == null)
                return Response.success(JSON.parseObject(json, clazz),
                        HttpHeaderParser.parseCacheHeaders(response));
            else
                return (Response<T>)Response.success(JSON.parseObject(json, typeToken.getType()),
                        HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }
}
