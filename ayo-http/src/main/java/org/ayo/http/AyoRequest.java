package org.ayo.http;

import org.ayo.http.callback.BaseHttpCallback;
import org.ayo.http.callback.FailInfo;
import org.ayo.http.callback.HttpProblem;
import org.ayo.http.converter.ResponseConverter;
import org.ayo.http.converter.TypeToken;
import org.ayo.http.impl.progress.ProgressRequestListener;
import org.ayo.http.impl.progress.ProgressResponseListener;
import org.ayo.http.stream.StreamConverter;
import org.ayo.http.utils.HttpHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 */
public class AyoRequest {

    private AyoRequest(){
    }

    public static AyoRequest request(){
        AyoRequest r = new AyoRequest();
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
    public String mediaType;

    public File file;

    public String url = "";
    public String method = "get";
    public Object tag = "";
    public static HttpWorker worker;

    public HttpIntercepter intercepter;
    public StreamConverter<?> streamConverter;
    public TopLevelConverter topLevelConverter;
    public ResponseConverter resonseConverter;
    public BaseHttpCallback<?> callback;


    //---------------------------------------------------------------//
    public AyoRequest tag(Object tag){
        this.tag = tag;
        return this;
    }

    public AyoRequest connectionTimeout(long time){
        connectionTimeout = time;
        return this;
    }

    public AyoRequest writeTimeout(long time){
        writeTimeout = time;
        return this;
    }

    public AyoRequest readTimeout(long time){
        readTimeout = time;
        return this;
    }

    ///---------------
    public AyoRequest param(String name, String value){
        if(value == null) value = "";
        params.put(name, value);
        return this;
    }

    public AyoRequest queryString(String name, String value){
        if(value == null) value = "";
        queryStrings.put(name, value);
        return this;
    }

    public AyoRequest path(String name, String value){
        if(value == null) value = "";
        pathParams.put(name, value);
        return this;
    }

    //--------------

    private boolean uploadFile = false;
    private boolean needCompress = false;

    public AyoRequest param(String name, File value){
        if(this.files == null) this.files = new HashMap<String, File>();
        files.put(name, value);
        uploadFile = true;
        return this;
    }

    public AyoRequest file(File f){
        file = f;
        return this;
    }

    public AyoRequest mediaType(String mediaType){
        this.mediaType = mediaType;
        return this;
    }

    public AyoRequest mediaType(String mime, String charset){
        this.mediaType = mime + "; charset=" + charset;
        return this;
    }


    public AyoRequest header(String name, String value){
        if(this.headers == null) this.headers = new HashMap<String, String>();
        headers.put(name, value);
        return this;
    }

    ///------------
    public AyoRequest actionGet(){
        this.method = "get";
        return this;
    }

    public AyoRequest actionPost(){
        this.method = "post";
        return this;
    }

    public AyoRequest actionPut(){
        this.method = "put";
        return this;
    }

    public AyoRequest actionDelete(){
        this.method = "delete";
        return this;
    }

    public AyoRequest actionHead(){
        this.method = "head";
        return this;
    }

    public AyoRequest actionPatch(){
        this.method = "patch";
        return this;
    }

    ///--------------

    public AyoRequest stringEntity(String entity){
        this.stringEntity = entity;
        return this;
    }

    public AyoRequest url(String url){
        this.url = url;
        return this;
    }

    public AyoRequest worker(HttpWorker worker){
        AyoRequest.worker = worker;
        return this;
    }

    public <T> AyoRequest streamConverter(StreamConverter<T> converter){
        this.streamConverter = converter;
        return this;
    }

    public AyoRequest topLevelConverter(TopLevelConverter converter){
        this.topLevelConverter = converter;
        return this;
    }

    public AyoRequest resonseConverter(ResponseConverter converter){
        this.resonseConverter = converter;
        return this;
    }

    public AyoRequest intercept(HttpIntercepter h){
        this.intercepter = h;
        return this;
    }

    public <T> AyoRequest callback(BaseHttpCallback<T> h, TypeToken<T> tTypeToken){
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
        worker.fire(this, this.callback);
    }

    public void fireSync(ProgressRequestListener progressRequestListener, ProgressResponseListener progressResponseListener){
        if(this.pathParams.size() > 0){
            for(String key: this.pathParams.keySet()){
                this.url = this.url.replace("{" + key + "}", this.pathParams.get(key) + "");
            }
        }

        this.url = HttpHelper.makeURL(this.url, this.queryStrings);

        worker.fireSync(this, token, progressRequestListener, progressResponseListener);
    }

    public static void cancelAll(Object tag){
        worker.cancelAll(tag);
    }

    public <T> io.reactivex.Observable<T> start(TypeToken<T> typeToken){
        this.token = typeToken;
        ObservableOnSubscribe<T> os = new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(final ObservableEmitter<T> e) throws Exception {
                callback = new BaseHttpCallback<T>() {
                    @Override
                    public void onFinish(boolean isSuccess, HttpProblem problem, FailInfo resp, T t) {
                        if(isSuccess){
                            e.onNext(t);
                        }else{
                            AyoHttpException ae = new AyoHttpException();
                            ae.failInfo = resp;
                            ae.problem = problem;
                            e.onError(ae);
                        }
                        e.onComplete();
                    }
                };
                fire();
            }
        };
        Observable<T> observable = Observable.create(os);
        return observable;
    }

