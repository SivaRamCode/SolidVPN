package com.siva.vpn.handlers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class PrefManager {

    public static final String PRF_SERVER = "server";
    public static final String PRF_APP_DATA = "app_data";


    public static final String KEY_SERVER_IPS = "server_ips";
    public static final String KEY_SERVER_NAMES = "server_names";
    public static final String KEY_CONNECTION_STATE = "connection_state";
    public static final String KEY_CONNECTION_INDEX = "connection_index";
    public static final String KEY_RATE_INDEX = "rate_index";
    public static final String KEY_PUSED_BY_ADS = "pused_by_ads";
    public static final String KEY_SPINER_INDEX = "spiner_index";
    public static final String KEY_Parent_INDEX = "PARENT_INDEX";
    public static final String KEY_NOT_FIRST_OPEN = "first_open";
    public static final String KEY_OPEN_COUNT = "open_count";


    public static final String MODE_WRITE = "edit";
    public static final String MODE_READ = "read";
    public static final String MODE_DELETE = "delete";
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private String PreferenceName = "";

    Context context;

    public PrefManager(Context ctx, String PrefName, String mode){
        context = ctx;
        PreferenceName = PrefName;
        if(mode.equals(MODE_READ)){
            prefs = context.getSharedPreferences(PreferenceName, MODE_PRIVATE);
        }else if(mode.equals(MODE_WRITE)){
            editor =  context.getSharedPreferences(PreferenceName, MODE_PRIVATE).edit();
        }else if(mode.equals(MODE_DELETE)){
            editor =  context.getSharedPreferences(PreferenceName, MODE_PRIVATE).edit();
            editor.clear().apply();
        }
    }

    public void SaveStringData(String key, String data){
        editor.putString(key, data);
        editor.apply();
    }

    public void SaveStringSet(String key, Set<String> data){
        editor.putStringSet(key, data);
        editor.apply();
    }

    public void SaveIntData(String key, int value){
        editor.putInt(key, value);
        editor.apply();
    }

    public void SaveBoolData(String key, Boolean flag){
        editor.putBoolean(key, flag);
        editor.apply();
    }


    public String ReadString(String key) {

        if(key == null || key.equals("")){
            return null;
        }else {
            return prefs.getString(key, "");
        }
    }

    public Set<String> ReadStringSet(String key) {

        if(key == null || key.equals("")){
            return null;
        }else {
            return prefs.getStringSet(key, null);
        }
    }


    public int ReadInt(String key) {

        if(key == null || key.equals("")){
            return 0;
        }else {
            return prefs.getInt(key, 0);
        }
    }

    public Boolean ReadBool(String key) {

        if(key == null || key.equals("")){
            return false;
        }else {
            return prefs.getBoolean(key, false);
        }
    }

}
