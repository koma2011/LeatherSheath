package com.ape.leather2.receiver;

import com.ape.leather2.module.data.LeatherData;
import com.ape.leather2.ui.LeatherViewManager;
import com.ape.leather2.module.log.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * @author juan.li
 * @date Dec 17, 2015 5:32:24 PM
 */
public class LeatherReceiver extends BroadcastReceiver {

    private static final String TAG = LeatherReceiver.class.getName();

    private static final String HALL_CHANGE_ACTION = "android.intent.action.HALL_CHANGED";
    private static final String HALL_STATUS_EXTRA_KEY = "state";
    public static final int HALL_STATUS_OPEN = 1;
    public static final int HALL_STATUS_CLOSE = 0;

    private LeatherViewManager mLeatherViewManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (HALL_CHANGE_ACTION.equals(intent.getAction()) &&
                intent.getExtras() != null) {
            int status = intent.getExtras().getInt(HALL_STATUS_EXTRA_KEY);
            Logger.i("KomaLeatherSheath", "-----------status : " + status);
            mLeatherViewManager = LeatherViewManager.getInstance(context.getApplicationContext());
            if (status == HALL_STATUS_OPEN) {
                if (LeatherData.getInstance().getLeatherStatus(context)) {
                    mLeatherViewManager.openBackgroundActivity();
                    mLeatherViewManager.openLeather();
                }
            } else if (status == HALL_STATUS_CLOSE) {
                mLeatherViewManager.closeBackgroundActivity();
                mLeatherViewManager.closeLeather();
            }
        }
    }

}
