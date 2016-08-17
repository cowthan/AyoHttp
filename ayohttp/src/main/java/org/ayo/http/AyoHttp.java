package org.ayo.http;

import android.text.TextUtils;

import org.ayo.http.callback.BaseHttpCallback;
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

}
