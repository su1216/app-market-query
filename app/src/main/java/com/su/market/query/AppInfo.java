package com.su.market.query;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo implements Comparable<AppInfo>, Parcelable {

    private String versionName;
    private long versionCode;
    private Drawable iconDrawable;
    @Searchable
    private String appName;
    private String packageName;
    private int flags;
    private Intent launchIntent;
    private String apkPath;

    public AppInfo() {}

    protected AppInfo(Parcel in) {
        versionName = in.readString();
        versionCode = in.readLong();
        appName = in.readString();
        packageName = in.readString();
        flags = in.readInt();
        launchIntent = in.readParcelable(Intent.class.getClassLoader());
        apkPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(versionName);
        dest.writeLong(versionCode);
        dest.writeString(appName);
        dest.writeString(packageName);
        dest.writeInt(flags);
        dest.writeParcelable(launchIntent, flags);
        dest.writeString(apkPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public long getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(long versionCode) {
        this.versionCode = versionCode;
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public Intent getLaunchIntent() {
        return launchIntent;
    }

    public void setLaunchIntent(Intent launchIntent) {
        this.launchIntent = launchIntent;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    @Override
    public int compareTo(AppInfo o) {
        return appName.compareTo(o.appName);
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", iconDrawable=" + iconDrawable +
                ", appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", flags=" + flags +
                ", launchIntent=" + launchIntent +
                ", apkPath='" + apkPath + '\'' +
                '}';
    }
}
