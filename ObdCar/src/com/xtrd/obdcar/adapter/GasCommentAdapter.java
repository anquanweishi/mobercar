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

import com.xtrd.obdcar.entity.GasComment;
import com.xtrd.obdcar.tumi.R;

public class GasCommentAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<GasComment> list;

	public GasCommentAdapter(Context context,ArrayList<GasComment> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public GasComment getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final GasComment info = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_gas_comment_item, null);
			viewHolder.layout_comment_item = (LinearLayout) convertView.findViewById(R.id.layout_comment_item);
			viewHolder.text_title = (TextView) convertView.findViewById(R.id.text_title);
			viewHolder.text_desc = (TextView) convertView.findViewById(R.id.text_desc);
			viewHolder.text_time = (TextView) convertView.findViewById(R.id.text_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		updateUI(viewHolder,info);
		return convertView;
	}
	
	private void updateUI(ViewHolder viewHolder, GasComment info) {
		if(info!=null) {
			viewHolder.text_title.setText(info.getUserName());
			viewHolder.text_desc.setText(info.getContent());
			viewHolder.text_time.setText(info.getCreateTime());
		}
	}
	
	final static class ViewHolder {

		public LinearLayout layout_comment_item;
		public ImageView img_comment;
		public TextView text_title;
		public TextView text_desc;
		public TextView text_time;

	}

}
