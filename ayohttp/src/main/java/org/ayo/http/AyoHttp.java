package org.ayo.http;

import android.text.TextUtils;

import org.ayo.http.callback.BaseHttpCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/16.
 */
public class AyoHttp {

    private AyoHttp(){
    }

    public static AyoHttp newInstance(){
        AyoHttp r = new AyoHttp();
        return r;
    }

    public Map<String, String> params = new HashMap<String, String>();
    public Map<String, String> pathParams = new HashMap<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();
    public Map<String, File> files = new HashMap<String, File>();
    public String stringEntity;
    public File file;  //post一个file，不知道这是什么原理，不过和表单提交不一样
    public String url = "";
    public String method = "get";
    public String flag = "";
    public HttpAdapter worker;

    public HttpIntercepter intercepter;
    public TopLevelConverter topLevelConverter;
    public ResonseConverter resonseConverter;
    public BaseHttpCallback<?> callback;


    //---------------------------------------------------------------//
    public AyoHttp flag(String flag){
        if(!TextUtils.isEmpty(this.flag)){
            throw new RuntimeException("flag is duplicated.");
        }

        this.flag = flag;
        return this;
    }

    public AyoHttp param(String name, String value){
        if(this.params == null) this.params = new HashMap<String, String>();
        if(value == null) value = "";
        params.put(name, value);
        return this;
    }

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

    public AyoHttp path(String name, String value){
        if(this.pathParams == null) this.pathParams = new HashMap<String, String>();
        pathParams.put(name, value);
        return this;
    }

    public AyoHttp header(String name, String value){
        if(this.headers == null) this.headers = new HashMap<String, String>();
        headers.put(name, value);
        return this;
    }

    public AyoHttp method(String method){
        this.method = method;
        return this;
    }

    /**
     * don't know how to pass this in volly, now just work in xutils
     * @param entity
     * @return
     */
    public AyoHttp stringEntity(String entity){
        this.stringEntity = entity;
        return this;
    }

    public AyoHttp url(String url){
        this.url = url;
        return this;
    }

    public AyoHttp adapter(HttpAdapter worker){
        this.worker = worker;
        return this;
    }


    public <T> void fire(){
        worker.fire(this);
    }

}
