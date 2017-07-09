package org.ayo.http.callback;

import org.ayo.http.converter.TypeToken;

public abstract class BaseHttpCallback<T> {

	public BaseHttpCallback(){
		token = new TypeToken<T>(){};
	}
	
	public abstract void onFinish(boolean isSuccess, HttpProblem problem, FailInfo resp, T t);
	
	public void onLoading(boolean isUpload, long current, long total){
		
	}

	TypeToken<T> token;

	public TypeToken<T> getTypeToken(){
		return token;
	}


}
