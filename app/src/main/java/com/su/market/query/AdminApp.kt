package com.su.market.query

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
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

    companion object {
        fun coolapkIntent(packageName: String): Intent {
            val intent = Intent()
            val componentName = ComponentName("com.coolapk.market", "com.coolapk.market.view.AppLinkActivity")
            intent.component = componentName
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse("https://coolapk.com/apk/$packageName")
            return intent
        }
    }
}
