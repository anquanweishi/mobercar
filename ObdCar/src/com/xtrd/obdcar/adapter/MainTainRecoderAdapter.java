package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.entity.Recoder;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.view.MyGridView;

public class MainTainRecoderAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Recoder> list;
	private FinalBitmap fb;
	private MainTainAdapter adapter;

	public MainTainRecoderAdapter(Context context,ArrayList<Recoder> list) {
		this.context = context;
		this.list = list;
		fb = FinalBitmap.create(context);
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public Recoder getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final Recoder info = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_maintain_recoder_item, null);
			viewHolder.layout_recoder_item = (LinearLayout) convertView.findViewById(R.id.layout_recoder_item);
			viewHolder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
			viewHolder.text_plate = (TextView) convertView.findViewById(R.id.text_plate);
			viewHolder.text_time = (TextView) convertView.findViewById(R.id.text_time);
			viewHolder.text_distance = (TextView) convertView.findViewById(R.id.text_distance);
			viewHolder.text_price = (TextView) convertView.findViewById(R.id.text_price);
			viewHolder.gridView = (MyGridView) convertView.findViewById(R.id.gridView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		updateUI(viewHolder,info);
		
		
		return convertView;
	}

	private void updateUI(ViewHolder viewHolder, Recoder info) {
		if(info!=null) {
			fb.display(viewHolder.img_icon, info.getIcon());
			viewHolder.text_plate.setText(info.getPlateNumber());
			viewHolder.text_time.setText(info.getLastTime());
			viewHolder.text_distance.setText(info.getDistance()+"公里");
			viewHolder.text_price.setText(info.getPrice()>=0?info.getPrice()+"元":"未知");
			if(viewHolder.gridView.getAdapter()==null) {
				adapter = new MainTainAdapter(context,info.getList());
				viewHolder.gridView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
		}
	}
	

	final static class ViewHolder {
		public LinearLayout layout_recoder_item;
		public ImageView img_icon;
		public TextView text_plate;
		public TextView text_time;
		public TextView text_distance;
		public TextView text_price;
		public MyGridView gridView;

	}
}
