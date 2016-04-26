package com.ape.leather2.module.mms;

import java.util.ArrayList;
import java.util.List;

import com.ape.leather2.module.log.Logger;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.Telephony;

/**
 * @author juan.li
 * @date Dec 1, 2015 3:02:01 PM
 */
public class MmsManager {
    
    private static final String TAG = MmsManager.class.getName();
    
    private IMmsCallback mCallback;
    private static MmsManager sMmsManager;
    private ContentResolver mResolver;
    private int mUnread = 0;
    private List<IMmsCallback> mMmsCallbackList;
    
    public static MmsManager getInstance(Context context) {
        if (sMmsManager == null) {
            sMmsManager = new MmsManager(context);
        }
        
        return sMmsManager;
    }
    
    private MmsManager(Context context) {
        Logger.i(TAG, "[MmsManager]create MmsManager instance~~");
        mResolver = context.getContentResolver();
    }
    
    public void open() {
        mResolver.registerContentObserver(Telephony.Sms.CONTENT_URI, true, mMmsObserver);
        mUnread = getUnread();
        Logger.i(TAG, "[open]register MmsObserver~~~");
    }
    
    public void close() {
        mResolver.unregisterContentObserver(mMmsObserver);
        mUnread = 0;
        Logger.i(TAG, "[close]unregister MmsObserver~~~");
    }
    
    /**
     * @deprecated use {@link #addMmsCallback(IMmsCallback)}}
     */
    @Deprecated
    public void setCallback(IMmsCallback callback) {
        mCallback = callback;
    }
    
    public int getUnread() {
        Cursor cursor = mResolver.query(Telephony.Sms.Inbox.CONTENT_URI,
                new String[] {Telephony.Sms.READ},
                Telephony.Sms.READ + "=?",
                new String[] {"0"}, null);

        int count = 0;
        if(cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }

        return count;
    }
    
    private ContentObserver mMmsObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            int count = getUnread();
            Logger.i(TAG, "[MmsObserver.onChange]count:%d, mUnread:%d",
                    count, mUnread);
            if (mCallback != null
                    && mUnread != count) {
                mCallback.onMmsUnreadUpdate(count);
            }
            
            if (mMmsCallbackList != null && mUnread != count) {
                for (int i = 0; i < mMmsCallbackList.size(); i ++) {
                    IMmsCallback callback = mMmsCallbackList.get(i);
                    callback.onMmsUnreadUpdate(count);
                }
            }
            mUnread = count;
        }
        
    };
    
    public void addMmsCallback(IMmsCallback callback) {
        if (mMmsCallbackList == null) {
            mMmsCallbackList = new ArrayList<>();
        }
        mMmsCallbackList.add(callback);
    }
    
    public void removeMmsCallback(IMmsCallback callback) {
        if (mMmsCallbackList != null) {
            mMmsCallbackList.remove(callback);
        }
    }
    
    public interface IMmsCallback {
        void onMmsUnreadUpdate(int unread);
    }
}
