package com.siva.vpn.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

public class FragActivity extends Activity {

	public static final String TAG = "OpenConnect";

	public static final String EXTRA_FRAGMENT_NAME = "app.openconnect.fragment_name";

	public static final String FRAGMENT_PREFIX = "app.openconnect.fragments.";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState == null) {
			try {
				String fragName = getIntent().getStringExtra(EXTRA_FRAGMENT_NAME);
				Fragment frag = (Fragment)Class.forName(FRAGMENT_PREFIX + fragName).newInstance();
				getFragmentManager().beginTransaction().add(android.R.id.content, frag).commit();
			} catch (Exception e) {
				Log.e(TAG, "unable to create fragment", e);
				finish();
			}
		}
    }

}
