package com.xtrd.obdcar.adapter;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.entity.MainTainChildItem;
import com.xtrd.obdcar.entity.MainTainItem;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;

public class MaintainItemAdapter extends BaseExpandableListAdapter {

	private List<MainTainItem> mList = null;
	private Context mContext = null;
	public MaintainItemAdapter(Context con,ArrayList<MainTainItem> list){
		this.mList = list;
		this.mContext = con;
	}

	@Override
	public MainTainChildItem getChild(int groupPosition, int childPosition) {
		return mList.get(groupPosition).getList().get(childPosition);
	}


	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}


	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		MainTainChildItem item = mList.get(groupPosition).getList().get(childPosition);
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.layout_par_miantain_item,null);
			holder.layout_part_maintain_item = (LinearLayout)convertView.findViewById(R.id.layout_part_maintain_item);
			holder.text_title = (TextView) convertView.findViewById(R.id.text_title);
			holder.text_count = (TextView) convertView.findViewById(R.id.text_count);
			holder.text_range = (TextView) convertView.findViewById(R.id.text_range);
			holder.text_time = (TextView) convertView.findViewById(R.id.text_time);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.text_title.setText(item.getName());
		proccessInfo(holder.text_count,item.getCount());
		if(StringUtils.isNullOrEmpty(item.getRange())) {
			holder.text_range.setVisibility(View.GONE);
		}else {
			holder.text_range.setVisibility(View.VISIBLE);
			holder.text_range.setText(item.getRange()+"公里保养");
			
		}
		if(StringUtils.isNullOrEmpty(item.getTime())) {
			holder.text_time.setVisibility(View.GONE);
		}else {
			holder.text_time.setVisibility(View.VISIBLE);
			holder.text_time.setText("上次保养时间："+item.getTime());
		}
		
		
		return convertView;
	}
	private void proccessInfo(TextView text_count, String count) {
		if(!StringUtils.isNullOrEmpty(count)) {
			String str = "第"+count+"次保养";
			SpannableString s = new SpannableString(str);
			s.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.top_bar_color)), str.indexOf(count), str.indexOf(count)+count.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			text_count.setText(s);
		}else {
			text_count.setText("没有保养记录");
		}
	}
	
	
	final static class ViewHolder {

		public LinearLayout layout_part_maintain_item;
		public TextView text_title;
		public TextView text_count;
		public TextView text_range;
		public TextView text_time;
	}
	
	class TextViewHolder {
		public TextView text_title;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mList!=null?mList.get(groupPosition).getList().size():0;
	}


	@Override
	public MainTainItem getGroup(int groupPosition) {
		return mList.get(groupPosition);
	}


	@Override
	public int getGroupCount() {
		return mList!=null?mList.size():0;
	}


	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}


	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		MainTainItem group = getGroup(groupPosition);
		TextViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new TextViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_textview, null);
			viewHolder.text_title = (TextView) convertView.findViewById(R.id.layout_textview);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (TextViewHolder) convertView.getTag();
		}
		viewHolder.text_title.setText(group.getName());
		return convertView;
	}


	@Override
	public boolean hasStableIds() {
		return true;
	}


	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}


}
