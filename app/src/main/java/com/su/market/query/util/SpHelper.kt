package com.su.market.query.util

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import com.su.market.query.R
import com.su.market.query.market.ApkData
import org.json.JSONObject

class SpHelper(private val widgetId: Int) {

    fun put(key: String, value: Any?): SpHelper {
        getJson(widgetId)
            .put(key, value)
        return this
    }

    fun getPackageName(): String = getJson(
        widgetId
    ).optString(KEY_PACKAGE_NAME)

    fun getString(key: String): String {
        val value = getJson(widgetId)
            .optString(key)
        if (TextUtils.isEmpty(value)) {
            return "0"
        }
        return value
    }

    fun getLong(key: String): Long = getJson(
        widgetId
    ).optLong(key, 0)

    fun getFloat(key: String): Float = getJson(
        widgetId
    ).optDouble(key, 0.0).toFloat()

    fun getSwitch(key: String): Boolean {
        val json = getJson(widgetId)
        if (json.has(key)) {
            return json.optBoolean(key)
        }
        return true
    }

    fun getLastApkData(): ApkData {
        val lastDownload = getFloat(KEY_DOWNLOAD)
        val downloadUnit = getString(KEY_DOWNLOAD_UNIT)
        val lastWatch = getFloat(KEY_WATCH)
        val watchUnit = getString(KEY_WATCH_UNIT)
        val lastComment = getFloat(KEY_COMMENT)
        val commentUnit = getString(KEY_COMMENT_UNIT)
        val lastRank = getFloat(KEY_SCORE)
        val lastRankCount = getFloat(KEY_SCORE_COUNT)
        val rankCountUnit = getString(KEY_SCORE_COUNT_UNIT)
        val lastTime: Long = getLong(KEY_TIME)
        return ApkData(
            lastDownload,
            downloadUnit,
            lastWatch,
            watchUnit,
            lastComment,
            commentUnit,
            lastRank,
            lastRankCount,
            rankCountUnit,
            lastTime
        )
    }

    fun remove(key: String): SpHelper {
        getJson(widgetId).remove(key)
        return this
    }

    fun delete() {
        widgetMap.remove("widget$widgetId")
        sDefaultSharedPreferences!!.edit().remove("widget$widgetId").apply()
    }

    fun apply() {
        val jsonString = getJson(
            widgetId
        ).toString()
        Log.i(TAG, "jsonString: $jsonString")
        sDefaultSharedPreferences!!.edit().putString("widget$widgetId", jsonString).apply()
    }

