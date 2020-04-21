package com.su.market.query.http;

/**
 * Http响应基类
 *
 * @param <T>
 */
public class HttpResult<T> {
    public static final int CODE_SUCCESS = 0;
    private int resCode;
    private String error;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "data=" + data +
                ", resCode=" + resCode +
                ", error='" + error + '\'' +
                '}';
    }
}
