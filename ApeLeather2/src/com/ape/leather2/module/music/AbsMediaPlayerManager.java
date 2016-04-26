package com.ape.leather2.module.music;

import com.ape.leather2.R;
import com.ape.leather2.module.log.Logger;
import com.ape.leather2.module.model.Command;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

/**
 * @author juan.li
 * @date 2015-11-13 17:53:00
 */
public abstract class AbsMediaPlayerManager {

    protected static final String TAG = AbsMediaPlayerManager.class.getName();

    protected static final int ELAPSE_TIME                    = 500;

    public static final int MESSAGE_MUSIC_PLAY              = 0x1000; // 4096
    public static final int MESSAGE_MUSIC_PAUSE             = 0x1001; // 4097
    public static final int MESSAGE_MUSIC_NEXT              = 0x1002; // 4098
    public static final int MESSAGE_MUSIC_STATE_CHANGED     = 0x1003; // 4099
    public static final int MESSAGE_MUSIC_META_CHANGED      = 0x1004; // 4100
    public static final int MESSAGE_MUSIC_PLAY_PAUSE        = 0x1005; // 4101

    private static final String ACTION_PLAYSTATE_CHANGED = "com.android.music.playstatechanged";
    private static final String ACTION_META_CHANGED = "com.android.music.metachanged";
    private static final String ACTION_OLD_PLAY_PAUSE = "PLAY_PAUSE";
    private static final String ACTION_NEW_PLAY_PAUSE = "com.android.music.play_pause";

    protected Context mContext;
    private IMediaPlayerCallbacks mCallback;
    private MediaStateChangeReceiver mChangeReceiver;

    public enum MediaState {
        PLAY, PAUSE, NEXT, PREV
    }

    private static int[] MEDIA_ICON = {
//            R.drawable.ic_cell_music_play,
//            R.drawable.ic_cell_music_pause,
//            R.drawable.ic_cell_music_next
    };

    private static Command[] COMMAND = {
            new Command(MESSAGE_MUSIC_PLAY).setElapse(ELAPSE_TIME),
            new Command(MESSAGE_MUSIC_PAUSE).setElapse(ELAPSE_TIME),
            new Command(MESSAGE_MUSIC_NEXT).setElapse(ELAPSE_TIME)
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Logger.i(TAG, "[handleMessage]what:%d", msg.what);
            switch (msg.what) {
                case MESSAGE_MUSIC_PLAY:
                    play();
                    break;
                case MESSAGE_MUSIC_PAUSE:
                    pause();
                    break;
                case MESSAGE_MUSIC_NEXT:
                    next();
                    break;

                case MESSAGE_MUSIC_STATE_CHANGED:
                case MESSAGE_MUSIC_META_CHANGED:
                case MESSAGE_MUSIC_PLAY_PAUSE:
                    if (mCallback != null) {
                        if (isPlaying()) {
                            mCallback.onMediaStateUpdate(MediaState.PAUSE);
                        } else {
                            mCallback.onMediaStateUpdate(MediaState.PLAY);
                        }
                    }
                    break;
            }
        }
    };

    private class MediaStateChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.i(TAG, "[MediaStateChangeReceiver.onReceive]Action:%s", intent.getAction());
            if (ACTION_PLAYSTATE_CHANGED.equals(intent.getAction())) {
                mHandler.sendEmptyMessage(MESSAGE_MUSIC_STATE_CHANGED);
            } else if (ACTION_META_CHANGED.equals(intent.getAction())) {
                mHandler.sendEmptyMessage(MESSAGE_MUSIC_META_CHANGED);
            } else if (ACTION_OLD_PLAY_PAUSE.equals(intent.getAction()) ||
                    ACTION_NEW_PLAY_PAUSE.equals(intent.getAction())) {
                mHandler.sendEmptyMessage(MESSAGE_MUSIC_PLAY_PAUSE);
            }
        }
    }

    public AbsMediaPlayerManager(Context context) {
        mContext = context;
    }

    public void setCallback(IMediaPlayerCallbacks callback) {
        this.mCallback = callback;
    }

    public void open() {
        if (mChangeReceiver == null) {
            mChangeReceiver = new MediaStateChangeReceiver();
        }

        mChangeReceiver = new MediaStateChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAYSTATE_CHANGED);
        filter.addAction(ACTION_META_CHANGED);
        filter.addAction(ACTION_OLD_PLAY_PAUSE);
        filter.addAction(ACTION_NEW_PLAY_PAUSE);
        mContext.registerReceiver(mChangeReceiver, filter);

        bindService();
    }

    public void close() {
        if (mChangeReceiver != null) {
            mContext.unregisterReceiver(mChangeReceiver);
            mChangeReceiver = null;
        }

        unbindService();
    }

    public abstract void bindService();

    public abstract void unbindService();

    public abstract boolean isPlaying();

    public abstract void pause();

    public abstract void play();

    public abstract void prev();

    public abstract void next();

    public abstract String getTrack();
    
    public abstract String getArtist();
    
    public abstract long position();
    
    public abstract long duration();
    
    public Command getCommand(MediaState state) {
        return COMMAND[state.ordinal()].add(mHandler);
    }

    public int getIcon(MediaState state) {
        return MEDIA_ICON[state.ordinal()];
    }
}
