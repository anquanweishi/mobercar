package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.entity.CarInfo;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.ImageUtils;

public class CarListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<CarInfo> list;

	public CarListAdapter(Context context,ArrayList<CarInfo> list) {
		this.context = context;
		this.list = list;
	}



	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public CarInfo getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final CarInfo info = getItem(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_car_list_item, null);
			viewHolder.layout_car_list_item = (LinearLayout)convertView.findViewById(R.id.layout_car_list_item);
			viewHolder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
			viewHolder.text_plate = (TextView) convertView.findViewById(R.id.text_plate);
			viewHolder.layout_range = (LinearLayout) convertView.findViewById(R.id.layout_range);
			viewHolder.edit_range = (EditText) convertView.findViewById(R.id.edit_range);
			viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		updateView(viewHolder,info);
		return convertView;
	}

	private void updateView(final ViewHolder viewHolder,final CarInfo info) {
		ImageUtils.displayBranchImg(context, viewHolder.img_icon, info.getBranchId());
		viewHolder.text_plate.setText(info.getPlateNumber());
		if(info.isChecked()) {
			viewHolder.layout_range.setVisibility(View.VISIBLE);
			viewHolder.checkBox.setButtonDrawable(R.drawable.ic_checked);
		}else {
			viewHolder.layout_range.setVisibility(View.INVISIBLE);
			viewHolder.checkBox.setButtonDrawable(R.color.transparent);
		}



		viewHolder.layout_car_list_item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for(CarInfo item : list) {
					item.setChecked(false);
					item.setDriveDistance(viewHolder.edit_range.getText().toString());
				}
				info.setChecked(true);
				CarListAdapter.this.notifyDataSetChanged();
			}
		});
	}


	final static class ViewHolder {
		public LinearLayout layout_car_list_item;
		public ImageView img_icon;
		public TextView text_plate;
		public LinearLayout layout_range;
		public EditText edit_range;
		public CheckBox checkBox;
	}


}
