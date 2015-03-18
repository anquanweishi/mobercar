package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xtrd.obdcar.entity.GasComment;
import com.xtrd.obdcar.entity.OilPrice;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

public class FriendReportAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<GasComment> list;

	public FriendReportAdapter(Context context,ArrayList<GasComment> list) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_friend_report_item, null);
			viewHolder.layout_friend_report = (LinearLayout) convertView.findViewById(R.id.layout_friend_report);
			viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			viewHolder.text_time = (TextView) convertView.findViewById(R.id.text_time);
			viewHolder.text_content = (TextView) convertView.findViewById(R.id.text_content);
			viewHolder.layout_price = (LinearLayout) convertView.findViewById(R.id.layout_price);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		updateUI(viewHolder,info);
		return convertView;
	}
	
	private void updateUI(ViewHolder viewHolder, GasComment info) {
		if(info!=null) {
			viewHolder.text_name.setText(info.getUserName());
			viewHolder.text_time.setText(info.getCreateTime());
			viewHolder.text_content.setText(info.getContent());
			if(info.getPrices()!=null&&info.getPrices().size()>0) {
				View view = null;
				LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, 0, Utils.dipToPixels(context, 10));
				OilPrice price = null;
				viewHolder.layout_price.removeAllViews();
				for(int i=0;i<info.getPrices().size();i++) {
					price = info.getPrices().get(i);
					view = LayoutInflater.from(context).inflate(R.layout.layout_oil_price_for_report, null);
					((TextView)view.findViewById(R.id.text_oil_type)).setText(price.getName()+"#");
					((EditText)view.findViewById(R.id.edit_price_input)).setText(price.getPrice()+"");
					((EditText)view.findViewById(R.id.edit_price_input)).setFocusable(false);
					((EditText)view.findViewById(R.id.edit_price_input)).setEnabled(false);
					viewHolder.layout_price.addView(view,params);
				}
			}
		}
	}
	
	final static class ViewHolder {

		public LinearLayout layout_friend_report;
		public TextView text_name;
		public TextView text_time;
		public TextView text_content;
		public LinearLayout layout_price;


	}

}
