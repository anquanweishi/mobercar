package com.xtrd.obdcar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.xtrd.obdcar.active.AddCarActivity;
import com.xtrd.obdcar.active.CarListActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.BarItem;
import com.xtrd.obdcar.entity.CarInfo;
import com.xtrd.obdcar.entity.LimitAndOil;
import com.xtrd.obdcar.entity.Weather;
import com.xtrd.obdcar.merchant.MerchantActivity;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.obdservice.CarUseReportActivity;
import com.xtrd.obdcar.obdservice.OneKeyHelpActivity;
import com.xtrd.obdcar.obdservice.ReportActivity;
import com.xtrd.obdcar.obdservice.UserDetailActivity;
import com.xtrd.obdcar.passport.LoginActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.ImageUtils;
import com.xtrd.obdcar.utils.LocationDecoder;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.vc.ProfessorQAActivity;
import com.xtrd.obdcar.view.Bars;
import com.xtrd.obdcar.view.CarLimitItem;
import com.xtrd.obdcar.view.PopupSpinner;
import com.xtrd.obdcar.view.WeatherView;

public class HomeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

	private static final String TAG = "HomeActivity";

	//jpush
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.xtrd.obdcar.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	private CarInfo car;

	public HomeActivity() {
		layout_id = R.layout.activity_home;
	}

	private SwipeRefreshLayout swipeLayout;
	//2.0
	private LinearLayout layout_has_car,layout_add_car,layout_order_maintain;
	private LinearLayout layout_weather_for_home;
	private TextView text_location,text_time,text_exponent;
	private LinearLayout layout_weather, layout_limit_oil,layout_bar;
	private ImageView img_car;
	private TextView text_car;
	private TextView text_car_plate;
	
	private Bars bars;


	public Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case XtrdApp.ID_SET_DEFAULT:
				startRefresh();
				break;
			case XtrdApp.ID_LOGIN:
				startRefresh();
				break;
			case XtrdApp.ID_BIND_CAR:
				startRefresh();
				break;
			default:
				break;
			}
		}

	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initTitle(0, R.drawable.ic_car_list_for_left,
				R.string.app_name, 0,R.drawable.ic_add_car_for_top);
		initView();
		regListener();
		updateCar();
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}

		registerMessageReceiver();  // used for receive msg
		JPushInterface.init(this);
		XtrdApp.addHandler(handler);

		startRefresh();
	}
	
	
	private void startRefresh() {
		try {
			Method refresh = null;
			Method[] methods = swipeLayout.getClass().getDeclaredMethods();
			for (Method method : methods) {
				if ("startRefresh".equals(method.getName())) {
					refresh = method;
				}
			}
			if (refresh != null) {
				refresh.setAccessible(true);
				refresh.invoke(swipeLayout);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onDestroy() {
		if(mMessageReceiver!=null) {
			unregisterReceiver(mMessageReceiver);
		}
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if(getParent()!=null) {
			getParent().onBackPressed();
		}
	}

	private void initView() {

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light,
				R.color.holo_orange_light, R.color.holo_red_light);
		
		layout_has_car = (LinearLayout)findViewById(R.id.layout_has_car);
		img_car = (ImageView)findViewById(R.id.img_car);
		text_car = (TextView)findViewById(R.id.text_car);
		text_car_plate = (TextView)findViewById(R.id.text_car_plate);
		layout_add_car = (LinearLayout)findViewById(R.id.layout_add_car);
		layout_order_maintain = (LinearLayout)findViewById(R.id.layout_order_maintain);

		layout_weather_for_home = (LinearLayout)findViewById(R.id.layout_weather_for_home); 
		text_location = (TextView)findViewById(R.id.text_location);
		text_time = (TextView)findViewById(R.id.text_time);
		text_time.setText(TimeUtils.getCurrentTime());
		text_exponent = (TextView)findViewById(R.id.text_exponent);
		//限号及油价
		layout_limit_oil = (LinearLayout)findViewById(R.id.layout_limit_oil);
		LimitAndOil limit = new LimitAndOil();
		limit.setTitle("限\n号");
		limit.setOneTitle("今天");
		limit.setTwoTitle("明天");
		CarLimitItem climit = new CarLimitItem(this,limit);
		layout_limit_oil.addView(climit);

		LimitAndOil oil = new LimitAndOil();
		oil.setTitle("油\n价");
		oil.setOneTitle("0#");
		oil.setOneValue(new String[]{"0.00"});
		oil.setTwoTitle("0#");
		oil.setTwoValue(new String[]{"0.00"});
		CarLimitItem coil = new CarLimitItem(this,oil);
		layout_limit_oil.addView(coil);
		//bar
		layout_bar = (LinearLayout)findViewById(R.id.layout_bar);
		bars = new Bars(this,initData());
		layout_bar.addView(bars);

		checkJpushReg();

	}

	private void updateCar() {
		swipeLayout.setRefreshing(false);
		if(StringUtils.isNullOrEmpty(SettingLoader.getCarPlate(this))) {
			layout_add_car.setVisibility(View.VISIBLE);
			layout_has_car.setVisibility(View.GONE);
			btn_left.setVisibility(View.GONE);
		}else {
			btn_left.setVisibility(View.VISIBLE);
			layout_add_car.setVisibility(View.GONE);
			layout_has_car.setVisibility(View.VISIBLE);
			ImageUtils.displayBranchImg(this, img_car, SettingLoader.getBranchId(this));
			text_car.setText(SettingLoader.getBranchName(this));
			text_car_plate.setText(SettingLoader.getCarPlate(this));
		}
	}

	private void regListener() {
		layout_has_car.setOnClickListener(this);
		layout_add_car.setOnClickListener(this);
		layout_order_maintain.setOnClickListener(this);
	}


	private ArrayList<BarItem> initData() {
		ArrayList<BarItem> list = new ArrayList<BarItem>();
		BarItem item = new BarItem(R.drawable.ic_bar_sos,"一键救援","金牌质保商家快速救援");
		item.setDest(OneKeyHelpActivity.class);
		list.add(item);

		item = new BarItem(R.drawable.ic_bar_violation,"违章查询","全国违章信息及时更新");
		item.setDest(IllegalActivity.class);
		list.add(item);

		item = new BarItem(R.drawable.ic_bar_use_report,"用车报告",null);
		item.setDest(CarUseReportActivity.class);
		list.add(item);

		item = new BarItem(R.drawable.ic_bar_qa,"专家问答",null);
		item.setDest(ProfessorQAActivity.class);
		list.add(item);

		item = new BarItem(R.drawable.ic_bar_oil_report,"油耗统计",null);
		item.setDest(ReportActivity.class);
		list.add(item);
		return list;
	}


	private void updateWeather(Weather weather) {
		String str = "适宜洗车";
		if(weather!=null) {
			String w = weather.getWeather();
			if(w.contains("雨")||w.contains("雪")||w.contains("雾")||
					w.contains("霾")||w.contains("沙")||w.contains("尘")){
				str = "不宜洗车";
			}
			text_exponent.setVisibility(View.VISIBLE);
			text_exponent.setText(str);
		}

		//天气
		if(weather==null){
			layout_weather_for_home.setVisibility(View.GONE);
		}else {
			layout_weather_for_home.setVisibility(View.VISIBLE);
			layout_weather = (LinearLayout)findViewById(R.id.layout_weather);
			layout_weather.removeAllViews();
			WeatherView weatherView = new WeatherView(this, weather);
			layout_weather.addView(weatherView);
		}
	}

	/**
	 * 更新车辆是否限行
	 * @param limit 
	 * @param carLimit
	 * @param length 
	 */
	private void updateCarLimit(LimitAndOil limit) {
		CarLimitItem item = (CarLimitItem) layout_limit_oil.getChildAt(0);
		if(item!=null) {
			item.updateLimit(item,limit);
		}
	}
	/**
	 * 更新油价
	 * @param limit 
	 * @param carLimit
	 * @param length 
	 */
	private void updateRealPrice(LimitAndOil limit) {
		CarLimitItem item = (CarLimitItem) layout_limit_oil.getChildAt(1);
		if(item!=null) {
			item.updateLimit(item,limit);
		}
	}
	
	@Override
	public void onRefresh() {
		if(SettingLoader.hasLogin(this)) {
			getDefaultCar();
		}else {
			getWLODate();
		}
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_left:
			PopupSpinner popup = new PopupSpinner(this, btn_left,new PopupSpinner.CallBack() {

				@Override
				public void callback() {
					updateCar();
				}
			});
			if(!popup.isShowing()) {
				popup.showPopWindow();
			}
			
			break;
		case R.id.btn_right:
			if(SettingLoader.hasLogin(this)) {
				intent = new Intent(this,CarListActivity.class);
			}else {
				intent = new Intent(this,LoginActivity.class);
			}
			startActivity(intent);
			
			break;
		case R.id.layout_has_car:
			intent = new Intent(this,UserDetailActivity.class);
			intent.putExtra(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
			intent.putExtra(ParamsKey.TYPE,1);
			startActivity(intent);
			break;
		case R.id.layout_add_car:
			intent = new Intent(this,AddCarActivity.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.layout_order_maintain:
			if(SettingLoader.hasLogin(this)) {
				intent = new Intent(this,MerchantActivity.class);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK==resultCode) {
			switch (requestCode) {
			case 1:
				updateCar();
				break;

			default:
				break;
			}
		}
	}


	/**
	 * 获取默认车辆
	 */
	private void getDefaultCar() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Version, Utils.getLocalAppVersion(this));
		NetRequest.requestUrlNoDialog(this,ApiConfig.getRequestUrl(ApiConfig.Car_Default_Url),params,new NetRequest.NetCallBack() {

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
								}
								car = new CarInfo();
								car.parser(json);
								getCarInfo(car.getVehicleId());
								SettingLoader.saveDefaultCar(HomeActivity.this, car);
								XtrdApp.sendMsg(XtrdApp.ID_CARINFO_GET);
								updateCar();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				swipeLayout.setRefreshing(false);
			}
		});
	}

	/**
	 * 获取车辆信息
	 * @param vehicleId
	 */
	private void getCarInfo(long vehicleId) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, vehicleId+"");
		NetRequest.requestUrlNoDialog(this,ApiConfig.getRequestUrl(ApiConfig.Car_Info_Url) ,params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String t) {
				LogUtils.e(TAG, "info "+t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								json = json.getJSONObject("result");
								if(json!=null) {
									car = new CarInfo();
									car.parser(json);
									SettingLoader.setCarLatLng(HomeActivity.this, car.getLatitude(), car.getLongitude(),true);
									SettingLoader.setBindBox(HomeActivity.this, SettingLoader.getVehicleId(HomeActivity.this)+","+(car.isBind()?"1":"0"));
									XtrdApp.sendMsg(XtrdApp.ID_CAR_BIND);
									updateRightBtn(car.isBind());
									getWLODate();
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
	/**
	 * 处理右边按钮显示
	 * @param bind
	 */
	protected void updateRightBtn(boolean bind) {
		if(bind) {
			btn_right.setVisibility(View.GONE);
		}else {
			btn_right.setVisibility(View.VISIBLE);
		}
	}

	protected void getWLODate() {
		swipeLayout.setRefreshing(false);
		if(SettingLoader.getCarLatLng(this)==null) {
			XtrdApp.getInstance().getCurrentLocation(new XtrdApp.LocationCallBack() {

				@Override
				public void callback(double longitude, double latitude,String city) {
					SettingLoader.setCurrentCity(HomeActivity.this, city);
					SettingLoader.setCarLatLng(HomeActivity.this, latitude, longitude,false);
					text_location.setVisibility(View.VISIBLE);
					text_location.setText(city);
					getWeather(city);
					getRestrict(city);
					getRealPrice(city);
				}

			});
		}else {
			LocationDecoder.decodeLocation(SettingLoader.getCarLatLng(this), new LocationDecoder.LocationCallBack() {

				@Override
				public void callback(String str, AddressComponent component) {
					SettingLoader.setCurrentCity(HomeActivity.this, component.city);
					text_location.setVisibility(View.VISIBLE);
					text_location.setText(component.city);
					getWeather(component.city);
					getRestrict(component.city);
					getRealPrice(component.city);
				}
			});
		}
	}



	/**
	 * 获取天气
	 * @param city
	 */
	private void getWeather(String city) {
		if(StringUtils.isNullOrEmpty(city)) {
			return;
		}
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.CITY, city);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.WEATHER_URL) ,params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, "weather "+t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								json = json.getJSONObject("result");
								Weather weather = new Weather();
								weather.parser(json);
								updateWeather(weather);
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

	/**
	 * 获取限行车辆
	 * @param vehicleId
	 */
	private void getRestrict(String city) {

		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.CITY, city);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Limit_URL) ,params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, "restrict " + t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								if(json.has("result")) {
									json = json.getJSONObject("result");
									LimitAndOil limit = new LimitAndOil();
									if(json.has("today")) {
										JSONObject today = json.getJSONObject("today");
										limit.setOneTitle("今天");
										if(today.has("rule")) {
											limit.setOneValue(today.getString("rule").split(","));
										}
									}
									if(json.has("tommow")) {
										JSONObject tommrow = json.getJSONObject("tommow");
										if(tommrow.has("rule")) {
											limit.setTwoTitle("明天");
											if(tommrow.has("rule")) {
												limit.setTwoValue(tommrow.getString("rule").split(","));
											}
										}
									}
									updateCarLimit(limit);
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

	/**
	 * 获取限行车辆
	 * @param vehicleId
	 */
	private void getRealPrice(String city) {

		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.CITY, city);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Real_Price_URL) ,params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, "restrict " + t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								if(json.has("result")) {
									JSONArray array = json.getJSONArray("result");
									LimitAndOil limit = null;
									if(array!=null) {
										if(array.length()==1) {
											limit = new LimitAndOil();
											json = array.optJSONObject(0);
											if(json.has("name")) {
												limit.setOneTitle(json.getString("name")+"#");
											}
											if(json.has("price")) {
												limit.setOneValue(json.getString("price").split(","));
											}
											limit.setOneRes(SettingLoader.getIconForOnePrice(HomeActivity.this,json.getString("name"),json.getDouble("price")));
											SettingLoader.setOnePriceValue(HomeActivity.this, json.getString("name")+"#"+json.getDouble("price"));
										}else if(array.length()==2) {
											limit = new LimitAndOil();
											json = array.optJSONObject(0);
											if(json.has("name")) {
												limit.setOneTitle(json.getString("name")+"#");
											}
											if(json.has("price")) {
												limit.setOneValue(json.getString("price").split(","));
											}
											limit.setOneRes(SettingLoader.getIconForOnePrice(HomeActivity.this,json.getString("name"),json.getDouble("price")));
											SettingLoader.setOnePriceValue(HomeActivity.this, json.getString("name")+"#"+json.getDouble("price"));
											json = array.optJSONObject(1);
											if(json.has("name")) {
												limit.setTwoTitle(json.getString("name")+"#");
											}
											if(json.has("price")) {
												limit.setTwoValue(json.getString("price").split(","));
											}
											limit.setTwoRes(SettingLoader.getIconForTwoPrice(HomeActivity.this,json.getString("name"),json.getDouble("price")));
											SettingLoader.setTwoPriceValue(HomeActivity.this, json.getString("name")+"#"+json.getDouble("price"));
										}
									}

									updateRealPrice(limit);
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


	/**
	 * 检查极光reg_id
	 */
	private void checkJpushReg() {
		if(!StringUtils.isNullOrEmpty(SettingLoader.getJpushId(this))) {
			Set<String> tags = new HashSet<String>();
			tags.add(SettingLoader.getChannel(this));
			JPushInterface.setAliasAndTags(this, SettingLoader.getLoginName(this),tags, new TagAliasCallback() {

				@Override
				public void gotResult(int arg0, String arg1, Set<String> arg2) {
					LogUtils.e(TAG, arg0 + " " + arg1 + " " + arg2);
				}
			});
			FinalHttp fh = new FinalHttp();
			AjaxParams params = new AjaxParams();
			params.put(ParamsKey.RegistrationId, SettingLoader.getJpushId(this));
			params.put(ParamsKey.Alias, SettingLoader.getLoginName(this));
			params.put(ParamsKey.Tag, SettingLoader.getChannel(this));
			params.put(ParamsKey.DeviceType, ParamsKey.Android);
			fh.post(ApiConfig.getRequestUrl(ApiConfig.Jpush_Reg_Url) ,params, new AjaxCallBack<String>() {

				private String msg;

				@Override
				public void onSuccess(String t) {
					LogUtils.e(TAG,"jpush"+t.toString());
					try {
						if(!StringUtils.isNullOrEmpty(t)) {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									SettingLoader.setJpushId(HomeActivity.this,null);
								}else {
									msg = json.getString("message");
									LogUtils.e(TAG, msg);
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
					LogUtils.e(TAG, "error msg "+strMsg);
				}
			});
		}
	}

	/**
	 * jpush
	 */
	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}


	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String messge = intent.getStringExtra(KEY_MESSAGE);
				String extras = intent.getStringExtra(KEY_EXTRAS);
				StringBuilder showMsg = new StringBuilder();
				showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
				if (!StringUtils.isNullOrEmpty(extras)) {
					showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
				}
				LogUtils.e(TAG, "receive message " + showMsg.toString());
			}
		}
	}
}
