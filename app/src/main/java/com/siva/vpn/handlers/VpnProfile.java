package com.siva.vpn.handlers;

import java.util.Locale;
import java.util.UUID;

import android.content.SharedPreferences;

public class VpnProfile implements Comparable<VpnProfile> {
    public static final String INLINE_TAG = "[[INLINE]]";

    public SharedPreferences mPrefs;
    public String mName;

    private UUID mUuid;

    private void loadPrefs(SharedPreferences prefs) {
    	mPrefs = prefs;

    	String uuid = mPrefs.getString("profile_uuid", null);
    	if (uuid != null) {
    		mUuid = UUID.fromString(uuid);
    	}
    	mName = mPrefs.getString("profile_name", null);
    }

    public VpnProfile(SharedPreferences prefs, String uuid, String name) {
    	prefs.edit()
    		.putString("profile_uuid", uuid)
    		.putString("profile_name", name)
    		.commit();
    	loadPrefs(prefs);
    }

    public VpnProfile(SharedPreferences prefs) {
    	loadPrefs(prefs);
    }

    public VpnProfile(String name, String uuid) {
        mUuid = UUID.fromString(uuid);
        mName = name;
    }

    public boolean isValid() {
        return mName != null && mUuid != null;
    }

    public UUID getUUID() {
        return mUuid;

    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }

    public String getUUIDString() {
        return mUuid.toString();
    }

	@Override
	public int compareTo(VpnProfile arg0) {
		Locale def = Locale.getDefault();
		return getName().toUpperCase(def).compareTo(arg0.getName().toUpperCase(def));
	}
}




