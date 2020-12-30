package com.siva.vpn.activity;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.collector.CrashReportData;
import org.acra.sender.HttpSender;
import org.acra.sender.ReportSenderException;

import android.content.pm.PackageManager;

import com.siva.vpn.R;
import com.siva.vpn.core.FragCache;
import com.siva.vpn.core.ProfileManager;
import com.siva.vpn.core.VPNLog;

import de.robv.android.xposed.XposedBridge;
import org.acra.sender.HttpSender.Method;
import org.acra.sender.HttpSender.Type;

@ReportsCrashes(
		mode = ReportingInteractionMode.DIALOG,
		resDialogText = R.string.crash_dialog_text,
		resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,

		formUri = "https://kpc.cloudant.com/acra-openconnect/_design/acra-storage/_update/report",
		formUriBasicAuthLogin="ineintlynnoveristimedesc",
		formUriBasicAuthPassword="mUmkrQIOKd3HalLf5AQuyxpA",

		formKey = ""
)

public class Application extends android.app.Application {

	private boolean isPackageInstalled(String name) {
		PackageManager pm = getPackageManager();
		try {
			pm.getPackageInfo(name, 0);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}


	private void setupACRA() {
		String[] strArr = {"com.koushikdutta.superuser", "com.noshufou.android.su", "com.noshufou.android.su.elite", "com.miui.uac", "eu.chainfire.supersu", "eu.chainfire.supersu.pro", XposedBridge.INSTALLER_PACKAGE_NAME, "biz.bokhorst.xprivacy", "biz.bokhorst.xprivacy.pro"};
		ACRA.init(this);
		ErrorReporter errorReporter = ACRA.getErrorReporter();
		errorReporter.setReportSender(new HttpSender(Method.PUT, Type.JSON, null) {
			public void send(CrashReportData crashReportData) throws ReportSenderException {
				crashReportData.put(ReportField.APPLICATION_LOG, VPNLog.dumpLast());
				super.send(crashReportData);
			}
		});
		for (int i = 0; i < 9; i++) {
			String str = strArr[i];
			StringBuilder sb = new StringBuilder();
			sb.append("pkg-");
			sb.append(str.replaceAll("\\.", "-"));
			errorReporter.putCustomData(sb.toString(), isPackageInstalled(str) ? "true" : "false");
		}
	}

	public void onCreate() {
		super.onCreate();
		//setupServerData();
		setupACRA();
		System.loadLibrary("openconnect");
		System.loadLibrary("stoken");
		ProfileManager.init(getApplicationContext());
		FragCache.init();
	}
}
