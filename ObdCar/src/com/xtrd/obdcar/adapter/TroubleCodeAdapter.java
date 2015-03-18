package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xtrd.obdcar.entity.VCondition;
import com.xtrd.obdcar.tumi.R;
/**
 * 附近加油站
 * @author Administrator
 *
 */
public class TroubleCodeAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<VCondition> list;

	public TroubleCodeAdapter(Context context,ArrayList<VCondition> list) {
		this.context = context;
		this.list = list;
	}

	
	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public VCondition getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final VCondition info = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_trouble_code_item, null);
			viewHolder.text_code = (TextView) convertView.findViewById(R.id.text_code);
			viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		updateUI(viewHolder,info);
		
		return convertView;
	}
	

	private void updateUI(ViewHolder viewHolder, VCondition info) {
		viewHolder.text_code.setText(info.getCode());
		viewHolder.text_name.setText(info.getName());
	}
	
	
	final static class ViewHolder {

		public TextView text_code;
		public TextView text_name;
	}
}
