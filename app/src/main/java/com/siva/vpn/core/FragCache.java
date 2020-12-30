package com.siva.vpn.core;

import java.util.HashMap;

public class FragCache {

	private static HashMap<String,String> mCache;

	public static synchronized void init() {
		mCache = new HashMap<String,String>();
	}

	private static String hashCode(String UUID, String key) {
		StringBuilder sb = new StringBuilder();
		if (UUID != null) {
			sb.append(UUID.hashCode());
		}
		sb.append(".");
		if (key != null) {
			sb.append(key.hashCode());
		}
		return sb.toString();
	}

	public static synchronized String get(String UUID, String key) {
		return mCache.get(hashCode(UUID, key));
	}

	public static String get(String key) {
		return get(null, key);
	}

	public static synchronized void put(String UUID, String key, String value) {
		mCache.put(hashCode(UUID, key), value);
	}

	public static void put(String key, String value) {
		put(null, key, value);
	}

}
