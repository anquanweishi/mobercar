package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.MyPoiInfo;
import com.xtrd.obdcar.nearby.PoiLineActivity;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.LocationDecoder;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
/**
 * 附近加油站
 * @author Administrator
 *
 */
public class NearShopAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<MyPoiInfo> list;
	private ClickCallback callback;

	public NearShopAdapter(Context context,ArrayList<MyPoiInfo> list,ClickCallback callback) {
		this.context = context;
		this.list = list;
		this.callback = callback;
	}

	
	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public MyPoiInfo getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final MyPoiInfo info = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_station_item, null);
			viewHolder.layout_station_item = (LinearLayout) convertView.findViewById(R.id.layout_station_item);
			viewHolder.text_index = (TextView) convertView.findViewById(R.id.text_index);
			viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			viewHolder.text_location = (TextView) convertView.findViewById(R.id.text_location);
			viewHolder.layout_price = (LinearLayout) convertView.findViewById(R.id.layout_price);
			viewHolder.btn_subscribe = (Button) convertView.findViewById(R.id.btn_subscribe);
			viewHolder.btn_nav = (ImageButton) convertView.findViewById(R.id.btn_nav);
			viewHolder.btn_nav.setVisibility(View.VISIBLE);
			viewHolder.btn_subscribe.setVisibility(View.GONE);
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
					for(MyPoiInfo item : list) {
						item.setChecked(false);
					}
					info.setChecked(true);
					NearShopAdapter.this.notifyDataSetChanged();
					if(callback!=null) {
						callback.callback(info);
					}
					break;
				case R.id.btn_nav:
					Intent intent = new Intent(context,PoiLineActivity.class);
					intent.putExtra("endLat", info.location.latitude);
					intent.putExtra("endLng", info.location.longitude);
					intent.putExtra("endTitle", info.name);
					intent.putExtra("convert", false);
					context.startActivity(intent);
					break;
				default:
					break;
				}
			}
		};
		viewHolder.btn_nav.setOnClickListener(click);
		viewHolder.layout_station_item.setOnClickListener(click);
		
		return convertView;
	}
	
	
	protected void subscribeStation(int stationId) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID, stationId+"");
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
								Utils.showToast(context, "订阅成功");
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


	private void updateUI(ViewHolder viewHolder, MyPoiInfo info) {
		if(info!=null) {
			viewHolder.text_name.setText(info.name);
			viewHolder.text_location.setText(info.address);
			if(StringUtils.isNullOrEmpty(info.getDistance())) {
				info.setDistance(LocationDecoder.getDistance(SettingLoader.getCarLatLng(context), info.location));
			}
			viewHolder.text_distance.setText(info.getDistance());
		
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
		public ImageButton btn_nav;
		public Button btn_subscribe;
		public LinearLayout layout_price;
	}
	
	public interface ClickCallback {
		void callback(MyPoiInfo item);
	}
}
