package org.ayo.http.callback;

/**
 *
 * 三种错误
 *
 * 1 服务器错误， 300， 400， 500，授权错误--1001，SSL错误--1002
 * 2 业务逻辑错误：如post参数的状态不对，无法插入数据库---2001
 * 3 本地解析错误：此时返回了正确的数据，但本地进行解析时，出现异常---2002
 * 4 本地离线---0
 * 5 超时------1
 *
 */
public class FailInfo {

    public int errorCode;
    public String dataErrorCode;
    public String dataErrorReason;

    public FailInfo(int errorCode, String dataErrorCode, String dataErrorReason) {
        this.errorCode = errorCode;
        this.dataErrorCode = dataErrorCode;
        this.dataErrorReason = dataErrorReason;
    }
}
