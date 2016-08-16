# AyoHttp
http库，可以兼容okhttp，volly等底层库，便于切换和测试，附带retrofit的demo

----------

低仿Retrofit，但没那么多工厂模式，也不支持注解和RxJava，也没加入Retrofit的Adapter机制

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


* 声明：
    * OkHttpWorker不是直接基于OkHttp，而是基于开源项目OkHttpUtils
        * 地址：https://github.com/hongyangAndroid/okhttputils
    * StreamConverter如果使用FileConverter，就是文件下载，但文件下载还是有很多细节的，不建议直接使用这个框架
        * 参考：https://github.com/Aspsine/MultiThreadDownload，已用在了正式项目里，省不少劲
    * StreamConverter如果使用ByteArrayConverter，就是byte[]，但一般用不到，所以并未真正支持
    * 整个框架仿照的是retrofit，主框架简单至极，worker和converter作为插件化子模块，想用哪个，就引用哪个
    * 在此感谢上面涉及到的三位作者
    * http原理blog：http://blog.csdn.net/lmj623565791/article/details/47911083

* 声明2：
    * 请求方式支持：
        * get
        * post
            * post from
            * post string
            * post file
        * put
        * delete
        * head
        * patch
   * Json解析中的TypeToken处理的还不完善，需要自己手动传入（在BaseHttpCallback传入）
   * 上传文件时，上传进度提示没有实现
   * 重发策略，本人尚未深入研究，还不知道是怎么回事
   * 缓存，也未深入研究
   * 响应code是300到400时，是重定向，怎么处理的，也不知道
   * 403的授权问题，okhttp可以支持
   * https的问题，okhttp也支持
   * 所有授权和https的问题，本框架不做过多封装，但各个worker会暴露出各个库的配置项，可以直接配置，demo会给出



## 2 使用


```java

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    ///下面这部分代码，就是固定模式，可以通过注解生成，当然retrofit支持，这里不支持
    getRequest().flag("测试接口")
            .actionGet()
            .url("http://chuanyue.iwomedia.cn/daogou/app/app?jid={jid}")
            .header("deviceId", "11122334")               //----请求头
            .path("jid", "234")                           //----path参数，会替换掉url中的{key}
            .queryString("nickname", "哈哈")              //----get参数，会拼到url中
            .queryString("os", "android")
//                .path("id", "1")
//                .param("pwd", "dddddfffggghhh")         //----post参数，form提交
//                .param("file-1", new File(""))          //----post参数，上传文件，可多文件上传，multipart的form提交
//                .file(new File(""))                     //----post提交文件，只支持一个文件，post stream方式
//                .stringEntity("hahahahahahahah哈哈哈哈哈哈哈哈lddddddddd2222222222") //----post提交文本，post stream方式
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