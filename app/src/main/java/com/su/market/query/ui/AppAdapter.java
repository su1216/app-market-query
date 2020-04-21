package com.su.market.query.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.su.market.query.AppInfo;
import com.su.market.query.R;
import com.su.market.query.util.SearchableHelper;
import com.su.market.query.widget.BaseRecyclerAdapter;
import com.su.market.query.widget.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppAdapter extends BaseRecyclerAdapter<AppInfo> {

    private Activity mActivity;
    private SearchableHelper mSearchableHelper;
    private List<Map<Integer, Integer>> mNameFilterColorIndexList = new ArrayList<>();

    public AppAdapter(Activity activity, SearchableHelper searchableHelper, List<AppInfo> data) {
        super(data);
        mActivity = activity;
        mSearchableHelper = searchableHelper;
    }

    @Override
    public int getLayoutId(int itemType) {
        return R.layout.item_app;
    }

    public void updateData(@NonNull String keyword, @NonNull List<AppInfo> data) {
        mNameFilterColorIndexList.clear();
        for (AppInfo appInfo : data) {
            String appName = appInfo.getAppName();
            mSearchableHelper.find(keyword, appName, mNameFilterColorIndexList);
        }
        super.updateData(data);
    }

    @Override
    protected void bindData(@NonNull BaseViewHolder holder, int position, int itemType) {
        AppInfo appInfo = getData().get(position);
        ImageView iconView = holder.getView(R.id.icon);
        TextView nameView = holder.getView(R.id.name);
        TextView packageNameView = holder.getView(R.id.package_name);
        TextView versionView = holder.getView(R.id.version);
        TextView systemView = holder.getView(R.id.system);
        iconView.setImageDrawable(appInfo.getIconDrawable());
        nameView.setText(appInfo.getAppName());
        if (mSearchableHelper != null) {
            mSearchableHelper.refreshFilterColor(nameView, position, mNameFilterColorIndexList);
        }
        packageNameView.setText(appInfo.getPackageName());
        versionView.setText(mActivity.getString(R.string.s_backslash_s, appInfo.getVersionName(), String.valueOf(appInfo.getVersionCode())));
        systemView.setText((ApplicationInfo.FLAG_SYSTEM & appInfo.getFlags()) == ApplicationInfo.FLAG_SYSTEM ? "system" : "");
        holder.itemView.setOnClickListener(v -> {
            AppInfo info = getData().get(position);
            Intent data = new Intent();
            data.putExtra("packageName", info.getPackageName());
            data.putExtra("appName", info.getAppName());
            mActivity.setResult(Activity.RESULT_OK, data);
            mActivity.finish();
        });
    }
}
