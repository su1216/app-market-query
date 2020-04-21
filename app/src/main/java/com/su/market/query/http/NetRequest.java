package com.su.market.query.http;

import com.alibaba.fastjson.TypeReference;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.Response;

/**
 * Created by su on 18-3-22.
 */

public class NetRequest<T> {
    private static final String TAG = NetRequest.class.getSimpleName();

    private String mUrl;
    private String mMethod = "POST";
    private String mMediaType;
    private boolean mProgressRequest;
    private boolean mProgressResponse;
    private String mDownloadFilepath;
    private TypeReference<T> mTypeReference;
    private Map<String, String> mFixedHeaderMap = new HashMap<>();
    private Map<String, String> mHeaderMap = new HashMap<>();
    private Map<String, Object> mFormBodyMap = new HashMap<>();
    private Map<String, MultipartFile> mMultipartMap = new HashMap<>();

    NetRequest(String url, TypeReference<T> typeReference) {
        mUrl = url;
        mTypeReference = typeReference;
        init();
    }

    public static <T> NetRequest<T> create(String url, TypeReference<T> typeReference) {
        return new NetRequest<>(url, typeReference);
    }

    public NetRequest<T> method(String method) {
        if (!"POST".equals(method) && !"GET".equals(method)) {
            throw new IllegalArgumentException("method is wrong: " + method);
        }
        mMethod = method;
        return this;
    }

    private void init() {
        mMediaType = "application/json";
    }

    public NetRequest<T> setMediaType(String type) {
        mMediaType = type;
        return this;
    }

    public String getMediaType() {
        return mMediaType;
    }

    public boolean isProgressRequest() {
        return mProgressRequest;
    }

    public NetRequest<T> progressRequest(boolean progressRequest) {
        this.mProgressRequest = progressRequest;
        return this;
    }

    public boolean isProgressResponse() {
        return mProgressResponse;
    }

    public NetRequest<T> progressResponse(boolean progressResponse) {
        this.mProgressResponse = progressResponse;
        return this;
    }

    public String getDownloadFilepath() {
        return mDownloadFilepath;
    }

    public NetRequest<T> downloadFilepath(String downloadFilepath) {
        this.mDownloadFilepath = downloadFilepath;
        return this;
    }

    public NetRequest<T> addHeader(String key, String value) {
        mHeaderMap.put(key, value);
        return this;
    }

    public TypeReference<T> getTypeReference() {
        return mTypeReference;
    }

    public Map<String, String> getHeaderMap() {
        return mHeaderMap;
    }

    public String getMethod() {
        return mMethod;
    }

    public Map<String, Object> getFormBodyMap() {
        return mFormBodyMap;
    }

    public NetRequest<T> addHeaders(Map<String, String> headers) {
        mHeaderMap.putAll(headers);
        return this;
    }

    public NetRequest<T> addParameter(String key, Object value) {
        mFormBodyMap.put(key, value);
        return this;
    }

    public NetRequest<T> addParameters(Map<String, Object> parameters) {
        mFormBodyMap.putAll(parameters);
        return this;
    }

    public NetRequest<T> addMultipart(MultipartFile multipartFile) {
        mMultipartMap.put(multipartFile.getName(), multipartFile);
        return this;
    }

    public NetRequest<T> addMultiparts(Map<String, MultipartFile> multipartMap) {
        mMultipartMap.putAll(multipartMap);
        return this;
    }

    public Map<String, MultipartFile> getMultipartMap() {
        return mMultipartMap;
    }

    public Observable<ProgressResult<T, Response>> build() {
        preBuild();
        return new OkHttpRequest<>(this).build();
    }

    public NetRequest<T> preBuild() {
        mHeaderMap.putAll(mFixedHeaderMap);
        return this;
    }

    public String getUrl() {
        return mUrl;
    }

    void checkParamsIsNull() {
        for (final Map.Entry<String, Object> entry : mFormBodyMap.entrySet()) {
            if (entry.getValue() == null) {
                throw new NetException(NetException.BUILD_REQUEST, 0, getUrl(), entry.getKey() + " is null");
            }
        }
    }
}

