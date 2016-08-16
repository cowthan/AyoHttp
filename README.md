# AyoHttp
http库，可以兼容okhttp，volly等底层库，便于切换和测试，附带retrofit的demo

----------

仿的Retrofit，但是不支持注解和RxJava，也没加入Retrofit的Adapter机制

---------

## 1 基本套路

* 发起一个请求，分为几步：
    * 配置：超时时间等
    * 构造请求AyoHttp：请求方式，path参数，query参数，post参数，上传流，文件流等
    * 发起请求：HttpWorker是请求的发起者，由各种第三方http库实现，现在支持OkHttpWorker
    * 响应：拿到header，解析InputStream，使用StreamConverter,支持byte[]，File，String三种解析
        * 超时，映射到BaseHttpCallback
        * 404， 500等，映射到BaseHttpCallback
        * byte[]和File直接映射到BaseHttpCallback
    * 对于String，可能是xml，json，protobuf等格式，并且里面分为状态字段和业务字段
        * TopLevelConverter解析状态字段，并映射到BaseHttpCallback（这个就是用户使用的callback）
        * ResposneConverter解析业务字段，并映射到BaseHttpCallback
    * 其中，ResponseConverter对应不同的解析器，如FastJson，Gson，Xml解析器等
    * 用户在BaseHttpCallback中实现自己的业务逻辑




## 2 代码


```java

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    getRequest().flag("测试接口")
            .actionGet()
            .url("http://chuanyue.iwomedia.cn/daogou/app/app")
            .header("deviceId", "11122334")
            .path("jid", "234")
            .queryString("nickname", "哈哈")
            .queryString("mobile", "15011571307")
            .queryString("code", "1234")
            .queryString("pwd", "111111")
            .queryString("icon", "")
            .queryString("jpushId", "")
            .queryString("deviceId", "")
            .queryString("os", "android")
//                .path("id", "1")
//                .param("pwd", "dddddfffggghhh")
//                .param("file-1", new File(""))
//                .file(new File(""))
//                .stringEntity("hahahahahahahah哈哈哈哈哈哈哈哈lddddddddd2222222222")
            .callback(new BaseHttpCallback<List<RespRegist>>() {
                @Override
                public void onFinish(boolean isSuccess, HttpProblem problem, FailInfo resp, List<RespRegist> respRegist) {
                    if(isSuccess){
                        Toast.makeText(getApplicationContext(), "请求成功--" + respRegist.size(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "请求失败：" + resp.dataErrorReason, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onLoading(long current, long total) {
                    super.onLoading(current, total);
                }
            }, new TypeToken<List<RespRegist>>(){})
            .fire();


}

public AyoHttp getRequest(){
    return AyoHttp.request()
                .connectionTimeout(10000)
                .writeTimeout(10000)
                .readTimeout(10000)
                .worker(new OkhttpWorker())
                .streamConverter(new StreamConverter.StringConverter())   //ByteArrayConverter   FileConverter
                .topLevelConverter(new SampleTopLevelConverter())
                .resonseConverter(new FastJsonConverter())
                .intercept(new LogIntercepter());
}

```