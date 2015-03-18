package com.xtrd.obdcar.self;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.GasCommentAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.GasStation;
import com.xtrd.obdcar.entity.OilPrice;
import com.xtrd.obdcar.nearby.NearCategoryActivity;
import com.xtrd.obdcar.nearby.PoiLineActivity;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.oil.FriendOilReportActivity;
import com.xtrd.obdcar.oil.OilReportActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.LocationDecoder;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

/**
 * from nearby 不带地图
 * 
 * @author Administrator
 * 
 */
public class GasStationDetailActivity extends BaseActivity {

	private static final int ID_OIL_ADD = 1;
	private TextView text_name;
	private TextView text_address;
	private TextView text_distance;
	private TextView text_report_count;

	private LinearLayout layout_nearby_report,layout_nearby;

	private GasStation item = new GasStation();
	private ListView listView;
	private Button btn_report_price;
	private LinearLayout layout_price;
	private GasCommentAdapter adapter;
	private MapView mapView;
	private BaiduMap mBaidumap;

	public GasStationDetailActivity() {
		layout_id = R.layout.activity_oil_station_detail;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String title = getIntent().getStringExtra("title");
		initTitle(0,R.drawable.btn_back_bg,StringUtils.isNullOrEmpty(title) ? getResources().getString(R.string.title_gas_station) : title, R.string.btn_nav,0);

		initView();
		regClick();
		getDetail();
	}

	private void initView() {
		
		mapView = (MapView)findViewById(R.id.bmapView);
		
		listView = (ListView)findViewById(R.id.listView);
		View view = LayoutInflater.from(this).inflate(R.layout.layout_gas_station_top, null);
		view.setBackgroundResource(R.drawable.listview_bg);
		listView.addHeaderView(view);
		adapter = new GasCommentAdapter(this, item.getComments());
		listView.setAdapter(adapter);
		
		text_name = (TextView) view.findViewById(R.id.text_name);
		text_address = (TextView) view.findViewById(R.id.text_address);
		text_distance = (TextView) view.findViewById(R.id.text_distance);
		btn_report_price = (Button) view.findViewById(R.id.btn_report_price);
		layout_price = (LinearLayout) view.findViewById(R.id.layout_price);
		text_report_count = (TextView) view.findViewById(R.id.text_report_count);
		layout_nearby_report = (LinearLayout) view.findViewById(R.id.layout_nearby_report);
		layout_nearby = (LinearLayout) view.findViewById(R.id.layout_nearby);
		if(getIntent().getBooleanExtra("from", false)) {
			mapView.setVisibility(View.VISIBLE);
			btn_report_price.setVisibility(View.GONE);
		}
	}
	
	

	private void regClick() {
		layout_nearby.setOnClickListener(this);
		layout_nearby_report.setOnClickListener(this);
		btn_report_price.setOnClickListener(this);
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
			intent = new Intent(this,PoiLineActivity.class);
			intent.putExtra("endLat", item.getLatitude());
			intent.putExtra("endLng", item.getLongitude());
			intent.putExtra("endTitle", item.getName());
			intent.putExtra("convert", false);
			startActivity(intent);
			break;
		case R.id.layout_nearby:
			intent = new Intent(this,NearCategoryActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_nearby_report:
			intent = new Intent(this,FriendOilReportActivity.class);
			intent.putExtra(ParamsKey.ID, item.getStationId());
			intent.putExtra("title", item.getName());
			intent.putExtra("address", item.getAddress());
			intent.putExtra("distance", item.getDistance()+"公里");
			startActivity(intent);
			break;
		case R.id.btn_report_price:
			intent = new Intent(this,OilReportActivity.class);
			intent.putExtra("gasStation", item);
			intent.putExtra("displayOne", true);
			startActivityForResult(intent, ID_OIL_ADD);
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
			case ID_OIL_ADD:
				if(item!=null) {
					item.setReportcount(item.getReportcount()+1);
					text_report_count.setText(item.getReportcount()+"");
				}
				break;

			default:
				break;
			}
		}
	}

	private void getDetail() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID,getIntent().getIntExtra(ParamsKey.ID, 0)+"");
		if(SettingLoader.getCarLatLng(this)!=null) {
			params.put(ParamsKey.LATITUDE,SettingLoader.getCarLatLng(this).latitude+"");
			params.put(ParamsKey.LONGITUDE,SettingLoader.getCarLatLng(this).longitude+"");
		}
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.GasStation_Detail_Url), params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								if(json.has("result")) {
									json = json.getJSONObject("result");
									item.parser(json);
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

			}
		});
	}

	protected void updateUI() {
		text_name.setText(item.getName());
		text_address.setText(item.getAddress());
		text_distance.setText(item.getDistance()+"公里");
		text_report_count.setText(item.getReportcount()+"");
		if(item.getPrices()!=null&&item.getPrices().size()>0) {
			View view = null;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			params.weight = 1;
			OilPrice price = null;
			layout_price.removeAllViews();
			for(int i=0;i<item.getPrices().size();i++) {
				price = item.getPrices().get(i);
				view = LayoutInflater.from(this).inflate(R.layout.layout_price_for_gas_station, null);
				((TextView)view.findViewById(R.id.text_oil_type)).setText(price.getName()+"#");
				((TextView)view.findViewById(R.id.text_oil_price)).setText(price.getPrice()>=0?price.getPrice()+"元":"未知");
				layout_price.addView(view,params);
				if(i<item.getPrices().size()-1) {
					addLine();
				}
			}
		}
		if(getIntent().getBooleanExtra("from", false)) {
			mBaidumap = mapView.getMap();
			final LatLng latlng = LocationDecoder.convertLatLng(new LatLng(item.getLatitude(), item.getLongitude()));
			MapStatus status = new MapStatus.Builder()
			.target(latlng)
			.overlook(0)
			.zoom(mBaidumap.getMaxZoomLevel())
			.build();
			MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(status);
			mBaidumap.animateMapStatus(u);
			BitmapDescriptor bdA = BitmapDescriptorFactory
					.fromResource(R.drawable.ic_findlocation);
			OverlayOptions ooA = new MarkerOptions().position(latlng).icon(bdA)
					.zIndex(9);
			mBaidumap.addOverlay(ooA);
			mBaidumap.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker arg0) {
					showInfoWindow(latlng,item,item.getAddress());
					return false;
				}

			});
		}
		
	}
	
	private void showInfoWindow(LatLng latlng, GasStation info,String location) {
		View view = getLayoutInflater().inflate(R.layout.layout_overlay, null);
		TextView text_location = (TextView) view.findViewById(R.id.text_location);
		TextView text_time = (TextView) view.findViewById(R.id.text_time);
		text_location.setText(location);
		text_time.setText("距离："+info.getDistance()+"公里");
		mBaidumap.showInfoWindow(new InfoWindow(view, latlng, null));
	}

	private void addLine() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dipToPixels(this, 1),LayoutParams.MATCH_PARENT);
		View view = new View(this);
		view.setBackgroundColor(getResources().getColor(R.color.interval));
		layout_price.addView(view,params);
	}
	
}
