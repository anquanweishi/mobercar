package com.xtrd.obdcar.self;

import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.adapter.GasStationAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.GasStation;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;

public class NearGasStationActivity extends BaseActivity {
	private ListView listView;
	private GasStationAdapter adapter;
	private TextView btn_distance_sort;
	private TextView btn_price_sort;
	
	private ArrayList<GasStation> list = new ArrayList<GasStation>();

	public NearGasStationActivity() {
		layout_id = R.layout.activity_near_gas_station;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_station,0,0);

		initView();
		regClick();
		getNearStation("0");
	}

	private void initView() {
		btn_distance_sort = (TextView)findViewById(R.id.btn_distance_sort);
		btn_price_sort = (TextView)findViewById(R.id.btn_price_sort);
		listView = (ListView)findViewById(R.id.listView);
	}
	
	private void regClick() {
		btn_distance_sort.setOnClickListener(this);
		btn_price_sort.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_distance_sort:
			btn_distance_sort.setBackgroundColor(getResources().getColor(R.color.gray));
			btn_price_sort.setBackgroundResource(R.drawable.listview_bg);
			list.clear();
			adapter.notifyDataSetChanged();
			getNearStation("0");
			break;
		case R.id.btn_price_sort:
			btn_price_sort.setBackgroundColor(getResources().getColor(R.color.gray));
			btn_distance_sort.setBackgroundResource(R.drawable.listview_bg);
			list.clear();
			adapter.notifyDataSetChanged();
			getNearStation("1");
			break;

		default:
			break;
		}
	}

	private void getNearStation(final String type) {
		if(SettingLoader.getCarLatLng(this)==null) {
			showLoading();
			XtrdApp.getInstance().getCurrentLocation(new XtrdApp.LocationCallBack(){

				@Override
				public void callback(double longitude, double latitude,
						String city) {
					AjaxParams params = new AjaxParams();
					params.put(ParamsKey.LONGITUDE,longitude+"");
					params.put(ParamsKey.LATITUDE,latitude+"");
					params.put(ParamsKey.TYPE,type);
					NetRequest.requestUrl(NearGasStationActivity.this, ApiConfig.getRequestUrl(ApiConfig.Subscrption_Url), params, new NetRequest.NetCallBack() {

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
			params.put(ParamsKey.TYPE,type);
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

				}
			});
		}
	}


	protected void updateUI() {
		if(list!=null&&list.size()>0) {
			adapter = new GasStationAdapter(this, list);
			listView.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}
	}
}
