package com.ape.leather2.module.telephone;

import com.ape.leather2.R;
import com.ape.leather2.module.log.Logger;
import com.ape.leather2.module.model.Command;
import com.ape.leather2.ui.LeatherViewManager;
import com.ape.leather2.utility.TimeUtil;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;

/**
 * @author juan.li
 * @date 2015-10-17 11:51:00
 */
public class InCallingManager {

    private static final String TAG = InCallingManager.class.getName();
    private static final String KOMA_TAG = "KomaLeatherSheath";
    private static InCallingManager sInCallingManager;

    public static final int MESSAGE_CALLING_ANSWER = 0x1000;    // 4096
    public static final int MESSAGE_CALLING_CALLING = 0x1001;    // 4097
    public static final int MESSAGE_CALLING_ENDCALl = 0x1002;    // 4098
    public static final int MESSAGE_CALLING_SILENCE_ON = 0x1003;    // 4099
    public static final int MESSAGE_CALLING_SILENCE_OFF = 0x1004;    // 4100
    public static final int MESSAGE_CALLING_SPEAK_ON = 0x1005;    // 4101
    public static final int MESSAGE_CALLING_SPEAK_OFF = 0x1006;    // 4102
    public static final int MESSAGE_CALLING_TIME_RUNNING = 0x1007;    // 4103

    public enum CallState {
        ANSWER(0),
        ENDCALL(1),
        SILENCE_ON(2),
        SILENCE_OFF(3),
        SPEAK_ON(4),
        SPEAK_OFF(5),
        RINGING(6),
        DIALING(7),
        MULTICALL(8),
        NONE(9);

        private int index;

        CallState(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    private Context mContext;
    private String mNumber;
    private String mDisplayName;
    private String mTime;
    private ContentResolver mResolver;
    private TelephonyReflect mReflect;
    private IPhoneStateCallback mCallback;
    private AudioManager mAudioManager;
    private CallStateReceiver mCallStateReceiver;

    private int mCallCount;
    private boolean mIsMultiCall;

    private CallState mState = CallState.NONE;

    private static int[] INCALLING_ICON = {
//            R.drawable.ic_cell_phone_answer,
//            R.drawable.ic_cell_phone_endcall,
//            R.drawable.ic_cell_phone_silence_on,
//            R.drawable.ic_cell_phone_endcall, // ic_cell_phone_silence_off
//            R.drawable.ic_cell_phone_speak_on,
//            R.drawable.ic_cell_phone_speak_off
    };

    private static Command[] COMMAND = {
            new Command(MESSAGE_CALLING_ANSWER),
            new Command(MESSAGE_CALLING_ENDCALl),
            new Command(MESSAGE_CALLING_SILENCE_ON),
            new Command(MESSAGE_CALLING_SILENCE_OFF),
            new Command(MESSAGE_CALLING_SPEAK_ON),
            new Command(MESSAGE_CALLING_SPEAK_OFF)
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Logger.i(KOMA_TAG, TAG + "[handleMessage]what:%d", msg.what);
            switch (msg.what) {

                case MESSAGE_CALLING_ANSWER:
                    //mReflect.answerRingingCall();
                    CallList.getInstance(mContext).answerIncomingCall();
                    break;

                case MESSAGE_CALLING_CALLING: {
                    mState = CallState.ANSWER;
                    if (mCallback != null) {
                        mCallback.onPhoneStateUpdate(CallState.ANSWER);
                    }
                    break;
                }

                case MESSAGE_CALLING_ENDCALl:
                    mReflect.endCall();
                    turnOnSpeaker(false);
                    mState = CallState.ENDCALL;
                    if (mCallback != null) {
                        mCallback.onPhoneStateUpdate(CallState.ENDCALL);
                    }
                    break;

                case MESSAGE_CALLING_SILENCE_ON:
                    mReflect.silenceRinger();
                    mState = CallState.SILENCE_ON;
                    if (mCallback != null) {
                        mCallback.onPhoneStateUpdate(CallState.SILENCE_ON);
                    }
                    break;

                case MESSAGE_CALLING_SILENCE_OFF:
                    //mState = CallState.SILENCE_OFF;
                    // to end call after ring off.
                    mReflect.endCall();
                    turnOnSpeaker(false);
                    mState = CallState.ENDCALL;
                    if (mCallback != null) {
                        mCallback.onPhoneStateUpdate(CallState.ENDCALL);
                    }
                    break;

                case MESSAGE_CALLING_SPEAK_ON:
                    turnOnSpeaker(true);
                    //mState = CallState.SPEAK_ON;
                    if (mCallback != null) {
                        mCallback.onPhoneStateUpdate(CallState.SPEAK_ON);
                    }
                    break;

                case MESSAGE_CALLING_SPEAK_OFF:
                    turnOnSpeaker(false);
                    //mState = CallState.SPEAK_OFF;
                    if (mCallback != null) {
                        mCallback.onPhoneStateUpdate(CallState.SPEAK_OFF);
                    }
                    break;

                case MESSAGE_CALLING_TIME_RUNNING:
                    mTime = (String) msg.obj;
                    if (mCallback != null) {
                        mCallback.onTimerRunning(mTime);
                    }
                    break;
            }
        }
    };

