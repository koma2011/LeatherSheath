package com.ape.leather2.ui.view.panel;

import com.ape.leather2.R;
import com.ape.leather2.module.geo.GeocodedLocation;
import com.ape.leather2.module.log.Logger;
import com.ape.leather2.module.telephone.InCallingManager;
import com.ape.leather2.module.telephone.InCallingManager.CallState;
import com.ape.leather2.module.telephone.InCallingManager.IPhoneStateCallback;
import com.ape.leather2.ui.multiwaveview.GlowPadWrapper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author juan.li
 * @date Dec 8, 2015 5:24:02 PM
 */
public class CallPanel extends LinearLayout implements GlowPadWrapper.AnswerListener, IPhoneStateCallback {
    
    private static final String TAG = CallPanel.class.getName();
    
    private Context mContext;
    
    private TextView mDisplayNameView;
    private TextView mLocationView;
    private TextView mCallStatusView;
    private TextView mElapsedTimeView;
    private GlowPadWrapper mGlowPadWrapper;
    
    private InCallingManager mInCallingManager;
    
    public CallPanel(Context context) {
        this(context, null, 0);
    }
    
    public CallPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public CallPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
        mContext = context;
        mInCallingManager = InCallingManager.getInstance(context);
        mInCallingManager.setCallback(this);
        
