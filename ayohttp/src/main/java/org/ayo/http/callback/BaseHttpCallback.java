package org.ayo.http.callback;

import org.ayo.http.converter.TypeToken;

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
		token = new TypeToken<T>(){};
	}
	
	public abstract void onFinish(boolean isSuccess, HttpProblem problem, FailInfo resp, T t);
	
	public void onLoading(long current, long total){
		
	}

	TypeToken<T> token;

	public TypeToken<T> getTypeToken(){
		return token;
	}


}
