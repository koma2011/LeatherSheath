package com.ape.leather2.module.telephone;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telecom.InCallService;
import android.telecom.Phone;
import android.util.Log;

public class InCallServiceImpl extends InCallService {
    
    private static final String TAG = "InCallServiceImpl";
    
    @Override
    public void onPhoneCreated(Phone phone) {
        Log.v(TAG, "onPhoneCreated");
        CallList.getInstance(this).setPhone(phone);
    }
    
    @Override
    public void onPhoneDestroyed(Phone phone) {
        Log.v(TAG, "onPhoneDestroyed");
        // Tear down the InCall system
        CallList.getInstance(this).clearPhone();
    }
    
    /**
     * M: used to register application text for plug in manager. @{
     * 
     * @param intent
     * @return parent in call service binder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
    /** @} */
}
