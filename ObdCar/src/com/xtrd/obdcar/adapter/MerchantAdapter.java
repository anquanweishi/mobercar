package com.xtrd.obdcar.adapter;


import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.Merchant;
import com.xtrd.obdcar.merchant.MerchantDetailActivity;
import com.xtrd.obdcar.reservation.ReservationActivity;
import com.xtrd.obdcar.tumi.R;
/**
 * 客户列表适配器
 *
 */
public class MerchantAdapter extends BaseAdapter {

	private List<Merchant> mList = null;
	private Context mContext = null;
	private FinalBitmap fb;
	public MerchantAdapter(Context con,List<Merchant> list){
		this.mList = list;
		this.mContext = con;
		fb = FinalBitmap.create(con);
	}


	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Merchant getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final Merchant info = mList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_merchant_item, null);
			viewHolder.layout_merchant_item = (LinearLayout) convertView.findViewById(R.id.layout_merchant_item);
			viewHolder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
			viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			viewHolder.img_auth_one = (ImageView) convertView.findViewById(R.id.img_auth_one);
			viewHolder.img_auth_two = (ImageView) convertView.findViewById(R.id.img_auth_two);
			viewHolder.img_auth_three = (TextView) convertView.findViewById(R.id.img_auth_three);
			viewHolder.img_auth_four = (TextView) convertView.findViewById(R.id.img_auth_four);
			viewHolder.text_address = (TextView) convertView.findViewById(R.id.text_address);
			viewHolder.text_distance = (TextView) convertView.findViewById(R.id.text_distance);
			viewHolder.btn_order = (Button) convertView.findViewById(R.id.btn_order);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		updateUI(viewHolder,info);
		OnClickListener click = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = null;
				switch (v.getId()) {
				case R.id.layout_merchant_item:
					intent = new Intent(mContext,MerchantDetailActivity.class);
					intent.putExtra(ParamsKey.MERCHANTID, info.getId());
					mContext.startActivity(intent);
					break;
				case R.id.btn_order:
					intent = new Intent(mContext,ReservationActivity.class);
					intent.putExtra(ParamsKey.MERCHANTID, info.getId());
					intent.putExtra("merchantName", info.getName());
					mContext.startActivity(intent);
					break;

				default:
					break;
				}
			}
		};
		viewHolder.layout_merchant_item.setOnClickListener(click);
		viewHolder.btn_order.setOnClickListener(click);
		return convertView;
	}
	
	private void updateUI(ViewHolder viewHolder, Merchant info) {
		if(info!=null) {
			fb.display(viewHolder.img_icon, info.getImgUrl());
			viewHolder.text_name.setText(info.getName());
			if(1==info.getGold()) {
				viewHolder.img_auth_one.setVisibility(View.VISIBLE);
			}else {
				viewHolder.img_auth_one.setVisibility(View.GONE);
			}
			if(1==info.getReal()) {
				viewHolder.img_auth_two.setVisibility(View.VISIBLE);
			}else {
				viewHolder.img_auth_two.setVisibility(View.GONE);
			}
			
			if(info.getScore()>0) {
				viewHolder.img_auth_three.setVisibility(View.VISIBLE);
				viewHolder.img_auth_three.setText(info.getScore()+"分");
			}else {
				viewHolder.img_auth_three.setVisibility(View.GONE);
			}
			
			if(1==info.getSpecial()) {
				viewHolder.img_auth_four.setVisibility(View.VISIBLE);
			}else {
				viewHolder.img_auth_four.setVisibility(View.GONE);
			}
			
			viewHolder.text_address.setText(info.getAddress());
			viewHolder.text_distance.setText(info.getDistance()+"公里");
		}
	}
	
	final static class ViewHolder {

		public LinearLayout layout_merchant_item;
		public ImageView img_icon;
		public TextView text_name;
		public ImageView img_auth_one;
		public ImageView img_auth_two;
		public TextView img_auth_three;
		public TextView img_auth_four;
		public TextView text_address;
		public TextView text_distance;
		public Button btn_order;
		
	}

}
