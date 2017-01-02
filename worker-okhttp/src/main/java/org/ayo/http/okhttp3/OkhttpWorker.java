package org.ayo.http.okhttp3;

import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.https.HttpsUtils;

import org.ayo.http.AyoHttp;
import org.ayo.http.HttpWorker;
import org.ayo.http.callback.BaseHttpCallback;
import org.ayo.http.callback.FailInfo;
import org.ayo.http.callback.HttpProblem;
import org.ayo.http.converter.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/8/16.
 */
public class OkhttpWorker extends HttpWorker {

    TypeToken typeToken;

    @Override
    protected void fire(AyoHttp request) {
        typeToken = request.token;
        request.intercepter.beforeRequest(request);

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        //CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000, TimeUnit.MILLISECONDS)
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .writeTimeout(30000, TimeUnit.MILLISECONDS)
                //.cookieJar(cookieJar)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)  //设置可访问所有的https网站
                .build();
        OkHttpUtils.initClient(okHttpClient);


        String url = request.url;
        //基于OkHttpUtils辅助类

        //1 method决定了OkHttpRequestBuilder的哪个子类
        if(request.method.equalsIgnoreCase("get")){
            OkHttpUtils
                    .get()
                    .tag(request.flag)
                    .url(url)
                    .headers(request.headers)
                    .build()
                    .execute(new MyStringCallback(request,  request.callback));
        }else if(request.method.equalsIgnoreCase("post")){
            boolean hasStringEntity = (request.stringEntity != null && !request.stringEntity.equals(""));
            boolean postFileLikeForm = (request.files != null && request.files.size() > 0);
            boolean postFileLikeStream = (request.file != null);

            //情况1：postForm
            if(!hasStringEntity && !postFileLikeForm && !postFileLikeStream){
                OkHttpUtils
                        .post()//
                        .tag(request.flag)
                        .url(url)
                        .headers(request.headers)
                        .params(request.params)
                        .build()
                        .execute(new MyStringCallback(request, request.callback));
            }
            //情况2：postString
            //情况3：postFile--流形式，不带name，带mime
            //情况4：postFile--表单形式，带name，带filename
            if(hasStringEntity){
                OkHttpUtils
                        .postString()
                        .tag(request.flag)
                        .url(url)
                        .headers(request.headers)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .content(request.stringEntity)
                        .build()
                        .execute(new MyStringCallback(request, request.callback));
            }else if(postFileLikeStream){
                OkHttpUtils
                        .postFile()
                        .tag(request.flag)
                        .url(url)
                        .headers(request.headers)//
                        .file(request.file)
                        .build()
                        .execute(new MyStringCallback(request, request.callback));
            }else if(postFileLikeForm){
                PostFormBuilder b = OkHttpUtils.post().tag(request.flag).url(request.url);
                for(String key: request.files.keySet()){
                    File f = request.files.get(key);
                    b.addFile(key, f.getName(), f);
                }

                b.url(url)
                        .params(request.params)
                        .headers(request.headers)
                        .build()
                        .execute(new MyStringCallback(request, request.callback));
            }

        }else if(request.method.equalsIgnoreCase("put")){
            OkHttpUtils
                    .put()
                    .tag(request.flag)
                    .url(url)
                    .headers(request.headers)
                    .requestBody("???")
                    .build()
                    .execute(new MyStringCallback(request, request.callback));
        }else if(request.method.equalsIgnoreCase("delete")){
            OkHttpUtils
                    .delete()
                    .tag(request.flag)
                    .url(url)
                    .headers(request.headers)
                    .requestBody("???")
                    .build()
                    .execute(new MyStringCallback(request, request.callback));
        }else if(request.method.equalsIgnoreCase("head")){
            OkHttpUtils
                    .head()
                    .tag(request.flag)
                    .url(url)
                    .headers(request.headers)
                    .params(request.params)
                    .build()
                    .execute(new MyStringCallback(request, request.callback));
        }else if(request.method.equalsIgnoreCase("patch")){
            OkHttpUtils
                    .patch()
                    .tag(request.flag)
                    .url(url)
                    .headers(request.headers)
                    .requestBody("???")
                    .build()
                    .execute(new MyStringCallback(request, request.callback));
        }else{
            throw new RuntimeException("使用了不支持的http谓词：" + request.method);
        }

    }

    public class MyStringCallback<T> extends StringCallback{

        private BaseHttpCallback<T> callback;
        AyoHttp request;

        public MyStringCallback(AyoHttp request, BaseHttpCallback<T> callback) {
            this.callback = callback;
            this.request = request;
        }


        @Override
        public void inProgress(float progress, long total, int id) {
            Log.i("进度:", progress + "/" + total);
            callback.onLoading((long) (progress*100), 100L);
            super.inProgress(progress, total, id);
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            callback.onFinish(false, HttpProblem.SERVER_ERROR, new FailInfo(404, "1", e.getLocalizedMessage()), null);
        }

        @Override
        public void onResponse(String response, int id) {
            request.intercepter.beforeTopLevelConvert(response);
            String s = request.topLevelConverter.convert(response, callback);
            if(s != null){
                T bean = null;
                try {
                    bean = (T) request.resonseConverter.convert(s, typeToken);
                }catch (Exception e){
                    e.printStackTrace();
                    if(callback != null){
                        callback.onFinish(false, HttpProblem.DATA_ERROR, new FailInfo(2002, "2002", "Converter转换错误"), null);
                    }
                    return;
                }
                if(callback != null){
                    callback.onFinish(true, HttpProblem.OK, null, bean);
                }

            }else{

            }
        }

        @Override
        public String parseNetworkResponse(Response response, int id) throws IOException {
            Map<String, String> m = new HashMap<>();

            Headers header = response.headers();
            if(header != null){
                for(String name: header.names()){
                    m.put(name, header.get(name));
                }
            }

            request.intercepter.responseHeader(m);
            return super.parseNetworkResponse(response, id);
        }
    }


}
