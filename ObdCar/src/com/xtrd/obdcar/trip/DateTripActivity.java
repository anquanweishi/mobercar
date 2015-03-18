package com.xtrd.obdcar.trip;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.TripAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.Trip;
import com.xtrd.obdcar.passport.LoginActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.DateDialog;

public class DateTripActivity extends BaseActivity {

	protected static final String TAG = "TripActivity";
	private ListView listView;
	private ArrayList<Trip> list = new ArrayList<Trip>();
	private boolean refresh = false;
	private int pageIndex = 0;
	private boolean hasMore = true;
	private String date = TimeUtils.getCurrentTime();
	private TextView tips_text;
	private TextView text_date;
	private Button btn_select;

	public DateTripActivity() {
		layout_id = R.layout.activity_score_select;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_date_trip, 0,0);

		initView();
		if(!SettingLoader.hasLogin(this)) {
			Intent intent = new Intent(this,LoginActivity.class);
			startActivityForResult(intent, 1);
			return;
		}
		getTripInfoByDate(date);
	}


	private void initView() {
		tips_text = (TextView)findViewById(R.id.tips_text);
		text_date = (TextView)findViewById(R.id.text_date);
		btn_select = (Button)findViewById(R.id.btn_select);
		listView = (ListView)findViewById(R.id.listView);
		btn_select.setOnClickListener(this);
		text_date.setOnClickListener(this);
		text_date.setText(date);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				refresh = false;
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if(hasMore) {
							getTripInfoByDate(date);
						}else {
							Utils.showToast(DateTripActivity.this, R.string.has_no_more_data);
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK==resultCode) {
			switch (requestCode) {
			case 1:
				finish();
				break;

			default:
				break;
			}
		}
	
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.text_date:
			showDatePicker();
			break;
		case R.id.btn_select:
			refresh = true;
			pageIndex = 0;
			getTripInfoByDate(date);
			break;

		default:
			break;
		}
	}

	private void showDatePicker() {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date parse = format.parse(date);
			new DateDialog(this,parse)
			.setPositiveButton(new DateDialog.OnClickListener() {

				@Override
				public void onClick(String time) {
					pageIndex = 0;
					date = time;
					text_date.setText(date);
					list.clear();
					getTripInfoByDate(date);
				}
			}).show();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
	private void getTripInfoByDate(String time) {
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showToast(this, getResources().getString(R.string.network_unavailable_tips));
			return;
		}

		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.PageIndex, pageIndex+"");
		//		params.put(ParamsKey.PageSize, "30");
		params.put(ParamsKey.StartTime, time+" 00:00:00");
		params.put(ParamsKey.EndTime, time+" 23:59:59");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Trip_DATE_URL) , params,new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, t.toString());
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
												Trip trip = null;
												for(int i=0;i<array.length();i++) {
													trip = new Trip();
													trip.parser((JSONObject)array.opt(i));
													list.add(trip);
												}
												pageIndex++;
												hasMore = true;
											}
										}else {
											hasMore = false;
										}
									}else {
										hasMore = false;
									}
								}else {
									msg = json.getString("message");
									Utils.showToast(DateTripActivity.this, msg);
								}
							}
							updateUI();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				updateUI();
				super.onFailure(t, errorNo, strMsg);
			}

		});
	}
	
	protected void updateUI() {
		if(list==null||list.size()==0) {
			tips_text.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			tips_text.setText(date+getResources().getString(R.string.tips_date_no_trip));
		}else {
			tips_text.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			if(listView.getAdapter()==null) {
				TripAdapter adapter = new TripAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				((TripAdapter)listView.getAdapter()).notifyDataSetChanged();
			}
		}
	}
}