    public static InCallingManager getInstance(Context context) {
        if (sInCallingManager == null) {
            sInCallingManager = new InCallingManager(context);
        }
        return sInCallingManager;
    }

    private InCallingManager(Context context) {
        Logger.i(KOMA_TAG, TAG + "[InCallingManager]registerReceiver");
        mContext = context;
        mReflect = TelephonyReflect.getInstance(context);
        mResolver = mContext.getContentResolver();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        mCallStateReceiver = new CallStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CallUtils.ACTION_CALL_STATE_CHANGED);
        filter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        filter.addAction(CallUtils.ACTION_SILENT_RING);
        mContext.registerReceiver(mCallStateReceiver, filter);
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void setCallback(IPhoneStateCallback callback) {
        mCallback = callback;
    }

    public void ringing(String number) {
        mState = CallState.RINGING;
        mNumber = number;
        mDisplayName = getDisplayName(number);
    }

    public void dialing(String number) {
        mState = CallState.DIALING;
        mNumber = number;
        mDisplayName = getDisplayName(number);
    }

    public CallState getCallStatus() {
        Logger.i(KOMA_TAG, "-------nthpower[getCallStatus]status:%s", mState);
        return mState;
    }

    public String getTime() {
        return mTime;
    }

    public void offHook() {
        mHandler.sendEmptyMessage(MESSAGE_CALLING_CALLING);
    }

    public void endCall() {
        mHandler.sendEmptyMessage(MESSAGE_CALLING_ENDCALl);
    }

    public void answer() {
        mHandler.sendEmptyMessage(MESSAGE_CALLING_ANSWER);
    }

    public boolean isOffHook() {
        return mReflect.isOffhook();
    }

    public String getDisplayName() {
        if (TextUtils.isEmpty(mDisplayName)) {
            mDisplayName = getDisplayName(mNumber);
        }
        return mDisplayName;
    }

    public String getNumber() {
        return mNumber;
    }

    public String getDisplayName(String number) {

        String[] projection = {PhoneLookup.DISPLAY_NAME, PhoneLookup.NUMBER};
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = mResolver.query(uri, projection, null, null, null);

        String displayName = number;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            displayName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
            cursor.close();
        }

        if (displayName == null) {
            displayName = mContext.getString(R.string.unknown_calling);
        }
        return displayName;
    }

    public int getIcon(CallState state) {
        return INCALLING_ICON[state.ordinal()];
    }

    public Command getCommand(CallState state) {
        return COMMAND[state.ordinal()].add(mHandler);
    }

    public interface IPhoneStateCallback {
        void onPhoneStateUpdate(CallState state);

        void onTimerRunning(String time);
    }

    private void turnOnSpeaker(boolean on) {
        Logger.i(KOMA_TAG, TAG + "turning speaker phone:%s, onOff:%s", mAudioManager.isSpeakerphoneOn(), on);
        if (mAudioManager.isSpeakerphoneOn() != on) {
            mAudioManager.setSpeakerphoneOn(on);
        }
    }

    // added by chb, for call state, begin.
    public boolean isCallSpeakerOn() {
        return mAudioManager.isSpeakerphoneOn();
    }

