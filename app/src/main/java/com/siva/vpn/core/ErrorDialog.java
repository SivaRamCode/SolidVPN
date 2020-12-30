package com.siva.vpn.core;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.siva.vpn.activity.MainActivity;
import com.siva.vpn.R;


public class ErrorDialog extends UserDialog
	implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {

	public String mTitle;
	public String mMessage;

	private Dialog mAlert;
	public ErrorDialog(SharedPreferences prefs, String title, String message) {
		super(prefs);
		mTitle = title;
		mMessage = message;
	}


	@Override
	public void onStart(Context context) {
		super.onStart(context);
		mAlert = new Dialog(context);
		mAlert.setContentView(R.layout.layout_internet);
		Button cancel_btn = (Button) mAlert.findViewById(R.id.cancel_btn);
		TextView message_tv = (TextView) mAlert.findViewById(R.id.message_tv);
		TextView title_tv = (TextView) mAlert.findViewById(R.id.title_tv);
		message_tv.setText(mMessage);
		title_tv.setText(mTitle);
		cancel_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				MainActivity.Status.setEnabled(true);
				MainActivity.Status.setAlpha(1.0f);
				MainActivity.board.setAlpha(1.0f);
				MainActivity.board.setEnabled(true);

				mAlert.dismiss();
			}
		});

		mAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mAlert.setCancelable(false);
		mAlert.show();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		finish(true);
		mAlert = null;

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
	}

	@Override
	public void onStop(Context context) {
		super.onStop(context);
		finish(null);
		if (mAlert != null) {
			mAlert.dismiss();
		}
	}
}
