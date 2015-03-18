package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtrd.obdcar.entity.Category;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.view.MyGridView;
/**
 * 附近加油站
 * @author Administrator
 *
 */
public class NearCategoryAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Category> list;
	private FinalBitmap fb;
	private double latitude,longitude;

	public NearCategoryAdapter(Context context,ArrayList<Category> list) {
		this.context = context;
		this.list = list;
		fb = FinalBitmap.create(context);
		
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
	public Category getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final Category info = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_category_group, null);
			viewHolder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
			viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			viewHolder.gridView = (MyGridView) convertView.findViewById(R.id.gridView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		updateUI(viewHolder,info);
		
		
		return convertView;
	}
	
	
	private void updateUI(ViewHolder viewHolder, Category info) {
		if(info!=null) {
			fb.display(viewHolder.img_icon, info.getImgUrl());
			viewHolder.text_name.setText(info.getName());
			if(viewHolder.gridView!=null&&viewHolder.gridView.getAdapter() instanceof GridAdapter) {
				((GridAdapter)viewHolder.gridView.getAdapter()).setLatLng(latitude, longitude);
				((GridAdapter)viewHolder.gridView.getAdapter()).notifyDataSetChanged();
			}else {
				GridAdapter adapter = new GridAdapter(context,info.getList());
				adapter.setLatLng(latitude, longitude);
				viewHolder.gridView.setAdapter(adapter);
			}
			
		}
	}
	
	
	final static class ViewHolder {

		public ImageView img_icon;
		public TextView text_name;
		public MyGridView gridView;
	}
}
