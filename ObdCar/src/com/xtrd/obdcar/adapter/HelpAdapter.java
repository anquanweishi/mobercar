package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.entity.GasStation;
import com.xtrd.obdcar.entity.HelpItem;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
/**
 * 附近加油站
 * @author Administrator
 *
 */
public class HelpAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<HelpItem> list;
	private boolean dail;

	public HelpAdapter(Context context,ArrayList<HelpItem> list) {
		this.context = context;
		this.list = list;
	}
	
	public void setDail(boolean dail) {
		this.dail = dail;
	}


	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public HelpItem getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final HelpItem info = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_onekey_help_item, null);
			viewHolder.layout_help_item = (LinearLayout) convertView.findViewById(R.id.layout_help_item);
			viewHolder.text_title = (TextView) convertView.findViewById(R.id.text_title);
			viewHolder.btn_dail = (ImageButton) convertView.findViewById(R.id.btn_dail);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		updateUI(viewHolder,info);

		OnClickListener click = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_dail:
					if(dail) {
						if(!StringUtils.isNullOrEmpty(info.getVal())) {
							Utils.makePhone(context, info.getVal());
						}
					}else {
						info.setChecked(!info.isChecked());
						HelpAdapter.this.notifyDataSetChanged();
					}
					break;
				default:
					break;
				}
			}
		};
		viewHolder.btn_dail.setOnClickListener(click);


		return convertView;
	}


	private void updateUI(ViewHolder viewHolder, HelpItem info) {
		if(info!=null) {
			if(dail) {
				viewHolder.btn_dail.setImageResource(R.drawable.ic_help_dail);
				viewHolder.text_title.setText(info.getName()+info.getVal());
			}else {
				viewHolder.text_title.setText(info.getName());
				if(info.isChecked()) {
					viewHolder.btn_dail.setImageResource(R.drawable.ic_help_sel);
				}else {
					viewHolder.btn_dail.setImageResource(R.drawable.ic_help_un_sel);
				}
			}
		}
	}


	final static class ViewHolder {

		public LinearLayout layout_help_item;
		public TextView text_title;
		public ImageButton btn_dail;


	}

	public interface ClickCallback {
		void callback(GasStation item);
	}
}
