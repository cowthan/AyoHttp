package org.ayo.http.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import org.ayo.http.converter.TypeToken;

import java.util.ArrayList;
import java.util.List;

public final class JsonUtils {

	private JsonUtils() {
	}

	public static <T> T getBean(String jsonString, TypeToken<T> type) {
		T t = null;
		t = JSON.parseObject(jsonString, new TypeReference<T>(){});
		return t;
	}

	public static String toJson(Object bean){

		if(bean == null){
			return "{}";
		}
		return JSON.toJSONString(bean);
	}

	public static <T> List<T> getBeanList(String jsonArrayString, Class<T> cls) {

		List<T> beanList = new ArrayList<T>();
		beanList = JSON.parseArray(jsonArrayString, cls);
		return beanList;
	}

	public static <T> T getBean(String jsonString, Class<T> cls) {
		T t = null;
		t = JSON.parseObject(jsonString, cls);
		return t;
	}

}
