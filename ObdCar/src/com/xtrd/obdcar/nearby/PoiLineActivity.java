package com.xtrd.obdcar.nearby;


import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.LocationDecoder;

public class PoiLineActivity extends BaseActivity implements BaiduMap.OnMapClickListener,
OnGetRoutePlanResultListener {

	protected static final String TAG = "TripMapActivity";
	private MapView mMapView;
	private BaiduMap mBaidumap;
	private RoutePlanSearch mSearch;
	private String startLocation="",endLocation="";
	private BitmapDescriptor markerA;
	private BitmapDescriptor markerB;
	private LinearLayout layout_top_view;
	
	public PoiLineActivity() {
		layout_id = R.layout.activity_trip_map;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_driveing, 0,0);
		initView();
		registerClick();

		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);

		double eLat = getIntent().getDoubleExtra("endLat", 0);
		double eLon = getIntent().getDoubleExtra("endLng", 0);
		
		LocationDecoder.decodeLocation(SettingLoader.getCarLatLng(this),
				new LocationDecoder.LocationCallBack() {
			@Override
			public void callback(String str, AddressComponent addressComponent) {
				startLocation = str;
			}
		});
		LocationDecoder.decodeLocation(eLat, eLon,getIntent().getBooleanExtra("convert", false),
				new LocationDecoder.LocationCallBack() {
			@Override
			public void callback(String str, AddressComponent addressComponent) {
				endLocation = str;
			}
		});
		
		markerA = BitmapDescriptorFactory.fromResource(R.drawable.start);
		markerB = BitmapDescriptorFactory.fromResource(R.drawable.end);

		if(eLat==0||eLon==0) {
			return;
		}
		//设置起终点信息，对于tranist search 来说，城市名无意义
		PlanNode stNode = PlanNode.withLocation(SettingLoader.getCarLatLng(this));
		PlanNode enNode = PlanNode.withLocation(new LatLng(eLat,eLon));
		mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
		
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

	private void registerClick() {
		//地图点击事件处理
		if(mBaidumap!=null) {
			mBaidumap.setOnMapClickListener(this);
		}
	}

	private void initView() {
		layout_top_view = (LinearLayout)findViewById(R.id.layout_top_view);
		layout_top_view.setVisibility(View.GONE);
		
		mMapView = (MapView)findViewById(R.id.bmapView);
		mBaidumap = mMapView.getMap();
	}

	@Override
	public void onGetDrivingRouteResult(final DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(PoiLineActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			//起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			//result.getSuggestAddrInfo()
			return;
		}
		
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			final MyDrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
			mBaidumap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker marker) {
					if(marker.getIcon()==markerA) {
						showInfoWindow(marker.getPosition(),"车辆当前位置",startLocation);
					}else if(marker.getIcon()==markerB) {
						showInfoWindow(marker.getPosition(),getIntent().getStringExtra("endTitle"),endLocation);
					}
					return false;
				}

			});
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {

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
		mSearch.destroy();
		mMapView.onDestroy();
		super.onDestroy();
	}

	//定制RouteOverly
	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

		@Override
		public BitmapDescriptor getStartMarker() {
			return markerA;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			return markerB;
		}

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}
	}
}