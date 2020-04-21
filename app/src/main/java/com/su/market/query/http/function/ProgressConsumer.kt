package com.su.market.query.http.function

import com.su.market.query.http.ProgressResult
import java.io.File

import io.reactivex.functions.Consumer
import okhttp3.Response

abstract class ProgressConsumer : Consumer<ProgressResult<File, Response>> {
    protected var start: Boolean = false
    protected var lastProgress: Float = 0.toFloat()
}
