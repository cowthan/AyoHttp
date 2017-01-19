package org.ayo.http.sample;

import org.ayo.converter.fastjson.JsonUtils;
import org.ayo.http.StringTopLevelModel;
import org.ayo.http.TopLevelConverter;

/**
 * Created by Administrator on 2016/8/16.
 */
public class TopLevelConverterTop implements TopLevelConverter<TopLevelConverterTop.TopLevelBean> {
    @Override
    public TopLevelConverterTop.TopLevelBean convert(String s) {

        TopLevelBean bean = JsonUtils.getBean(s, TopLevelBean.class);
        return bean;
    }

    public static class TopLevelBean extends StringTopLevelModel {
        public int code;
        public String message;
        public String result;

        public boolean isOk(){
            return code == 0;
        }

        @Override
        public String getResult() {
            return result;
        }

        @Override
        public String getErrorCode() {
            return code + "";
        }

        public String getErrorMsg(){
            return message;
        }
    }
}
