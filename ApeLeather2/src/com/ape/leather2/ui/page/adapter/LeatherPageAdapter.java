package com.ape.leather2.ui.page.adapter;

import java.util.ArrayList;
import java.util.List;

import com.ape.leather2.module.log.Logger;
import com.ape.leather2.ui.page.AbsLeatherPage;

import android.view.ViewGroup;

/**
 * @author juan.li
 * @date Dec 3, 2015 10:56:06 AM
 */
public class LeatherPageAdapter extends AbsPageAdapter {
    
    private static final String TAG = LeatherPageAdapter.class.getName();
    
    private List<AbsLeatherPage> mPageList = new ArrayList<>();
    
    private ViewGroup mParent;
    
    public LeatherPageAdapter(ViewGroup parent) {
        mParent = parent;
    }
    
    public void setPageList(List<AbsLeatherPage> list) {
        mPageList = list;
        Logger.i(TAG, "[setPageList]page size:%d", mPageList.size());
        notifyDataSetChanged();
    }

    @Override
    public AbsLeatherPage getItem(int position) {
        return mPageList.get(position);
    }
    
    @Override
    public int getCount() {
        return mPageList.size();
    }
}
