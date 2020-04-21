package com.su.market.query.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.pm.PackageInfoCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.su.market.query.AppExecutors;
import com.su.market.query.AppInfo;
import com.su.market.query.R;
import com.su.market.query.util.SearchableHelper;
import com.su.market.query.shell.ShellUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppListActivity extends CommonBaseActivity implements SearchView.OnQueryTextListener {

    public static final String TAG = AppListActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private View mProgressBar;
    private AppAdapter mAppAdapter;
    private String mKeyword;
    private List<AppInfo> mAppInfoList = new ArrayList<>();
    private SearchableHelper mInstalledSearchableHelper = new SearchableHelper(AppInfo.class);
    private String mQueryText = "";
    private AppExecutors mAppExecutors = AppExecutors.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        mAppAdapter = new AppAdapter(this, mInstalledSearchableHelper, Collections.emptyList());
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAppAdapter);
        mProgressBar = findViewById(R.id.progress_bar);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setTitle(getString(R.string.app_list));
        mInstalledSearchableHelper.initSearchToolbar(getMToolbar(), this);
        mAppExecutors.diskIO().execute(() -> {
            if (mAppInfoList.isEmpty()) {
                mAppInfoList.addAll(getAppInfoList(this));
            }
            runOnUiThread(() -> {
                MenuItem menuItem = getMToolbar().getMenu().findItem(R.id.search);
                menuItem.setVisible(true);
                filter("");
                mRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            });
        });
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mQueryText = s;
        filter(mQueryText);
        return false;
    }

    @NonNull
    private List<AppInfo> getInstalledAppInfoList(String keyword) {
        mInstalledSearchableHelper.clear();
        List<AppInfo> list = new ArrayList<>();
        for (AppInfo appInfo : mAppInfoList) {
            if ((ApplicationInfo.FLAG_SYSTEM & appInfo.getFlags()) == 0
                    && mInstalledSearchableHelper.find(keyword, appInfo)) {
                list.add(appInfo);
            }
        }
        return list;
    }

    private static List<AppInfo> getAppInfoList(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        List<AppInfo> installedList = new ArrayList<>();
        for (int i = 0; i < packageInfoList.size(); i++) {
            PackageInfo packageInfo = packageInfoList.get(i);
            AppInfo appInfo = makeAppInfo(context, packageInfo);
            if ((ApplicationInfo.FLAG_SYSTEM & appInfo.getFlags()) == 0) {
                installedList.add(appInfo);
            }
        }

        if (installedList.isEmpty()) {
            installedList.addAll(initAppInfoListWithCommandPm(context));
        } else if (installedList.size() == 1) {
            AppInfo appInfo = installedList.get(0);
            if (TextUtils.equals(appInfo.getPackageName(), context.getPackageName())) {
                installedList.addAll(initAppInfoListWithCommandPm(context));
            }
        }

        Collections.sort(installedList);
        return installedList;
    }

    private static List<AppInfo> initAppInfoListWithCommandPm(Context context) {
        List<AppInfo> appInfoList = new ArrayList<>();
        List<String> packageList = ShellUtil.getAllApps();
        PackageManager pm = context.getPackageManager();
        for (String packageName : packageList) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                AppInfo appInfo = makeAppInfo(context, packageInfo);
                appInfoList.add(appInfo);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, e);
            }
        }
        return appInfoList;
    }

    private static AppInfo makeAppInfo(Context context, PackageInfo packageInfo) {
        PackageManager pm = context.getPackageManager();
        AppInfo appInfo = new AppInfo();
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        appInfo.setAppName(applicationInfo.loadLabel(pm).toString());
        if (applicationInfo.loadIcon(pm) != null) {
            appInfo.setIconDrawable(applicationInfo.loadIcon(pm));
        }
        appInfo.setPackageName(packageInfo.packageName);
        appInfo.setVersionCode(PackageInfoCompat.getLongVersionCode(packageInfo));
        appInfo.setVersionName(packageInfo.versionName);
        appInfo.setFlags(applicationInfo.flags);
        appInfo.setLaunchIntent(pm.getLaunchIntentForPackage(packageInfo.packageName));
        appInfo.setApkPath(applicationInfo.sourceDir);
        return appInfo;
    }

    private void filter(String keyword) {
        if (!TextUtils.isEmpty(keyword) //避免页面restore时查询无结果
                && TextUtils.equals(mKeyword, keyword)) { //避免在条件查询切换tab时多次查询
            return;
        }
        mKeyword = keyword;
        List<AppInfo> list = new ArrayList<>(getInstalledAppInfoList(keyword));
        mInstalledSearchableHelper.clear();
        mAppAdapter.updateData(keyword, list);
    }

    @Override
    public int menuRes() {
        return R.menu.search_menu;
    }
}
