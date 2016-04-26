package com.ape.leather2.ui.view;

import com.ape.leather2.R;
import com.ape.leather2.module.log.Logger;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * @author juan.li
 * @date Dec 3, 2015 11:29:28 AM
 */
public class ContainerView extends RelativeLayout {
    
    private static final String TAG = ContainerView.class.getName();
    
    private static final int INDEX_CONTAINER                            = 0x01;
    
    private RelativeLayout mContainer;
    private View mContentView;
    
    public ContainerView(Context context) {
        this(context, null, 0);
    }
    
    public ContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public ContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
        TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.ContainerView, defStyleAttr, 0);
        
        int width = typedArray.getDimensionPixelSize(R.styleable.ContainerView_container_width, -1);
        int height = typedArray.getDimensionPixelSize(R.styleable.ContainerView_container_height, -1);
        int x = typedArray.getDimensionPixelSize(R.styleable.ContainerView_container_x, -1);
        int y = typedArray.getDimensionPixelSize(R.styleable.ContainerView_container_y, -1);
        
        typedArray.recycle();
        
        LayoutParams containerParams = new LayoutParams(width, height);
        containerParams.setMargins(x, y, 0, 0);
        
        mContainer = new RelativeLayout(context);
        mContainer.setId(INDEX_CONTAINER);
        mContainer.setGravity(Gravity.CENTER);
        addView(mContainer, containerParams);

        mContentView = null;
    }
    
    public void setSize(int width, int height) {
        LayoutParams params = (LayoutParams) mContainer.getLayoutParams();
        params.width = width;
        params.height = height;
        mContainer.setLayoutParams(params);
    }
    
    public void setPoint(int x, int y) {
        LayoutParams params = (LayoutParams) mContainer.getLayoutParams();
        params.leftMargin = x;
        params.topMargin = y;
        mContainer.setLayoutParams(params);
    }
    
    public void replace(View view) {
        if (mContentView != null) {
            mContainer.removeView(mContentView);
        }
        
        mContentView = view;
        
        LayoutParams params = (LayoutParams) mContainer.getLayoutParams();
        ViewGroup.LayoutParams contentParams = mContentView.getLayoutParams();
        if (contentParams == null) {
            contentParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        contentParams.height = params.height;
        contentParams.width = params.width;
        
        mContainer.addView(mContentView, contentParams);
        Logger.i(TAG, "nthpower[replace]replace view finish~~");
    }
}
