package com.xtrd.obdcar.adapter;


import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.EMMessage;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.DateUtils;
import com.xtrd.obdcar.utils.ImageUtils;



public class ChatAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<EMMessage> list;
	private Context context;


	public ChatAdapter(Context context,ArrayList<EMMessage> list) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.list = list;
	}


	public int getCount() {
		return list!=null?list.size():0;
	}

	public EMMessage getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}


	public View getView(final int position, View convertView, ViewGroup parent) {
		final EMMessage message = getItem(position);
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.row_message, null);
			holder.receive_item = (RelativeLayout)convertView.findViewById(R.id.layout_received);
			holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
			holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
			holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			// 这里是文字内容
			holder.layout_send_item = (RelativeLayout)convertView.findViewById(R.id.layout_send_item);
			holder.iv_send_userhead = (ImageView) convertView.findViewById(R.id.iv_send_userhead);
			holder.tv_send_userId = (TextView) convertView.findViewById(R.id.tv_send_userid);
			holder.tv_send = (TextView) convertView.findViewById(R.id.tv_send_chatcontent);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		updateUI(message, holder);


		TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);

		if (position == 0) {
			timestamp.setText(DateUtils.getTimestampString(new Date(message.getTime())));
			timestamp.setVisibility(View.VISIBLE);
		} else {
			if (DateUtils.isCloseEnough(message.getTime(), list.get(position - 1).getTime())) {
				timestamp.setVisibility(View.GONE);
			} else {
				timestamp.setText(DateUtils.getTimestampString(new Date(message.getTime())));
				timestamp.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}


	private void updateUI(EMMessage message, ViewHolder holder) {
		if(1==message.getType()) {
			holder.layout_send_item.setVisibility(View.GONE);
			holder.receive_item.setVisibility(View.VISIBLE);

			holder.head_iv.setImageResource(R.drawable.ic_launcher);
			holder.tv.setText(message.getContent());
			holder.tv_userId.setText(message.getUser());
		}else {
			holder.layout_send_item.setVisibility(View.VISIBLE);
			holder.receive_item.setVisibility(View.GONE);
			ImageUtils.displayBranchImg(context, holder.iv_send_userhead, SettingLoader.getBranchId(context));
			holder.tv_send.setText(message.getContent());
			holder.tv_send_userId.setText(message.getUser());
		}
	}


	public static class ViewHolder {

		public RelativeLayout receive_item;
		public ImageView head_iv;
		public TextView tv_userId;
		public TextView tv;
		public RelativeLayout layout_send_item;
		public ImageView iv_send_userhead;
		public TextView tv_send_userId;
		public TextView tv_send;
		
	}

}