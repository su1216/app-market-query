package com.su.market.query.http.transformer

import com.alibaba.fastjson.JSON
import com.su.market.query.http.NetException
import com.su.market.query.http.NetRequest
import com.su.market.query.http.NetResponse
import com.su.market.query.http.ProgressResult
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.Response

/**
 * Created by su on 18-2-5.
 */

class ParserTransformer<T> : ObservableTransformer<ProgressResult<T, Response>, NetResponse<T>> {
    override fun apply(upstream: Observable<ProgressResult<T, Response>>): ObservableSource<NetResponse<T>> {
        return upstream
                .filter { progressResult -> progressResult.type == ProgressResult.NetStatus.OVER }
                .map { progressResult -> NetResponse(progressResult.request, parseNetworkResponse(progressResult.request, progressResult.response.body!!.string())) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseNetworkResponse(request: NetRequest<T>, json: String): T? {
        try {
            val type = request.typeReference.type
            return if (type == String::class.java) {
                json as T
            } else {
                JSON.parseObject<T>(json, type)
            }
        } catch (e: RuntimeException) {
            throw NetException(NetException.PARSER, request.url, e.message, e)
        }

    }
}
