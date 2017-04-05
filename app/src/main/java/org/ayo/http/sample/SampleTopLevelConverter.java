package org.ayo.http.sample;

import org.ayo.converter.fastjson.JsonUtils;
import org.ayo.http.TopLevelConverter;
import org.ayo.http.callback.BaseHttpCallback;
import org.ayo.http.callback.FailInfo;
import org.ayo.http.callback.HttpProblem;

/**
 * Created by Administrator on 2016/8/16.
 */
public class SampleTopLevelConverter implements TopLevelConverter {
    @Override
    public String convert(String s, BaseHttpCallback callback) {

        TopLevelBean bean = JsonUtils.getBean(s, TopLevelBean.class);
        if(bean.isOk()){
            return bean.result;
        }else{
            if(callback != null){
                callback.onFinish(false, HttpProblem.LOGIC_FAIL, new FailInfo(bean.code, "2001", bean.getErrorMsg()), null);
            }
        }

        return null;
    }

    public static class TopLevelBean{
        public int code;
        public String message;
        public String result;

        public boolean isOk(){
            return code == 0;
        }
        public String getErrorMsg(){
            return message;
        }
    }
}
