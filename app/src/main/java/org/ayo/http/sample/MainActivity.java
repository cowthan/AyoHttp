package org.ayo.http.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.ayo.converter.fastjson.FastJsonConverter;
import org.ayo.http.AyoHttp;
import org.ayo.http.converter.TypeToken;
import org.ayo.http.okhttp3.OkhttpWorker;
import org.ayo.http.sample.model.RespRegist;
import org.ayo.http.stream.StreamConverter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

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



                ///测正常http
//                .callback(new BaseHttpCallback<List<RespRegist>>() {
//                    @Override
//                    public void onFinish(boolean isSuccess, HttpProblem problem, FailInfo resp, List<RespRegist> respRegist) {
//                        if(isSuccess){
//                            Toast.makeText(getApplicationContext(), "注册成功--" + respRegist.size(), Toast.LENGTH_SHORT).show();
//                        }else{
//                            Toast.makeText(getApplicationContext(), "注册失败：" + resp.dataErrorReason, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onLoading(long current, long total) {
//                        super.onLoading(current, total);
//                    }
//                }, new TypeToken<List<RespRegist>>(){}).fire();

                ///测RxJava
                .start(new TypeToken<List<RespRegist>>(){})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Consumer<List<RespRegist>>() {
                    @Override
                    public void accept(List<RespRegist> respRegists) throws Exception {
                        Toast.makeText(getApplicationContext(), "注册成功--" + respRegists.size(), Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        AyoHttp.AyoHttpException e = (AyoHttp.AyoHttpException) throwable;
                        Toast.makeText(getApplicationContext(), "注册失败：" + e.failInfo.dataErrorReason, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public AyoHttp getRequest(){
        return AyoHttp.request()
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
