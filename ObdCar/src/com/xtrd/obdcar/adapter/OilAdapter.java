package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xtrd.obdcar.entity.OBDOil;
import com.xtrd.obdcar.tumi.R;

public class OilAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<OBDOil> list;

	public OilAdapter(Context context,ArrayList<OBDOil> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public OBDOil getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		OBDOil oil = list.get(arg0);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_textview, null);
			viewHolder.text_title = (TextView) convertView.findViewById(R.id.layout_textview);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.text_title.setText(oil.getName());
		return convertView;
	}
	class ViewHolder {
		public TextView text_title;
	}
}