        LayoutInflater.from(context).inflate(R.layout.panel_call_layout, this, true);
        initViews();
    }
    
    private void initViews() {
        mDisplayNameView = (TextView) findViewById(R.id.name);
        mLocationView = (TextView) findViewById(R.id.location);
        mCallStatusView = (TextView) findViewById(R.id.status);
        mElapsedTimeView = (TextView) findViewById(R.id.elapsed);
        mGlowPadWrapper = (GlowPadWrapper) findViewById(R.id.glowpad);
        
        mGlowPadWrapper.setAnswerListener(this);
    }
    
    @Override
    protected void onAttachedToWindow() {
        Logger.i(TAG, "nthpower[onAttachedToWindow]");
        super.onAttachedToWindow();
        phoneStateUpdate(mInCallingManager.getCallStatus());
    }

    @Override
    protected void onDetachedFromWindow() {
        Logger.i(TAG, "nthpower[onDetachedFromWindow]");
        super.onDetachedFromWindow();
        mGlowPadWrapper = null;
    }

    @Override
    public void onAnswer(int videoState, Context context) {
        Logger.i(TAG, "nthpower[onAnswer]");
        mInCallingManager.answer();
    }

    @Override
    public void onDecline(Context context) {
        Logger.i(TAG, "nthpower[onDecline]");
        mInCallingManager.endCall();
    }

    @Override
    public void onText() {
        Logger.i(TAG, "nthpower[onText]");
    }
    
    @Override
    public void onPhoneStateUpdate(CallState state) {
        Logger.i(TAG, "nthpower[onPhoneStateUpdate]state:%s", state);
        phoneStateUpdate(state);
    }

    @Override
    public void onTimerRunning(String time) {
        mElapsedTimeView.setText(time);
    }
    
    private void showIncoming() {
        String displayName = mInCallingManager.getDisplayName();
        String number = mInCallingManager.getNumber();
        if (displayName.equals(number)) {
            GeocodedLocation location = GeocodedLocation.getLocation(mContext, number);
            if (location != null) {
                String address = location.getAreaCode().getAddress();
                mLocationView.setText(address);
                mLocationView.setVisibility(View.VISIBLE);
            } else {
                mLocationView.setVisibility(View.GONE);
            }
        } else {
            mLocationView.setText(number);
            mLocationView.setVisibility(View.VISIBLE);
        }
        mDisplayNameView.setText(displayName);
        mElapsedTimeView.setVisibility(View.GONE);
        if (mGlowPadWrapper == null) {
            mGlowPadWrapper = (GlowPadWrapper) findViewById(R.id.glowpad);
        }
        mGlowPadWrapper.stopPing();
        mGlowPadWrapper.setHandleDrawableImage(R.drawable.ic_in_call_touch_sub_handle);
        mGlowPadWrapper.setTargetResources(R.array.incoming_call_widget_2way_targets_sub);
        mGlowPadWrapper.setTargetDescriptionsResourceId(R.array.incoming_call_widget_2way_target_descriptions);
        mGlowPadWrapper.setDirectionDescriptionsResourceId(R.array.incoming_call_widget_2way_direction_descriptions);
        mGlowPadWrapper.resetChevronResources(R.array.incoming_call_chevron_2way_targets);
        mGlowPadWrapper.setEnableTarget(R.array.incoming_call_widget_2way_targets_sub, true);
        mGlowPadWrapper.setAnswerListener(this);
        mGlowPadWrapper.reset(false);
        mGlowPadWrapper.startPing();
    }
    
    private void showCalling(CallState state) {
        String displayName = mInCallingManager.getDisplayName();
        String number = mInCallingManager.getNumber();
        if (state == CallState.MULTICALL) {
            mDisplayNameView.setText(R.string.multiple_calls);
            mLocationView.setVisibility(View.GONE);
        } else {
            if (displayName.equals(number)) {
                GeocodedLocation location = GeocodedLocation.getLocation(mContext, number);
                if (location != null) {
                    String address = location.getAreaCode().getAddress();
                    mLocationView.setText(address);
                    mLocationView.setVisibility(View.VISIBLE);
                } else {
                    mLocationView.setVisibility(View.GONE);
                }
            } else {
                mLocationView.setText(number);
                mLocationView.setVisibility(View.VISIBLE);
            }
            mDisplayNameView.setText(displayName);
        }
        
        if (state == CallState.DIALING ||
                state == CallState.MULTICALL) {
            mElapsedTimeView.setVisibility(View.GONE);
        } else {
            mElapsedTimeView.setVisibility(View.VISIBLE);
        }
        
        if (mGlowPadWrapper == null) {
            mGlowPadWrapper = (GlowPadWrapper) findViewById(R.id.glowpad);
            mGlowPadWrapper.setVisibility(View.VISIBLE);
        } else {
            if (state == CallState.MULTICALL) {
                mGlowPadWrapper.setVisibility(View.GONE);
            } else {
                mGlowPadWrapper.setVisibility(View.VISIBLE);
            }
        }
        
        if (state == CallState.MULTICALL) {
            return;
        }
        
        mGlowPadWrapper.stopPing();
        mGlowPadWrapper.setHandleDrawableImage(R.drawable.ic_in_call_touch_sub_handle_callend);
        mGlowPadWrapper.setTargetResources(R.array.callscreen_widget_1way_targets_sub);
        mGlowPadWrapper.setTargetDescriptionsResourceId(R.array.callscreen_widget_1way_target_descriptions);
        mGlowPadWrapper.setDirectionDescriptionsResourceId(R.array.callscreen_widget_1way_direction_descriptions);
        mGlowPadWrapper.resetChevronResources(R.array.incoming_call_chevron_1way_targets);
        mGlowPadWrapper.setEnableTarget(R.array.callscreen_widget_1way_targets_sub, true);
        mGlowPadWrapper.setAnswerListener(this);
        mGlowPadWrapper.reset(false);
        mGlowPadWrapper.startPing();
    }
    
    private void phoneStateUpdate(CallState state) {
        Logger.i(TAG, "nthpower[phoneStateUpdate]state:%s", state);
        switch (state) {
            case RINGING:
                showIncoming();
                break;
                
            case DIALING:
            case ANSWER:
            case MULTICALL:
            case SILENCE_OFF:
            case SILENCE_ON:
            case SPEAK_ON:
            case SPEAK_OFF:
                showCalling(state);
                break;
                
            case ENDCALL:
                break;
                
            default:
                break;
        }
    }
}
