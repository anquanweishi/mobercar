package com.xtrd.obdcar.adapter;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtrd.obdcar.entity.CarModel;
import com.xtrd.obdcar.entity.CarSeries;
import com.xtrd.obdcar.tumi.R;

public class SeriesAdapter extends BaseExpandableListAdapter {

	private List<CarSeries> mList = null;
	private Context mContext = null;
	public SeriesAdapter(Context con,ArrayList<CarSeries> list){
		this.mList = list;
		this.mContext = con;
	}

	@Override
	public CarModel getChild(int groupPosition, int childPosition) {
		return mList.get(groupPosition).getList().get(childPosition);
	}


	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}


	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		CarModel model = mList.get(groupPosition).getList().get(childPosition);
		TextViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new TextViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_textview, null);
			viewHolder.text_title = (TextView) convertView.findViewById(R.id.layout_textview);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (TextViewHolder) convertView.getTag();
		}
		viewHolder.text_title.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		viewHolder.text_title.setText(model.getName());
		return convertView;
	}
	class TextViewHolder {
		public TextView text_title;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mList!=null?mList.get(groupPosition).getList().size():0;
	}


	@Override
	public CarSeries getGroup(int groupPosition) {
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
		CarSeries series = mList.get(groupPosition);
		View view = null;
		ViewHolder holder = null;
		if(convertView == null){
			view = View.inflate(mContext, R.layout.layout_expand_list_item,null);
			holder = new ViewHolder();
			holder.title_text = (TextView) view.findViewById(R.id.title_text);
			holder.img_arrow = (ImageView) view.findViewById(R.id.img_arrow);
			view.setTag(holder);
		}else{
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		holder.title_text.setText(series.getName());
		
		if(isExpanded) {
			holder.img_arrow.setImageResource(R.drawable.arrow_down);
		}else {
			holder.img_arrow.setImageResource(R.drawable.arrow_right);
		}
		
		return view;
	}


	@Override
	public boolean hasStableIds() {
		return true;
	}


	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	final static class ViewHolder {
		public ImageView img_arrow;
		public TextView title_text;
	}

}
