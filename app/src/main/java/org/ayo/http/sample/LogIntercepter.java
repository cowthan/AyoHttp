package org.ayo.http.sample;

import org.ayo.http.AyoRequest;
import org.ayo.http.HttpIntercepter;
import org.ayo.http.utils.HttpHelper;
import org.ayo.http.utils.HttpPrinter;

import java.util.Map;

/**
 */
public class LogIntercepter extends HttpIntercepter {

    private Object flag = "";

    @Override
    public void beforeRequest(AyoRequest req) {
        //打印请求信息
        flag = req.tag;
        HttpPrinter.printRequest(req.tag + "  ", req);
    }

    @Override
    public void responseHeader(Map<String, String> header) {
        HttpHelper.printMap(flag + "  Response Header：", header);
    }

    @Override
    public void beforeTopLevelConvert(String s) {
        HttpPrinter.print(flag + " Response--: ", s);
    }
}
