package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xtrd.obdcar.entity.CategoryItem;
import com.xtrd.obdcar.nearby.NearShopActivity;
import com.xtrd.obdcar.tumi.R;

public class GridAdapter extends BaseAdapter {

	private ArrayList<CategoryItem> list;
	private Context context;
	private double latitude,longitude;
	
	public GridAdapter(Context context,ArrayList<CategoryItem> list) {
		this.context = context;
		this.list = list;
	}
	
	
	public void setLatLng(double latitude,double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public CategoryItem getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final CategoryItem item = list.get(position);
		TextViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new TextViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_textview, null);
			viewHolder.layout_textview = (TextView) convertView.findViewById(R.id.layout_textview);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (TextViewHolder) convertView.getTag();
		}
		viewHolder.layout_textview.setText(item.getWord());
		OnClickListener click = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.layout_textview:
					Intent intent = new Intent(context,NearShopActivity.class);
					intent.putExtra("keyword", item.getWord());
					intent.putExtra("id", item.getId());
					intent.putExtra("latitude",latitude);
					intent.putExtra("longitude",longitude);
					((Activity)context).startActivityForResult(intent,1);
					break;
				default:
					break;
				}
			}
		};
		viewHolder.layout_textview.setOnClickListener(click);
		return convertView;
	}
	class TextViewHolder {
		public TextView layout_textview;
	}

}
