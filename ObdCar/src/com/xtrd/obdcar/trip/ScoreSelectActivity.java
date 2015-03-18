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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.TripScoreAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.Trip;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.DateDialog;

public class ScoreSelectActivity extends BaseActivity {

	protected static final String TAG = "ScoreSelectActivity";
	private TextView text_date;
	private ListView listView;
	private Button btn_select;
	private ArrayList<Trip> list = new ArrayList<Trip>();
	private String time = TimeUtils.getCurrentTime();
	private int pageIndex = 0;
	protected boolean hasMore = true;
	private boolean refresh = true;
	private TextView tips_text;

	public ScoreSelectActivity() {
		layout_id = R.layout.activity_score_select;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,
				R.string.title_drive_score_query, 0, 0);
		initView();

		showDateDialog();
	}

	private void initView() {
		tips_text = (TextView)findViewById(R.id.tips_text);
		text_date = (TextView)findViewById(R.id.text_date);
		btn_select = (Button)findViewById(R.id.btn_select);
		listView = (ListView)findViewById(R.id.listView);
		btn_select.setOnClickListener(this);
		text_date.setOnClickListener(this);
		text_date.setText(time);

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if(hasMore) {
							refresh = false;
							getTripInfoByDate(time);
						}else {
							Utils.showToast(ScoreSelectActivity.this, R.string.has_no_more_data);
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
				Trip trip = list.get(position);
				Intent intent = new Intent(ScoreSelectActivity.this,DriveScoreActivity.class);
				intent.putExtra("tripId",trip.getTripId());
				intent.putExtra("startGps", trip.getStartGps());
				intent.putExtra("endGps", trip.getEndGps());
				intent.putExtra("tripId", trip.getTripId());
				intent.putExtra("duation", trip.getDuration());
				intent.putExtra("distance", trip.getDistance());
				intent.putExtra("drivescore", trip.getDrivingScore());
				intent.putExtra("from", true);
				startActivity(intent);
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
			pageIndex = 0;
			getTripInfoByDate(time);
			break;
		case R.id.text_date:
			showDateDialog();
			break;
		default:
			break;
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
					getTripInfoByDate(time);
				}
			}).show();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void getTripInfoByDate(String time) {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.PageIndex, pageIndex +"");
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
				dismissLoading();
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
									if(refresh) {
										list.clear();
									}
									if(json.has("result")) {
										json = json.getJSONObject("result");
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
											}else {
												hasMore = false;
											}
										}
									}else {
										hasMore = false;
									}
								}else {
									msg = json.getString("message");
									Utils.showToast(ScoreSelectActivity.this, msg);
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				updateUI();
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dismissLoading();
				Utils.showToast(ScoreSelectActivity.this,getResources().getString(R.string.data_load_fail));
			}

		});
	}

	protected void updateUI() {
		if(list==null||list.size()==0) {
			tips_text.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			tips_text.setText(time+getResources().getString(R.string.tips_date_no_score));
		}else {
			tips_text.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			if(listView.getAdapter()==null) {
				TripScoreAdapter adapter = new TripScoreAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				((TripScoreAdapter)listView.getAdapter()).notifyDataSetChanged();
			}
		}
	}

}
