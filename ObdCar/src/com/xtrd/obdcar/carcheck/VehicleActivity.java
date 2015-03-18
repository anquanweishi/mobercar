package com.xtrd.obdcar.carcheck;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.CarHealthAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.SimpleTrip;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.DateDialog;

public class VehicleActivity extends BaseActivity {

	protected static final String TAG = "VehicleActivity";
	protected ArrayList<SimpleTrip> list = new ArrayList<SimpleTrip>();
	private String time = TimeUtils.getCurrentTime();
	private int pageIndex = 0;
	private boolean hasMore = true;
	private boolean refresh = true;
	private Button btn_select;
	private ListView listView;
	private TextView text_date;
	private TextView tips_text;

	public VehicleActivity() {
		layout_id = R.layout.activity_vehicle;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_car_search, 0,0);

		initView();
		
		getDetail(time);
	}

	private void initView() {
		text_date = (TextView)findViewById(R.id.text_date);
		btn_select = (Button)findViewById(R.id.btn_select);
		listView = (ListView)findViewById(R.id.listView);
		btn_select.setOnClickListener(this);
		text_date.setOnClickListener(this);
		text_date.setText(time);
		tips_text = (TextView)findViewById(R.id.tips_text);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if(hasMore) {
							refresh = false;
							pageIndex++;
							getDetail(time);
						}else {
							Utils.showToast(VehicleActivity.this, R.string.has_no_more_data);
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(list.get(position).getTroubleCodeNumber()>0) {
					Intent intent = new Intent(VehicleActivity.this,TroubleResultActivity.class);
					intent.putExtra("vehicleId", list.get(position).getVehicleId());
					intent.putExtra("from", true);
					startActivity(intent);
				}else {
					Intent intent = new Intent(VehicleActivity.this,CarCheckActivity.class);
					intent.putExtra("dataId", list.get(position).getDataId()+"");
					intent.putExtra("vehicleId", list.get(position).getVehicleId());
					intent.putExtra("troubleNum", list.get(position).getTroubleCodeNumber());
					intent.putExtra("from", true);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_select:
			refresh = true;
			list.clear();
			getDetail(time);
			break;
		case R.id.text_date:
			showDateDialog();
			break;
		default:
			break;
		}
	}


	protected void updateUI() {
		if(list==null||list.size()==0) {
			listView.setVisibility(View.GONE);
			tips_text.setVisibility(View.VISIBLE);
			tips_text.setText(String.format(getResources().getString(R.string.tips_car_check_by_date), time));
		}else {
			listView.setVisibility(View.VISIBLE);
			tips_text.setVisibility(View.GONE);
			if(listView.getAdapter()==null) {
				CarHealthAdapter adapter = new CarHealthAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				((CarHealthAdapter)listView.getAdapter()).notifyDataSetChanged();
			}
		}
	}

	private void showDateDialog() {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date parse = format.parse(time);
			new DateDialog(this,parse)
			.setPositiveButton(new DateDialog.OnClickListener() {

				@Override
				public void onClick(String date) {
					refresh = true;
					pageIndex = 0;
					time = date;
					text_date.setText(time);
					getDetail(time);
				}
			}).show();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}


	private void getDetail(String time) {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.SimpleMode, "yes");
		params.put(ParamsKey.PageIndex, pageIndex +"");
		params.put(ParamsKey.StartTime, time+" 00:00:00");
		params.put(ParamsKey.EndTime, time+" 23:59:59");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.OBD_Date_Get_Url) ,params, new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, "default "+t.toString());
				dismissLoading();
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

										if(refresh) {
											list.clear();
										}

										if(json.has("data")) {
											JSONArray array = json.getJSONArray("data");
											if(array!=null&&array.length()>0) {
												SimpleTrip trip = null;
												for(int i=0;i<array.length();i++) {
													trip = new SimpleTrip();
													trip.parser(array.optJSONObject(i));
													list.add(trip);
												}
												hasMore = true;
											}else {
												hasMore = false;
											}
										}
									}else {
										hasMore = false;
									}
									updateUI();
								}else {
									msg = json.getString("message");
									Utils.showToast(VehicleActivity.this, msg);
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
				Utils.showToast(VehicleActivity.this, getResources().getString(R.string.data_load_fail));
				dismissLoading();
			}

		});
	}

}
