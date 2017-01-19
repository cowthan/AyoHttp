package org.ayo.http;

import android.text.TextUtils;

import org.ayo.http.callback.BaseHttpCallback;
import org.ayo.http.callback.FailInfo;
import org.ayo.http.callback.HttpProblem;
import org.ayo.http.converter.ResponseConverter;
import org.ayo.http.converter.TypeToken;
import org.ayo.http.stream.StreamConverter;
import org.ayo.http.utils.HttpHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class AyoHttp {

    private AyoHttp(){
    }

    public static AyoHttp request(){
        AyoHttp r = new AyoHttp();
        return r;
    }

    public TypeToken token;

    public long connectionTimeout = 30000;
    public long writeTimeout = 30000;
    public long readTimeout = 30000;

    public Map<String, String> params = new HashMap<String, String>();
    public Map<String, String> pathParams = new HashMap<String, String>();
    public Map<String, String> queryStrings = new HashMap<>();


    public Map<String, String> headers = new HashMap<String, String>();
    public Map<String, File> files = new HashMap<String, File>();

    public String stringEntity;

    public File file;

    public String url = "";
    public String method = "get";
    public String flag = "";
    public HttpWorker worker;

    public HttpIntercepter intercepter;
    public StreamConverter<?> streamConverter;
    public TopLevelConverter topLevelConverter;
    public ResponseConverter resonseConverter;
    public BaseHttpCallback<?> callback;


    //---------------------------------------------------------------//
    public AyoHttp flag(String flag){
        if(!TextUtils.isEmpty(this.flag)){
            throw new RuntimeException("flag is duplicated.");
        }

        this.flag = flag;
        return this;
    }

    public AyoHttp connectionTimeout(long time){
        connectionTimeout = time;
        return this;
    }

    public AyoHttp writeTimeout(long time){
        writeTimeout = time;
        return this;
    }

    public AyoHttp readTimeout(long time){
        readTimeout = time;
        return this;
    }

    ///---------------
    public AyoHttp param(String name, String value){
        if(value == null) value = "";
        params.put(name, value);
        return this;
    }

    public AyoHttp queryString(String name, String value){
        if(value == null) value = "";
        queryStrings.put(name, value);
        return this;
    }

    public AyoHttp path(String name, String value){
        if(value == null) value = "";
        pathParams.put(name, value);
        return this;
    }

    //--------------

    private boolean uploadFile = false;
    private boolean needCompress = false;

    public AyoHttp param(String name, File value){
        if(this.files == null) this.files = new HashMap<String, File>();
        files.put(name, value);
        uploadFile = true;
        return this;
    }

    public AyoHttp file(File f){
        file = f;
        return this;
    }


    public AyoHttp header(String name, String value){
        if(this.headers == null) this.headers = new HashMap<String, String>();
        headers.put(name, value);
        return this;
    }

    ///------------
    public AyoHttp actionGet(){
        this.method = "get";
        return this;
    }

    public AyoHttp actionPost(){
        this.method = "post";
        return this;
    }

    public AyoHttp actionPut(){
        this.method = "put";
        return this;
    }

    public AyoHttp actionDelete(){
        this.method = "delete";
        return this;
    }

    public AyoHttp actionHead(){
        this.method = "head";
        return this;
    }

    public AyoHttp actionPatch(){
        this.method = "patch";
        return this;
    }

    ///--------------

    public AyoHttp stringEntity(String entity){
        this.stringEntity = entity;
        return this;
    }

    public AyoHttp url(String url){
        this.url = url;
        return this;
    }

    public AyoHttp worker(HttpWorker worker){
        this.worker = worker;
        return this;
    }

    public <T> AyoHttp streamConverter(StreamConverter<T> converter){
        this.streamConverter = converter;
        return this;
    }

    public AyoHttp topLevelConverter(TopLevelConverter converter){
        this.topLevelConverter = converter;
        return this;
    }

    public AyoHttp resonseConverter(ResponseConverter converter){
        this.resonseConverter = converter;
        return this;
    }

    public AyoHttp intercept(HttpIntercepter h){
        this.intercepter = h;
        return this;
    }

    public <T> AyoHttp callback(BaseHttpCallback<T> h, TypeToken<T> tTypeToken){
        this.callback = h;
        token = tTypeToken;
        return this;
    }

    public void fire(){

        if(this.pathParams.size() > 0){
            for(String key: this.pathParams.keySet()){
                this.url = this.url.replace("{" + key + "}", this.pathParams.get(key) + "");
            }
        }

        this.url = HttpHelper.makeURL(this.url, this.queryStrings);

        worker.fire(this);
    }

    public Class<? extends StringTopLevelModel> classTop;

    public static <T, E extends StringTopLevelModel> void processStringResponse(String respStr, TopLevelConverter<E> topLevelConverter, ResponseConverter<T> resonseConverter, TypeToken<T> typeToken, BaseHttpCallback<T> callback){

        StringTopLevelModel resp = null;
        try {
            resp = topLevelConverter.convert(respStr);
        }catch (Exception e){
            e.printStackTrace();
            callback.onFinish(false, HttpProblem.DATA_ERROR, new FailInfo(102, "102", "Converter转换错误，可能是服务器错误，也可能是本地Top逻辑错误"), null);
            return;
        }

        if(resp != null){
            if(resp.isOk()){

                T bean = null;
                try {
                    if(typeToken == new TypeToken<String>(){}){
                        bean = (T) resp.getResult();
                    }else if(typeToken == new TypeToken<Boolean>(){}){
                        bean = (T) Boolean.TRUE;
                    }else{
                        bean = (T) resonseConverter.convert(resp.getResult(), typeToken);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    callback.onFinish(false, HttpProblem.DATA_ERROR, new FailInfo(104, "104", "业务数据解析错误"), null);
                    return;
                }

                callback.onFinish(true, HttpProblem.OK, null, bean);

            }else{
                callback.onFinish(false, HttpProblem.LOGIC_FAIL, new FailInfo(101, resp.getErrorCode(), resp.getErrorMsg()), null);
            }
        }else{
            callback.onFinish(false, HttpProblem.LOGIC_FAIL, new FailInfo(103, "103", "响应结果可能转换为了一个null对象"), null);
        }


    }

}
