package com.ape.leather2.module.telephone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.telecom.Phone;
import android.telecom.InCallService.VideoCall;
import android.util.Log;

public class CallList {
    
    private static final String TAG = "MyCallList";
    
    private Context mContext;
    private Phone mPhone;
    private int mCallCount;
    
    private ArrayList<android.telecom.Call> mInComingCallList = new ArrayList<android.telecom.Call>();
    
    private static CallList sInstance;
    
    private CallList(Context context) {
        mContext = context;
    }
    
    public static synchronized CallList getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CallList(context);
        }
        return sInstance;
    }
    
    public void setPhone(Phone phone) {
        mPhone = phone;
        mPhone.addListener(mPhoneListener);
    }
    
    public void clearPhone() {
        mPhone.removeListener(mPhoneListener);
        mPhone = null;
    }
    
    private Phone.Listener mPhoneListener = new Phone.Listener() {
        
        @Override
        public void onCallAdded(Phone phone, android.telecom.Call telecommCall) {
            Log.d(TAG, "onCallAdded,  " + callToString(telecommCall));
            mCallCount++;
            
            int state = telecommCall.getState();
            android.telecom.Call.Details details = telecommCall.getDetails();
            telecommCall.addListener(mTelecommCallListener);
            dispatchCallState(telecommCall);

            updateIncomingCall(telecommCall);
        }
        
        @Override
        public void onCallRemoved(Phone phone, android.telecom.Call telecommCall) {
            Log.d(TAG, "onCallRemoved.");
            mCallCount--;
            updateIncomingCall(telecommCall);
        }
    };
    
    public String getNumber(android.telecom.Call.Details details) {
        if (details.getGatewayInfo() != null) {
            return details.getGatewayInfo().getOriginalAddress().getSchemeSpecificPart();
        } else {
            Uri handle = details.getHandle();
            return handle == null ? null
                    : details.getHandle().getSchemeSpecificPart();
        }
    }
    
    private android.telecom.Call.Listener mTelecommCallListener = new android.telecom.Call.Listener() {
        
        @Override
        public void onStateChanged(android.telecom.Call call, int newState) {
            Log.d(TAG, "onStateChanged, " + callToString(call));
            update();
            dispatchCallState(call);
        }
        
        @Override
        public void onParentChanged(android.telecom.Call call,
                android.telecom.Call newParent) {
            Log.d(TAG, "onParentChanged, " + callToString(call));
            update();
        }
        
        @Override
        public void onChildrenChanged(android.telecom.Call call,
                List<android.telecom.Call> children) {
            Log.d(TAG, "onChildrenChanged, " + callToString(call));
            update();
        }
        
        @Override
        public void onDetailsChanged(android.telecom.Call call,
                android.telecom.Call.Details details) {
            Log.d(TAG, "onDetailsChanged, " + callToString(call));
            update();
            // handleDetailsChanged(details);
        }
        
        @Override
        public void onCannedTextResponsesLoaded(android.telecom.Call call,
                List<String> cannedTextResponses) {
            Log.d(TAG, "onCannedTextResponsesLoaded, " + callToString(call));
            update();
        }
        
        @Override
        public void onPostDialWait(android.telecom.Call call,
                String remainingPostDialSequence) {
            Log.d(TAG, "onPostDialWait, " + callToString(call));
            update();
        }
        
        @Override
        public void onVideoCallChanged(android.telecom.Call call,
                VideoCall videoCall) {
            update();
        }
        
        @Override
        public void onCallDestroyed(android.telecom.Call call) {
            Log.d(TAG, "onCallDestroyed, " + callToString(call));
            call.removeListener(mTelecommCallListener);
        }
        
        @Override
        public void onConferenceableCallsChanged(android.telecom.Call call,
                List<android.telecom.Call> conferenceableCalls) {
            Log.d(TAG, "onConferenceableCallsChanged, " + callToString(call));
            update();
        }
    };
    
    private String callToString(android.telecom.Call call) {
        android.telecom.Call.Details details = call.getDetails();
        
        StringBuilder value = new StringBuilder();
        value.append("call, state:").append(call.getState());
        value.append(", getConnectTimeMillis:").append(details.getConnectTimeMillis());
        value.append(", getNumber:").append(getNumber(details));
        
        return value.toString();
    }
    
    private void update() {
        
    }

    private void dispatchCallState(android.telecom.Call call) {
        Log.i(TAG, "dispatchCallState, " + callToString(call));
        android.telecom.Call.Details details = call.getDetails();
        
        Intent intent = new Intent(CallUtils.ACTION_CALL_STATE_CHANGED);
        intent.putExtra(CallUtils.KEY_NUMBER, getNumber(details));
        intent.putExtra(CallUtils.KEY_STATE, call.getState());
        intent.putExtra(CallUtils.KEY_TIME, SystemClock.elapsedRealtime());
        intent.putExtra(CallUtils.KEY_CALL_COUNT, mCallCount);
        mContext.sendBroadcast(intent);
    }

    public boolean answerIncomingCall() {
        boolean retValue = false;
        Log.i(TAG, "answerIncomingCall, mInComingCallList.size:" + mInComingCallList.size());
        if (mInComingCallList.size() >= 1) {
            android.telecom.Call call = mInComingCallList.get(0);
            if (call != null) {
                Log.i(TAG, "answerIncomingCall, incomingCall:" + callToString(call));
                call.answer(0);
                retValue = true;
            }
            mInComingCallList.remove(0);
        }

        return retValue;
    }

    private void updateIncomingCall(android.telecom.Call call) {
        Log.i(TAG, "updateIncomingCall, mInComingCallList.size:" + mInComingCallList.size()
                + ", call:" + callToString(call));

        if (call.getState() == CallUtils.STATE_RINGING && !mInComingCallList.contains(call)) {
            mInComingCallList.add(0, call);
        } else {
            Iterator<android.telecom.Call> it = mInComingCallList.iterator();
            while (it.hasNext()) {
                android.telecom.Call myCall = it.next();
                if (call.equals(myCall)) {
                    Log.i(TAG, "updateIncomingCall, (remove)- ");
                    it.remove();
                }
            }
        }
    }
}
