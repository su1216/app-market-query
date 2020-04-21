package com.su.market.query.http.observer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.su.market.query.Base;
import com.su.market.query.BuildConfig;
import com.su.market.query.http.NetException;
import com.su.market.query.http.ProgressResult;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.util.EndConsumerHelper;

/**
 * Created by su on 18-3-21.
 */

@SuppressLint("StaticFieldLeak")
public class BaseObserver<T> implements Observer<T>, Disposable {
    private static final String TAG = BaseObserver.class.getSimpleName();
    private static Toast sFailureToast = Toast.makeText(Base.context, "请求失败,请检查网络设置", Toast.LENGTH_LONG);
    private static Toast sErrorToast = Toast.makeText(Base.context, "请求失败,请重试", Toast.LENGTH_LONG);
    private static Toast sParseErrorToast = Toast.makeText(Base.context, "", Toast.LENGTH_LONG);
    private static Toast sRequestErrorToast = Toast.makeText(Base.context, "请求参数不合法", Toast.LENGTH_LONG);

    private List<BaseObserver<?>> mObservers;

    /**
     * copy from {@link io.reactivex.observers.DisposableObserver}
     **/
    final AtomicReference<Disposable> upstream = new AtomicReference<>();

    public BaseObserver(List<BaseObserver<?>> observers) {
        this.mObservers = observers;
    }

    @Override
    public void onSubscribe(Disposable s) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onSubscribe: " + s);
        }
        EndConsumerHelper.setOnce(this.upstream, s, getClass());
        if (mObservers != null) {
            mObservers.add(this);
        }
    }

    @Override
    public void onNext(T t) {
        if (BuildConfig.DEBUG) {
            if (t instanceof ProgressResult) {
                ProgressResult result = (ProgressResult) t;
                if (result.getType() == ProgressResult.NetStatus.OVER) {
                    Log.d(TAG, "onNext: " + t);
                }
            } else {
                Log.d(TAG, "onNext: " + t);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof NetException) {
            processException((NetException) e);
        } else {
            sErrorToast.show();
        }

        if (BuildConfig.DEBUG) {
            Toast.makeText(Base.context, e.toString(), Toast.LENGTH_LONG).show();
            Log.w(TAG, "onError", e);
        }

        if (mObservers != null) {
            mObservers.remove(this);
        }
    }

    @Override
    public void onComplete() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onComplete");
        }
        if (mObservers != null) {
            mObservers.remove(this);
        }
    }

    private static void processException(NetException e) {
        switch (e.getErrorCode()) {
            case NetException.NETWORK:
                if (isNetworkAvailable()) {
                    sErrorToast.show();
                } else {
                    sFailureToast.show();
                }
                break;
            case NetException.PARSER:
                sParseErrorToast.setText("数据解析失败\n" + "url: " + e.getUrl());
                sParseErrorToast.show();
                break;
            case NetException.BUILD_REQUEST:
                sRequestErrorToast.show();
                break;
            default:
                break;
        }
    }

    /**
     * 判断当前网络是否可用
     */
    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Base.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    @Override
    public void dispose() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "dispose");
        }
        DisposableHelper.dispose(upstream);
    }

    @Override
    public boolean isDisposed() {
        return upstream.get() == DisposableHelper.DISPOSED;
    }
}
