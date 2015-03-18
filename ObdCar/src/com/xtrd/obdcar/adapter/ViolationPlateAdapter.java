package com.xtrd.obdcar.adapter;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xtrd.obdcar.entity.PlateEntity;
import com.xtrd.obdcar.tumi.R;
/**
 * 客户列表适配器
 *
 */
public class ViolationPlateAdapter extends BaseAdapter{

	private ArrayList<PlateEntity> mList = null;
	private Context mContext = null;
	public ViolationPlateAdapter(Context con,ArrayList<PlateEntity> list){
		this.mList = list;
		this.mContext = con;
	}


	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public PlateEntity getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final PlateEntity info = getItem(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_textview, null);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.layout_textview);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvTitle.setText(info.getCar());

		return convertView;
	}
	final static class ViewHolder {
		TextView tvTitle;
	}
}
