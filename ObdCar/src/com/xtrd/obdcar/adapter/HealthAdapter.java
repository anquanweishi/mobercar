package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtrd.obdcar.entity.CarTrouble;
import com.xtrd.obdcar.tumi.R;

public class HealthAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<CarTrouble> list;
	private boolean trouble;

	public HealthAdapter(Context context,ArrayList<CarTrouble> list) {
		this.context = context;
		this.list = list;
	}

	public void setTrouble(boolean trouble) {
		this.trouble = trouble;
	}
	
	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public CarTrouble getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CarTrouble item = getItem(position);
		ViewHolder view = null;
		if (convertView == null) {
			view = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_health_item, null);
			view.view = (View) convertView.findViewById(R.id.view);
			view.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
			view.text_info = (TextView) convertView.findViewById(R.id.text_info);
			convertView.setTag(view);
		} else {
			view = (ViewHolder) convertView.getTag();
		}
		if(trouble) {
			view.text_info.setText(item.getDetail().getName());
			view.text_info.setTextColor(Color.RED);
		}else {
			view.text_info.setTextColor(context.getResources().getColor(R.color.blue_text));
			view.text_info.setText(item.getName()+"  " + item.getValue()+item.getUnit());
		}
		return convertView;
	}
	class ViewHolder {
		View view;
		ImageView img_icon;
		TextView text_info;
	}
}
