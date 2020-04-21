package com.su.market.query.market

import android.text.TextUtils
import android.util.Log
import java.text.DecimalFormat
import java.util.*

object UnitUtil {
    private val TAG = UnitUtil::class.java.simpleName
    private const val THOUSAND = 1000
    private const val WAN = THOUSAND * 10
    private const val MILLION = THOUSAND * THOUSAND
    private const val YI = WAN * WAN
    private const val BILLION = MILLION * THOUSAND
    private const val UNIT_GE = "个"
    private const val UNIT_SHI = "十"
    private const val UNIT_BAI = "百"
    private const val UNIT_QIAN = "千"
    private const val UNIT_K = "K"
    private const val UNIT_WAN = "万"
    private const val UNIT_M = "M"
    private const val UNIT_YI = "亿"
    private val DECIMAL_FORMAT_MAP: MutableMap<String, ThreadLocal<DecimalFormat>> = HashMap()

    private fun getDecimalFormat(pattern: String): DecimalFormat {
        var tl: ThreadLocal<DecimalFormat>? = DECIMAL_FORMAT_MAP.get(pattern)
        if (tl == null) {
            tl = object : ThreadLocal<DecimalFormat>() {
                override fun initialValue(): DecimalFormat {
                    return DecimalFormat(pattern)
                }
            }
            DECIMAL_FORMAT_MAP[pattern] = tl
        }
        return tl.get()!!
    }

    private fun getFormatPattern(scale: Int = 1) = "#.${"#".repeat(scale)}"

    fun appendUnit(number: Float, originUnit: String?): String {
        val realNumber = toNumber(number, originUnit)
        return emitNumber(realNumber, 1)
    }

    fun emitNumber(number: Float, scale: Int = 1): String {
        if (number == 0.0f) {
            return "0"
        }
        val decimalFormat = getDecimalFormat(getFormatPattern(scale))
        return when {
            number >= BILLION -> "${decimalFormat.format(number / BILLION)}B"
            number >= MILLION -> "${decimalFormat.format(number / MILLION)}M"
            number >= THOUSAND -> "${decimalFormat.format(number / THOUSAND)}K"
            else -> decimalFormat.format(number)
        }
    }

    fun toNumber(number: Float, unit: String?): Float {
        if (TextUtils.isEmpty(unit)) {
            return number
        }
        return if (TextUtils.equals(unit, UNIT_GE)) {
            number
        } else if (TextUtils.equals(unit, UNIT_SHI)) {
            number * 10
        } else if (TextUtils.equals(unit, UNIT_BAI)) {
            number * 100
        } else if (TextUtils.equals(unit, UNIT_QIAN) || TextUtils.equals(unit, UNIT_K)) {
            number * THOUSAND
        } else if (TextUtils.equals(unit, UNIT_WAN)) {
            number * THOUSAND * 10
        } else if (TextUtils.equals(unit, UNIT_M)) {
            number * MILLION
        } else if (TextUtils.equals(unit, UNIT_YI)) {
            number * MILLION * MILLION * 100
        } else {
            Log.e(TAG, "toNumber error. unit: $unit")
            number
        }
    }
}
