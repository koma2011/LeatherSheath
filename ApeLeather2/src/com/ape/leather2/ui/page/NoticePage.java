package com.ape.leather2.ui.page;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.ape.leather2.R;
import com.ape.leather2.module.log.Logger;
import com.ape.leather2.module.mms.MmsManager;
import com.ape.leather2.module.mms.MmsManager.IMmsCallback;
import com.ape.leather2.module.telephone.CallInfo;
import com.ape.leather2.module.telephone.MissedCallManager;
import com.ape.leather2.module.telephone.MissedCallManager.IMissedCallback;
import com.ape.leather2.module.time.DateTimeManager;
import com.ape.leather2.ui.LeatherViewManager;
import com.ape.leather2.ui.activity.LeatherActivity;
import com.ape.leather2.utility.LeatherUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author juan.li
 * @date Dec 3, 2015 3:43:35 PM
 */
public class NoticePage extends AbsLeatherPage implements IMissedCallback, IMmsCallback {
    
    private static final String TAG = NoticePage.class.getName();
    
    private View mUnreadLayout;
    private View mDivideView;
    private ImageView mUnreadImage;
    private TextView mUnreadHintView;
    private ListView mMissedListView;
    
    private MmsManager mMmsManager;
    private MissedCallManager mMissedCallManager;
    private MissedListAdapter mAdapter;
    private List<CallInfo> mCallInfoList;
    
    public NoticePage(Context context) {
        super(context);
    }

    @Override
    public View onCreate(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_notice_layout, null);
        
        mUnreadLayout = view.findViewById(R.id.unread_layout);
        mDivideView = view.findViewById(R.id.divide);
        mUnreadImage = (ImageView) view.findViewById(R.id.unread);
        mUnreadHintView = (TextView) view.findViewById(R.id.message_hint);
        mMissedListView = (ListView) view.findViewById(R.id.missed_list);
        
        mMmsManager = MmsManager.getInstance(mContext);
        mMissedCallManager = MissedCallManager.getInstance(mContext);
        mMmsManager.addMmsCallback(this);
        mMissedCallManager.addMissedCallback(this);
        
        mCallInfoList = mMissedCallManager.getMissedCallInfo();
        
        mMissedListView.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                CallInfo callInfo = (CallInfo) mAdapter.getItem(position);
                Logger.i(TAG, "[onItemClick]name:%s, number:%s",
                        callInfo.getName(), callInfo.getNumber());
                
                LeatherViewManager.getInstance(mContext).closeLeather();
                
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + callInfo.getNumber()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                LeatherActivity.getInstance().getApplicationContext().startActivity(intent);
            }
        });
        updateUnread();
        
        mAdapter = new MissedListAdapter();
        mAdapter.setCallInfoList(mCallInfoList);
        mMissedListView.setAdapter(mAdapter);
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
    public void onMmsUnreadUpdate(int unread) {
        updateUnread();
    }

    @Override
    public void onMissedCallUpdate(List<CallInfo> callInfo) {
        Logger.i(TAG, "[onMissedCallUpdate]");
        mCallInfoList.clear();
        mCallInfoList.addAll(callInfo);
        mAdapter.notifyDataSetChanged();
    }
    
    private void updateUnread() {
        int count = mMmsManager.getUnread();
        if (count != 0) {
            mUnreadImage.setImageResource(LeatherUtil.getImageResource(count));
            mUnreadLayout.setVisibility(View.VISIBLE);
            mUnreadHintView.setText(mContext.getString(R.string.page_notice_unread_hint));
            if (mCallInfoList.size() != 0) {
                mDivideView.setVisibility(View.VISIBLE);
            } else {
                mDivideView.setVisibility(View.GONE);
            }
        } else {
            mUnreadLayout.setVisibility(View.GONE);
            mDivideView.setVisibility(View.GONE);
        }
    }
    
    private class MissedListAdapter extends BaseAdapter {

        private List<CallInfo> mCallInfoList;
        
        public void setCallInfoList(List<CallInfo> list) {
            mCallInfoList = list;
        }
        
        @Override
        public int getCount() {
            return mCallInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mCallInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_missed_item, null);
                
                holder.nameView = (TextView) convertView.findViewById(R.id.name);
                holder.dateView = (TextView) convertView.findViewById(R.id.date);
                holder.numberView = (TextView) convertView.findViewById(R.id.number);
                
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            CallInfo callInfo = mCallInfoList.get(position);
            Logger.i(TAG, "[getView]name:%s, date:%s, number:%s",
                    callInfo.getName(), callInfo.getDate(), callInfo.getNumber());
            String name;
            if (callInfo.getName() == null) {
                name = mContext.getString(R.string.page_notice_unknown_contact);
            } else {
                name = callInfo.getName();
            }
            
            String format;
            long callTime = Long.valueOf(callInfo.getDate());
            long diff = System.currentTimeMillis() - callTime;
            if (Math.abs(diff) < DateTimeManager.DAY) {
                format = DateFormat.is24HourFormat(mContext) ? "HH:mm" : "hh:mm a";
            } else {
                format = "yyyy-MM-dd";
            }
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(callTime);
            String language = Locale.getDefault().getLanguage();
            String time;
            if ("ar".equals(language) || "hi".equals(language)) {
                time = new SimpleDateFormat(format, Locale.ENGLISH).format(calendar.getTime());
            } else {
                time = new SimpleDateFormat(format, Locale.getDefault()).format(calendar.getTime());
            }
            holder.nameView.setText(name);
            holder.dateView.setText(time);
            holder.numberView.setText(callInfo.getNumber());
            return convertView;
        }
        
        class ViewHolder {
            TextView nameView;
            TextView dateView;
            TextView numberView;
        }
    }
}
