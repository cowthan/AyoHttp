package org.ayo.retrofit.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.ayo.http.retrofit.ServiceFactory;
import org.ayo.retrofit.sample.http.XXService;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tv = (TextView) findViewById(R.id.tv);

        tv.setText("开始请求...");
        final XXService xxService = ServiceFactory.getInstance().createService(XXService.class);
//        xxService.getAppList("android", 10)
//                .compose(TransformUtils.<DaogouResponseWrapper<List<AppInfo>>>defaultSchedulers())
//                .subscribe(new HttpResultSubscriber<List<AppInfo>, DaogouResponseWrapper<List<AppInfo>>>(){
//
//                    @Override
//                    public void onSuccess(List<AppInfo> appInfos) {
//                        Log.i("http", "返回结果：" + appInfos.size());
//                        tv.setText("返回结果：" + appInfos.size() + "条");
//                    }
//
//                    @Override
//                    public void onFail(Throwable e) {
//                        Log.e("http", e.getLocalizedMessage());
//                        e.printStackTrace();
//
//                        ///404, 500等错误
//                        ///超时，没测
//                        ///离线，没测
//                        ///业务逻辑错误，code不为0的情况，也在这里
//
//
//                    }
//                });


        xxService.getAppList("android", 10)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<DaogouResponseWrapper<List<AppInfo>>, Observable<DaogouResponseWrapper<List<AppInfo>>>>() {

                    @Override
                    public Observable<DaogouResponseWrapper<List<AppInfo>>> call(DaogouResponseWrapper<List<AppInfo>> wrapper) {
                        if(wrapper.isOk()){
                            return xxService.getAppList("ios", 10);
                        }else{
                            ///这怎么处理？？业务逻辑错误的回调和映射没法处理
                            return null;
                        }
                    }
                })
                .subscribe(new Subscriber<DaogouResponseWrapper<List<AppInfo>>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("http--2", e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DaogouResponseWrapper<List<AppInfo>> appInfos) {
                        Log.i("http--2", "返回结果：" + appInfos.isOk());
                    }
                });
    }
}
