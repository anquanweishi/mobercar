package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtrd.obdcar.entity.SimpleTrip;
import com.xtrd.obdcar.tumi.R;

public class CarHealthAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<SimpleTrip> list;

	public CarHealthAdapter(Context context,ArrayList<SimpleTrip> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public SimpleTrip getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SimpleTrip item = getItem(position);
		ViewHolder view = null;
		if (convertView == null) {
			view = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_vehicle_item, null);
			view.view = (View) convertView.findViewById(R.id.view);
			view.text_time = (TextView) convertView.findViewById(R.id.text_time);
			view.text_info = (TextView) convertView.findViewById(R.id.text_info);
			view.img_arrow = (ImageView) convertView.findViewById(R.id.img_arrow);
			convertView.setTag(view);
		} else {
			view = (ViewHolder) convertView.getTag();
		}
		view.text_time.setText(item.getGpsTime());
		if(item.getTroubleCodeNumber()>0) {
			SpannableString s = new SpannableString("车况			故障" + item.getTroubleCodeNumber());
			s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)), 2, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			view.text_info.setText(s);
		}else {
			view.text_info.setText("车况			正常");
		}
		
		return convertView;
	}
	class ViewHolder {
		View view;
		TextView text_time;
		TextView text_info;
		ImageView img_arrow;
	}
}
