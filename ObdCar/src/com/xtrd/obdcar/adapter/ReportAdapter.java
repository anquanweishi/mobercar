package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtrd.obdcar.entity.FuelReport;
import com.xtrd.obdcar.tumi.R;

public class ReportAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<FuelReport> list;

	public ReportAdapter(Context context,ArrayList<FuelReport> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public FuelReport getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FuelReport item = getItem(position);
		ViewHolder view = null;
		if (convertView == null) {
			view = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_report_item, null);
			view.view = (View) convertView.findViewById(R.id.view);
			view.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
			view.text_info = (TextView) convertView.findViewById(R.id.text_info);
			convertView.setTag(view);
		} else {
			view = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}
	class ViewHolder {
		View view;
		ImageView img_icon;
		TextView text_info;
	}

}