    public static class AyoHttpException extends Exception{
        public HttpProblem problem;
        public FailInfo failInfo;
    }

    public Class<? extends StringTopLevelModel> classTop;

    public static <T, E extends StringTopLevelModel> void processStringResponse(String respStr, TopLevelConverter<E> topLevelConverter, ResponseConverter<T> resonseConverter, TypeToken<T> typeToken, BaseHttpCallback<T> callback){

        StringTopLevelModel resp = null;
        try {
            resp = topLevelConverter.convert(respStr);
        }catch (Exception e){
            e.printStackTrace();
            callback.onFinish(false, HttpProblem.DATA_ERROR, new FailInfo(702, "顶层Converter转换错误，一般是json解析错误"), null);
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
                    callback.onFinish(true, HttpProblem.OK, null, bean);
                }catch (Exception e){
                    e.printStackTrace();
                    callback.onFinish(false, HttpProblem.DATA_ERROR, new FailInfo(703, "业务层数据解析错误，一般是json解析错误"), null);
                    return;
                }
            }else{
                callback.onFinish(false, HttpProblem.LOGIC_FAIL, new FailInfo(resp.getErrorCode(), resp.getErrorMsg()), null);
            }
        }else{
            callback.onFinish(false, HttpProblem.LOGIC_FAIL, new FailInfo(704, "顶层Conveter响应结果得到了一个null对象"), null);
        }
    }


    public static <T, E extends StringTopLevelModel> AyoResponse processStringResponseSync(String respStr, TopLevelConverter<E> topLevelConverter, ResponseConverter<T> resonseConverter, TypeToken<T> typeToken, BaseHttpCallback<T> callback){

        StringTopLevelModel resp = null;
        try {

            resp = topLevelConverter.convert(respStr);
        }catch (Exception e){
            e.printStackTrace();
            FailInfo failInfo = new FailInfo(-702, "顶层Converter转换错误，一般是json解析错误");
            //callback.onFinish(false, HttpProblem.DATA_ERROR, failInfo, null);
            AyoResponse<T> r = new AyoResponse<>();
            r.failInfo = failInfo;
            return r;
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
                    AyoResponse<T> r = new AyoResponse<>();
                    r.data = bean;
                    return r;
                    //callback.onFinish(true, HttpProblem.OK, null, bean);
                }catch (Exception e){
                    e.printStackTrace();
                    FailInfo failInfo = new FailInfo(-703, "业务层数据解析错误，一般是json解析错误");
                    //callback.onFinish(false, HttpProblem.DATA_ERROR, failInfo, null);
                    AyoResponse<T> r = new AyoResponse<>();
                    r.failInfo = failInfo;
                    return r;
                }
            }else{
                FailInfo failInfo = new FailInfo(resp.getErrorCode(), resp.getErrorMsg());
                //callback.onFinish(false, HttpProblem.LOGIC_FAIL, new FailInfo(resp.getErrorCode(), resp.getErrorMsg()), null);
                AyoResponse<T> r = new AyoResponse<>();
                r.failInfo = failInfo;
                return r;
            }
        }else{
            FailInfo failInfo = new FailInfo(-704, "顶层Conveter响应结果得到了一个null对象");
            //callback.onFinish(false, HttpProblem.LOGIC_FAIL, new FailInfo(-704, "顶层Conveter响应结果得到了一个null对象"), null);
            AyoResponse<T> r = new AyoResponse<>();
            r.failInfo = failInfo;
            return r;

        }
    }
}
