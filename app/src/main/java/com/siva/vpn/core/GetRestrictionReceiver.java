package com.siva.vpn.core;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.siva.vpn.R;

import java.util.ArrayList;

public class GetRestrictionReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onReceive(final Context context, Intent intent) {
        final PendingResult result = goAsync();

        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void run() {
                final Bundle extras = new Bundle();

                ArrayList<RestrictionEntry> restrictionEntries = initRestrictions(context);

                extras.putParcelableArrayList(Intent.EXTRA_RESTRICTIONS_LIST, restrictionEntries);
                result.setResult(Activity.RESULT_OK,null,extras);
                result.finish();
            }
        }.run();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private ArrayList<RestrictionEntry> initRestrictions(Context context) {
        ArrayList<RestrictionEntry> restrictions = new ArrayList<RestrictionEntry>();
        RestrictionEntry allowChanges = new RestrictionEntry("allow_changes",false);
        allowChanges.setTitle(context.getString(R.string.allow_vpn_changes));
        restrictions.add(allowChanges);

        return restrictions;
    }
}
