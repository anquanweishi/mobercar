package com.xtrd.obdcar;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.active.CarListActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.CacheManager;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.Trip;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.passport.LoginActivity;
import com.xtrd.obdcar.self.MyFavoriteActivity;
import com.xtrd.obdcar.self.MySubscriptionActivity;
import com.xtrd.obdcar.self.NotificationActivity;
import com.xtrd.obdcar.setting.MyReservationActivity;
import com.xtrd.obdcar.setting.SettingActivity;
import com.xtrd.obdcar.trip.TripMapActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.ImageUtils;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.ObdDialog;

public class PersonalActivity extends BaseActivity {

	protected static final String TAG = "PersonalActivity";
	private ImageView img_icon;
	private TextView text_phone,text_location,text_score,text_num,text_plate,text_range,text_box_range;
	private LinearLayout layout_track,layout_my_sub,layout_fav,layout_my_notify,layout_real_time;
	private TextView text_tips_login;

	public Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case XtrdApp.ID_SET_DEFAULT:
				getMyInfo();
				break;
			case XtrdApp.ID_CARINFO_GET:
				text_tips_login.setVisibility(View.GONE);
				getMyInfo();
				break;
			default:
				break;
			}
		}

	};

	public PersonalActivity() {
		layout_id = R.layout.activity_myself;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, 0, R.string.BaseTabActivity_PersonlCenter, 0, R.drawable.ic_setting_bg);
		initView();
		registerClick();
		XtrdApp.addHandler(handler);
		if(SettingLoader.hasLogin(this)) {
			getMyInfo();
			text_tips_login.setVisibility(View.GONE);
			if(!SettingLoader.hasBindBox(this)) {
				text_tips_login.setVisibility(View.VISIBLE);
				text_tips_login.setText(getResources().getString(R.string.text_tips_no_bind));
			}
		}else {
			updateUI(CacheManager.getInstance(this).getMyInfo("data_my_info.txt"));
			text_tips_login.setVisibility(View.VISIBLE);
			text_tips_login.setText(getResources().getString(R.string.text_tips_no_login));
		}
	}

	private void registerClick() {
		layout_track.setOnClickListener(this);
		layout_my_sub.setOnClickListener(this);
		layout_fav.setOnClickListener(this);
		layout_my_notify.setOnClickListener(this);
		layout_real_time.setOnClickListener(this);
	}



	private void initView() {
		text_tips_login = (TextView)findViewById(R.id.text_tips_login);

		img_icon = (ImageView)findViewById(R.id.img_icon);
		text_phone = (TextView)findViewById(R.id.text_phone);
		text_location = (TextView)findViewById(R.id.text_location);
		text_score = (TextView)findViewById(R.id.text_score);
		text_plate = (TextView)findViewById(R.id.text_plate);
		text_num = (TextView)findViewById(R.id.text_num);
		text_range = (TextView)findViewById(R.id.text_range);
		text_box_range = (TextView)findViewById(R.id.text_box_range);
		layout_track = (LinearLayout)findViewById(R.id.layout_track);
		layout_my_sub = (LinearLayout)findViewById(R.id.layout_my_sub);
		layout_fav = (LinearLayout)findViewById(R.id.layout_fav);
		layout_my_notify = (LinearLayout)findViewById(R.id.layout_my_notify);
		layout_real_time = (LinearLayout)findViewById(R.id.layout_real_time);

		//		FinalBitmap fb = FinalBitmap.create(this);
		//		fb.display(img_icon, ApiConfig.getRequestUrl(ApiConfig.Car_Logo_Url)+"?"+ParamsKey.Car_Branch+"="+SettingLoader.getBranchId(this));
		//		text_plate.setText(SettingLoader.getCarPlate(this));
		//		text_location.setText(SettingLoader.getCurrentCity(this));
	}


	private void updateUI(JSONObject json) {
		try {
			if(json!=null) {
				if(!SettingLoader.hasLogin(this)) {
					text_phone.setText("体验帐号");
					if(json.has("plateNumber")) {
						text_plate.setText(json.getString("plateNumber"));
					}
					img_icon.setImageResource(R.drawable.ic_my_unlogin);
				}else {
					if(json.has("nickName")) {
						text_phone.setText(json.getString("nickName"));
					}else {
						String phone = SettingLoader.getLoginName(this);
						if(Utils.isMobile(phone)) {
							text_phone.setText(phone.replace(phone.subSequence(4, 7), "*"));
						}else {
							text_phone.setText(phone);
						}
					}
					text_plate.setText(SettingLoader.getCarPlate(this));
					if(json.has("bid")) {
						ImageUtils.displayBranchImg(this, img_icon, json.getInt("bid"));
					}
				}

				

				if(json.has("bra")) {
					text_num.setText(json.getString("bra"));
				}
				if(json.has("ser")) {
					text_num.setText(text_num.getText().toString()+json.getString("ser"));
				}
				if(json.has("distance")) {
					text_range.setText(json.getInt("distance")+"公里");
				}
				if(json.has("my")) {
					text_box_range.setText(json.getInt("my")+"公里");
				}
				if(json.has("score")) {
					text_score.setText(json.getInt("score")+"积分");
				}
				if(json.has("hideTrip")) {
					SettingLoader.setTripHide(this, json.getInt("hideTrip")==0?false:true);
				}
				if(json.has("one")) {
					text_location.setText(json.getString("one"));
				}
				if(json.has("two")) {
					text_location.setText(text_location.getText().toString()+json.getString("two"));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		if(getParent()!=null) {
			getParent().onBackPressed();
		}
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_right:
			if(SettingLoader.hasLogin(this)) {
				intent = new Intent(this,SettingActivity.class);
				startActivity(intent);
			}else {
				intent = new Intent(this,LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.layout_track:
			if(SettingLoader.hasLogin(this)) {
				if(SettingLoader.hasBindBox(this)) {
					getLatestTrip();
				}else {
					showSimulateDialog();
				}
			}else {
				intent = new Intent(this,LoginActivity.class);
				startActivity(intent);
			}

			break;
		case R.id.layout_my_sub:
			if(SettingLoader.hasLogin(this)) {
				intent = new Intent(this,MyReservationActivity.class);
				startActivity(intent);
			}else {
				intent = new Intent(this,LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.layout_fav:
			if(SettingLoader.hasLogin(this)) {
				intent = new Intent(this,MyFavoriteActivity.class);
				startActivity(intent);
			}else {
				intent = new Intent(this,LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.layout_my_notify:
			if(SettingLoader.hasLogin(this)) {
				intent = new Intent(this,NotificationActivity.class);
				startActivity(intent);
			}else {
				intent = new Intent(this,LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.layout_real_time:
			if(SettingLoader.hasLogin(this)) {
				intent = new Intent(this,MySubscriptionActivity.class);
				startActivity(intent);
			}else {
				intent = new Intent(this,LoginActivity.class);
				startActivity(intent);
			}
			break;

		default:
			break;
		}
	}

	private void showSimulateDialog() {
		ObdDialog dialog = new ObdDialog(this).setTitle("温馨提示")
				.setMessage("您还没绑定盒子，可查看模拟数据。")
				.setPositiveButton("查看模拟", new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(PersonalActivity.this,TripMapActivity.class);
						startActivity(intent);
					}
				})
				.setNegativeButton("绑定盒子", new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(PersonalActivity.this,CarListActivity.class);
						startActivity(intent);
					}
				});
		if(!dialog.isShowing()) {
			dialog.show();
		}
	}

	private void getMyInfo() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.My_Info_Get_Url) ,params,new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json!=null) {
							if(json.has("status")) {
								int status = json.getInt("status");
								if (1 == status) {
									if(json.has("result")) {
										updateUI(json.getJSONObject("result"));
									}else {
										updateUI(null);
									}
								}
							}
						}
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

	private void getLatestTrip() {
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showToast(this, getResources().getString(R.string.network_unavailable_tips));
			return;
		}

		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		NetRequest.requestUrl(this,ApiConfig.getRequestUrl(ApiConfig.Trip_Load_Url) , params,new NetRequest.NetCallBack() {
			private String msg;
			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								json = json.getJSONObject("result");
								Trip trip = new Trip();
								trip.parser(json);
								gotoMap(trip);
							}
						}else {
							msg = json.getString("message");
							Utils.showToast(PersonalActivity.this, msg);
						}
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

	protected void gotoMap(final Trip trip) {
		if(!NetUtils.isWifiNet(this)) {
			new ObdDialog(this).setTitle("温馨提示")
			.setMessage("您的手机当前网络环境不是wifi，确定查看地图吗？")
			.setPositiveButton(getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.setNegativeButton("确定", new ObdDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(PersonalActivity.this,TripMapActivity.class);
					intent.putExtra("tripId", trip.getTripId());
					intent.putExtra("displayTrip", true);
					if(trip.getStartGps()!=null) {
						intent.putExtra("sLat", trip.getStartGps().getLatitude());
						intent.putExtra("sLon", trip.getStartGps().getLongitude());
						intent.putExtra("starttime", trip.getStartGps().getGpsTime());
					}
					if(trip.getEndGps()!=null) {
						intent.putExtra("eLat", trip.getEndGps().getLatitude());
						intent.putExtra("eLon", trip.getEndGps().getLongitude());
						intent.putExtra("endtime", trip.getEndGps().getGpsTime());
					}
					startActivity(intent);
				}
			}).show();
		}else {
			Intent intent = new Intent(PersonalActivity.this,TripMapActivity.class);
			intent.putExtra("tripId", trip.getTripId());
			intent.putExtra("displayTrip", true);
			if(trip.getStartGps()!=null) {
				intent.putExtra("sLat", trip.getStartGps().getLatitude());
				intent.putExtra("sLon", trip.getStartGps().getLongitude());
				intent.putExtra("starttime", trip.getStartGps().getGpsTime());
			}
			if(trip.getEndGps()!=null) {
				intent.putExtra("eLat", trip.getEndGps().getLatitude());
				intent.putExtra("eLon", trip.getEndGps().getLongitude());
				intent.putExtra("endtime", trip.getEndGps().getGpsTime());
			}
			startActivity(intent);
		}
	}

}
