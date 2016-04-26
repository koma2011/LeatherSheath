package com.ape.leather2.ui.page.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.ape.leather2.module.log.Logger;
import com.ape.leather2.ui.page.AbsLeatherPage;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author juan.li
 * @date Dec 3, 2015 9:36:37 AM
 */
public abstract class AbsPageAdapter extends PagerAdapter {
    
    protected static final String TAG = AbsPageAdapter.class.getName();
    
    private Map<Integer, AbsLeatherPage> mLeatherPageMap = new TreeMap<>();
    private Map<String, View> mPageViewMap = new HashMap<>();
    
    public abstract AbsLeatherPage getItem(int position);
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int itemId = getItemId(position);
        
        AbsLeatherPage page = mLeatherPageMap.get(itemId);
        Logger.i(TAG, "[instantiateItem]get page from map, position:%d, itemId:%s, page:%s",
                position, itemId, page);
        if (page == null) {
            page = getItem(position);
            if (page.getTag() == null) {
                throw new IllegalArgumentException("Page's id must be set!!!");
            }
            Logger.i(TAG, "[instantiateItem]page not in map, instance and put in page map, page tag:%s", page.getTag());
            mLeatherPageMap.put(itemId, page);
        } else {
            View view = mPageViewMap.get(page.getTag());
            container.removeView(view);
            mLeatherPageMap.remove(itemId);
            mPageViewMap.remove(page.getTag());
        }
        
        View view = mPageViewMap.get(page.getTag());
        if (view == null) {
            Logger.i(TAG, "[instantiateItem]inflate view, page tag:%s", page.getTag());
            view = page.onCreate(container);
            mPageViewMap.put(page.getTag(), view);
        }
        
        Logger.i(TAG, "[instantiateItem]PM_size:%d, VM_size:%d, p:%d, Id:%s, c_count:%d, page:%s",
                mLeatherPageMap.size(), mPageViewMap.size(), position, itemId, container.getChildCount(), page.getTag());
        
        container.addView(view);
        
        return view;
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        int itemId = getItemId(position);
        AbsLeatherPage page = mLeatherPageMap.get(itemId);
        if (page != null) {
            Logger.i(TAG, "[destroyItem]position:%d, itemId:%s, page:%s",
                    position, itemId, page.getTag());
            View view = mPageViewMap.get(page.getTag());
            if (view != null) {
                container.removeView(view);
                mPageViewMap.remove(page.getTag());
            }
            mLeatherPageMap.remove(itemId);
//            page.onDestory();
        }
    }
    
    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
    
    public int getItemId(int position) {
        return position;
    }
}
