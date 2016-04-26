/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.ape.leather2.ui.multiwaveview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
//import android.telecom.VideoProfile;
import android.util.AttributeSet;
import android.view.View;


import com.ape.leather2.R;
import com.ape.leather2.module.log.Logger;

public class GlowPadWrapper extends GlowPadView implements GlowPadView.OnTriggerListener {

    private static final String TAG = GlowPadWrapper.class.getName();
    
    // Parameters for the GlowPadView "ping" animation; see triggerPing().
    private static final int PING_MESSAGE_WHAT = 101;
    private static final boolean ENABLE_PING_AUTO_REPEAT = true;
    private static final long PING_REPEAT_DELAY_MS = 1200;

    private final Handler mPingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PING_MESSAGE_WHAT:
                    triggerPing();
                    break;
            }
        }
    };

    private AnswerListener mAnswerListener;
    private boolean mPingEnabled = true;
    private boolean mTargetTriggered = false;

    public GlowPadWrapper(Context context) {
        super(context);
        Logger.d(TAG, "class created " + this + " ");
    }

    public GlowPadWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        Logger.d(TAG, "class created " + this);
    }

    @Override
    protected void onFinishInflate() {
        Logger.d(TAG, "onFinishInflate()");
        super.onFinishInflate();
        setOnTriggerListener(this);
    }

    public void startPing() {
        Logger.d(TAG, "startPing");
        mPingEnabled = true;
        triggerPing();
    }

    public void stopPing() {
        Logger.d(TAG, "stopPing");
        mPingEnabled = false;
        mPingHandler.removeMessages(PING_MESSAGE_WHAT);
    }

    private void triggerPing() {
        Logger.d(TAG, "triggerPing(): " + mPingEnabled + " " + this);
        if (mPingEnabled && !mPingHandler.hasMessages(PING_MESSAGE_WHAT)) {
            ping();

            if (ENABLE_PING_AUTO_REPEAT) {
                mPingHandler.sendEmptyMessageDelayed(PING_MESSAGE_WHAT, PING_REPEAT_DELAY_MS);
            }
        }
    }

    @Override
    public void onGrabbed(View v, int handle) {
        Logger.d(TAG, "onGrabbed()");
        stopPing();
    }

    @Override
    public void onReleased(View v, int handle) {
        Logger.d(TAG, "onReleased()");
        if (mTargetTriggered) {
            mTargetTriggered = false;
        } else {
            startPing();
        }
    }

    @Override
    public void onTrigger(View v, int target) {
        Logger.d(TAG, "onTrigger() view=" + v + " target=" + target);
        final int resId = getResourceIdForTarget(target);
        switch (resId) {
            case R.drawable.ic_lockscreen_answer_sub:
//                mAnswerListener.onAnswer(VideoProfile.VideoState.AUDIO_ONLY, getContext());
                mAnswerListener.onAnswer(0, getContext());
                mTargetTriggered = true;
                break;
            case R.drawable.ic_lockscreen_decline_sub:
                mAnswerListener.onDecline(getContext());
                mTargetTriggered = true;
                break;
            default:
                // Code should never reach here.
                Logger.e(TAG, "Trigger detected on unhandled resource. Skipping.");
        }
    }

    @Override
    public void onGrabbedStateChange(View v, int handle) {

    }

    @Override
    public void onFinishFinalAnimation() {

    }

    public void setAnswerListener(AnswerListener listener) {
        mAnswerListener = listener;
    }

    public interface AnswerListener {
        void onAnswer(int videoState, Context context);
        void onDecline(Context context);
        void onText();
    }
}
