package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.entity.MainTainChildItem;
import com.xtrd.obdcar.tumi.R;

/**
 * 保养项
 * @author Administrator
 *
 */
public class MainTainAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<MainTainChildItem> list;

	public MainTainAdapter(Context context,ArrayList<MainTainChildItem> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public MainTainChildItem getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final MainTainChildItem info = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_maintain_item, null);
			viewHolder.layout_maintain_item = (LinearLayout) convertView.findViewById(R.id.layout_maintain_item);
			viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			viewHolder.img_check = (ImageView) convertView.findViewById(R.id.img_check);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		updateUI(viewHolder,info);
		return convertView;
	}
	
	private void updateUI(ViewHolder viewHolder, MainTainChildItem info) {
		if(info!=null) {
			viewHolder.text_name.setText(info.getName());
			if(info.isChecked()) {
				viewHolder.img_check.setVisibility(View.VISIBLE);
			}else {
				viewHolder.img_check.setVisibility(View.GONE);
			}
		}
	}
	
	final static class ViewHolder {

		public LinearLayout layout_maintain_item;
		public TextView text_name;
		public ImageView img_check;
	}
}
