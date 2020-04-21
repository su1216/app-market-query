package com.su.market.query.ui

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import com.su.market.query.R
import com.su.market.query.market.Market
import kotlinx.android.synthetic.main.activity_config.*

class ConfigActivity : CommonBaseActivity() {

    private var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        val fragment = ConfigFragment.newInstance(mAppWidgetId)
        done.setOnClickListener {
            if (fragment.mPackageName == null) {
                Toast.makeText(this, "请输入包名", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            fragment.save()
            finishThis()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment, "debug_list")
            .commit()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setTitle(R.string.app_name)
        mToolbar!!.navigationIcon = null
    }

    private fun finishThis() {
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}
