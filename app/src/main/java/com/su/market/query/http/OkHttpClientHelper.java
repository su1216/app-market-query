package com.su.market.query.http;

import com.chuckerteam.chucker.api.ChuckerInterceptor;
import com.su.market.query.Base;
import com.su.market.query.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpMethod;

/**
 * Created by su on 18-3-21.
 */

class OkHttpClientHelper extends OkHttpClient {

    public static final String TAG = OkHttpClientHelper.class.getSimpleName();
    private static OkHttpClient sClient;
    private static OkHttpClient sDownloadClient;

    private OkHttpClientHelper() {}

    static {
        OkHttpClient client = new OkHttpClient();
        Builder builder = client.newBuilder()
                .protocols(Util.immutableListOf(Protocol.HTTP_1_1, Protocol.HTTP_2)) //just for http1.1
                .connectTimeout(30L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .writeTimeout(30L, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new ChuckerInterceptor(Base.context));
        }
        builder.addNetworkInterceptor(chain -> {
            Request request = chain.request();
            ProgressListenerConfig config = request.tag(ProgressListenerConfig.class);
            if (config.isProgressRequest() && HttpMethod.requiresRequestBody(request.method())) {
                // 重新构造request
                request = request
                        .newBuilder()
                        .method(request.method(), new ProgressRequestBody(request.body(), config.getListener()))
                        .build();
            }
            return chain.proceed(request);
        });
        sClient = builder.build();

        OkHttpClient downloadClient = new OkHttpClient();
        Builder downloadBuilder = downloadClient.newBuilder()
                .protocols(Util.immutableListOf(Protocol.HTTP_1_1, Protocol.HTTP_2)) //just for http1.1
                .connectTimeout(30L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .writeTimeout(30L, TimeUnit.SECONDS);
        downloadBuilder.addNetworkInterceptor(chain -> {
            Request request = chain.request();
            ProgressListenerConfig config = request.tag(ProgressListenerConfig.class);
            if (config.isProgressRequest() && HttpMethod.requiresRequestBody(request.method())) {
                // 重新构造request
                request = request
                        .newBuilder()
                        .method(request.method(), new ProgressRequestBody(request.body(), config.getListener()))
                        .build();
            }
            return chain.proceed(request);
        });
        sDownloadClient = downloadBuilder.build();
    }

    public static OkHttpClient getOkHttpInstance() {
        return sClient;
    }

    public static OkHttpClient getOkHttpDownloadInstance() {
        return sDownloadClient;
    }
}
