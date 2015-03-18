package com.xtrd.obdcar.trip;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
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
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.TripActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.GPSInfo;
import com.xtrd.obdcar.obdservice.FindLocationActivity;
import com.xtrd.obdcar.obdservice.ReportActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.LocationDecoder;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.vc.DashBoardActivity;

public class TripMapActivity extends BaseActivity implements BaiduMap.OnMapClickListener {

	protected static final String TAG = "TripMapActivity";
	private static final int Interval_Time = 10*1000;
	private MapView mMapView;
	private BaiduMap mBaidumap;
	private String startLocation="",endLocation="";
	private BitmapDescriptor markerA;
	private BitmapDescriptor markerB;
	private BitmapDescriptor markerC;
	protected ArrayList<GPSInfo> list = new ArrayList<GPSInfo>();
	private List<LatLng> points = new ArrayList<LatLng>();

	private int currentTripId;//当前行程id
	private String gpsPreTime;
	private int carStatus;
	protected boolean tripChange = false;//trip是否变更
	private Timer timer;

	private ImageView btn_track_dashboard,btn_track_report,btn_track_car_location,btn_self_location;

	public TripMapActivity() {
		layout_id = R.layout.activity_trip_map;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean displayTrip = getIntent().getBooleanExtra("displayTrip", false);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_trip_line, displayTrip?R.string.title_trip:0, 0);
		initView();
		double sLat = getIntent().getDoubleExtra("sLat", 0);
		double sLon = getIntent().getDoubleExtra("sLon", 0);
		double eLat = getIntent().getDoubleExtra("eLat", 0);
		double eLon = getIntent().getDoubleExtra("eLon", 0);
		LocationDecoder.decodeLocation(sLat, sLon,true,
				new LocationDecoder.LocationCallBack() {
			@Override
			public void callback(String str, AddressComponent addressComponent) {
				startLocation = str;
			}
		});
		LocationDecoder.decodeLocation(eLat, eLon,true,
				new LocationDecoder.LocationCallBack() {
			@Override
			public void callback(String str, AddressComponent addressComponent) {
				endLocation = str;
			}
		});
		registerClick();
		carStatus = getIntent().getIntExtra("carStatus", 0);
		if(SettingLoader.hasLogin(this)&&SettingLoader.hasBindBox(this)) {
			if(2==carStatus) {
				getRunningGpsInfo();
			}else {
				getGpsInfo();
			}
		}else {
			getSimulationDate();
		}

