package com.ape.leather2;

import com.ape.leather2.module.log.Logger;
import com.ape.leather2.module.mms.MmsManager;
import com.ape.leather2.module.telephone.MissedCallManager;
import com.ape.leather2.service.LeatherAccessibilityManager;
import com.ape.leather2.ui.LeatherViewManager;

import android.app.Application;
import android.content.Context;

/**
 * @author juan.li
 * @date Dec 1, 2015 11:26:54 AM
 */
public class LeatherApplication extends Application {

    private static final String KOMA_TAG = "KomaLeatherSheath";
    private static final String TAG = LeatherApplication.class.getName();

    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i(KOMA_TAG, TAG + "[onCreate]Leather application create");
        mContext = getApplicationContext();
        LeatherViewManager.getInstance(this);
        LeatherAccessibilityManager.getInstance().init(this);
        MmsManager.getInstance(this).open();
        MissedCallManager.getInstance(this).open();
    }

    public static LeatherViewManager getLeatherViewManager(Context context) {
        return LeatherViewManager.getInstance(context);
    }
}
