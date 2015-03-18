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
import android.widget.TextView;

import com.xtrd.obdcar.entity.VCondition;
import com.xtrd.obdcar.entity.VConditionGroup;
import com.xtrd.obdcar.tumi.R;

public class HistoryVCAdapter extends BaseExpandableListAdapter {

	private List<VConditionGroup> mList = null;
	private Context context = null;
	public HistoryVCAdapter(Context con,ArrayList<VConditionGroup> list){
		this.mList = list;
		this.context = con;
	}
	@Override
	public int getGroupCount() {
		return mList!=null?mList.size():0;
	}
	@Override
	public int getChildrenCount(int groupPosition) {
		return mList.get(groupPosition).getList().size();
	}
	@Override
	public VConditionGroup getGroup(int groupPosition) {
		return mList.get(groupPosition);
	}
	@Override
	public VCondition getChild(int groupPosition, int childPosition) {
		return mList.get(groupPosition).getList().get(childPosition);
	}
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	@Override
	public boolean hasStableIds() {
		return true;
	}
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		VConditionGroup group = mList.get(groupPosition);
		View view = null;
		ViewHolder holder = null;
		if(convertView == null){
			view = View.inflate(context, R.layout.layout_history_vc_group,null);
			holder = new ViewHolder();
			holder.text_date = (TextView) view.findViewById(R.id.text_date);
			holder.text_count = (TextView) view.findViewById(R.id.text_count);
			view.setTag(holder);
		}else{
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		holder.text_date.setText(group.getTime());
		processCount(holder.text_count,group.getCount());
	
		return view;
	}
	
	
	private void processCount(TextView text_count, String count) {
		String str = count+"个故障";
		SpannableString s = new SpannableString(str);
		s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)), 0, count.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		text_count.setText(s);
	}


	final static class ViewHolder {
		public TextView text_date;
		public TextView text_count;
	}
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		VCondition condition = mList.get(groupPosition).getList().get(childPosition);
		TextViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new TextViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_history_vc_child, null);
			viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			viewHolder.text_code = (TextView) convertView.findViewById(R.id.text_code);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (TextViewHolder) convertView.getTag();
		}
		viewHolder.text_name.setText(condition.getName());
		viewHolder.text_code.setText(condition.getCode());
		return convertView;
	}
	
	class TextViewHolder {
		public TextView text_name;
		public TextView text_code;
	}
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}


}
