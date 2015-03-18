package com.xtrd.obdcar.trip;

import java.math.BigDecimal;
import java.util.ArrayList;

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
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.GPSInfo;
import com.xtrd.obdcar.entity.Trip;
import com.xtrd.obdcar.entity.Velocity;
import com.xtrd.obdcar.passport.LoginActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.LocationDecoder;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class DriveScoreActivity extends BaseActivity {
	protected static final String TAG = "DriveScoreActivity";
	private TextView text_start,text_start_loc,text_end,text_end_loc,text_duation,text_distance;
	private GPSInfo startGps;
	private GPSInfo endGps;
	private TextView text_score;
	private RatingBar ratingbar;
	private TextView text_drive_level;
	private LinearLayout layout_lineview;
	protected Trip trip = new Trip();
	
	public DriveScoreActivity() {
		layout_id = R.layout.activity_drive_score;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		if(!SettingLoader.hasLogin(this)) {
			Intent intent = new Intent(this,LoginActivity.class);
			startActivityForResult(intent, 1);
			return;
		}
		
		boolean from = getIntent().getBooleanExtra("from", false);
		if(from) {
			initTitle(0, R.drawable.btn_back_bg,
					R.string.title_drive_safe, 0, R.drawable.ic_search_bg);
			getTrip();
		}else {
			initTitle(0, R.drawable.btn_back_bg,
					R.string.title_drive_safe, 0,0);
			trip.setTripId(getIntent().getIntExtra("tripId",0));
			startGps = getIntent().getParcelableExtra("startGps");
			endGps = getIntent().getParcelableExtra("endGps");
			trip.setStartGps(startGps);
			trip.setEndGps(endGps);
			trip.setDuration(getIntent().getLongExtra("duation", 0));
			trip.setDistance(getIntent().getDoubleExtra("distance", 0));
			trip.setDrivingScore(getIntent().getDoubleExtra("drivescore",0));
			showLoading();
			updateTop();
			getSpeedAndTime();
			getRank();
		}
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

	private void updateTop() {
		if(startGps!=null) {
			text_start.setText(String.format(getResources().getString(R.string.text_score_start_time),startGps.getGpsTime()));
			LocationDecoder.decodeLocation(startGps.getLatitude(), startGps.getLongitude(),true, new LocationDecoder.LocationCallBack() {
				
				@Override
				public void callback(String str, AddressComponent addressComponent) {
					text_start_loc.setText(String.format(getResources().getString(R.string.text_score_start_loc), str));
				}
			});
		}
		if(endGps!=null) {
			text_end.setText(String.format(getResources().getString(R.string.text_score_end_time), endGps.getGpsTime()));
			LocationDecoder.decodeLocation(endGps.getLatitude(), endGps.getLongitude(),true, new LocationDecoder.LocationCallBack() {
				
				@Override
				public void callback(String str, AddressComponent addressComponent) {
					text_end_loc.setText(String.format(getResources().getString(R.string.text_score_end_loc), str));
				}
			});
		}
		
		text_duation.setText(String.format(getResources().getString(R.string.text_drive_duation), TimeUtils.autoFormat(trip.getDuration())));
		text_distance.setText(String.format(getResources().getString(R.string.text_drive_distance), trip.getDistance()));
		text_score.setText(String.format(getResources().getString(R.string.text_drive_score), (int)trip.getDrivingScore()));
		ratingbar.setRating(5*(float)trip.getDrivingScore()/100);
	}

	private void initView() {
		text_start = (TextView)findViewById(R.id.text_start);
		text_start_loc = (TextView)findViewById(R.id.text_start_loc);
		text_end = (TextView)findViewById(R.id.text_end);
		text_end_loc = (TextView)findViewById(R.id.text_end_loc);
		text_duation = (TextView)findViewById(R.id.text_duation);
		text_distance = (TextView)findViewById(R.id.text_distance);
		
		layout_lineview = (LinearLayout)findViewById(R.id.layout_lineview);
		
		
		text_score = (TextView)findViewById(R.id.text_score);
		ratingbar = (RatingBar)findViewById(R.id.ratingbar);
		text_drive_level = (TextView)findViewById(R.id.text_drive_level);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			Intent intent = new Intent(this,ScoreSelectActivity.class);
			intent.putExtra("tripId", trip.getTripId());
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	
	protected void updateRank(double rank) {
		BigDecimal bg = new BigDecimal(rank);
		String percent = (int)(bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()*100)+"%";
		String str = String.format(getResources().getString(R.string.text_drive_rank),percent);
		SpannableString s = new SpannableString(str);
		s.setSpan(new ForegroundColorSpan(Color.RED), str.indexOf(percent), str.indexOf(percent)+percent.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(2.0f), str.indexOf(percent), str.indexOf(percent)+percent.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
		text_drive_level.setText(s);
	}
	
	private void updateLineView(ArrayList<Velocity> list) {
		if(list!=null&&list.size()>0) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,Utils.dipToPixels(this, 240));
			layout_lineview.addView(LineView.getLineView(this, list),params);
//			layout_lineview.addView(LineView.getBarChartView(this, list),params);
		}
	}
	
	/**
	 * 可不调用
	 */
	private void getTrip() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Service_Trip_Url) ,params,new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				super.onStart();
				showLoading();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, t);
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
									json = json.getJSONObject("result");
									trip = new Trip();
									trip.parser(json);
									startGps = trip.getStartGps();
									endGps = trip.getEndGps();
									updateTop();
									getSpeedAndTime();
									getRank();
								}else {
									msg = json.getString("message");
									Utils.showToast(DriveScoreActivity.this, msg);
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
				dismissLoading();
				Utils.showToast(DriveScoreActivity.this,getResources().getString(R.string.data_load_fail));
				super.onFailure(t, errorNo, strMsg);
			}

		});
	}


	
	private void getSpeedAndTime() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.TripId,trip.getTripId()+"");
//		params.put(ParamsKey.PageSize, "30");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Drive_URL) , params,new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, t);
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
									json = json.getJSONObject("result");
									if(json.has("data")) {
										JSONArray array = json.getJSONArray("data");
										if(array!=null&&array.length()>0) {
											ArrayList<Velocity> list = new ArrayList<Velocity>();
											Velocity veloctiy = null;
											for(int i=0;i<array.length();i++) {
												veloctiy = new Velocity();
												veloctiy.parser((JSONObject)array.opt(i));
												list.add(veloctiy);
											}
											updateLineView(list);
										}
									}
								}else {
									msg = json.getString("message");
									Utils.showToast(DriveScoreActivity.this, msg);
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
				dismissLoading();
			}

		});
	}
	private void getRank() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.TripId,trip.getTripId()+"");
//		params.put(ParamsKey.PageSize, "30");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Drive_Rank_URL) , params,new AjaxCallBack<String>() {
			
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
									double rank = json.getDouble("result");
									updateRank(rank);
								}else {
									msg = json.getString("message");
									Utils.showToast(DriveScoreActivity.this, msg);
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

	
}
