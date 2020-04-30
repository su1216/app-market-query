package com.su.market.query.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.su.market.query.AdminApp
import com.su.market.query.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : CommonBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        assistantLayout.setOnClickListener {
            try {
                startActivity(AdminApp.coolapkIntent("com.su.assistant.pro"))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "ActivityNotFoundException", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setTitle(R.string.app_name)
        mToolbar!!.navigationIcon = null
    }

    companion object {
        fun goStoreAppDetail(context: Context) {
            val uri =
                Uri.parse("market://details?id=" + context.packageName)
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            flags = if (Build.VERSION.SDK_INT >= 21) {
                flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
            } else {
                flags or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
            }
            goToMarket.addFlags(flags)
            val chooser =
                Intent.createChooser(goToMarket, "")
            if (goToMarket.resolveActivity(context.packageManager) != null) {
                context.startActivity(chooser)
                return
            }
            Toast.makeText(
                context,
                context.resources.getString(R.string.no_app_market_hint),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
