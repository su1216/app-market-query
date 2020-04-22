package com.su.market.query.component.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.su.market.query.BuildConfig;
import com.su.market.query.R;
import com.su.market.query.http.NetException;
import com.su.market.query.http.NullObject;
import com.su.market.query.http.function.BusinessFunction;
import com.su.market.query.http.observer.BaseObserver;
import com.su.market.query.market.ApkData;
import com.su.market.query.market.Coolapk;
import com.su.market.query.market.Market;
import com.su.market.query.market.UnitUtil;
import com.su.market.query.util.NumberUtil;
import com.su.market.query.util.SpHelper;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

/**
 * Created by su on 20-4-18.
 */
public class MonitorWidgetProvider extends AppWidgetProvider {

    private static final String TAG = MonitorWidgetProvider.class.getSimpleName();
    public static final String ACTION_UPDATE_DATA = "com.su.market.query.action.UPDATE_DATA";
    public static final String ACTION_INIT = "com.su.market.query.action.INIT";
    private static final int REQUEST_MAIN = 1;
    //不同widgetId需要不同的REQUEST
    private static final int REQUEST_DATA = 100;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm", Locale.CHINESE);
    private Market mMarket = new Coolapk();

    private static RemoteViews makeRemoteViews(Context context) {
        return new RemoteViews(context.getPackageName(), R.layout.app_widget_monitor);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "action: " + action);
        }
        if (ACTION_INIT.equals(action)
                || ACTION_UPDATE_DATA.equals(action)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
            if (ACTION_INIT.equals(action)) {
                resetWidget(context, appWidgetId);
            } else {
                execute(context, appWidgetId);
            }
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Log.d(TAG, "onAppWidgetOptionsChanged appWidgetId: " + appWidgetId);
        execute(context, appWidgetId);
    }

    //会有莫名其妙的id进来
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: " + Arrays.toString(appWidgetIds));
        Set<Integer> set = SpHelper.Companion.getWidgetSet();
        set.clear();
        for (int id : appWidgetIds) {
            set.add(id);
            execute(context, id);
            resetWidget(context, id);
        }
        SpHelper.Companion.applyWidgetIds();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Set<Integer> set = SpHelper.Companion.getWidgetSet();
        for (int id : appWidgetIds) {
            set.remove(id);
            SpHelper spHelper = new SpHelper(id);
            spHelper.delete();
        }
        SpHelper.Companion.applyWidgetIds();
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled");
    }

    private void resetWidget(Context context, final int appWidgetId) {
        SpHelper spHelper = new SpHelper(appWidgetId);
        ApkData lastApkData = spHelper.getLastApkData();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = makeRemoteViews(context);
        String packageName = spHelper.getPackageName();
        Log.d(TAG, "resetWidget appWidgetId: " + appWidgetId + " packageName: " + packageName);
        setIcon(context, packageName, views);
        views.setOnClickPendingIntent(R.id.refresh, makeUpdatePendingIntent(context, appWidgetId));
        views.setTextViewText(R.id.download_increment, "+0.0");
        views.setTextViewText(R.id.download, UnitUtil.INSTANCE.appendUnit(lastApkData.getDownload(), lastApkData.getDownloadUnit()));
        views.setTextViewText(R.id.watch_increment, "+0");
        views.setTextViewText(R.id.watch, UnitUtil.INSTANCE.appendUnit(lastApkData.getWatch(), lastApkData.getWatchUnit()));
        views.setTextViewText(R.id.comment_increment, "+0");
        views.setTextViewText(R.id.comment, UnitUtil.INSTANCE.appendUnit(lastApkData.getComment(), lastApkData.getCommentUnit()));
        views.setTextViewText(R.id.rank_increment, "+0");
        views.setTextViewText(R.id.rank, NumberUtil.INSTANCE.formatScore(spHelper.getFloat(SpHelper.KEY_SCORE)));
        views.setTextViewText(R.id.rank_count_increment, "+0");
        views.setTextViewText(R.id.rank_count, UnitUtil.INSTANCE.appendUnit(lastApkData.getRankCount(), lastApkData.getRankCountUnit()));
        views.setTextViewText(R.id.time_increment, "+0");
        views.setTextViewText(R.id.time, SDF.format(new Date()));
        setLaunchPendingIntent(context, views, mMarket);
        setVisible(spHelper, views);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void updateUi(Context context, int appWidgetId, ApkData apkData) {
        Log.d(TAG, "updateUi appWidgetId: " + appWidgetId);
        SpHelper spHelper = new SpHelper(appWidgetId);
        ApkData lastApkData = spHelper.getLastApkData();
        float incrementDownload = apkData.downloadIncrement(lastApkData);
        float incrementWatch = apkData.watchIncrement(lastApkData);
        float incrementComment = apkData.commentIncrement(lastApkData);
        float incrementRank = apkData.rankIncrement(lastApkData);
        float incrementRankCount = apkData.rankCountIncrement(lastApkData);
        long lastTime = lastApkData.getTime();
        long incrementTime;
        if (lastTime == 0) {
            incrementTime = 0;
        } else {
            incrementTime = (apkData.getTime() - lastTime) / 1000 / 60;
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = makeRemoteViews(context);
        setIcon(context, spHelper.getPackageName(), views);
        views.setOnClickPendingIntent(R.id.refresh, makeUpdatePendingIntent(context, appWidgetId));
        views.setTextViewText(R.id.download_increment, plusSign(incrementDownload) + UnitUtil.INSTANCE.emitNumber(incrementDownload, 1));
        views.setTextViewText(R.id.download, UnitUtil.INSTANCE.appendUnit(apkData.getDownload(), apkData.getDownloadUnit()));
        views.setTextViewText(R.id.watch_increment, plusSign(incrementWatch) + UnitUtil.INSTANCE.emitNumber(incrementWatch, 1));
        views.setTextViewText(R.id.watch, UnitUtil.INSTANCE.appendUnit(apkData.getWatch(), apkData.getWatchUnit()));
        views.setTextViewText(R.id.comment_increment, plusSign(incrementComment) + UnitUtil.INSTANCE.emitNumber(incrementComment, 1));
        views.setTextViewText(R.id.comment, UnitUtil.INSTANCE.appendUnit(apkData.getComment(), apkData.getCommentUnit()));
        views.setTextViewText(R.id.rank_increment, plusSign(incrementRank) + NumberUtil.INSTANCE.formatScore(incrementRank));
        views.setTextViewText(R.id.rank, NumberUtil.INSTANCE.formatScore(apkData.getRank()));
        views.setTextViewText(R.id.rank_count_increment, plusSign(incrementRankCount) + UnitUtil.INSTANCE.emitNumber(incrementRankCount, 1));
        views.setTextViewText(R.id.rank_count, UnitUtil.INSTANCE.appendUnit(apkData.getRankCount(), apkData.getRankCountUnit()));
        views.setTextViewText(R.id.time_increment, "+" + incrementTime);
        views.setTextViewText(R.id.time, SDF.format(new Date(System.currentTimeMillis())));
        setLaunchPendingIntent(context, views, mMarket);
        setVisible(spHelper, views);
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
    }

    private static void setIcon(Context context, String packageName, RemoteViews views) {
        if (TextUtils.isEmpty(packageName)) {
            views.setViewVisibility(R.id.icon, View.GONE);
            return;
        }
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            Drawable drawable = applicationInfo.loadIcon(pm);
            if (drawable != null) {
                views.setImageViewBitmap(R.id.icon, drawable2Bitmap(drawable));
                views.setViewVisibility(R.id.icon, View.VISIBLE);
            }
        } catch (PackageManager.NameNotFoundException e) {
            views.setViewVisibility(R.id.icon, View.GONE);
            Log.w(TAG, e);
        }
    }

    private static void setVisible(SpHelper spHelper, RemoteViews views) {
        views.setViewVisibility(R.id.download_layout, spHelper.getSwitch(SpHelper.KEY_DOWNLOAD_SWITCH) ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.watch_layout, spHelper.getSwitch(SpHelper.KEY_WATCH_SWITCH) ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.comment_layout, spHelper.getSwitch(SpHelper.KEY_COMMENT_SWITCH) ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.rank_count_layout, spHelper.getSwitch(SpHelper.KEY_SCORE_SWITCH) ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.rank_layout, spHelper.getSwitch(SpHelper.KEY_SCORE_COUNT_SWITCH) ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.time_layout, spHelper.getSwitch(SpHelper.KEY_TIME_SWITCH) ? View.VISIBLE : View.GONE);
    }

    private static String plusSign(double d) {
        return d >= 0 ? "+" : "-";
    }

    private static void setLaunchPendingIntent(Context context, RemoteViews views, Market market) {
        Intent intent = Market.Companion.getLaunchIntent(context, market.getMarketInfo());
        if (intent == null) {
            views.setOnClickPendingIntent(R.id.icon, null);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pi = PendingIntent.getActivity(context, REQUEST_MAIN, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.icon, pi);
        }
    }

    private static PendingIntent makeUpdatePendingIntent(Context context, int appWidgetId) {
        Log.d(TAG, "makeUpdatePendingIntent appWidgetId: " + appWidgetId);
        Intent mainIntent = new Intent(context, MonitorWidgetProvider.class);
        mainIntent.setAction(ACTION_UPDATE_DATA);
        mainIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(context, REQUEST_DATA + appWidgetId, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void execute(final Context context, final int appWidgetId) {
        SpHelper spHelper = new SpHelper(appWidgetId);
        String packageName = spHelper.getPackageName();
        if (TextUtils.isEmpty(packageName)) {
            Log.d(TAG, "execute invalid appWidgetId: " + appWidgetId);
            return;
        }
        Log.d(TAG, "execute appWidgetId: " + appWidgetId + " packageName: " + packageName);
        showProgressBar(context, appWidgetId, true);
        mMarket.getNetRequest(packageName)
                .map(new BusinessFunction<String, NullObject>() {
                    @Override
                    public NullObject onSuccess(String httpResult) {
                        ApkData apkData = mMarket.parse(httpResult);
                        updateUi(context, appWidgetId, apkData);
                        SpHelper.Companion.saveApkData(appWidgetId, apkData);
                        return NullObject.NULL_OBJECT;
                    }
                })
                .safeSubscribe(new BaseObserver<NullObject>(null) {
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        showProgressBar(context, appWidgetId, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showProgressBar(context, appWidgetId, false);
                        if (e instanceof NetException) {
                            NetException netException = (NetException) e;
                            if (netException.getCode() == 404) {
                                Toast.makeText(context, R.string.no_app_found, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        super.onError(e);
                    }
                });
    }

    private static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    private static void showProgressBar(Context context, int appWidgetId, boolean show) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final RemoteViews views = makeRemoteViews(context);
        if (show) {
            views.setViewVisibility(R.id.progress_bar, View.VISIBLE);
            views.setViewVisibility(R.id.refresh, View.GONE);
        } else {
            views.setViewVisibility(R.id.progress_bar, View.GONE);
            views.setViewVisibility(R.id.refresh, View.VISIBLE);
        }
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
    }
}
