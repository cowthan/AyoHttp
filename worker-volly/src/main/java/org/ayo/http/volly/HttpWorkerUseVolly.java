
package org.ayo.http.volly;

import com.alibaba.json.JSON;
import com.alibaba.json.JSONException;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.ParseError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.GsonRequest;
import com.android.volley.request.MultiPartXRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.dongqiudi.news.BaseApplication;
import com.dongqiudi.news.util.AppUtils;
import com.dongqiudi.news.util.OkHttp3Stack;
import com.dongqiudi.news.util.Trace;

import org.apache.http.protocol.HTTP;
import org.ayo.http.AyoHttp;
import org.ayo.http.HttpWorker;
import org.ayo.http.StringTopLevelModel;
import org.ayo.http.callback.BaseHttpCallback;
import org.ayo.http.callback.FailInfo;
import org.ayo.http.callback.HttpProblem;
import org.ayo.http.converter.TypeToken;
import org.ayo.http.dqd.DqdTopLevelJsonModel;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by qiaoliang on 2017/3/28.
 */

public class HttpWorkerUseVolly extends HttpWorker {

    private static final String TAG = "server-commu";

    public static RequestQueue mRequestQueue;

    public static Application app;

    public static void init(Application app) {
        HttpWorkerUseVolly.app = app;
        OkHttp3Stack stack = new OkHttp3Stack();
        mRequestQueue = Volley.newRequestQueue(app, stack);

    }

    @Override
    protected void fire(AyoHttp request) {
        request.intercepter.beforeRequest(request);

        String url = request.url;
        // 基于OkHttpUtils辅助类

        // 1 method决定了OkHttpRequestBuilder的哪个子类
        if (request.method.equalsIgnoreCase("get")) {
            doNormalRequest(request, Request.Method.GET, url, request.token, request.headers,
                    request.params, request.callback);
        } else if (request.method.equalsIgnoreCase("post")) {
            boolean hasStringEntity = (request.stringEntity != null
                    && !request.stringEntity.equals(""));
            boolean postFileLikeForm = (request.files != null && request.files.size() > 0);
            boolean postFileLikeStream = (request.file != null);

            // 情况1：postForm
            if (!hasStringEntity && !postFileLikeForm && !postFileLikeStream) {
                doNormalRequest(request, Request.Method.POST, url, request.token, request.headers,
                        request.params, request.callback);
            }
            // 情况2：postString
            // 情况3：postFile--流形式，不带name，带mime
            // 情况4：postFile--表单形式，带name，带filename
            if (hasStringEntity) {
                doPostStreamRequest(request, Request.Method.POST, request.url, request.token,
                        request.headers, request.params, request.callback);
            } else if (postFileLikeStream) {
                doPostStreamRequest(request, Request.Method.POST, request.url, request.token,
                        request.headers, request.params, request.callback);
            } else if (postFileLikeForm) {
                doMultipartRequest(request, url, request.token, request.callback);
            }

        } else if (request.method.equalsIgnoreCase("put")) {
            doNormalRequest(request, Request.Method.PUT, url, request.token, request.headers,
                    request.params, request.callback);
        } else if (request.method.equalsIgnoreCase("delete")) {
            doNormalRequest(request, Request.Method.DELETE, url, request.token, request.headers,
                    request.params, request.callback);
        } else if (request.method.equalsIgnoreCase("head")) {
            doNormalRequest(request, Request.Method.HEAD, url, request.token, request.headers,
                    request.params, request.callback);
        } else if (request.method.equalsIgnoreCase("patch")) {
            doNormalRequest(request, Request.Method.PATCH, url, request.token, request.headers,
                    request.params, request.callback);
        } else {
            throw new RuntimeException("使用了不支持的http谓词：" + request.method);
        }

    }

