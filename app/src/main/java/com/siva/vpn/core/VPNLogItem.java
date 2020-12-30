package com.siva.vpn.core;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;

public class VPNLogItem implements Serializable {
	private static final long serialVersionUID = 7341923752956090364L;

	private long mLogtime = System.currentTimeMillis();
	private String mMsg;
	@SuppressWarnings("unused")	private int mLevel;

	public VPNLogItem(int level, String msg) {
		this.mLevel = level;
		this.mMsg = msg;
	}

	public String format(Context context, String timeFormat) {
		String pfx = "";
		if (!timeFormat.equals("none")) {
			Date d = new Date(mLogtime);
			java.text.DateFormat formatter;

			if (timeFormat.equals("long")) { 
				formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
			} else {
				formatter = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
			}
			pfx = formatter.format(d) + " ";
		}
		return pfx + mMsg;
	}

	public String toString() {
		return format(null, "long");
	}
}
