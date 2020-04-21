package com.su.market.query.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.su.market.query.R
import com.su.market.query.http.observer.BaseObserver
import java.util.*

/**
 * Created by su on 2020/4/18.
 */
abstract class CommonBaseActivity : AppCompatActivity() {
    @kotlin.jvm.JvmField
    var observers: MutableList<BaseObserver<*>> = ArrayList()
    var mToolbar: Toolbar? = null

    protected fun setTitle(title: String?) {
        mToolbar!!.title = title
    }

    override fun setTitle(titleRes: Int) {
        setTitle(getString(titleRes))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupToolbar()
    }

    private fun setupToolbar() {
        mToolbar = findViewById(R.id.id_toolbar)
        mToolbar?.let {
            val menuRes = menuRes()
            if (menuRes != 0) {
                mToolbar!!.inflateMenu(menuRes)
            }
            mToolbar!!.setOnMenuItemClickListener { item: MenuItem? -> onOptionsItemSelected(item) }
            mToolbar!!.setNavigationOnClickListener { popStackIfNeeded(this@CommonBaseActivity) }
        }
    }

    override fun onDestroy() {
        val it = observers.iterator()
        while (it.hasNext()) {
            val baseObserver = it.next()
            if (!baseObserver.isDisposed) {
                baseObserver.dispose()
            }
            it.remove()
        }
        super.onDestroy()
    }

    @MenuRes
    open fun menuRes(): Int = 0

    companion object {
        fun popStackIfNeeded(activity: CommonBaseActivity) {
            val count: Int = activity.supportFragmentManager.backStackEntryCount
            if (count == 0) {
                activity.onBackPressed()
            } else {
                activity.supportFragmentManager.popBackStack()
            }
        }
    }
}
