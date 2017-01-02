package org.ayo.retrofit.sample.http;

import org.ayo.retrofit.sample.AppInfo;
import org.ayo.retrofit.sample.DaogouResponseWrapper;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/8/18.
 */
public interface XXService {

    String BASE_URL = "http://chuanyue.iwomedia.cn/";  //会被ServiceFactory反射到

    @GET("daogou/app/app1")
    Observable<DaogouResponseWrapper<List<AppInfo>>> getAppList(@Query("os") String os, @Query("page") int page);

}
