package com.ape.leather2.module.telephone;

import java.util.ArrayList;
import java.util.List;

import com.ape.leather2.module.log.Logger;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.CommonDataKinds.Phone;

/**
 * @author juan.li
 * @date 2015-10-16 19:23:00
 */
public class MissedCallManager {

    private static final String TAG = MissedCallManager.class.getName();

    private Context mContext;
    private IMissedCallback mCallback;
    private ContentResolver mResolver;
    private int mMissedCall = 0;

    private static MissedCallManager sMissedCallManager;
    private List<IMissedCallback> mMissedCallbackList;

    public static MissedCallManager getInstance(Context context) {
        if (sMissedCallManager == null) {
            sMissedCallManager = new MissedCallManager(context);
        }
        return sMissedCallManager;
    }

    private MissedCallManager(Context context) {
        mContext = context;
        mResolver = mContext.getContentResolver();
    }

    public void open() {
        mResolver.registerContentObserver(CallLog.Calls.CONTENT_URI, true, mMissCallObserver);
        mMissedCall = getMissedCall();
        Logger.i(TAG, "[open]register MissCallObserver~~~");
    }

    public void close() {
        mResolver.unregisterContentObserver(mMissCallObserver);
        mMissedCall = 0;
        Logger.i(TAG, "[close]unregister MissCallObserver~~~");
    }

    /**
     * @deprecated Use {@link #addMissedCallback(IMissedCallback)}
     */
    @Deprecated
    public void setCallback(IMissedCallback callback) {
        mCallback = callback;
    }
    
    public int getMissedCall() {
        String[] projection = new String[] {
                Phone._ID,
                Calls.NUMBER,
                Calls.DATE };

        StringBuilder builder = new StringBuilder();
        builder.append(Calls.TYPE).append(" = ").append(Calls.MISSED_TYPE);
        builder.append(" AND ").append(Calls.IS_READ).append(" = 0");

        Cursor cursor = mResolver.query(CallLog.Calls.CONTENT_URI,
                projection, builder.toString(), null, Calls.DEFAULT_SORT_ORDER);

        int missed = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                missed ++;
            }

            cursor.close();
        }
        return missed;
    }

    public List<CallInfo> getMissedCallInfo() {
        List<CallInfo> callInfos = new ArrayList<>();
        
        String[] projection = new String[] {
            Calls.CACHED_NAME,
            Calls.NUMBER,
            Calls.DATE
        };
        
        StringBuilder builder = new StringBuilder();
        builder.append(Calls.TYPE).append(" = ").append(Calls.MISSED_TYPE);
        builder.append(" AND ").append(Calls.IS_READ).append(" =0 ");
        
        Cursor cursor = mResolver.query(CallLog.Calls.CONTENT_URI,
                projection, builder.toString(), null, Calls.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CallInfo callInfo = new CallInfo();
                callInfo.setName(cursor.getString(cursor.getColumnIndex(Calls.CACHED_NAME)));
                callInfo.setNumber(cursor.getString(cursor.getColumnIndex(Calls.NUMBER)));
                callInfo.setDate(cursor.getString(cursor.getColumnIndex(Calls.DATE)));
                callInfos.add(callInfo);
            }
            cursor.close();
        }
        
        return callInfos;
    }
    
    private ContentObserver mMissCallObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            List<CallInfo> callInfos = getMissedCallInfo();
            int missed = callInfos.size();
            
            Logger.i(TAG, "[MissCallObserver.onChange]missed:%d, mMissedCall:%d",
                    missed, mMissedCall);
            if (mCallback != null && mMissedCall != missed) {
                mCallback.onMissedCallUpdate(callInfos);
            }
            
            if (mMissedCallbackList != null && mMissedCall != missed) {
                for (int i = 0; i < mMissedCallbackList.size(); i ++) {
                    IMissedCallback callback = mMissedCallbackList.get(i);
                    callback.onMissedCallUpdate(callInfos);
                }
            }
            mMissedCall = missed;
        }
    };

    public void addMissedCallback(IMissedCallback callback) {
        if (mMissedCallbackList == null) {
            mMissedCallbackList = new ArrayList<>();
        }
        mMissedCallbackList.add(callback);
    }
    
    public void removeMissedCallback(IMissedCallback callback) {
        if (mMissedCallbackList != null) {
            mMissedCallbackList.remove(callback);
        }
    }
    
    public interface IMissedCallback {
        void onMissedCallUpdate(List<CallInfo> callInfo);
    }
}
