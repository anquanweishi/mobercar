package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtrd.obdcar.entity.MainTainChildItem;
import com.xtrd.obdcar.tumi.R;

/**
 * 保养项
 * @author Administrator
 *
 */
public class MainTainCheckdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<MainTainChildItem> list;
	private CallBack callback;

	public MainTainCheckdapter(Context context,ArrayList<MainTainChildItem> list,CallBack callback) {
		this.context = context;
		this.list = list;
		this.callback = callback;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_maintain_select_item, null);
			viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			viewHolder.img_remove = (ImageView) convertView.findViewById(R.id.img_remove);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		OnClickListener click = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.img_remove:
					MainTainCheckdapter.this.notifyDataSetChanged();
					if(callback!=null) {
						callback.callback(info);
					}
					list.remove(info);
					break;

				default:
					break;
				}
			}
		};
		
		viewHolder.img_remove.setOnClickListener(click);
	
		updateUI(viewHolder,info);
		return convertView;
	}
	
	private void updateUI(ViewHolder viewHolder, MainTainChildItem info) {
		if(info!=null) {
			viewHolder.text_name.setText(info.getName());
		}
	}
	
	final static class ViewHolder {

		public TextView text_name;
		public ImageView img_remove;
	}
	
	public interface CallBack {
		void callback(MainTainChildItem item);
	}
}
