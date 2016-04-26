package com.ape.leather2.module.data;


import com.ape.leather2.module.log.Logger;

import android.content.Context;
import android.content.SharedPreferences;

public class LeatherData {
    
    private static final String TAG = LeatherData.class.getName();


    private Boolean mLeatherStatus;
    
    
     private static LeatherData ourInstance = new LeatherData();

     public static LeatherData getInstance() {
         return ourInstance;
     }
    
     private LeatherData() {
         
     }
     
     public void setLeatherStatusOpen(Context context) {
         setLeatherStatus(context, true);
     }
     
     public void setLeatherStatusClose(Context context) {
         setLeatherStatus(context, false);
     }
     
     public boolean getLeatherStatus(Context context) {
         if(mLeatherStatus != null) {
             return mLeatherStatus.booleanValue();
         }
         SharedPreferences sp = context.getSharedPreferences(LeatherDataConfig.LEATHER_DATA, Context.MODE_PRIVATE);
         boolean status = sp.getBoolean(LeatherDataConfig.LEATHER_STATUS, true);
         Logger.i(TAG, "nthpower[getLeatherStatus]status:%s", status);
         return status;
     }
     
     private void setLeatherStatus(Context context, boolean status) {
         mLeatherStatus = Boolean.valueOf(status);
         SharedPreferences sp = context.getSharedPreferences(LeatherDataConfig.LEATHER_DATA, Context.MODE_PRIVATE);
         SharedPreferences.Editor editor = sp.edit();
         editor.putBoolean(LeatherDataConfig.LEATHER_STATUS, status);
         editor.commit();
     }
     

}
