package com.su.market.query.http.function;

import android.view.Gravity;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.su.market.query.Base;
import com.su.market.query.http.HttpResult;
import com.su.market.query.http.NetException;
import com.su.market.query.http.NetResponse;

import io.reactivex.functions.Function;

/**
 * Created by su on 18-3-21.
 */

public abstract class BusinessFunction<T, E> implements Function<NetResponse<T>, E> {
    private boolean mDowntime;
    private int mErrorCode;
    private String mErrorMessage = "";
    private Toast mErrorMessageToast = Toast.makeText(Base.context, "", Toast.LENGTH_LONG);

    private NetResponse<T> mResponse;

    @Override
    public E apply(NetResponse<T> response) throws Exception {
        mResponse = response;
        getErrorCode(response.getResult());
        mDowntime = mErrorCode == 699;
        getErrorMessage(response.getResult());
        processErrorCode(mResponse);
        return onSuccess(response.getResult());
    }

    public abstract E onSuccess(T httpResult) throws Exception;

    private void getErrorCode(T response) {
        if (response instanceof HttpResult) {
            HttpResult httpResult = (HttpResult) response;
            mErrorCode = httpResult.getResCode();
        } else if (response instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) response;
            mErrorCode = jsonObject.getIntValue("code");
        }
    }

    protected int getErrorCode() {
        return mErrorCode;
    }

    private void getErrorMessage(T response) {
        if (response instanceof HttpResult) {
            HttpResult httpResult = (HttpResult) response;
            mErrorMessage = httpResult.getError();
        } else if (response instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) response;
            mErrorMessage = jsonObject.getString("msg");
        }
    }

    protected String getErrorMessage() {
        return mErrorMessage;
    }

    protected void processErrorCode(NetResponse<T> response) {
        if (isDowntime()) {
            throw new NetException(NetException.DOWNTIME, 0, response.getRequest().getUrl(), "");
        }
    }

    protected void toastErrorMessage(int gravity) {
        if (!isDowntime() && mErrorCode != 302) {
            mErrorMessageToast.setGravity(gravity, 0, 0);
            mErrorMessageToast.setText(mErrorMessage);
            mErrorMessageToast.show();
        }
    }

    protected NetResponse<T> getResponse() {
        return mResponse;
    }

    protected void toastErrorMessage() {
        toastErrorMessage(Gravity.CENTER);
    }

    protected boolean isDowntime() {
        return mDowntime;
    }
}
