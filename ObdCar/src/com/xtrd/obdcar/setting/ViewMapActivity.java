package com.xtrd.obdcar.setting;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.tumi.R;

public class ViewMapActivity extends BaseActivity {

	private MapView mMapView;
	private LinearLayout mainview;

	public ViewMapActivity() {
		layout_id = R.layout.activity_view_map;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		initTitle(0, R.drawable.btn_back_bg, intent.getStringExtra("title"), 0, 0);
		if (intent.hasExtra("x") && intent.hasExtra("y")) {
			Bundle b = intent.getExtras();
			LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));
			mMapView = new MapView(this,
					new BaiduMapOptions().mapStatus(new MapStatus.Builder()
					.target(p).build()));
		} else {
			mMapView = new MapView(this, new BaiduMapOptions());
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		mainview = (LinearLayout)findViewById(R.id.mainview);
		mainview.addView(mMapView,params);
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

	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();
	}


}
