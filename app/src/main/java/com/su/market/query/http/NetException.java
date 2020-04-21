package com.su.market.query.http;

/**
 * Created by su on 18-3-21.
 */

public class NetException extends RuntimeException {

    public static final int NETWORK = 1;
    public static final int PARSER = 2;
    public static final int BUILD_REQUEST = 4;
    public static final int LOGOUT = 5;
    public static final int DOWNTIME = 6;

    private int code;
    private int errorType = -1;
    private String url;


    public NetException(int errorType, String url, String message, Throwable e) {
        super(message, e);
        this.errorType = errorType;
        this.url = url;
    }

    public NetException(int errorType, int code, String url, String message) {
        super(message);
        this.code = code;
        this.errorType = errorType;
        this.url = url;
    }

    public int getErrorCode() {
        return errorType;
    }

    @Override
    public String toString() {
        return "NetException{" +
                "code=" + code +
                ", errorType=" + errorType +
                ", url='" + url + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public int getCode() {
        return code;
    }

    public String getErrorHint() {
        String hint = "";
        switch (errorType) {
            case NETWORK:
                hint = "网络错误";
                break;
            case PARSER:
                hint = "数据解析错误";
                break;
            case BUILD_REQUEST:
                hint = "构建request错误";
                break;
            case LOGOUT:
                hint = "退出登录";
                break;
            case DOWNTIME:
                hint = "服务器维护";
                break;
            default:
                break;

        }
        return hint;
    }
}
