package com.xtrd.obdcar.adapter;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.Favorite;
import com.xtrd.obdcar.entity.FavoriteGroup;
import com.xtrd.obdcar.merchant.MerchantDetailActivity;
import com.xtrd.obdcar.nearby.NearShopDetailActivity;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.ObdDialog;

public class FavoriteAdapter extends BaseExpandableListAdapter {

	private List<FavoriteGroup> mList = null;
	private Context context = null;
	private FinalBitmap fb;

	public FavoriteAdapter(Context con, ArrayList<FavoriteGroup> list) {
		this.mList = list;
		this.context = con;
		fb = FinalBitmap.create(con);
	}

	@Override
	public int getGroupCount() {
		return mList != null ? mList.size() : 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mList.get(groupPosition).getList().size();
	}

	@Override
	public FavoriteGroup getGroup(int groupPosition) {
		return mList.get(groupPosition);
	}

	@Override
	public Favorite getChild(int groupPosition, int childPosition) {
		return mList.get(groupPosition).getList().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		FavoriteGroup group = mList.get(groupPosition);
		View view = null;
		ViewHolder holder = null;
		if (convertView == null) {
			view = View.inflate(context, R.layout.layout_textview, null);
			holder = new ViewHolder();
			holder.layout_textview = (TextView) view
					.findViewById(R.id.layout_textview);
			holder.layout_textview.setTextColor(context.getResources()
					.getColor(R.color.top_bar_color));
			holder.layout_textview.setGravity(Gravity.LEFT);
			holder.layout_textview.setBackgroundColor(context.getResources().getColor(R.color.header_color));
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		holder.layout_textview.setText(group.getName());
		return view;
	}

	final static class ViewHolder {
		public TextView layout_textview;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder viewHolder = null;
		final FavoriteGroup group = getGroup(groupPosition);
		final Favorite favorite = group.getList().get(childPosition);
		if (convertView == null) {
			viewHolder = new ChildViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_fav_item, null);
			viewHolder.layout_fav_item = (LinearLayout) convertView.findViewById(R.id.layout_fav_item);
			viewHolder.img_icon = (View) convertView.findViewById(R.id.img_icon);
			viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			viewHolder.img_auth_one = (ImageView) convertView.findViewById(R.id.img_auth_one);
			viewHolder.img_auth_two = (TextView) convertView.findViewById(R.id.img_auth_two);
			viewHolder.text_address = (TextView) convertView.findViewById(R.id.text_address);
			viewHolder.text_distance = (TextView) convertView.findViewById(R.id.text_distance);
			viewHolder.btn_fav = (Button) convertView.findViewById(R.id.btn_fav);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChildViewHolder) convertView.getTag();
		}
		OnClickListener click = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_fav :
					showCancelDialog(favorite,group);
					break;
				case R.id.layout_fav_item:
					Intent intent = null;
					if(1==group.getType()) {
						intent = new Intent(context,MerchantDetailActivity.class);
						intent.putExtra(ParamsKey.MERCHANTID,favorite.getId());
						intent.putExtra("merchantName", favorite.getName());
						context.startActivity(intent);
					}else {
						intent = new Intent(context,NearShopDetailActivity.class);
						intent.putExtra("title", favorite.getName());
						intent.putExtra("phone",favorite.getPhone());
						intent.putExtra("address",favorite.getAddress());
						intent.putExtra("latitude",favorite.getLatitude());
						intent.putExtra("longitude",favorite.getLongitude());
						intent.putExtra("needHideFav",true);
						context.startActivity(intent);
					}
					break;
				default:
					break;
				}
			}
		};
		viewHolder.btn_fav.setOnClickListener(click);
		viewHolder.layout_fav_item.setOnClickListener(click);
		updateUI(viewHolder,favorite,group.getType());
		return convertView;
	}


	protected void showCancelDialog(final Favorite favorite, final FavoriteGroup group) {
		new ObdDialog(context).setTitle("温馨提示")
		.setMessage("您确认取消该收藏吗？")
		.setPositiveButton(context.getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.setNegativeButton(context.getResources().getString(R.string.btn_confirm), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				cancelStore(favorite,group);
			}
		}).show();
	}

	/**
	 * 收藏
	 * @param group
	 */
	private void cancelStore(final Favorite favorite,final FavoriteGroup group) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.MERCHANTID, favorite.getId()+"");
		params.put(ParamsKey.TYPE, "0");
		if(0==group.getType()) {
			params.put("category", "1");
		}
		NetRequest.requestUrl(context,
				ApiConfig.getRequestUrl(ApiConfig.Store_Merchant_Url), params,
				new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if (!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if (json.has("status")) {
							int status = json.getInt("status");
							if (1 == status) {
								Utils.showToast(context, "取消收藏成功");
								group.getList().remove(favorite);
								FavoriteAdapter.this.notifyDataSetChanged();
							} else {
								String message = json.getString("message");
								Utils.showToast(context, message);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
			}
		});
	}

	private void updateUI(ChildViewHolder viewHolder, Favorite favorite, int type) {
		if(favorite!=null) {
			
			if(1==type) {
				viewHolder.img_icon.setVisibility(View.VISIBLE);
				fb.display(viewHolder.img_icon, favorite.getImgUrl());
				if(0==favorite.getType()) {//普通
					viewHolder.img_auth_one.setVisibility(View.GONE);
				}else if(1==favorite.getType()) {//实地
					viewHolder.img_auth_one.setVisibility(View.VISIBLE);
					viewHolder.img_auth_one.setImageResource(R.drawable.ic_auth_merchant_two);
				}else if(2==favorite.getType()) {//金牌
					viewHolder.img_auth_one.setVisibility(View.VISIBLE);
					viewHolder.img_auth_one.setImageResource(R.drawable.ic_auth_merchant);
				}
				
				if(1==favorite.getSpecial()) {
					viewHolder.img_auth_two.setVisibility(View.VISIBLE);
				}else {
					viewHolder.img_auth_two.setVisibility(View.GONE);
				}
			}else {
				viewHolder.img_icon.setVisibility(View.GONE);
			}
			viewHolder.text_name.setText(favorite.getName());
			viewHolder.text_distance.setText(favorite.getDistance()+"公里");
			viewHolder.text_address.setText(favorite.getAddress());
			
		}

	}

	final static class ChildViewHolder {

		public LinearLayout layout_fav_item;
		public View img_icon;
		public TextView text_name;
		public ImageView img_auth_one;
		public TextView img_auth_two;
		public TextView text_address;
		public TextView text_distance;
		public Button btn_fav;

	}
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
