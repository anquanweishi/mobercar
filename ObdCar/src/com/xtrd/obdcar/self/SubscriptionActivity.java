package com.xtrd.obdcar.self;


import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.adapter.SubscriptionAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.GasStation;
import com.xtrd.obdcar.entity.OilPrice;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class SubscriptionActivity extends BaseActivity {
	private ListView listView;
	private ImageButton btn_arrow; 
	private LinearLayout layout_item;
	private LinearLayout layout_mapview;
	private MapView mMapView;
	private SubscriptionAdapter adapter;
	private ArrayList<GasStation> list = new ArrayList<GasStation>();
	private BaiduMap baiduMap;
	private View view_interval;
	private int currentPosition = 0;

	public SubscriptionActivity() {
		layout_id = R.layout.activity_subscription_map;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_station,R.string.btn_detail,0);

		initView();

		getNearbyGasStation();

	}

	private void initView() {
//		mMapView = (MapView)findViewById(R.id.bmapView);
		layout_mapview = (LinearLayout)findViewById(R.id.layout_mapview);
		BaiduMapOptions opt = new BaiduMapOptions();
		opt.zoomControlsEnabled(false);
		mMapView = new MapView(this, opt);
		layout_mapview.addView(mMapView);
		baiduMap = mMapView.getMap();
		listView = (ListView)findViewById(R.id.listView);
		btn_arrow = (ImageButton)findViewById(R.id.btn_arrow);
		view_interval = (View)findViewById(R.id.view_interval);
		layout_item = (LinearLayout)findViewById(R.id.layout_display_item);
		View btn_subscribe = layout_item.findViewById(R.id.btn_subscribe);
		btn_subscribe.setOnClickListener(this);
		btn_arrow.setOnClickListener(this);

		baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				for(int i=0;i<list.size();i++) {
					if(marker.getPosition().latitude==list.get(i).getLatlng().latitude&&marker.getPosition().longitude==list.get(i).getLatlng().longitude) {
						if(View.VISIBLE == listView.getVisibility()) {
							listView.setVisibility(View.GONE);
							layout_item.setVisibility(View.VISIBLE);
						}
						currentPosition = i;
						list.get(i).setChecked(true);
						updateItem(i);
						processOverlay(i);
					}else {
						list.get(i).setChecked(false);
					}
				}
				if(adapter!=null) {
					adapter.notifyDataSetChanged();
				}
				listView.setSelection(currentPosition);
				return false;
			}

		});

	}

	private void processOverlay(int position) {
		if(list!=null&&list.size()>0&&list.get(position)!=null) {
			MapStatus status = new MapStatus.Builder()
			.target(new LatLng(list.get(position).getLatitude(),list.get(position).getLongitude()))
			.zoom(baiduMap.getMaxZoomLevel())
			.build();
			baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(status));
			baiduMap.clear();
			GasStation item = null;
			//定义Maker坐标点  
			LatLng point = null;
			//构建Marker图标  
			//		BitmapDescriptor bitmap = BitmapDescriptorFactory  
			//				.fromResource(R.drawable.ic_poi_index);  
			LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.marker_view, null);
			BitmapDescriptor bitmap = null;
			OverlayOptions option = null;
			for(int i=0;i<list.size();i++) {
				if(position==i) {
					((TextView)view.getChildAt(0)).setGravity(Gravity.CENTER_HORIZONTAL);
					((TextView)view.getChildAt(0)).setPadding(0, Utils.dipToPixels(this, 5), 0, 0);
					((TextView)view.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
					((TextView)view.getChildAt(0)).setBackgroundResource(R.drawable.ic_current_marker);
				}else {
					((TextView)view.getChildAt(0)).setGravity(Gravity.CENTER);
					((TextView)view.getChildAt(0)).setPadding(0, 0, 0, 0);
					((TextView)view.getChildAt(0)).setTextColor(getResources().getColor(R.color.top_bar_color));
					((TextView)view.getChildAt(0)).setBackgroundResource(R.drawable.ic_poi_index);
				}
				((TextView)view.getChildAt(0)).setText((i+1)+"");
				bitmap =  BitmapDescriptorFactory.fromView(view);
				item = list.get(i);
				point = new LatLng(item.getLatitude(),item.getLongitude());
				//构建MarkerOption，用于在地图上添加Marker  
				option  = new MarkerOptions()  
				.position(point)
				.icon(bitmap);  
				//在地图上添加Marker，并显示  
				baiduMap.addOverlay(option);
			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			intent = new Intent(this,GasStationDetailActivity.class);
			intent.putExtra(ParamsKey.ID, getSelectItemId());
			intent.putExtra("title", getSelectItemName());
			startActivity(intent);
			break;
		case R.id.btn_arrow:
			//动画隐藏和显示 
			if(View.VISIBLE == listView.getVisibility()) {
				btn_arrow.setImageResource(R.drawable.arrow_up);
				listView.setVisibility(View.GONE);
				view_interval.setVisibility(View.VISIBLE);
				layout_item.setVisibility(View.VISIBLE);
				updateItem(currentPosition);
			}else {
				btn_arrow.setImageResource(R.drawable.arrow_down);
				listView.setVisibility(View.VISIBLE);
				layout_item.setVisibility(View.GONE);
				view_interval.setVisibility(View.GONE);
			}
			break;
		case R.id.btn_subscribe:
			if(list.get(currentPosition)!=null) {
				if(1==list.get(currentPosition).getAttent()) {
					cancelOrder(list.get(currentPosition));
				}else {
					subscribeStation(list.get(currentPosition));
				}
			}
			break;
		default:
			break;
		}
	}

	private int getSelectItemId() {
		for(GasStation item : list) {
			if(item.isChecked()) {
				return item.getStationId();
			}
		}
		return 0;
	}
	private String getSelectItemName() {
		for(GasStation item : list) {
			if(item.isChecked()) {
				return item.getName();
			}
		}
		return "";
	}

	/**
	 * 刷新一条
	 * @param gasStation
	 */
	private void updateItem(int position) {
		if(list!=null&&list.size()>0&&list.get(position)!=null) {
			((LinearLayout)layout_item.findViewById(R.id.text_index).getParent()).setVisibility(View.VISIBLE);
			((LinearLayout)layout_item.findViewById(R.id.text_index).getParent()).setBackgroundResource(R.drawable.ic_poi_index_solid);
			((TextView)layout_item.findViewById(R.id.text_index)).setTextColor(getResources().getColor(R.color.white));
			((TextView)layout_item.findViewById(R.id.text_name)).setTextColor(getResources().getColor(R.color.top_bar_color));
			((TextView)layout_item.findViewById(R.id.text_location)).setTextColor(getResources().getColor(R.color.top_bar_color));
			((TextView)layout_item.findViewById(R.id.text_distance)).setTextColor(getResources().getColor(R.color.top_bar_color));
			((TextView)layout_item.findViewById(R.id.text_index)).setText((position+1)+"");
			((TextView)layout_item.findViewById(R.id.text_name)).setText(list.get(position).getName());
			((TextView)layout_item.findViewById(R.id.text_location)).setText(list.get(position).getAddress());
			((TextView)layout_item.findViewById(R.id.text_distance)).setText(list.get(position).getDistance()+"公里");
			((Button)layout_item.findViewById(R.id.btn_subscribe)).setVisibility(View.VISIBLE);
			((Button) layout_item.findViewById(R.id.btn_subscribe))
					.setText(1 == list.get(position).getAttent() ? getResources()
							.getString(R.string.text_cancel_subscription)
							: getResources().getString(
									R.string.btn_subscribe));
			if(list.get(position).getPrices()!=null&&list.get(position).getPrices().size()>0) {
				View view = null;
				OilPrice price = null;
				((LinearLayout)layout_item.findViewById(R.id.layout_price)).removeAllViews();
				for(int i=0;i<list.get(position).getPrices().size();i++) {
					if(i>1){
						break;
					}
					price = list.get(position).getPrices().get(i);
					view = LayoutInflater.from(this).inflate(R.layout.layout_sub_price_item, null);
					((TextView)view.findViewById(R.id.text_oil_type)).setText(price.getName()+"#");
					((TextView)view.findViewById(R.id.text_oil_price)).setText(price.getPrice()>=0?price.getPrice()+"元":"未知");
					((LinearLayout)layout_item.findViewById(R.id.layout_price)).addView(view);
				}
			}
		}else {
			((LinearLayout)layout_item.findViewById(R.id.text_index).getParent()).setVisibility(View.GONE);
			((Button)layout_item.findViewById(R.id.btn_subscribe)).setVisibility(View.GONE);
		}
	}

	/**
	 * 搜索附近的加油站
	 */
	private void getNearbyGasStation() {
		if(SettingLoader.getCarLatLng(this)==null) {
			showLoading();
			XtrdApp.getInstance().getCurrentLocation(new XtrdApp.LocationCallBack(){

				@Override
				public void callback(double longitude, double latitude,
						String city) {
					AjaxParams params = new AjaxParams();
					params.put(ParamsKey.LONGITUDE,longitude+"");
					params.put(ParamsKey.LATITUDE,latitude+"");
					NetRequest.requestUrl(SubscriptionActivity.this, ApiConfig.getRequestUrl(ApiConfig.Subscrption_Url), params, new NetRequest.NetCallBack() {

						@Override
						public void sucCallback(String str) {
							try {
								if(!StringUtils.isNullOrEmpty(str)) {
									JSONObject json = new JSONObject(str);
									if(json.has("status")) {
										int status = json.getInt("status");
										if(1==status) {
											if(json.has("result")) {
												JSONArray array = json.getJSONArray("result");
												GasStation item = null;
												for(int i=0;i<array.length();i++) {
													item = new GasStation();
													item.parser(array.optJSONObject(i));
													item.setLatlng(new LatLng(item.getLatitude(),item.getLongitude()));
													list.add(item);
												}
											}
										}
									}
									updateUI();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void failCallback(int errorNo, String strMsg) {
							// TODO Auto-generated method stub

						}
					});
				}});
		}else {
			AjaxParams params = new AjaxParams();
			params.put(ParamsKey.LONGITUDE,SettingLoader.getCarLatLng(this).longitude+"");
			params.put(ParamsKey.LATITUDE,SettingLoader.getCarLatLng(this).latitude+"");
			NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Subscrption_Url), params, new NetRequest.NetCallBack() {

				@Override
				public void sucCallback(String str) {
					try {
						if(!StringUtils.isNullOrEmpty(str)) {
							JSONObject json = new JSONObject(str);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									if(json.has("result")) {
										JSONArray array = json.getJSONArray("result");
										GasStation item = null;
										for(int i=0;i<array.length();i++) {
											item = new GasStation();
											item.parser(array.optJSONObject(i));
											item.setLatlng(new LatLng(item.getLatitude(),item.getLongitude()));
											list.add(item);
										}
									}
								}
							}
							updateUI();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void failCallback(int errorNo, String strMsg) {
					// TODO Auto-generated method stub

				}
			});
		}
	}
	private void updateUI() {

		if(list==null||list.size()==0) {

		}else {
			list.get(currentPosition).setChecked(true);
			if(adapter==null) {
				adapter = new SubscriptionAdapter(this, list,new SubscriptionAdapter.ClickCallback() {

					@Override
					public void callback(GasStation item) {
						for(int i=0;i<list.size();i++) {
							if(item.getLatitude()==list.get(i).getLatitude()&&item.getLongitude()==list.get(i).getLongitude()) {
								currentPosition = i;
								list.get(currentPosition).setChecked(true);
								updateItem(i);
								processOverlay(i);
							}else {
								list.get(i).setChecked(false);
							}
							if(adapter!=null) {
								adapter.notifyDataSetChanged();
							}
							listView.setSelection(currentPosition);
						}
					}
				});
				listView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
			processOverlay(currentPosition);
		}

	}
	protected void subscribeStation(final GasStation info) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID, info.getStationId()+"");
		params.put(ParamsKey.TYPE, info.getAttent()==1?"0":"1");
		NetRequest.requestUrl(this,
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
								Utils.showToast(SubscriptionActivity.this, "订阅成功");
								((Button)layout_item.findViewById(R.id.btn_subscribe)).setText("取消订阅");

							} else {
								if(json.has("message")) {
									String msg = json.getString("message");
									Utils.showToast(SubscriptionActivity.this, msg);
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
		NetRequest.requestUrl(this,
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
								Utils.showToast(SubscriptionActivity.this, "取消成功");
								info.setAttent(0);
								((Button)layout_item.findViewById(R.id.btn_subscribe)).setText("订阅");
							} else {
								if(json.has("message")) {
									String msg = json.getString("message");
									Utils.showToast(SubscriptionActivity.this, msg);
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

}
