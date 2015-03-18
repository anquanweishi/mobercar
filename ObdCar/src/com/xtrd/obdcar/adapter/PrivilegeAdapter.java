package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.entity.Privilege;
import com.xtrd.obdcar.merchant.PrivilegeActivity;
import com.xtrd.obdcar.tumi.R;

public class PrivilegeAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Privilege> list;

	public PrivilegeAdapter(Context context,ArrayList<Privilege> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list!=null?list.size():0;
	}

	@Override
	public Privilege getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final Privilege info = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_merchant_vip, null);
			viewHolder.layout_privilege_item = (LinearLayout) convertView.findViewById(R.id.layout_privilege_item);
			viewHolder.text_title = (Button) convertView.findViewById(R.id.text_title);
			viewHolder.text_desc = (TextView) convertView.findViewById(R.id.text_desc);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		updateUI(viewHolder,info);
		
		OnClickListener click = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.layout_privilege_item:
					Intent intent = new Intent(context,PrivilegeActivity.class);
					intent.putExtra("id", info.getId());
					context.startActivity(intent);
					break;

				default:
					break;
				}
			}
		};
		viewHolder.layout_privilege_item.setOnClickListener(click);
		return convertView;
	}
	
	private void updateUI(ViewHolder viewHolder, Privilege info) {
		if(info!=null) {
			if(0==info.getType()) {
				viewHolder.text_title.setText("优惠");
			}else {
				viewHolder.text_title.setText("专享");
			}
			viewHolder.text_desc.setText(info.getName());
		}
	}
	
	final static class ViewHolder {

		public LinearLayout layout_privilege_item;
		public Button text_title;
		public TextView text_desc;
	}
}