    private long startTime = 0;
    private CallTimer mCallTimer = new CallTimer(new Runnable() {

        @Override
        public void run() {
            long elapsed = SystemClock.elapsedRealtime() - startTime;
            String time = TimeUtil.elapsedTimer(elapsed);

            Message message = mHandler.obtainMessage();
            message.what = InCallingManager.MESSAGE_CALLING_TIME_RUNNING;
            message.obj = time;
            mHandler.sendMessage(message);
        }

    });

    private class CallStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.i(KOMA_TAG, "-------receive -----[CallStateReceiver.onReceive]action:%s", intent.getAction());
            String action = intent.getAction();
            if (CallUtils.ACTION_CALL_STATE_CHANGED.equals(action)) {
                int callState = intent.getIntExtra(CallUtils.KEY_STATE, 0);
                String number = intent.getStringExtra(CallUtils.KEY_NUMBER);
                long eventTime = intent.getLongExtra(CallUtils.KEY_TIME, 0);

                mCallCount = intent.getIntExtra(CallUtils.KEY_CALL_COUNT, 0);
                Logger.i(KOMA_TAG, "onReceive, callState:" + callState + ", number:" + number + ", mCallCount:" + mCallCount);
                if ((mCallCount > 1 || mIsMultiCall)
                        && !(mCallCount == 1 && callState == CallUtils.STATE_DISCONNECTED)) {
                    if (callState == CallUtils.STATE_CONNECTING
                            || callState == CallUtils.STATE_DISCONNECTED
                            || callState == CallUtils.STATE_HOLDING
                            || callState == CallUtils.STATE_RINGING) {
                        mCallTimer.cancel();
                        mIsMultiCall = true;
                        mState = CallState.MULTICALL;
                        if (mCallback != null) {
                            mCallback.onPhoneStateUpdate(mState);
                        }
                    }
                    return;
                } else {
                    mIsMultiCall = false;
                }

                LeatherViewManager leatherViewManager = LeatherViewManager.getInstance(mContext);
                if ((callState == CallUtils.STATE_CONNECTING ||
                        callState == CallUtils.STATE_ACTIVE ||
                        callState == CallUtils.STATE_RINGING ||
                        callState == CallUtils.STATE_DIALING) &&
                        leatherViewManager.isHallActive() && !leatherViewManager.isOpened()) {
                    leatherViewManager.openLeather();
                }

                switch (callState) {
                    case CallUtils.STATE_PRE_DIAL_WAIT:
                    case CallUtils.STATE_CONNECTING:
                        //case CallUtils.STATE_DIALING:
                        dialing(number);
                        leatherViewManager.openCallPanel();
                        break;

                    case CallUtils.STATE_ACTIVE:
                        startTime = eventTime; //SystemClock.elapsedRealtime();
                        mCallTimer.start(1000);
                        offHook();
                        break;

                    case CallUtils.STATE_DISCONNECTING:
                    case CallUtils.STATE_DISCONNECTED:
                        if (mState != CallState.NONE) {
                            mCallTimer.cancel();
                            turnOnSpeaker(false);
                            mState = CallState.NONE;
                            mCallCount = 0;
                            if (mCallback != null) {
                                mCallback.onPhoneStateUpdate(CallState.ENDCALL);
                            }
                            leatherViewManager.closeCallPanel();
                        }
                        break;

                    case CallUtils.STATE_RINGING:
                        Logger.i(KOMA_TAG,"-------ringing----");
                        ringing(number);
                        leatherViewManager.openCallPanel();
                        break;
                }
            } else if (AudioManager.ACTION_HEADSET_PLUG.equals(action)) {
                boolean speakerOn = isCallSpeakerOn();
                int headsetState = intent.getIntExtra("state", 0);
                Logger.i(KOMA_TAG, "ACTION_HEADSET_PLUG, speakerOn:" + speakerOn + ", headsetState:" + headsetState);
            } else if (CallUtils.ACTION_SILENT_RING.equals(action)) {
                updateSienceState();
            }
        }
    }

    ;

    public void updateSienceState() {
        if (mState == CallState.RINGING) {
            mState = CallState.SILENCE_ON;
            if (mCallback != null) {
                mCallback.onPhoneStateUpdate(CallState.SILENCE_ON);
            }
        }
    }

    public int getCallCount() {
        return mCallCount;
    }
    // added by chb, for call state, end.
}