    companion object {
        val TAG = SpHelper::class.java.simpleName
        const val NAME = "market"
        private var sDefaultSharedPreferences: SharedPreferences? = null
        lateinit var KEY_MARKET: String
        lateinit var KEY_PACKAGE_NAME: String
        lateinit var KEY_CHOOSE_APP: String
        lateinit var KEY_SCORE_SWITCH: String
        lateinit var KEY_SCORE: String
        lateinit var KEY_SCORE_COUNT_SWITCH: String
        lateinit var KEY_SCORE_COUNT: String
        lateinit var KEY_SCORE_COUNT_UNIT: String
        lateinit var KEY_WATCH_SWITCH: String
        lateinit var KEY_WATCH: String
        lateinit var KEY_WATCH_UNIT: String
        lateinit var KEY_COMMENT_SWITCH: String
        lateinit var KEY_COMMENT: String
        lateinit var KEY_COMMENT_UNIT: String
        lateinit var KEY_DOWNLOAD_SWITCH: String
        lateinit var KEY_DOWNLOAD: String
        lateinit var KEY_DOWNLOAD_UNIT: String
        lateinit var KEY_TIME_SWITCH: String
        lateinit var KEY_TIME: String
        var widgetSet: MutableSet<Int> = HashSet()
        var widgetMap: MutableMap<String, JSONObject> = HashMap()

        fun applyWidgetIds() {
            val set = HashSet<String>()
            for (id in widgetSet) {
                set.add(id.toString())
            }
            sDefaultSharedPreferences!!.edit().putStringSet("ids", set).apply()
        }

        fun initSharedPreferences(context: Context) {
            sDefaultSharedPreferences = context.getSharedPreferences(
                NAME, Context.MODE_PRIVATE)
            val resources = context.resources
            KEY_MARKET = resources.getString(
                R.string.key_choose_market
            )
            KEY_PACKAGE_NAME = resources.getString(
                R.string.key_package_name
            )
            KEY_CHOOSE_APP = resources.getString(
                R.string.key_choose_app
            )
            KEY_SCORE_SWITCH = resources.getString(
                R.string.key_score_switch
            )
            KEY_SCORE = resources.getString(
                R.string.key_score
            )
            KEY_SCORE_COUNT_SWITCH = resources.getString(
                R.string.key_score_count_switch
            )
            KEY_SCORE_COUNT = resources.getString(
                R.string.key_score_count
            )
            KEY_SCORE_COUNT_UNIT = resources.getString(
                R.string.key_score_count_unit
            )
            KEY_WATCH_SWITCH = resources.getString(
                R.string.key_watch_switch
            )
            KEY_WATCH = resources.getString(
                R.string.key_watch
            )
            KEY_WATCH_UNIT = resources.getString(
                R.string.key_watch_unit
            )
            KEY_COMMENT_SWITCH = resources.getString(
                R.string.key_comment_switch
            )
            KEY_COMMENT = resources.getString(
                R.string.key_comment
            )
            KEY_COMMENT_UNIT = resources.getString(
                R.string.key_comment_unit
            )
            KEY_DOWNLOAD_SWITCH = resources.getString(
                R.string.key_download_switch
            )
            KEY_DOWNLOAD = resources.getString(
                R.string.key_download
            )
            KEY_DOWNLOAD_UNIT = resources.getString(
                R.string.key_download_unit
            )
            KEY_TIME_SWITCH = resources.getString(
                R.string.key_time_switch
            )
            KEY_TIME = resources.getString(
                R.string.key_time
            )

            val set = sDefaultSharedPreferences!!.getStringSet("ids", HashSet())!!
            for (id in set) {
                widgetSet.add(id.toInt())
            }
            for (entry in sDefaultSharedPreferences!!.all) {
                if (!entry.key.startsWith("widget")) {
                    continue
                }
                val jsonString = sDefaultSharedPreferences!!.getString(entry.key, "")!!
                val json = if (TextUtils.isEmpty(jsonString)) {
                    JSONObject()
                } else {
                    JSONObject(jsonString)
                }
                widgetMap[entry.key] = json
            }
        }

        private fun getJson(id: Int): JSONObject {
            var saved = widgetMap["widget$id"]
            if (saved == null) {
                saved = JSONObject()
                widgetMap["widget$id"] = saved
            }
            return saved
        }

        fun saveApkData(appWidgetId: Int, apkData: ApkData) {
            val spHelper = SpHelper(appWidgetId)
            spHelper.put(KEY_DOWNLOAD, apkData.download)
            spHelper.put(KEY_DOWNLOAD_UNIT, apkData.downloadUnit)
            spHelper.put(KEY_WATCH, apkData.watch)
            spHelper.put(KEY_WATCH_UNIT, apkData.watchUnit)
            spHelper.put(KEY_COMMENT, apkData.comment)
            spHelper.put(KEY_COMMENT_UNIT, apkData.commentUnit)
            spHelper.put(KEY_SCORE, apkData.rank)
            spHelper.put(KEY_SCORE_COUNT, apkData.rankCount)
            spHelper.put(KEY_SCORE_COUNT_UNIT, apkData.rankCountUnit)
            spHelper.put(KEY_TIME, apkData.time)
            spHelper.apply()
        }
    }
}
