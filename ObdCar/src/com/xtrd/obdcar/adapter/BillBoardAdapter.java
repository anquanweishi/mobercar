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

import com.xtrd.obdcar.entity.CarUser;
import com.xtrd.obdcar.entity.GasStation;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.ImageUtils;
/**
 * 附近加油站
 * @author Administrator
 *
 */
public class BillBoardAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<CarUser> list;

	public BillBoardAdapter(Context context,ArrayList<CarUser> list) {
		this.context = context;
		this.list = list;
	}

	
	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public CarUser getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final CarUser info = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_bill_board_item, null);
			viewHolder.layout_bill_item = (LinearLayout) convertView.findViewById(R.id.layout_bill_item);
			viewHolder.text_rank = (TextView) convertView.findViewById(R.id.text_rank);
			viewHolder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
			viewHolder.text_phone = (TextView) convertView.findViewById(R.id.text_phone);
			viewHolder.text_loc = (TextView) convertView.findViewById(R.id.text_loc);
			viewHolder.text_branch = (TextView) convertView.findViewById(R.id.text_branch);
			viewHolder.text_fuel = (TextView) convertView.findViewById(R.id.text_fuel);
			viewHolder.text_unit = (TextView) convertView.findViewById(R.id.text_unit);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		updateUI(viewHolder,info,(position+1));
		return convertView;
	}
	

	private void updateUI(ViewHolder viewHolder, CarUser info,int position) {
		if(info!=null) {
			if(position<=3){
				if(1==position) {
					viewHolder.text_rank.setBackgroundResource(R.drawable.ic_first);
				}else if(2==position){
					viewHolder.text_rank.setBackgroundResource(R.drawable.ic_second);
				}else {
					viewHolder.text_rank.setBackgroundResource(R.drawable.ic_third);
				}
				viewHolder.text_rank.setText("");
			}else {
				viewHolder.text_rank.setText(position+"");
				viewHolder.text_rank.setBackgroundResource(R.drawable.ic_num_bg);
			}
		}
		ImageUtils.displayBranchImg(context, viewHolder.img_icon, info.getBranchId());
		try {
			viewHolder.text_phone.setText(info.getNick());
		} catch (Exception e) {
			e.printStackTrace();
		}
		viewHolder.text_loc.setText(info.getCity());
		viewHolder.text_branch.setText(info.getSer());
		viewHolder.text_fuel.setText(info.getVal()+"");
		viewHolder.text_unit.setText(info.getUnit());
	}
	
	
	final static class ViewHolder {

		public LinearLayout layout_bill_item;
		public TextView text_rank;
		public ImageView img_icon;
		public TextView text_phone;
		public TextView text_loc;
		public TextView text_branch;
		public TextView text_fuel;
		public TextView text_unit;
	}
	
	public interface ClickCallback {
		void callback(GasStation item);
	}
}
