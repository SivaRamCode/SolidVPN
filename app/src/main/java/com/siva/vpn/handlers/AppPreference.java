package com.siva.vpn.handlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
public class AppPreference {

     private static Context mContext;

    private static AppPreference appPreference = null;

    private SharedPreferences sharedPreferences, settingsPreferences;
    private SharedPreferences.Editor editor;

    public static AppPreference getInstance(Context context) {
        if (appPreference == null) {
            mContext = context;
            appPreference = new AppPreference();
        }
        return appPreference;
    }

    private AppPreference() {
        sharedPreferences = mContext.getSharedPreferences("VPN_PREF", Context.MODE_PRIVATE);
        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = sharedPreferences.edit();
    }

    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void setInteger(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInteger(String key) {
        return sharedPreferences.getInt(key, -1);
    }


    public boolean isNotificationOn() {
        return settingsPreferences.getBoolean("perf_notification", true);
    }

    public boolean isCookieEnabled() {
        return settingsPreferences.getBoolean("perf_cookie", true);
    }

    public boolean isZoomEnabled() {
        return settingsPreferences.getBoolean("perf_zoom", true);
    }


}
