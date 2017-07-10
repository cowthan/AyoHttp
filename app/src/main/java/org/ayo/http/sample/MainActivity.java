package org.ayo.http.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.ayo.http.AyoRequest;
import org.ayo.http.callback.BaseHttpCallback;
import org.ayo.http.callback.FailInfo;
import org.ayo.http.callback.HttpProblem;
import org.ayo.http.converter.TypeToken;
import org.ayo.http.impl.FastJsonConverter;
import org.ayo.http.impl.OkhttpWorker;
import org.ayo.http.stream.StreamConverter;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_http);

        getRequest().tag("测试接口")
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
                //测正常http
                .callback(new BaseHttpCallback<String>() {
                    @Override
                    public void onFinish(boolean isSuccess, HttpProblem problem, FailInfo failInfo, String respRegist) {
                        if(isSuccess){
                            Toast.makeText(getApplicationContext(), "注册成功--" + respRegist, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "注册失败：" + failInfo.code + ", " + failInfo.reason, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoading(boolean isUpload, long current, long total) {
                        Log.e("http", isUpload + "， " + current + "/" + total);
                    }
                }, new TypeToken<String>(){}).fire();

                ///测RxJava
//                .start(new TypeToken<List<RespRegist>>(){})
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//
//                .subscribe(new Consumer<List<RespRegist>>() {
//                    @Override
//                    public void accept(List<RespRegist> respRegists) throws Exception {
//                        Toast.makeText(getApplicationContext(), "注册成功--" + respRegists.size(), Toast.LENGTH_SHORT).show();
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        AyoRequest.AyoHttpException e = (AyoRequest.AyoHttpException) throwable;
//                        Toast.makeText(getApplicationContext(), "注册失败：" + e.failInfo.reason, Toast.LENGTH_SHORT).show();
//                    }
//                });

    }


    public AyoRequest getRequest(){
        return AyoRequest.request()
                    .connectionTimeout(10000)
                    .writeTimeout(10000)
                    .readTimeout(10000)
                    .worker(new OkhttpWorker())
                    .streamConverter(new StreamConverter.StringConverter())   //ByteArrayConverter   FileConverter
                    .topLevelConverter(new TopLevelConverterTop())
                    .resonseConverter(new FastJsonConverter())
                    .intercept(new LogIntercepter());
    }
}