    /**
     * 处理get, 非multipart也非stream的post， put， delete
     */
    private <T> void doNormalRequest(final AyoHttp rawReq, int method, String url,
                                     final TypeToken<T> clazz, Map<String, String> header, Map<String, String> params,
                                     final BaseHttpCallback<T> callback) {

        GsonRequest<T> req = new GsonRequest<T>(method, url, null, header, params,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T model) {
                        onRespOk(model, callback);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        onRespError(error, callback);
                    }
                }, new GsonRequest.OnParseNetworkResponseListener() {
                    @Override
                    public Response onParse(NetworkResponse response) {
                        return onStringResponseParse(rawReq, clazz, response);
                    }
                });
        req.setTag(rawReq.flag);
        //req.setRetryPolicy();///这里面可以设置这个请求的超时时间之类的
        ///req.setShouldCache(false); ///是否需要缓存
        BaseApplication.getInstance().addToRequestQueue(req);
    }

    private <T> void doMultipartRequest(final AyoHttp rawReq, String url, final TypeToken<T> clazz,
                                        final BaseHttpCallback<T> callback) {

        /// 有统一的header：
        // req.setHeaders(AppUtils.getOAuthMap(context));

        MultiPartXRequest<T> req = new MultiPartXRequest(Request.Method.POST, url,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T model) {
                        onRespOk(model, callback);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                        onRespError(error, callback);
                    }
                }) {
            @Override
            protected Response parseNetworkResponse(NetworkResponse response) {
                return onStringResponseParse(rawReq, clazz, response);
            }
        };
        req.setProgressListener(new Response.ProgressListener() {
            @Override
            public void onProgress(long transferredBytes, long totalSize) {
                Trace.d(TAG, "onProgress:" + transferredBytes + "	" + totalSize);
            }
        });

        if (rawReq.headers != null && !rawReq.headers.isEmpty()) {
            Iterator<String> it = rawReq.headers.keySet().iterator();
            String key;
            String value;
            while (it.hasNext()) {
                key = it.next();
                if (TextUtils.isEmpty(key))
                    continue;
                value = rawReq.headers.get(key);
                if (TextUtils.isEmpty(value))
                    continue;
                req.addHeader(key, value);
            }
        }

        if (rawReq.params != null && !rawReq.params.isEmpty()) {
            Iterator<String> it = rawReq.params.keySet().iterator();
            String key;
            String value;
            while (it.hasNext()) {
                key = it.next();
                if (TextUtils.isEmpty(key))
                    continue;
                value = rawReq.params.get(key);
                if (TextUtils.isEmpty(value))
                    continue;
                req.addMultipartParam(key, "text/*", value);
            }
        }
        if (rawReq.files != null && !rawReq.files.isEmpty()) {
            Iterator<String> it = rawReq.files.keySet().iterator();
            String key;
            int i = 1;
            MultiPartXRequest.MultiFileParam param;
            while (it.hasNext()) {
                key = it.next();
                String value = rawReq.files.get(key).getAbsolutePath();
                String extension = AppUtils.getFileExtensionByFileName(value);
                if (TextUtils.isEmpty(value))
                    continue;
                param = new MultiPartXRequest.MultiFileParam(value, "p" + i,
                        "{{p" + i + "}}." + extension, "image/*", HTTP.UTF_8);
                i++;
                req.addMultiFileParam(param);
            }
        }
        req.setTag(rawReq.flag);
        BaseApplication.getInstance().addToRequestQueue(req);
    }



    private <T> void doPostStreamRequest(final AyoHttp rawReq, int method, String url,
                                         final TypeToken<T> clazz, Map<String, String> header, Map<String, String> params,
                                         final BaseHttpCallback<T> callback) {
        //req.setTag(rawReq.flag);
        throw new RuntimeException("还没有实现post stream行为");
    }

    public <T> Response onStringResponseParse(AyoHttp req, TypeToken<T> typeToken,
                                              NetworkResponse response) {

        try {
            String json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            req.intercepter.beforeTopLevelConvert(json);

            // 处理json
            /// --本来这一句就够了，但现在怕volly的success里可能有别的处理，所以暂时先走volly
            // AyoHttp.processStringResponse(json, rawReq.topLevelConverter,
            // rawReq.resonseConverter, clazz, callback);
            // ~~over

            StringTopLevelModel resp = null;
            try {
                resp = req.topLevelConverter.convert(json);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "安卓报错--Converter转换错误，可能是服务器错误，也可能是本地Top逻辑错误");
                return Response.error(new DqdHttpError()
                        .failInfo(new FailInfo(500, 500, "安卓报错--Converter转换错误，可能是服务器错误，也可能是本地Top逻辑错误")));
                // callback.onFinish(false, HttpProblem.SERVER_ERROR, new
                // FailInfo(500, "500",
                // "安卓报错--Converter转换错误，可能是服务器错误，也可能是本地Top逻辑错误"), null);
                // return;
            }

            if (resp != null) {
                if (resp.isOk()) {

                    T bean = null;
                    try {
                        if (typeToken == new TypeToken<String>() {
                        }) {
                            bean = (T)resp.getResult();
                        } else if (typeToken == new TypeToken<Boolean>() {
                        }) {
                            bean = (T) Boolean.TRUE;
                        } else {
                            bean = (T)req.resonseConverter.convert(resp.getResult(), typeToken);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "安卓报错--业务数据解析错误");
                        return Response.error(new DqdHttpError()
                                .failInfo(new FailInfo(500, 500, "安卓报错--业务数据解析错误")));
                        // e.printStackTrace();
                        // callback.onFinish(false, HttpProblem.DATA_ERROR, new
                        // FailInfo(500, "500", "业务数据解析错误"), null);
                        // return;
                    }

                    return Response.success(bean, HttpHeaderParser.parseCacheHeaders(response));

                } else {
                    return Response.error(new DqdHttpError()
                            .failInfo(new FailInfo(444, resp.getErrorCode(), resp.getErrorMsg())));
                    // callback.onFinish(false, HttpProblem.LOGIC_FAIL, , null);
                }
            } else {
                return Response.error(new DqdHttpError()
                        .failInfo(new FailInfo(500, 500, "http响应结果可能转换为了一个null对象")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }

    }

    private <T> void onRespOk(T model, BaseHttpCallback<T> callback){
        callback.onFinish(true, HttpProblem.OK, null, model);
    }

    private void onRespError(VolleyError error, BaseHttpCallback<?> callback){
        FailInfo failInfo = null;
        if (error instanceof DqdHttpError) {
            failInfo = ((DqdHttpError)error).failInfo();
        }else{
            String data = AppUtils.getErrorMessage(error);
            if(data == null){
                failInfo = null;
            }else{
                try {
                    DqdTopLevelJsonModel m = JSON.parseObject(data, DqdTopLevelJsonModel.class);
                    failInfo = new FailInfo(error.networkResponse.statusCode, m.getErrorCode(), m.getErrorMsg());
                    failInfo.setExtraParam(m.params);
                } catch (JSONException e) {
                    e.printStackTrace();
                    failInfo = null;
                }
            }
        }
        if (failInfo == null) {
            failInfo = new FailInfo(500, 500, "意料之外的错误：" + error.getMessage());
        }
        callback.onFinish(false, null, failInfo, null);
    }

    public static class DqdHttpError extends VolleyError {
        private FailInfo failInfo;

        public FailInfo failInfo() {
            return failInfo;
        }

        public DqdHttpError failInfo(FailInfo failInfo) {
            this.failInfo = failInfo;
            return this;
        }
    }
}
