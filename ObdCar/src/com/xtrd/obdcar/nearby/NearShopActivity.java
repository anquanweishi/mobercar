package com.xtrd.obdcar.nearby;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.adapter.NearShopAdapter;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.MyPoiInfo;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.LocationDecoder;
import com.xtrd.obdcar.utils.PoiUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class NearShopActivity extends BaseActivity {
	private ListView listView;
	private ImageButton btn_arrow; 
	private LinearLayout layout_item;
	private LinearLayout layout_mapview;
	private MapView mMapView;
	private NearShopAdapter adapter;

	private ArrayList<MyPoiInfo> list = new ArrayList<MyPoiInfo>();
	private static final int pageSize = 10;
	private int currentPage = 0;
	private double latitude;
	private double longitude;
	private BaiduMap baiduMap;
	private String title;
	private View view_interval;

	private int currentPosition=0;


	public NearShopActivity() {
		layout_id = R.layout.activity_subscription_map;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		title = getIntent().getStringExtra("keyword");
		initTitle(0, R.drawable.btn_back_bg,!StringUtils.isNullOrEmpty(title)?"附近"+title:getResources().getString(R.string.title_station),R.string.btn_detail,0);

		initView();
		latitude = getIntent().getDoubleExtra("latitude", 0);
		longitude = getIntent().getDoubleExtra("longitude", 0);
		getPoiDate();
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
		layout_item = (LinearLayout)findViewById(R.id.layout_display_item);
		view_interval = (View)findViewById(R.id.view_interval);
		layout_item.findViewById(R.id.btn_subscribe).setVisibility(View.GONE);
		btn_arrow.setOnClickListener(this);

		baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				for(int i=0;i<list.size();i++) {
					if(marker.getPosition().latitude==list.get(i).location.latitude&&marker.getPosition().longitude==list.get(i).location.longitude) {
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

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			intent = new Intent(this,NearShopDetailActivity.class);
			MyPoiInfo info = getSelectItemName();
			if(info!=null) {
				intent.putExtra("title",info.name);
				intent.putExtra("phone",info.phoneNum);
				intent.putExtra("address",info.address);
				intent.putExtra(ParamsKey.PoiId,info.uid);
				intent.putExtra("id",getIntent().getIntExtra("id",0));
				intent.putExtra("latitude",info.location.latitude);
				intent.putExtra("longitude",info.location.longitude);
				if(getIntent().getBooleanExtra("fromService", false)) {
					startActivity(intent);
				}else {
					startActivityForResult(intent,1);
				}
			}
			break;
		case R.id.btn_arrow:
			//动画隐藏和显示 
			if(View.VISIBLE == listView.getVisibility()) {
				btn_arrow.setImageResource(R.drawable.arrow_up);
				listView.setVisibility(View.GONE);
				layout_item.setVisibility(View.VISIBLE);
				view_interval.setVisibility(View.VISIBLE);
				updateItem(currentPosition);
			}else {
				btn_arrow.setImageResource(R.drawable.arrow_down);
				listView.setVisibility(View.VISIBLE);
				layout_item.setVisibility(View.GONE);
				view_interval.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK==resultCode) {
			switch (requestCode) {
			case 1:
				if(data.getBooleanExtra("finish", false)) {
					Intent intent = new Intent();
					intent.putExtra("finish", true);
					intent.putExtra("latitude",data.getDoubleExtra("latitude", 0));
					intent.putExtra("longitude",data.getDoubleExtra("longitude", 0));
					setResult(RESULT_OK, intent);
					finish();
				}
				break;

			default:
				break;
			}
		}
	}


	private MyPoiInfo getSelectItemName() {
		for(MyPoiInfo item : list) {
			if(item.isChecked()) {
				return item;
			}
		}
		return null;
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
			((TextView)layout_item.findViewById(R.id.text_name)).setText(list.get(position).name);
			((TextView)layout_item.findViewById(R.id.text_location)).setText(list.get(position).address);
			((TextView)layout_item.findViewById(R.id.text_distance)).setText(LocationDecoder.getDistance(SettingLoader.getCarLatLng(this), list.get(position).location));
		}else {
			((LinearLayout)layout_item.findViewById(R.id.text_index).getParent()).setVisibility(View.GONE);
		}
	}

	/**
	 * 搜索附近的加油站
	 */
	private void getPoiDate() {
		showLoading();
		if(latitude!=0&&longitude!=0) {
			PoiUtils.getPoiByNearby(title,currentPage, pageSize,new LatLng(latitude,longitude), new PoiUtils.PoiCallBack() {

				@Override
				public void callback(String key, List<PoiInfo> allPoi) {
					dismissLoading();
					if(allPoi!=null&&allPoi.size()>0) {
						currentPage++;
						for(PoiInfo info : allPoi) {
							list.add(copy(info));
						}
					}
					updateUI();
				}

				@Override
				public void callbackFail() {
					dismissLoading();
					updateUI();
				}
			});
		}else {
			if(SettingLoader.getCarLatLng(this)!=null) {
				PoiUtils.getPoiByNearby(title,currentPage, pageSize,SettingLoader.getCarLatLng(this), new PoiUtils.PoiCallBack() {

					@Override
					public void callback(String key, List<PoiInfo> allPoi) {
						dismissLoading();
						if(allPoi!=null&&allPoi.size()>0) {
							currentPage++;
							for(PoiInfo info : allPoi) {
								list.add(copy(info));
							}
						}
						updateUI();
					}

					@Override
					public void callbackFail() {
						dismissLoading();
						updateUI();
					}
				});
			}else {
				XtrdApp.getInstance().getCurrentLocation(new XtrdApp.LocationCallBack() {

					@Override
					public void callback(double longitude, double latitude, String city) {
						PoiUtils.getPoiByNearby(title,currentPage, pageSize,new LatLng(latitude,longitude), new PoiUtils.PoiCallBack() {

							@Override
							public void callback(String key, List<PoiInfo> allPoi) {
								dismissLoading();
								if(allPoi!=null&&allPoi.size()>0) {
									currentPage++;
									for(PoiInfo info : allPoi) {
										list.add(copy(info));
									}
								}
								updateUI();
							}

							@Override
							public void callbackFail() {
								dismissLoading();
								updateUI();
							}
						});
					}
				});
			}
		}
	}
	protected MyPoiInfo copy(PoiInfo info) {
		MyPoiInfo item = new MyPoiInfo();
		item.address = info.address;
		item.name = info.name;
		item.location = info.location;
		item.phoneNum = info.phoneNum;
		item.city = info.city;
		item.uid = info.uid;
		return item;
	}

	private void updateUI() {

		if(list==null||list.size()==0) {

		}else {
			list.get(currentPosition).setChecked(true);
			if(adapter==null) {
				adapter = new NearShopAdapter(this, list,new NearShopAdapter.ClickCallback() {

					@Override
					public void callback(MyPoiInfo item) {
						for(int i=0;i<list.size();i++) {
							if(item.location.latitude==list.get(i).location.latitude&&item.location.longitude==list.get(i).location.longitude) {
								currentPosition = i;
								list.get(currentPosition).setChecked(true);
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
					}
				});
				listView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
			processOverlay(currentPosition);
		}

	}

	private void processOverlay(int position) {
		baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(list.get(position).location));
		baiduMap.clear();
		MyPoiInfo item = null;
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
			point = item.location;
			//构建MarkerOption，用于在地图上添加Marker  
			option  = new MarkerOptions()  
			.position(point)  
			.icon(bitmap);  
			//在地图上添加Marker，并显示  
			baiduMap.addOverlay(option);
		}
	}
}
