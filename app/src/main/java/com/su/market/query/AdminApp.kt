package com.su.market.query

import android.app.Application
import com.su.market.query.util.SpHelper
import com.tencent.bugly.crashreport.CrashReport

class AdminApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Base.init(this)
        SpHelper.initSharedPreferences(this)
        if (!BuildConfig.DEBUG) {
            CrashReport.enableBugly(true)
            CrashReport.initCrashReport(this)
        }
    }
}
