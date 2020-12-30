package com.siva.vpn.fragments;

import java.util.Calendar;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.siva.vpn.activity.FragActivity;
import com.siva.vpn.R;

public class FeedbackFragment extends Fragment {

	public static final String TAG = "OpenConnect";
	public static final String marketURI = "market://details?id=app.openconnect";
	private static final int nagDays = 14;
	private static final long nagUses = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {

    	View v = inflater.inflate(R.layout.feedback, container, false);

    	final Activity act = getActivity();
    	Button b;

    	b = (Button)v.findViewById(R.id.i_love_it);
    	b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				recordNag(act);

				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(marketURI));
				try {
					startActivity(i);
				} catch (ActivityNotFoundException e) {
				}
				act.finish();
			}
    	});

    	b = (Button)v.findViewById(R.id.needs_work);
    	b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				recordNag(act);

				String ver = "???";
				try {
					PackageInfo packageinfo = act.getPackageManager().getPackageInfo(act.getPackageName(), 0);
					ver = packageinfo.versionName;
				} catch (NameNotFoundException e) {
				}
				Intent i = new Intent(android.content.Intent.ACTION_SEND);

				i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"cernekee+oc@gmail.com"});
				i.putExtra(android.content.Intent.EXTRA_SUBJECT, "ics-openconnect v" +
						ver + " - Needs Improvement!");
				i.setType("plain/text");

				try {
					startActivity(i);
				} catch (ActivityNotFoundException e) {
				}
				act.finish();
			}
    	});

    	b = (Button)v.findViewById(R.id.maybe_later);
    	b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				act.finish();
			}
    	});

    	return v;
    }

    private static void recordNag(Context ctx) {
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
    	sp.edit().putBoolean("feedback_nagged", true).commit();
    }

    private static boolean isNagOK(Context ctx) {
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);

    	if (sp.getBoolean("feedback_nagged", false)) {
    		return false;
    	}

    	long first = sp.getLong("first_use", -1);
    	if (first == -1) {
    		return false;
    	}

    	Calendar now = Calendar.getInstance();
    	Calendar nagDay = Calendar.getInstance();
    	nagDay.setTimeInMillis(first);
    	nagDay.add(Calendar.DATE, nagDays);
    	if (!now.after(nagDay)) {
    		return false;
    	}

    	long numUses = sp.getLong("num_uses", 0);
        return numUses >= nagUses;
    }

    public static void feedbackNag(Context ctx) {
    	if (!isNagOK(ctx)) {
    		return;
    	}
    	recordNag(ctx);

		Intent intent = new Intent(ctx, FragActivity.class);
		intent.putExtra(FragActivity.EXTRA_FRAGMENT_NAME, "FeedbackFragment");
		ctx.startActivity(intent);
    }

    public static void recordUse(Context ctx, boolean success) {
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
    	if (sp.getLong("first_use", -1) == -1) {
    		long now = Calendar.getInstance().getTimeInMillis();
    		sp.edit().putLong("first_use", now).apply();
    	}
    	if (!success) {
    		return;
    	}

    	long numUses = sp.getLong("num_uses", 0);
    	sp.edit().putLong("num_uses", numUses + 1).apply();
    }

    public static void recordProfileAdd(Context ctx) {
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
    	long count = sp.getLong("num_profiles_added", 0) + 1;
    	sp.edit().putLong("num_profiles_added", count).apply();
    }
}
