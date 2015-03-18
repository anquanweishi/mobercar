package com.xtrd.obdcar.trip;

import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.FuelInfo;
import com.xtrd.obdcar.entity.FuelReport;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class FuelActivity extends BaseActivity {
	protected static final String TAG = "FuelActivity";
	protected FuelInfo fuelInfo;
	private ArrayList<FuelReport> list = new ArrayList<FuelReport>();


	private TextView text_fuel_amount;
	private TextView text_legend;
	private TextView text_avg_legend;
	private TextView text_avg_fuel;
	private TextView text_max_fuel;
	private TextView text_min_fuel;
	private LinearLayout layout_lineview_one;
	private LinearLayout layout_lineview_two;

	public FuelActivity() {
		layout_id = R.layout.activity_fuel;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_fuel_detail,0,0);
		initView();
		updateFuelInfo();
		getFuelDetail();
	}

	private void initView() {
		text_fuel_amount = (TextView)findViewById(R.id.text_fuel_amount);
		text_legend = (TextView)findViewById(R.id.text_legend);
		text_avg_legend = (TextView)findViewById(R.id.text_avg_legend);
		text_avg_fuel = (TextView)findViewById(R.id.text_avg_fuel);
		text_max_fuel = (TextView)findViewById(R.id.text_max_fuel);
		text_min_fuel = (TextView)findViewById(R.id.text_min_fuel);
		layout_lineview_one = (LinearLayout)findViewById(R.id.layout_lineview_one);
		layout_lineview_two = (LinearLayout)findViewById(R.id.layout_lineview_two);
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

	private void updateFuelInfo() {
		Intent intent = getIntent();
		double distance = intent.getDoubleExtra("distance", 0);
		long duration = intent.getLongExtra("duration", 0);
		double fuelAmount = intent.getDoubleExtra("fuelAmount", 0);
		long idleDuration = intent.getLongExtra("idleDuration", 0);
		double topSpeed = intent.getDoubleExtra("topSpeed", 0);
		double avgSpeed = intent.getDoubleExtra("avgSpeed", 0);
		
		text_fuel_amount.setText(String.format(getResources().getString(R.string.detail_fuel_distance), distance));
		text_legend.setText(String.format(getResources().getString(R.string.detail_fuel_duration), TimeUtils.autoFormat(duration)));
		text_avg_legend.setText(String.format(getResources().getString(R.string.detail_fuel_cost), fuelAmount));
		text_avg_fuel.setText(String.format(getResources().getString(R.string.detail_fuel_idle), TimeUtils.autoFormat(idleDuration)));
		text_max_fuel.setText(String.format(getResources().getString(R.string.detail_top_speed), topSpeed));
		text_min_fuel.setText(String.format(getResources().getString(R.string.detail_avg_speed), avgSpeed));
	}

	protected void updateReport() {
		layout_lineview_one.removeAllViews();
		layout_lineview_two.removeAllViews();
		if(list!=null&&list.size()>0) {
			LinearLayout.LayoutParams LParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,Utils.dipToPixels(this, 240));
			layout_lineview_one.addView(VelocityLineView.getLineView(this, list),LParams);
			LinearLayout.LayoutParams FParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,Utils.dipToPixels(this, 240));
			layout_lineview_two.addView(FuelCoseLineView.getLineView(this, list),FParams);
		}else {
			Utils.showToast(this, "没有找到数据哦！");
		}
	}


	private void getFuelDetail() {
		String startTime = getIntent().getStringExtra("startTime");
		String endTime = getIntent().getStringExtra("endTime");
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.StartTime, startTime);
		params.put(ParamsKey.EndTime, endTime);
		fh.configTimeout(5*1000);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Trip_Fuel_Detail_URL) ,params, new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				LogUtils.e(TAG, "getFuelByDate "+t.toString());
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
										if(json.has("data")) {
											JSONArray array = json.getJSONArray("data");
											list.clear();
											if(array!=null&&array.length()>0) {
												FuelReport report = null;
												for(int i=0;i<array.length();i++) {
													report = new FuelReport();
													report.parser(array.optJSONObject(i));
													list.add(report);
												}
											}
											updateReport();
										}
									}
								}else {
									msg = json.getString("message");
									Utils.showToast(FuelActivity.this, msg);
								}
								updateReport();
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
				Utils.showToast(FuelActivity.this,getResources().getString(R.string.data_load_fail));
				dismissLoading();
			}
		});
	}

}
