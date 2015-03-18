package com.xtrd.obdcar.obdservice;


import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.crashlytics.android.Crashlytics;
import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.GPSInfo;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.LocationDecoder;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class FindLocationActivity extends BaseActivity implements BaiduMap.OnMapClickListener{

	protected static final String TAG = "FindLocationActivity";
	private MapView mMapView;
	private BaiduMap mBaidumap;
	private String location;
	private boolean from;//是否定位人的位置 true 为定位人的位置
	private LinearLayout layout_top_view;
	public FindLocationActivity() {
		layout_id = R.layout.activity_trip_map;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String title = getIntent().getStringExtra("title");
		initTitle(0, R.drawable.btn_back_bg,StringUtils.isNullOrEmpty(title)?"当前位置":title , 0, 0);
		initView();
		from = getIntent().getBooleanExtra("from", false);
		if(from) {
			getSelfLocation();
		}else {
			getGpsInfo();
		}
	}

	private void getSelfLocation() {
		XtrdApp.getInstance().getCurrentLocation(new XtrdApp.LocationAddressCallBack() {
			
			@Override
			public void callback(double longitude, double latitude, String detail) {
				location = detail;
				GPSInfo info = new GPSInfo();
				info.setLatitude(latitude);
				info.setLongitude(longitude);
				info.setGpsTime(TimeUtils.getCurrentTime());
				updateUI(info,false);
				
			}
		});
		
	}

	private void initView() {
		layout_top_view = (LinearLayout)findViewById(R.id.layout_top_view);
		layout_top_view.setVisibility(View.GONE);
		
		mMapView = (MapView)findViewById(R.id.bmapView);
		mBaidumap = mMapView.getMap();
		
		
		if(mBaidumap!=null) {
			mBaidumap.setOnMapClickListener(this);
		}
	}

	protected void updateUI(final GPSInfo info, boolean convert) {
		if(info==null||mBaidumap==null) {
			return;
		}
		
		Crashlytics.setDouble("latitude", info.getLatitude());
		Crashlytics.setDouble("longitude", info.getLongitude());
		
		if(!from) {
			LocationDecoder.decodeLocation(info.getLatitude(), info.getLongitude(),convert,
					new LocationDecoder.LocationCallBack() {
				@Override
				public void callback(String str, AddressComponent addressComponent) {
					location = str;
					Crashlytics.setString("currentCity", location);
					
				}
			});
		}

		final LatLng latlng = LocationDecoder.convertLatLng(new LatLng(info.getLatitude(), info.getLongitude()));
		MapStatus status = new MapStatus.Builder()
		.target(latlng)
		.overlook(0)
		.zoom(mBaidumap.getMaxZoomLevel())
		.build();
		MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(status);
		mBaidumap.animateMapStatus(u);
		BitmapDescriptor bdA = BitmapDescriptorFactory
				.fromResource(from?R.drawable.ic_self_location:R.drawable.ic_findlocation);
		OverlayOptions ooA = new MarkerOptions().position(latlng).icon(bdA)
				.zIndex(9);
		mBaidumap.addOverlay(ooA);
		mBaidumap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				showInfoWindow(latlng,info,location);
				return false;
			}

		});
	}


	private void showInfoWindow(LatLng latlng, GPSInfo info,String location) {
		View view = getLayoutInflater().inflate(R.layout.layout_overlay, null);
		TextView text_location = (TextView) view.findViewById(R.id.text_location);
		TextView text_time = (TextView) view.findViewById(R.id.text_time);
		text_location.setText(location);
		text_time.setText(info.getGpsTime());
		mBaidumap.showInfoWindow(new InfoWindow(view, latlng, null));
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;

		default:
			break;
		}
	}


	private void getGpsInfo() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this)+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Gps_Url) , params,new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, t);
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									if(json.has("result")) {
										json = json.getJSONObject("result");
										GPSInfo info = new GPSInfo();
										info.parser(json);
										updateUI(info,true);
									}
								}else {
									msg = json.getString("message");
									Utils.showToast(FindLocationActivity.this, msg);
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
			}

		});
	}

	@Override
	public void onMapClick(LatLng arg0) {
		if(mBaidumap!=null) {
			mBaidumap.hideInfoWindow();
		}
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}

}
