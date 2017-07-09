package org.ayo.http.callback;

/**
 *
 * 1 server error， 300， 400， 500，auth--1001，SSL--1002
 * 2 logic error：post pameter，can't update db---2001
 * 3 local decode error---2002
 * 4 offline---0
 * 5 timeout------1
 *
 */
public class FailInfo {

    public int code;
//    public String dataErrorCode;
    public String reason;

    public FailInfo(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }
    //    public FailInfo(int errorCode, String dataErrorCode, String dataErrorReason) {
//        this.errorCode = errorCode;
//        this.dataErrorCode = dataErrorCode;
//        this.dataErrorReason = dataErrorReason;
//    }
}
