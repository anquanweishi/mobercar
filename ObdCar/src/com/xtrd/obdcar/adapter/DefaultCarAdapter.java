package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtrd.obdcar.entity.CarInfo;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.ImageUtils;

public class DefaultCarAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<CarInfo> list;
	private boolean from;

	public DefaultCarAdapter(Context context,ArrayList<CarInfo> list) {
		this.context = context;
		this.list = list;
	}
	
	public void setFrom(boolean from) {
		this.from = from;
	}


	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public CarInfo getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final CarInfo info = getItem(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_car_select, null);
			viewHolder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
			viewHolder.text_plate = (TextView) convertView.findViewById(R.id.text_plate);
			viewHolder.text_branch = (TextView)convertView.findViewById(R.id.text_branch);
			viewHolder.checkBox = (ImageView) convertView.findViewById(R.id.checkBox);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		ImageUtils.displayBranchImg(context, viewHolder.img_icon, info.getBranchId());
		viewHolder.text_plate.setText(info.getPlateNumber());
		viewHolder.text_branch.setText(info.getBranch()+"  " + info.getSeries());
		if(from) {
			viewHolder.checkBox.setImageResource(R.drawable.arrow);
		}else {
			if(info.isChecked()) {
				viewHolder.checkBox.setImageResource(R.drawable.ic_checked);
			}else {
				viewHolder.checkBox.setImageResource(R.color.transparent);
			}
		}
		
		return convertView;
	}
	
	final static class ViewHolder {
		public TextView text_branch;
		public ImageView img_icon;
		public TextView text_plate;
		public ImageView checkBox;
	}

	
}
