package com.su.market.query.market

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import com.su.market.query.Base
import com.su.market.query.R
import com.su.market.query.http.NetResponse
import io.reactivex.Observable

/*
* 使用包名： 小米、酷安
* 使用应用名：360
* 使用路径：华为（C10233295）
* */
interface Market {
    fun parse(html: String): ApkData

    fun getNetRequest(query: String): Observable<NetResponse<String>>

    fun getMarketInfo(): MarketInfo

    companion object {
        const val INDEX_COOLAPK = 0
        const val INDEX_360 = 1
        fun toFloat(input: String?): Float {
            return if (TextUtils.isEmpty(input)) {
                0.0f
            } else try {
                input!!.toFloat()
            } catch (e: NumberFormatException) {
                0.0f
            }
        }

        fun queryWithPackageName(marketId: String): Boolean = when (marketId) {
            "coolapk" -> true
            else -> false
        }

        fun getLaunchIntent(context: Context, info: MarketInfo): Intent? = context.packageManager.getLaunchIntentForPackage(info.packageName)

        fun makeMarketInfo(index: Int): MarketInfo {
            val resources = Base.context.resources
            val markets = resources.getStringArray(R.array.markets)
            val ids = resources.getStringArray(R.array.market_ids)
            val packages = resources.getStringArray(R.array.market_package_names)
            return MarketInfo(index, markets[index], ids[index], packages[index])
        }
    }
}
