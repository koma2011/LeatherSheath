package com.ape.leather2.ui.page;

import java.util.Formatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.ape.leather2.R;
import com.ape.leather2.module.log.Logger;
import com.ape.leather2.module.music.AbsMediaPlayerManager.MediaState;
import com.ape.leather2.module.music.IMediaPlayerCallbacks;
import com.ape.leather2.module.music.MediaPlayerManager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author juan.li
 * @date Dec 3, 2015 3:42:42 PM
 */
public class MusicPage extends AbsLeatherPage implements
        OnClickListener, IMediaPlayerCallbacks {
    
    private static final String TAG = MusicPage.class.getName();
    
    private static final int MESSAGE_MUSIC_PROGRESS                 = 0x1000;
    
    private TextView mMusicNameView;
    private TextView mMusicArtistView;
    private TextView mCurrTimeView;
    private TextView mTotalTimeView;
    private ProgressBar mProgressBar;
    private ImageView mPrevButton;
    private ImageView mNextButton;
    private ImageView mPauseButton;
    
    private MediaPlayerManager mMediaPlayerManager;
    
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_MUSIC_PROGRESS:
                    playing();
                    break;
            }
        }
        
    };
    
    public MusicPage(Context context) {
        super(context);
    }

    @Override
    public View onCreate(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_music_layout, null);
        
        mMusicNameView = (TextView) view.findViewById(R.id.music_name);
        mMusicArtistView = (TextView) view.findViewById(R.id.music_artist);
        mCurrTimeView = (TextView) view.findViewById(R.id.current_time);
        mTotalTimeView = (TextView) view.findViewById(R.id.total_time);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mPrevButton = (ImageView) view.findViewById(R.id.btn_prev);
        mNextButton = (ImageView) view.findViewById(R.id.btn_next);
        mPauseButton = (ImageView) view.findViewById(R.id.btn_pause);
        
        mPrevButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);
        
        mMediaPlayerManager = MediaPlayerManager.getInstance(mContext);
        mMediaPlayerManager.setCallback(this);
        mMediaPlayerManager.open();
        updateMetaData();
        return view;
    }
    
//    @Override
//    public void onDestory() {
//        // TODO Auto-generated method stub
//        
//    }
    
    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public void onClick(View v) {
        if (v == mPrevButton) {
            mMediaPlayerManager.prev();
        } else if (v == mNextButton) {
            mMediaPlayerManager.next();
        } else if (v == mPauseButton) {
            if (mMediaPlayerManager.isPlaying()) {
                stopTimer();
                mMediaPlayerManager.pause();
            } else {
                resetTimer();
                mMediaPlayerManager.play();
            }
        }
    }
    
    @Override
    public void onMediaStateUpdate(MediaState state) {
        updateMetaData();
        playing();
    }
    
    private void resetTimer() {
        stopTimer();
        startTimer();
    }
    
    private void stopTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
    
    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(MESSAGE_MUSIC_PROGRESS);
                }
            };
        }
        
        mTimer.schedule(mTimerTask, 500, 500);
    }
    
    private void updateMetaData() {
        String track = mMediaPlayerManager.getTrack();
        String artist = mMediaPlayerManager.getArtist();
        
        if (!TextUtils.isEmpty(track)) {
            mMusicNameView.setText(track);
        } else {
            mMusicNameView.setText(mContext.getString(R.string.page_music_name));
        }
        
        if (!TextUtils.isEmpty(artist)) {
            mMusicArtistView.setText(artist);
        } else {
            mMusicArtistView.setText(mContext.getString(R.string.page_music_artist));
        }
        
        if (!mMediaPlayerManager.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.stat_btn_play);
        } else {
            mPauseButton.setImageResource(R.drawable.stat_btn_pause);
        }
    }
    
    @SuppressWarnings("resource")
    private String formatTime(long second) {
        String time = mContext.getString(second < 3600 ? R.string.durationformatshort
                : R.string.durationformatlong);
        
        final Object[] timeArgs = new Object[5];
        timeArgs[0] = second / 3600;
        timeArgs[1] = second / 60;
        timeArgs[2] = (second / 60) % 60;
        timeArgs[3] = second;
        timeArgs[4] = second % 60;
        
        return new Formatter(new StringBuilder(), Locale.getDefault()).format(time, timeArgs).toString();
    }
    
    private void playing() {
        long position = mMediaPlayerManager.position();
        long duration = mMediaPlayerManager.duration();
        Logger.i(TAG, "[playing]position:%d, duration:%d", position, duration);
        int progress = 0;
        if (position > 0 && duration > 0) {
            progress = (int) (1000 * position / duration);
        }
        mProgressBar.setProgress(progress);
        mCurrTimeView.setText(formatTime(position / 1000));
        mTotalTimeView.setText(formatTime(duration / 1000));
    }
}
