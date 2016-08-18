package org.ayo.retrofit.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.ayo.http.retrofit.HttpResultSubscriber;
import org.ayo.http.retrofit.ServiceFactory;
import org.ayo.http.retrofit.TransformUtils;
import org.ayo.retrofit.sample.http.XXService;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tv = (TextView) findViewById(R.id.tv);

        tv.setText("开始请求...");
        XXService xxService = ServiceFactory.getInstance().createService(XXService.class);
        xxService.getAppList("android", 10)
                .compose(TransformUtils.<DaogouResponseWrapper<List<AppInfo>>>defaultSchedulers())
                .subscribe(new HttpResultSubscriber<List<AppInfo>, DaogouResponseWrapper<List<AppInfo>>>(){

                    @Override
                    public void onSuccess(List<AppInfo> appInfos) {
                        Log.i("http", "返回结果：" + appInfos.size());
                        tv.setText("返回结果：" + appInfos.size() + "条");
                    }

                    @Override
                    public void onFail(Throwable e) {
                        Log.e("http", e.getLocalizedMessage());
                        e.printStackTrace();

                        ///404, 500等错误
                        ///超时，没测
                        ///离线，没测
                        ///业务逻辑错误，code不为0的情况，也在这里


                    }
                });


//        xxService.getAppList("android")
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Func1<DaogouResponseWrapper<List<AppInfo>>, Observable<List<AppInfo>>>() {
//
//                    @Override
//                    public Observable<List<AppInfo>> call(DaogouResponseWrapper<List<AppInfo>> wrapper) {
//                        if(wrapper.isOk()){
//                            return Observable.just(wrapper.getResult());
//                        }else{
//                            ///这怎么处理？？业务逻辑错误的回调和映射没法处理
//                        }
//                    }
//                })
//                .subscribe(new Subscriber<List<AppInfo>>() {
//
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("http--2", e.getMessage());
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(List<AppInfo> appInfos) {
//                        Log.i("http--2", "返回结果：" + appInfos.size());
//                    }
//                });
    }
}