		//地图点击事件处理
		if(mBaidumap!=null) {
			mBaidumap.setOnMapClickListener(this);
		}

	}


	/**
	 * 获取模拟gps数据
	 */
	private void getSimulationDate() {
		int tripId = getIntent().getIntExtra("tripId", 0);
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.TYPE, "gps");
		params.put(ParamsKey.Value, tripId+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Simulate_Url) , params,new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, t.toString());
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
										JSONArray array = json.getJSONArray("result");
										if(array!=null&&array.length()>0) {
											GPSInfo info = null;
											for(int i=0;i<array.length();i++) {
												info = new GPSInfo();
												info.parser((JSONObject)array.opt(i));
												list.add(info);
											}
										}
									}
								}else {
									msg = json.getString("message");
									Utils.showToast(TripMapActivity.this, msg);
								}
							}
							updateUI(false);
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
				Utils.showToast(TripMapActivity.this, "数据加载失败");
			}

		});
	}

	private void getRunningGpsInfo() {
		int tripId = getIntent().getIntExtra("tripId", 0);
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.TripId, tripId+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Trip_Current_Load_Url) , params,new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									json = json.getJSONObject("result");
									if(json.has("tripId")) {
										currentTripId = json.getInt("tripId");
									}
									if(json.has("gpsPreTime")) {
										gpsPreTime = json.getString("gpsPreTime");
									}
									LogUtils.e(TAG, "gpsPreTime " + gpsPreTime);
									if(json.has("gpsDatas")) {
										JSONArray array = json.getJSONArray("gpsDatas");
										if(array!=null&&array.length()>0) {
											GPSInfo info = null;
											for(int i=0;i<array.length();i++) {
												info = new GPSInfo();
												info.parser((JSONObject)array.opt(i));
												list .add(info);
											}
										}
									}
									startTimer();
								}else {
									msg = json.getString("message");
									Utils.showToast(TripMapActivity.this, msg);
								}
							}
							updateUI(false);
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
				Utils.showToast(TripMapActivity.this, "数据加载失败");
			}

		});
	}

	/**
	 * 定时获取更新的gps信息
	 */
	protected void startTimer() {
		if(timer!=null) {
			return;
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				getNextGpsInfo();
			}
		}, Interval_Time, Interval_Time);

	}

	private void getNextGpsInfo() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.TripId, currentTripId+"");
		params.put(ParamsKey.GpsPreTime, gpsPreTime);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Trip_Current_Time_Load_Url) , params,new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									json = json.getJSONObject("result");
									if(json.has("tripId")) {
										if(currentTripId!=json.getInt("tripId")) {
											tripChange  = true;
										}
										currentTripId = json.getInt("tripId");
									}
									if(json.has("gpsPreTime")) {
										gpsPreTime = json.getString("gpsPreTime");
									}
									LogUtils.e(TAG, "gpsPreTime " + gpsPreTime);
									if(json.has("gpsDatas")) {
										JSONArray array = json.getJSONArray("gpsDatas");
										if(tripChange) {
											list.clear();
											tripChange = false;
										}
										if(array!=null&&array.length()>0) {
											GPSInfo info = null;
											for(int i=0;i<array.length();i++) {
												info = new GPSInfo();
												info.parser((JSONObject)array.opt(i));
												list .add(info);
											}
										}
									}
								}else {
									msg = json.getString("message");
									Utils.showToast(TripMapActivity.this, msg);
								}
							}
							updateUI(true);
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
				Utils.showToast(TripMapActivity.this, "数据加载失败");
			}

		});
	}

	private void getGpsInfo() {
		int tripId = getIntent().getIntExtra("tripId", 0);
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.TripId, tripId+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Trip_Gps_Url) , params,new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									json = json.getJSONObject("result");

									if(json.has("data")) {
										JSONArray array = json.getJSONArray("data");
										if(array!=null&&array.length()>0) {
											GPSInfo info = null;
											for(int i=0;i<array.length();i++) {
												info = new GPSInfo();
												info.parser((JSONObject)array.opt(i));
												list.add(info);
											}
										}
									}
								}else {
									msg = json.getString("message");
									Utils.showToast(TripMapActivity.this, msg);
								}
							}
							updateUI(false);
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
				Utils.showToast(TripMapActivity.this, "数据加载失败");
			}

		});
	}


	protected void updateUI(boolean centerByLast) {
		if(mBaidumap==null) {
			return;
		}
		try {
			mBaidumap.clear();
			// 添加折线
			points.clear();
			if(list!=null&&list.size()>0) {
				LatLng latLng = null;
				GPSInfo info = null;
				for(int i=0;i<list.size();i++) {
					info = list.get(i);
					latLng =  new LatLng(info.getLatitude(), info.getLongitude());
					points.add(LocationDecoder.convertLatLng(latLng));
				}
			}

			if(StringUtils.isNullOrEmpty(endLocation)) {
				LocationDecoder.decodeLocation(list.get(list.size()-1).getLatitude(), list.get(list.size()-1).getLongitude(),true,
						new LocationDecoder.LocationCallBack() {
					@Override
					public void callback(String str, AddressComponent addressComponent) {
						endLocation = str;
					}
				});
			}

			OverlayOptions ooA = new MarkerOptions().position(points.get(0)).icon(markerA)
					.zIndex(9);
			//		mMarkerA = (Marker) (mBaidumap.addOverlay(ooA));
			mBaidumap.addOverlay(ooA);

			OverlayOptions ooB = new MarkerOptions().position(points.get(points.size()-1)).icon(2==carStatus?markerC:markerB)
					.zIndex(9);
			//		mMarkerB = (Marker) (mBaidumap.addOverlay(ooB));
			mBaidumap.addOverlay(ooB);

			if(2<=points.size()&&points.size()<=1000) {
				PolylineOptions ooPolyline = new PolylineOptions().width(Utils.dipToPixels(this, 3))
						.color(Color.BLUE).points(points);
				mBaidumap.addOverlay(ooPolyline);
			}else if(points.size()>1000){
				int count = points.size()%1000==0?points.size()/1000:points.size()/1000+1;
				PolylineOptions ooPolyline = null;
				for(int i=0;i<count;i++) {
					ooPolyline = new PolylineOptions().width(Utils.dipToPixels(this, 3))
							.color(Color.BLUE).points(points.subList((i==0?0:1000*1+1), (i==(count-1)?points.size():(1000*i+1000))));
					mBaidumap.addOverlay(ooPolyline);
				}
			}


			float level = LocationDecoder.getZoomLevel(list);
			LogUtils.e(TAG, "zoom level " + level);
			//overlook 俯视  zoom缩放
			MapStatus status = null;
			if(centerByLast) {
				GPSInfo gpsInfo = list.get(list.size()-1);
				LatLng center = LocationDecoder.convertLatLng(new LatLng(gpsInfo.getLatitude(),gpsInfo.getLongitude()));
				status = new MapStatus.Builder()
				.target(center)
				.overlook(0)
				//				.zoom(level)
				.build();
			}else {
				status = new MapStatus.Builder()
				.target(getCenterPoint(points))
				.overlook(0)
				.zoom(level)
				.build();
			}
			//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
			MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(status);
			//改变地图状态
			//		mBaidumap.setMapStatus(mMapStatusUpdate);
			mBaidumap.animateMapStatus(mMapStatusUpdate);

			mBaidumap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker marker) {
					if(marker.getIcon()==markerA) {
						showInfoWindow(marker.getPosition(),startLocation,getIntent().getStringExtra("starttime"));
					}else if(marker.getIcon()==markerB||marker.getIcon()==markerC) {
						String time = getIntent().getStringExtra("endtime");
						if(StringUtils.isNullOrEmpty(time)) {
							time = list.get(list.size()-1).getGpsTime();
						}
						showInfoWindow(marker.getPosition(),endLocation,time);
					}
					return false;
				}

			});
			LogUtils.e("ZoomLevel", mBaidumap.getMaxZoomLevel()+" min " + mBaidumap.getMinZoomLevel());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private LatLng getCenterPoint(List<LatLng> points) {
		LatLng start = LocationDecoder.getStart();
		LatLng end = LocationDecoder.getEnd();
		return new LatLng((start.latitude+end.latitude)/2, (start.longitude+end.longitude)/2);
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
			if(getIntent().getBooleanExtra("displayTrip", false)) {
				intent = new Intent(this,TripActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.btn_track_dashboard:
			if(!SettingLoader.hasLogin(this)) {
				Utils.showToast(this, "您还未登录，请登录后重试。");
				return;
			}
			if(!SettingLoader.hasBindBox(this)) {
				Utils.showToast(this, "您还未绑定盒子，请绑定后重试。");
				return;
			}
			intent = new Intent(this,DashBoardActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_track_report:
			if(!SettingLoader.hasLogin(this)) {
				Utils.showToast(this, "您还未登录，请登录后重试。");
				return;
			}
			if(!SettingLoader.hasBindBox(this)) {
				Utils.showToast(this, "您还未绑定盒子，请绑定后重试。");
				return;
			}
			intent = new Intent(this,ReportActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_track_car_location:
			if(!SettingLoader.hasLogin(this)) {
				Utils.showToast(this, "您还未登录，请登录后重试。");
				return;
			}
			if(!SettingLoader.hasBindBox(this)) {
				Utils.showToast(this, "您还未绑定盒子，请绑定后重试。");
				return;
			}
			intent = new Intent(this,FindLocationActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_self_location:
			if(!SettingLoader.hasLogin(this)) {
				Utils.showToast(this, "您还未登录，请登录后重试。");
				return;
			}
			if(!SettingLoader.hasBindBox(this)) {
				Utils.showToast(this, "您还未绑定盒子，请绑定后重试。");
				return;
			}
			intent = new Intent(this,FindLocationActivity.class);
			intent.putExtra("from", true);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void registerClick() {
		btn_track_dashboard.setOnClickListener(this);
		btn_track_report.setOnClickListener(this);
		btn_track_car_location.setOnClickListener(this);
		btn_self_location.setOnClickListener(this);
	}

	private void initView() {
		markerA = BitmapDescriptorFactory.fromResource(R.drawable.start);
		markerB = BitmapDescriptorFactory.fromResource(R.drawable.end);
		markerC = BitmapDescriptorFactory.fromResource(R.drawable.ic_findlocation);

		mMapView = (MapView)findViewById(R.id.bmapView);
		mBaidumap = mMapView.getMap();


		btn_track_report = (ImageView)findViewById(R.id.btn_track_dashboard);
		btn_track_dashboard = (ImageView)findViewById(R.id.btn_track_report);
		btn_track_car_location = (ImageView)findViewById(R.id.btn_track_car_location);
		btn_self_location = (ImageView)findViewById(R.id.btn_self_location);

		if(getIntent().getBooleanExtra("displaySingle", false)) {
			btn_track_report.setVisibility(View.GONE);
			btn_track_dashboard.setVisibility(View.GONE);
			btn_track_car_location.setVisibility(View.GONE);
			btn_self_location.setVisibility(View.GONE);
		}else {
			btn_track_report.setVisibility(View.VISIBLE);
			btn_track_dashboard.setVisibility(View.VISIBLE);
			btn_track_car_location.setVisibility(View.VISIBLE);
			btn_self_location.setVisibility(View.VISIBLE);
		}
	}

	private void showInfoWindow(LatLng latLng,String location,String time) {
		View view = getLayoutInflater().inflate(R.layout.layout_overlay, null);
		TextView text_location = (TextView) view.findViewById(R.id.text_location);
		TextView text_time = (TextView) view.findViewById(R.id.text_time);
		text_location.setText(location);
		text_time.setText(time);
		mBaidumap.showInfoWindow(new InfoWindow(view, latLng, null));
	}

	@Override
	public void onMapClick(LatLng point) {
		if(mBaidumap!=null) {
			mBaidumap.hideInfoWindow();
		}
	}

	@Override
	public boolean onMapPoiClick(MapPoi poi) {
		return false;
	}


	@Override
	protected void onStop() {
		if(timer!=null) {
			timer.cancel();
			timer = null;
		}
		super.onStop();
	}


	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		super.onDestroy();
	}


}
