package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xtrd.obdcar.entity.CarTrouble;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

public class HealthScroolAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<CarTrouble> list;
	private boolean trouble;

	public HealthScroolAdapter(Context context,ArrayList<CarTrouble> list) {
		this.context = context;
		this.list = list;
	}
	
	public void setTrouble(boolean trouble) {
		this.trouble = trouble;
	}

	@Override
	public int getCount() {
//		return list!=null?list.size():0;
		return Integer.MAX_VALUE;
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
		TextView textview = new TextView(context);
		if(list.size()>0) {
			CarTrouble item = getItem(position%list.size());
			textview.setHeight(Utils.dipToPixels(context, 40));
			textview.setGravity(Gravity.CENTER);
			textview.setTextSize(context.getResources().getDimension(R.dimen.Smallest_font));
			textview.setTextColor(Color.WHITE);
			if(trouble) {
				textview.setText(item.getDetail().getName());
			}else {
				textview.setText(item.getName()+"  " + item.getValue()+item.getUnit());
			}
		}
		return textview;
	}
	
}
