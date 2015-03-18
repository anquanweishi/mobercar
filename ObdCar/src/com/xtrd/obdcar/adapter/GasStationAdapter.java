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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.GasStation;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.self.GasStationDetailActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.LocationDecoder;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
/**
 * 附近加油站
 * @author Administrator
 *
 */
public class GasStationAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<GasStation> list;

	public GasStationAdapter(Context context,ArrayList<GasStation> list) {
		this.context = context;
		this.list = list;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_near_station_item, null);
			viewHolder.layout_station_item = (LinearLayout) convertView.findViewById(R.id.layout_station_item);
			viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			viewHolder.text_type_92 = (TextView) convertView.findViewById(R.id.text_type_92);
			viewHolder.text_price_92 = (TextView) convertView.findViewById(R.id.text_price_92);
			viewHolder.text_distance = (TextView) convertView.findViewById(R.id.text_distance);
			viewHolder.text_type_95 = (TextView) convertView.findViewById(R.id.text_type_95);
			viewHolder.text_price_95 = (TextView) convertView.findViewById(R.id.text_price_95);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		updateUI(viewHolder,info);
		
		OnClickListener click = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.layout_station_item:
					Intent intent = new Intent(context,GasStationDetailActivity.class);
					intent.putExtra(ParamsKey.ID, info.getStationId());
					intent.putExtra("title", info.getName());
					intent.putExtra("from", true);
					context.startActivity(intent);
					break;
				default:
					break;
				}
			}
		};
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


	private void updateUI(ViewHolder viewHolder, GasStation info) {
		try {
			if(info!=null) {
				viewHolder.text_name.setText(info.getName());
				viewHolder.text_distance.setText(LocationDecoder.getDistance(SettingLoader.getCarLatLng(context),new LatLng(info.getLatitude(),info.getLongitude())));
				if(info.getPrices()!=null&&info.getPrices().size()>0) {
					if(info.getPrices().size()<2) {
						viewHolder.text_type_92.setText(info.getPrices().get(0).getName()+"#");
						viewHolder.text_price_92.setText(info.getPrices().get(0).getPrice()>=0?info.getPrices().get(0).getPrice()+"元":"未知");
						viewHolder.text_type_95.setText("");
						viewHolder.text_price_95.setText("");
					}else {
						viewHolder.text_type_92.setText(info.getPrices().get(0).getName()+"#");
						viewHolder.text_price_92.setText(info.getPrices().get(0).getPrice()>=0?info.getPrices().get(0).getPrice()+"元":"未知");
						viewHolder.text_type_95.setText(info.getPrices().get(1).getName()+"#");
						viewHolder.text_price_95.setText(info.getPrices().get(1).getPrice()>=0?info.getPrices().get(1).getPrice()+"元":"未知");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	final static class ViewHolder {
		public LinearLayout layout_station_item;
		public TextView text_name;
		public TextView text_type_92;
		public TextView text_price_92;
		public TextView text_distance;
		public TextView text_type_95;
		public TextView text_price_95;
	}
	
	public interface ClickCallback {
		void callback(GasStation item);
	}
}
