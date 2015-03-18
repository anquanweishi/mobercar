package com.xtrd.obdcar.adapter;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtrd.obdcar.entity.ViolationCity;
import com.xtrd.obdcar.tumi.R;
/**
 * 客户列表适配器
 *
 */
public class ViolationCityAdapter extends BaseAdapter{

	private ArrayList<ViolationCity> mList = null;
	private Context mContext = null;
	public ViolationCityAdapter(Context con,ArrayList<ViolationCity> list){
		this.mList = list;
		this.mContext = con;
	}


	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public ViolationCity getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final ViolationCity info = getItem(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.model_list_item, null);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.text_group);
			viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.text_title);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvLetter.setText(info.getCityName());

		return convertView;
	}
	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		ImageView mHeader;
	}
}
