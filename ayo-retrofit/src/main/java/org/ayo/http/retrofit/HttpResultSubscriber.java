package org.ayo.http.retrofit;

import android.util.Log;

import org.ayo.http.retrofit.rxjava.HttpException;

import rx.Subscriber;

/**
 * Created by Administrator on 2016/8/18.
 */
public abstract class HttpResultSubscriber<T, R extends ResponseWrapper<T>> extends Subscriber<R> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Log.e("http", e.getMessage());
        e.printStackTrace();
        //在这里做全局的错误处理
        if (e instanceof HttpException) {
            // ToastUtils.getInstance().showToast(e.getMessage());
        }
        onFail(e);
    }

    @Override
    public void onNext(R t) {
        if (t.isOk())
            onSuccess(t.getResult());
        else
            onFail(new Throwable("error = " + t.getErrorMessage()));
    }

    public abstract void onSuccess(T t);

    public abstract void onFail(Throwable e);
}
