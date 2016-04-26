package com.ape.leather2.ui.view.panel;

import java.util.ArrayList;
import java.util.List;

import com.ape.leather2.R;
import com.ape.leather2.common.indicator.CirclePageIndicator;
import com.ape.leather2.common.indicator.LinePageIndicator;
import com.ape.leather2.module.log.Logger;
import com.ape.leather2.module.mms.MmsManager;
import com.ape.leather2.module.mms.MmsManager.IMmsCallback;
import com.ape.leather2.module.telephone.CallInfo;
import com.ape.leather2.module.telephone.MissedCallManager;
import com.ape.leather2.module.telephone.MissedCallManager.IMissedCallback;
import com.ape.leather2.ui.page.AbsLeatherPage;
import com.ape.leather2.ui.page.LeatherPage;
import com.ape.leather2.ui.page.MusicPage;
import com.ape.leather2.ui.page.NoticePage;
import com.ape.leather2.ui.page.adapter.LeatherPageAdapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * @author juan.li
 * @date Dec 3, 2015 3:32:27 PM
 */
public class MasterPanel extends LinearLayout implements IMmsCallback, IMissedCallback, 
        ViewPager.OnPageChangeListener {
    
    private static final String TAG = MasterPanel.class.getName();
    
    private Context mContext;
    private ViewPager mViewPager;
//    private CirclePageIndicator mIndicator;
    private LinePageIndicator mIndicator;
    private LeatherPageAdapter mAdapter;
    
    private LeatherPage mLeatherPage;
    private MusicPage mMusicPage;
    private NoticePage mNoticePage;
    private int mCurrentPageIndex;
    
    private List<AbsLeatherPage> mPageList;
    
    private MmsManager mMmsManager;
    private MissedCallManager mMissedCallManager;
    
    public MasterPanel(Context context) {
        this(context, null, 0);
    }
    
    public MasterPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public MasterPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
        mContext = context;
        
        LayoutInflater.from(context).inflate(R.layout.panel_master_layout, this, true);
        
        mMmsManager = MmsManager.getInstance(context);
        mMissedCallManager = MissedCallManager.getInstance(context);
        
        mMmsManager.addMmsCallback(this);
        mMissedCallManager.addMissedCallback(this);
        
        initViews();
    }
    
    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
//        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator = (LinePageIndicator) findViewById(R.id.indicator);
        
        mNoticePage = new NoticePage(mContext);
        mLeatherPage = new LeatherPage(mContext);
        mMusicPage = new MusicPage(mContext);
        
        if (mAdapter == null) {
            mAdapter = new LeatherPageAdapter(this);
        }
        
        if (mPageList == null) {
            mPageList = new ArrayList<>();
        } else {
            mPageList.clear();
        }
        
        int unread = mMmsManager.getUnread();
        int missed = mMissedCallManager.getMissedCall();
        
        if (unread > 0 || missed > 0) {
            mPageList.add(mNoticePage);
        }
        mPageList.add(mLeatherPage);
        mPageList.add(mMusicPage);
        
        mAdapter.setPageList(mPageList);
        mViewPager.setAdapter(mAdapter);
        
        mCurrentPageIndex = getMasterPageIndex();
        int current = mViewPager.getCurrentItem();
        Logger.i(TAG, "[initViews]current:%d", current);
        mViewPager.setOnPageChangeListener(this);
//        mViewPager.addOnPageChangeListener(this);
        mIndicator.setViewPager(mViewPager);
//        mIndicator.setSnap(true);
        mIndicator.setCurrentItem(mCurrentPageIndex);
        mViewPager.setCurrentItem(mCurrentPageIndex, false);
    }

    private void resume() {
        mPageList.clear();
        
        int unread = mMmsManager.getUnread();
        int missed = mMissedCallManager.getMissedCall();
        
        if (unread > 0 || missed > 0) {
            mPageList.add(mNoticePage);
        }
        mPageList.add(mLeatherPage);
        mPageList.add(mMusicPage);
        
        mAdapter.setPageList(mPageList);
        mCurrentPageIndex = getMasterPageIndex();
        mIndicator.setCurrentItem(mCurrentPageIndex);
        mViewPager.setCurrentItem(mCurrentPageIndex, false);
        int current = mViewPager.getCurrentItem();
        Logger.i(TAG, "[resume]current:%d", current);
    }
    
    public int getMasterPageIndex() {
        if (mPageList.size() >= 3) {
            return 1;
        } else {
            return 0;
        }
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        mMmsManager.open();
//        mMissedCallManager.open();
        resume();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        mMmsManager.close();
//        mMissedCallManager.close();
    }

    @Override
    public void onMissedCallUpdate(List<CallInfo> callInfo) {
        resume();
    }

    @Override
    public void onMmsUnreadUpdate(int unread) {
        resume();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Logger.i(TAG, "[onPageSelected]position:%d", position);
        mCurrentPageIndex = position;
    }
}
