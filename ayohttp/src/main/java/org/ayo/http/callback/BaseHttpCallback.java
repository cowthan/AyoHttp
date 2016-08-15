package org.ayo.http.callback;

/**
 *
 * request.callback(
 * 		new GeniusHttpCallback<List<Article>>(Article.class, MyHttpResponse.class){
 * 			void onFinish(){}
 * 		}
 * 	).go();
 * 
 * @author cowthan
 *
 * @param <T>
 */
public abstract class BaseHttpCallback<T> {
	
	public BaseHttpCallback(){
	}
	
	public abstract void onFinish(boolean isSuccess, HttpProblem problem, FailInfo resp, T t);
	
	public void onLoading(long current, long total){
		
	}
	
	

}
