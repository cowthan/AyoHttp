package org.ayo.http.ok;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

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
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/8/16.
 */
public class OkhttpWorker extends HttpWorker {


    @Override
    protected void fire(AyoHttp request) {

        OkHttpUtils.getInstance().setConnectTimeout(30000, TimeUnit.MILLISECONDS); //连接超时，30秒
        OkHttpUtils.getInstance().setReadTimeout(30000, TimeUnit.MILLISECONDS); //读超时，30秒
        OkHttpUtils.getInstance().setWriteTimeout(30000, TimeUnit.MILLISECONDS); //写超时，30秒
        //使用https，但是默认信任全部证书
        OkHttpUtils.getInstance().setCertificates();

        String url = request.url;
        //基于OkHttpUtils辅助类


        //1 method决定了OkHttpRequestBuilder的哪个子类
        if(request.method.equalsIgnoreCase("get")){
            OkHttpUtils
                    .get()
                    .headers(request.headers)
                    .url(url)
                    .tag(request.flag)
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
                        .url(url)
                        .headers(request.headers)
                        .params(request.params)
                        .tag(request.flag)
                        .build()
                        .execute(new MyStringCallback(request, request.callback));
            }
            //情况2：postString
            //情况3：postFile--流形式，不带name，带mime
            //情况4：postFile--表单形式，带name，带filename
            if(hasStringEntity){
                OkHttpUtils
                        .postString()
                        .url(url)
                        .headers(request.headers)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .content(request.stringEntity)
                        .tag(request.flag)
                        .build()
                        .execute(new MyStringCallback(request, request.callback));
            }else if(postFileLikeStream){
                OkHttpUtils
                        .postFile()
                        .url(url)
                        .headers(request.headers)//
                        .file(request.file)
                        .build()
                        .execute(new MyStringCallback(request, request.callback));
            }else if(postFileLikeForm){
                PostFormBuilder b = OkHttpUtils.post();
                for(String key: request.files.keySet()){
                    File f = request.files.get(key);
                    b.addFile(key, f.getName(), f);
                }

                b.url(url)//
                        .params(request.params)//
                        .headers(request.headers)//
                        .build()//
                        .execute(new MyStringCallback(request, request.callback));
            }

        }else{
            throw new RuntimeException("使用了不支持的http谓词：" + request.method);
        }

    }

    private void addHeader(OkHttpRequestBuilder builder, Map<String, String> headers){

    }

    public class MyStringCallback<T> extends StringCallback
    {

        private BaseHttpCallback<T> callback;
        AyoHttp request;

        public MyStringCallback(AyoHttp request, BaseHttpCallback<T> callback) {
            this.callback = callback;
            this.request = request;
        }

        @Override
        public void onBefore(Request request)
        {
        }

        @Override
        public void onAfter()
        {
        }

        @Override
        public void onError(Call call, Exception e)
        {
            e.printStackTrace();
            callback.onFinish(false, HttpProblem.SERVER_ERROR, new FailInfo(404, "1", e.getLocalizedMessage()), null);
        }

        @Override
        public void onResponse(String response)
        {
            //Log.i("dddddd", "onResponse--" + response);

            String s = request.topLevelConverter.convert(response, callback);
            if(s != null){
                T bean = null;
                try {
                    bean = request.resonseConverter.convert(s, new TypeToken<T>() {});
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

            }
        }

        @Override
        public void inProgress(float progress)
        {
            callback.onLoading((long) (progress*100), 100L);
        }

        @Override
        public String parseNetworkResponse(Response response) throws IOException {

            Map<String, String> m = new HashMap<>();

            Headers header = response.headers();
            if(header != null){
                for(String name: header.names()){
                    m.put(name, header.get(name));
                }
            }

            request.intercepter.responseHeader(m);
            return super.parseNetworkResponse(response);
        }
    }


}
