package com.su.market.query

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

@SuppressLint("StaticFieldLeak")
object Base {
    lateinit var context: Context
    var versionCode: Long = 0
    lateinit var versionName: String

    fun init(context: Context) {
        this.context = context

        val pm: PackageManager = context.packageManager
        val pi: PackageInfo = pm.getPackageInfo(context.packageName, 0)
        versionName = pi.versionName
        versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pi.longVersionCode
        } else {
            pi.versionCode.toLong()
        }
    }
}
