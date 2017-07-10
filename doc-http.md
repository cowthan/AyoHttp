# AyoHttp
http库，可以兼容okhttp，volly等底层库，便于切换和测试，附带retrofit的demo

----------

低仿Retrofit，但没那么多工厂模式，也不支持注解和RxJava，也没加入Retrofit的Adapter机制

另外，关于网络性能优化，参考：  
http://www.trinea.cn/android/android-http-api-compare/  
http://www.trinea.cn/android/mobile-performance-optimization/

有用的就3个模块：ayo-http, converter-fastjson, worker-okhttp

其他模块是retrofit，okhttp，okhttpUtils的demo测试

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
    * 仿照Retrofit和OkHttpUtils，超时设置，cookie，https等的配置项，都使用okhttp和volly的原生设置，而不纳入框架内管理

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


OKHttp支持的请求方式：以下代码是纯okhttp代码
```java
Request request = new Request.Builder()
        .url("https://api.github.com/markdown/raw")

        //all request methods
        .get()
        .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
        .post(RequestBody)
        .put(RequestBody)
        .delete()
        .delete(RequestBody)
        .head()
        .patch(RequestBody)
        .method(method, RequestBody)
        ///

        .addHeader(name, value)
        .cacheControl(CacheControl)
        .build();
```

RequestBody包括：
```
//post file：单文件上传，不以键值对的形式，应该也没法带其他post参数
public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
RequestBody.create(MEDIA_TYPE_MARKDOWN, file)

//post multipart：表单上传文件，可多文件上传，可附带post参数
RequestBody requestBody = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("title", "Square Logo")
        .addFormDataPart("image", "logo-square.png", RequestBody.create(MEDIA_TYPE_PNG, new File("website/static/logo-square.png")))
        .build();

//form表单：普通post
RequestBody formBody = new FormBody.Builder()
        .add("search", "Jurassic Park")
        .build();

//post string
String postBody = ""
        + "Releases\n"
        + "--------\n"
        + "\n"
        + " * _1.0_ May 6, 2013\n"
        + " * _1.1_ June 15, 2013\n"
        + " * _1.2_ August 11, 2013\n";
RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody)

RequestBody.create支持：byte[], ByteString，File，String


//post streaming：自己构建RequestBody
RequestBody requestBody = new RequestBody() {
      @Override public MediaType contentType() {
        return MEDIA_TYPE_MARKDOWN;
      }

      @Override public void writeTo(BufferedSink sink) throws IOException {
        sink.writeUtf8("Numbers\n");
        sink.writeUtf8("-------\n");
        for (int i = 2; i <= 997; i++) {
          sink.writeUtf8(String.format(" * %s = %s\n", i, factor(i)));
        }
      }

      private String factor(int n) {
        for (int i = 2; i < n; i++) {
          int x = n / i;
          if (x * i == n) return factor(x) + " × " + i;
        }
        return Integer.toString(n);
      }
    };

    Request request = new Request.Builder()
        .url("https://api.github.com/markdown/raw")
        .post(requestBody)
        .build();

//带上传进度：需要拦截器，拦截上面构建的RequestBody
Request request = new Request.Builder()
    .url("https://publicobject.com/helloworld.txt")
    .build();

final ProgressListener progressListener = new ProgressListener() {
  @Override public void update(long bytesRead, long contentLength, boolean done) {
    System.out.println(bytesRead);
    System.out.println(contentLength);
    System.out.println(done);
    System.out.format("%d%% done\n", (100 * bytesRead) / contentLength);
  }
};

OkHttpClient client = new OkHttpClient.Builder()
    .addNetworkInterceptor(new Interceptor() {
      @Override public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
            .body(new ProgressResponseBody(originalResponse.body(), progressListener))
            .build();
      }
    })
    .build();

Response response = client.newCall(request).execute();

```

关于MediaType：这个其实是http header里的contentType和charset俩东西
* 在RequestBody.create(MediaType, String或File）时，需要手动传入MediaType
* 在FormBody里，这个值是：application/x-www-form-urlencoded
* 在Multipart里，这个值是：multipart/mixed
* post  string时，值是类似：application/json; charset=utf-8
* 上传文件时（post file形式，非multipart形式）：
    * 文本文件markdown，值是：text/x-markdown; charset=utf-8
    * 文件是png
    
关于cancel：取消请求
* 首先得给RequestBody设置一个tag
* call.cancel(tag)
* okttpClient.dispatcher().cancelAll();
* 就看call.cancel(tag)
    * mOkHttpClient.newCall(req).enqueue或execute
    * newCall返回的是RealCall的对象
    * RealCall的cancel调用了HttpEngine的cancel
    * HttpEngine调用了StreamAllocation的cancel
    * HttpEngine里调用了这俩：
        * HttpStream streamToCancel的cancel，估计是读写的流
        * RealConnection connectionToCancel，管理底层socket连接
     * 所以okhttp的cancel是真正的cancel
* 再看volly的cancel：
    * mRequestQueue.cancelAll(tag)
    * 调用了Request.cancel()
    * 这里面就设置了isCanceled = true，估计不会去关闭底层IO流和socket，而只是切断了回调

Okhttp解析发起请求：
```
private final OkHttpClient client = new OkHttpClient();
同步：Response response = client.newCall(request).execute();
异步：enqueue和callback，注意进度的callback需要自己实现（上传进度和下载进度）
```

OKHttp解析Response：
```
解析code，判断是否成功：
response.code()
response.isSuccessful()---code是200就是true

解析Header
response.header("Server")
或者
Headers responseHeaders = response.headers();
for (int i = 0, size = responseHeaders.size(); i < size; i++) {
  System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
}

解析Body：ReponseBody
long contentLength = response.body().contentLenght()
MediaType mime = response.body().contentType()

String json = response.body().string()
byte[] bytes = response.body().bytes()
InputStream inputStream = response.body().byteStream()
Reader reader = response.body().charStream()
BufferedSource buffer = response.body().source();

```


* 其他问题：
    * okhttp的CacheController
    * okhttp的安全相关，Authenticate，Certificate，handshake，authenticator，CertificatePinner，trustManagerForCertificates
    * okhttp的拦截器怎么用
    * okhttp的cancel请求
    * okhttp的Callback
    * okhttp的execute和enqueue


## 2 使用

* 引库，需要3个库，核心库，converter库，worker库
    * 核心库提供了基本框架，但不具备实际功能
        * compile 'org.ayo:ayo-http:v1.0.0'
    * converter库提供了对业务字段的解析，现在只支持fastjson
        * compile 'org.ayo.http:converter-fastjson:v1.0.0'
    * worker库支持定制底层http实现，现在只支持okhttp3
        * compile 'org.ayo.http:worker-okhttp:v1.0.0'

* 项目代码
    * 你需要自己提供一个状态字段解析器，因为各个项目都不一样，一般形式是：{ code: 0, msg: "错误原因", result:[]或{} }
    * 日志相关，参考demo

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