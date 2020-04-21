package com.su.market.query.ui

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.preference.*
import com.su.market.query.R
import com.su.market.query.util.SpHelper

class ConfigFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
    Preference.OnPreferenceClickListener {

    private var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var mSpHelper: SpHelper
    var mMarket: String? = null
    var mPackageName: String? = null
    var mAppName: String? = null
    private lateinit var mMarketPreference: ListPreference
    private lateinit var mPackageNamePreference: Preference
    private lateinit var mChoosePreference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        mAppWidgetId = arguments!!.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)
        mSpHelper = SpHelper(mAppWidgetId)
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = SpHelper.NAME
        addPreferencesFromResource(R.xml.preference_main)
        mMarketPreference = findPreference(SpHelper.KEY_MARKET)!!
        mMarketPreference.onPreferenceChangeListener = this
        mPackageNamePreference = findPreference(SpHelper.KEY_PACKAGE_NAME)!!
        mPackageNamePreference.onPreferenceChangeListener = this
        mChoosePreference = findPreference(SpHelper.KEY_CHOOSE_APP)!!
        mChoosePreference.onPreferenceClickListener = this
        mChoosePreference.onPreferenceChangeListener = this
        val scoreSwitchPreference = findPreference<Preference>(SpHelper.KEY_SCORE_SWITCH)!! as SwitchPreference
        scoreSwitchPreference.onPreferenceChangeListener = this
        scoreSwitchPreference.isChecked = true
        val scoreCountSwitchPreference = findPreference<Preference>(SpHelper.KEY_SCORE_COUNT_SWITCH)!! as SwitchPreference
        scoreCountSwitchPreference.onPreferenceChangeListener = this
        scoreCountSwitchPreference.isChecked = true
        val watchSwitchPreference = findPreference<Preference>(SpHelper.KEY_WATCH_SWITCH)!! as SwitchPreference
        watchSwitchPreference.onPreferenceChangeListener = this
        watchSwitchPreference.isChecked = true
        val commentSwitchPreference = findPreference<Preference>(SpHelper.KEY_COMMENT_SWITCH)!! as SwitchPreference
        commentSwitchPreference.onPreferenceChangeListener = this
        commentSwitchPreference.isChecked = true
        val downloadSwitchPreference = findPreference<Preference>(SpHelper.KEY_DOWNLOAD_SWITCH)!! as SwitchPreference
        downloadSwitchPreference.onPreferenceChangeListener = this
        downloadSwitchPreference.isChecked = true
        val timeSwitchPreference = findPreference<Preference>(SpHelper.KEY_TIME_SWITCH)!! as SwitchPreference
        timeSwitchPreference.onPreferenceChangeListener = this
        timeSwitchPreference.isChecked = true
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        if (TextUtils.equals(preference.key, SpHelper.KEY_PACKAGE_NAME)) {
            mPackageName = newValue as String
            (preference as EditTextPreference).summary = mPackageName
            preference.summary = ""
        } else if (TextUtils.equals(preference.key, SpHelper.KEY_MARKET)) {
            mMarket = newValue as String
            preference.summary = mMarketPreference.entry
        }
        mSpHelper.put(preference.key, newValue)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            mPackageName = data?.getStringExtra("packageName")
            mAppName = data?.getStringExtra("appName")
            mChoosePreference.summary = mPackageName
            mPackageNamePreference.summary = ""
            mSpHelper.put(SpHelper.KEY_PACKAGE_NAME, mPackageName!!)
        }
    }

    fun save() = mSpHelper.apply()

    companion object {
        private const val REQUEST_CHOOSE_APP = 1
        fun newInstance(widgetId: Int): ConfigFragment {
            val fragment = ConfigFragment()
            val args = Bundle()
            args.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        when(preference.key) {
            SpHelper.KEY_CHOOSE_APP -> {
                startActivityForResult(Intent(activity!!, AppListActivity::class.java), REQUEST_CHOOSE_APP)
                return true
            }
        }
        return false
    }
}
