package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xtrd.obdcar.entity.HotLine;
import com.xtrd.obdcar.tumi.R;

public class HotLineAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<HotLine> list;

	public HotLineAdapter(Context context,ArrayList<HotLine> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public HotLine getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final HotLine info = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_hotline_item, null);
			viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			viewHolder.text_phone = (TextView) convertView.findViewById(R.id.text_phone);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.text_name.setText(info.getName());
		viewHolder.text_phone.setText(info.getTelephone());
		return convertView;
	}
	
	final class ViewHolder {
		TextView text_name;
		TextView text_phone;
	}

}
