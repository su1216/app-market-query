package com.su.market.query.market

import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.TypeReference
import com.su.market.query.Url
import com.su.market.query.http.NetRequest
import com.su.market.query.http.NetResponse
import com.su.market.query.http.transformer.ParserTransformer
import io.reactivex.Observable
import java.util.regex.Pattern

class Coolapk : Market {

    override fun parse(html: String): ApkData {
        if (TextUtils.isEmpty(html)) {
            return ApkData(0.0f, "", 0.0f, "", 0.0f, "", 0.0f, 0.0f, "", 0)
        }
        var download = 0.0f
        var downloadUnit: String? = ""
        var watch = 0.0f
        var watchUnit: String? = ""
        var comment = 0.0f
        var commentUnit: String? = ""
        var rank = 0.0f
        var rankCount = 0.0f
        var rankCountUnit: String? = ""
        val time = System.currentTimeMillis()
        try {
            val regexMatcher = PATTERN_COOLAPK_INFO.matcher(html)
            while (regexMatcher.find()) {
                download = Market.toFloat(regexMatcher.group(1))
                downloadUnit = regexMatcher.group(2)
                watch = Market.toFloat(regexMatcher.group(3))
                watchUnit = regexMatcher.group(4)
                comment = Market.toFloat(regexMatcher.group(5))
                commentUnit = regexMatcher.group(6)
            }
        } catch (e: Exception) {
        }
        val rankMatcher = PATTERN_COOLAPK_RANK.matcher(html)
        while (rankMatcher.find()) {
            rank = Market.toFloat(rankMatcher.group(1))
        }
        val countMatcher = PATTERN_COOLAPK_COUNT.matcher(html)
        while (countMatcher.find()) {
            rankCount = Market.toFloat(countMatcher.group(1))
            rankCountUnit = countMatcher.group(2)
        }

        val apkData = ApkData(
            download,
            downloadUnit,
            watch,
            watchUnit,
            comment,
            commentUnit,
            rank,
            rankCount,
            rankCountUnit,
            time
        )
        Log.d(TAG, "ApkData: $apkData")
        return apkData
    }

    override fun getMarketInfo(): MarketInfo = Market.makeMarketInfo(Market.INDEX_COOLAPK)

    override fun getNetRequest(query: String): Observable<NetResponse<String>> {
        return NetRequest.create<String>(
            Url.COOLAPK + query,
            object : TypeReference<String?>() {})
            .addHeader("Connection", "close")
            .addHeader("Accept-Encoding", "identity")
            .method("GET")
            .build()
            .compose(ParserTransformer())
    }

    companion object {
        private val TAG = Coolapk::class.java.simpleName
        private const val UNIT_GROUP_REGEX = "([万亿KkMmBb])?"

        private val PATTERN_COOLAPK_INFO = Pattern.compile(
            "<p class=\"apk_topba_message\">.*?(?:\\d+(?:\\.\\d+)?\\s*(?:MB|M|GB|G|KB|K))\\s*/\\s*(\\d+(?:\\.\\d+)?)${UNIT_GROUP_REGEX}(?:下载)\\s*/\\s*(\\d+(?:\\.\\d+)?)${UNIT_GROUP_REGEX}(?:人关注)\\s*/\\s*(\\d+(?:\\.\\d+)?)${UNIT_GROUP_REGEX}(?:个评论).*?</p>",
            Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE or Pattern.DOTALL or Pattern.MULTILINE
        )
        private val PATTERN_COOLAPK_RANK = Pattern.compile(
            "<p class=\"rank_num\">(.*?)</p>",
            Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE or Pattern.DOTALL or Pattern.MULTILINE
        )
        private val PATTERN_COOLAPK_COUNT = Pattern.compile(
            "<p class=\"apk_rank_p1\">共(\\d+(?:\\.\\d+)?)${UNIT_GROUP_REGEX}个评分</p>",
            Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE or Pattern.DOTALL or Pattern.MULTILINE
        )
    }
}
