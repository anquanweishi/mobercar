package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.GasStation;
import com.xtrd.obdcar.entity.OilPrice;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
/**
 * 附近加油站
 * @author Administrator
 *
 */
public class SubscriptionAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<GasStation> list;
	private ClickCallback callback;

	public SubscriptionAdapter(Context context,ArrayList<GasStation> list,ClickCallback callback) {
		this.context = context;
		this.list = list;
		this.callback = callback;
	}

	
	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public GasStation getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final GasStation info = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_station_item, null);
			viewHolder.layout_station_item = (LinearLayout) convertView.findViewById(R.id.layout_station_item);
			viewHolder.text_index = (TextView) convertView.findViewById(R.id.text_index);
			viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			viewHolder.text_location = (TextView) convertView.findViewById(R.id.text_location);
			viewHolder.layout_price = (LinearLayout) convertView.findViewById(R.id.layout_price);
			viewHolder.btn_subscribe = (Button) convertView.findViewById(R.id.btn_subscribe);
			viewHolder.text_distance = (TextView) convertView.findViewById(R.id.text_distance);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		viewHolder.text_index.setText((position+1)+"");
		updateUI(viewHolder,info);
		
		OnClickListener click = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.layout_station_item:
					for(GasStation item : list) {
						item.setChecked(false);
					}
					info.setChecked(true);
					SubscriptionAdapter.this.notifyDataSetChanged();
					if(callback!=null) {
						callback.callback(info);
					}
					break;
				case R.id.btn_subscribe:
					if(1==info.getAttent()) {
						cancelOrder(info);
					}else {
						subscribeStation(info);
					}
					break;
				default:
					break;
				}
			}
		};
		viewHolder.btn_subscribe.setOnClickListener(click);
		viewHolder.layout_station_item.setOnClickListener(click);
		
		return convertView;
	}
	
	
	protected void subscribeStation(final GasStation info) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID, info.getStationId()+"");
		params.put(ParamsKey.TYPE, info.getAttent()==1?"0":"1");
		NetRequest.requestUrl(context,
				ApiConfig.getRequestUrl(ApiConfig.Subscribe_Url), params,
				new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if (!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if (json.has("status")) {
							int status = json.getInt("status");
							if (1 == status) {
								info.setAttent(1);
								Utils.showToast(context, "订阅成功");
								SubscriptionAdapter.this.notifyDataSetChanged();

							} else {
								if(json.has("message")) {
									String msg = json.getString("message");
									Utils.showToast(context, msg);
								}
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
	
	protected void cancelOrder(final GasStation info) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID, info.getStationId()+"");
		NetRequest.requestUrl(context,
				ApiConfig.getRequestUrl(ApiConfig.Cancel_Subscrption_Url), params,
				new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if (!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if (json.has("status")) {
							int status = json.getInt("status");
							if (1 == status) {
								Utils.showToast(context, "取消成功");
								info.setAttent(0);
								list.remove(info);
								SubscriptionAdapter.this.notifyDataSetChanged();
							} else {
								if(json.has("message")) {
									String msg = json.getString("message");
									Utils.showToast(context, msg);
								}
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


	private void updateUI(ViewHolder viewHolder, GasStation info) {
		if(info!=null) {
			viewHolder.text_name.setText(info.getName());
			viewHolder.text_location.setText(info.getAddress());
			viewHolder.text_distance.setText(info.getDistance()+"公里");
			if(info.getPrices()!=null&&info.getPrices().size()>0) {
				View view = null;
				OilPrice price = null;
				viewHolder.layout_price.removeAllViews();
				for(int i=0;i<info.getPrices().size();i++) {
					if(i>1){
						break;
					}
					price = info.getPrices().get(i);
					view = LayoutInflater.from(context).inflate(R.layout.layout_sub_price_item, null);
					((TextView)view.findViewById(R.id.text_oil_type)).setText(price.getName()+"#");
					((TextView)view.findViewById(R.id.text_oil_price)).setText(price.getPrice()>=0?price.getPrice()+"元":"未知");
					viewHolder.layout_price.addView(view);
				}
			}
			if(1==info.getAttent()) {
				viewHolder.btn_subscribe.setText("取消订阅");
			}else {
				viewHolder.btn_subscribe.setText("订阅");
			}
			
			if(info.isChecked()) {
				((LinearLayout)viewHolder.text_index.getParent()).setBackgroundResource(R.drawable.ic_poi_index_solid);
				viewHolder.text_index.setTextColor(context.getResources().getColor(R.color.white));
				viewHolder.text_name.setTextColor(context.getResources().getColor(R.color.top_bar_color));
				viewHolder.text_location.setTextColor(context.getResources().getColor(R.color.top_bar_color));
				viewHolder.text_distance.setTextColor(context.getResources().getColor(R.color.top_bar_color));
			}else {
				((LinearLayout)viewHolder.text_index.getParent()).setBackgroundResource(R.drawable.ic_poi_index);
				viewHolder.text_index.setTextColor(context.getResources().getColor(R.color.top_bar_color));
				viewHolder.text_name.setTextColor(context.getResources().getColor(R.color.text_gray_color));
				viewHolder.text_location.setTextColor(context.getResources().getColor(R.color.text_gray_color));
				viewHolder.text_distance.setTextColor(context.getResources().getColor(R.color.text_gray_color));
			}
		}
	
	}
	
	
	final static class ViewHolder {
		public LinearLayout layout_station_item;
		public TextView text_index;
		public TextView text_name;
		public TextView text_location;
		public TextView text_distance;
		public Button btn_subscribe;
		public LinearLayout layout_price;
	}
	
	public interface ClickCallback {
		void callback(GasStation item);
	}
}
