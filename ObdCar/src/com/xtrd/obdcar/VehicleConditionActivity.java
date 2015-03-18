package com.xtrd.obdcar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.Config;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.CacheManager;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.CarCondtion;
import com.xtrd.obdcar.entity.CarSysItem;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.vc.CarSystemVcActivity;
import com.xtrd.obdcar.vc.DashBoardActivity;
import com.xtrd.obdcar.vc.HistoryVCActivity;
import com.xtrd.obdcar.vc.ProfessorQAActivity;
import com.xtrd.obdcar.vc.TroubleCodeActivity;
import com.xtrd.obdcar.view.CheckProgressView;

public class VehicleConditionActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

	protected static final String TAG = "VehicleConditionActivity";
	private SwipeRefreshLayout swipeLayout;
	private LinearLayout layout_experience;
	private Button btn_experience;
	private Button btn_buy;
	private TextView text_tips;
	private TextView text_tips_desc;
	private ScrollView scrollView;
	private Button btn_meter,btn_qa,btn_trouble_code,btn_vc_history;
	private Button btn_power_sys,btn_under_sys,btn_body_sys,btn_signal_sys;
	private CheckProgressView mTasksView;
	private ImageView img_car;
	private TextView text_oil_cost;
	private TextView text_range_cost;
	private TextView text_oil_status;
	private LinearLayout layout_vc_bg;
	private CarCondtion condition = new CarCondtion();

	public VehicleConditionActivity() {
		layout_id = R.layout.activity_vehicle_condition;
	}

	public Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case XtrdApp.ID_HIDE_TRIP:
				if(SettingLoader.isTripHide(VehicleConditionActivity.this)) {
					btn_right.setVisibility(View.GONE);
				}else {
					btn_right.setVisibility(View.VISIBLE);
				}
				break;
			case XtrdApp.ID_CAR_BIND:
				if(SettingLoader.hasBindBox(VehicleConditionActivity.this)) {
					layout_experience.setVisibility(View.GONE);
					scrollView.setVisibility(View.VISIBLE);
					btn_right.setVisibility(View.VISIBLE);
					getVc();
				}
				break;
			default:
				break;
			}
		}

	};



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0,0,R.string.title_vc, R.string.title_trip,0);
		XtrdApp.addHandler(handler);
		initView();
		regClick();
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
	public void onRefresh() {
		if(SettingLoader.hasLogin(this)&&SettingLoader.hasBindBox(this)) {
			getVc();
		}else {
			condition = CacheManager.getInstance(this).getVcDefault("plist.txt");
			updateUI();
		}
	}
	@Override
	public void onBackPressed() {
		if(!getIntent().getBooleanExtra("from", false)) {
			if(getParent()!=null) {
				getParent().onBackPressed();
			}
		}else {
			super.onBackPressed();
		}
	}


	private void initView() {
		layout_experience = (LinearLayout)findViewById(R.id.layout_experience);
		text_tips = (TextView)findViewById(R.id.text_tips);
		text_tips_desc = (TextView)findViewById(R.id.text_tips_desc);
		btn_experience = (Button)findViewById(R.id.btn_experience);
		btn_buy = (Button)findViewById(R.id.btn_buy);
		btn_experience.setOnClickListener(this);
		btn_buy.setOnClickListener(this);

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light,
				R.color.holo_orange_light, R.color.holo_red_light);

		scrollView = (ScrollView)findViewById(R.id.scrollView);
		if(SettingLoader.hasLogin(this)&&SettingLoader.hasBindBox(this)) {
			layout_experience.setVisibility(View.GONE);
			scrollView.setVisibility(View.VISIBLE);
			if(SettingLoader.isTripHide(this)) {
				btn_right.setVisibility(View.GONE);
			}else {
				btn_right.setVisibility(View.VISIBLE);
			}
		}else {
			btn_right.setVisibility(View.GONE);
			layout_experience.setVisibility(View.VISIBLE);
			scrollView.setVisibility(View.GONE);
			if(!SettingLoader.hasLogin(this)) {
				text_tips.setText("对不起，您还没有登录");
			}
			if(!SettingLoader.hasBindBox(this)) {
				text_tips.setText("对不起，您还没有购买盒子");
			}
		}

		text_oil_status = (TextView)findViewById(R.id.text_oil_status);
		layout_vc_bg = (LinearLayout)findViewById(R.id.layout_vc_bg);

		btn_meter = (Button)findViewById(R.id.btn_meter);
		btn_qa = (Button)findViewById(R.id.btn_qa);
		btn_trouble_code = (Button)findViewById(R.id.btn_trouble_code);
		btn_vc_history = (Button)findViewById(R.id.btn_vc_history);

		img_car = (ImageView)findViewById(R.id.img_car);
		mTasksView = (CheckProgressView) findViewById(R.id.tasks_view);
		mTasksView.setCallback(new CheckProgressView.Callback() {

			@Override
			public void callback() {
				getVc();
			}
		});
		text_oil_cost = (TextView)findViewById(R.id.text_oil_cost);
		text_range_cost = (TextView)findViewById(R.id.text_range_cost);
		//底部
		btn_power_sys = (Button)findViewById(R.id.btn_power_sys);
		btn_under_sys = (Button)findViewById(R.id.btn_under_sys);
		btn_body_sys = (Button)findViewById(R.id.btn_body_sys);
		btn_signal_sys = (Button)findViewById(R.id.btn_signal_sys);

	}
	private void regClick() {
		btn_meter.setOnClickListener(this);
		btn_qa.setOnClickListener(this);
		btn_trouble_code.setOnClickListener(this);
		btn_vc_history.setOnClickListener(this);
		btn_power_sys.setOnClickListener(this);
		btn_under_sys.setOnClickListener(this);
		btn_body_sys.setOnClickListener(this);
		btn_signal_sys.setOnClickListener(this);
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
			intent = new Intent(this,TripActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_meter:
			if(SettingLoader.hasBindBox(this)) {
				intent = new Intent(this,DashBoardActivity.class);
				startActivity(intent);
			}else {
				Utils.showToast(this, getResources().getString(R.string.tips_no_box));
			}
			break;
		case R.id.btn_qa:
			intent = new Intent(this,ProfessorQAActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_trouble_code:
			intent = new Intent(this,TroubleCodeActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_vc_history:
			if(!SettingLoader.hasLogin(this)) {
				Utils.showToast(this, R.string.tips_no_login);
				return;
			}
			if(!SettingLoader.hasBindBox(this)) {
				Utils.showToast(this, R.string.tips_no_box);
				return;
			}
			intent = new Intent(this,HistoryVCActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_power_sys:
			if(condition.getList()!=null&&condition.getList().size()>=4) {
				if(condition.getList().get(0).getSize()>0) {
					intent = new Intent(this,CarSystemVcActivity.class);
					intent.putExtra("title", condition.getList().get(0).getTitle());
					intent.putExtra("data", condition.getList().get(0).getList());
					startActivity(intent);
				}else {
					Utils.showToast(this, "系统正常");
				}
			}
			break;
		case R.id.btn_under_sys:
			if(condition.getList()!=null&&condition.getList().size()>=4) {
				if(condition.getList().get(2).getSize()>0) {
					intent = new Intent(this,CarSystemVcActivity.class);
					intent.putExtra("title", condition.getList().get(1).getTitle());
					intent.putExtra("data", condition.getList().get(1).getList());
					startActivity(intent);
				}else {
					Utils.showToast(this, "系统正常");
				}
			}
			break;
		case R.id.btn_body_sys:
			if(condition.getList()!=null&&condition.getList().size()>=4) {
				if(condition.getList().get(1).getSize()>0) {
					intent = new Intent(this,CarSystemVcActivity.class);
					intent.putExtra("title", condition.getList().get(2).getTitle());
					intent.putExtra("data", condition.getList().get(2).getList());
					startActivity(intent);
				}else {
					Utils.showToast(this, "系统正常");
				}
				
			}
			break;
		case R.id.btn_signal_sys:
			if(condition.getList()!=null&&condition.getList().size()>=4) {
				if(condition.getList().get(3).getSize()>0) {
					intent = new Intent(this,CarSystemVcActivity.class);
					intent.putExtra("title", condition.getList().get(3).getTitle());
					intent.putExtra("data", condition.getList().get(3).getList());
					startActivity(intent);
				}else {
					Utils.showToast(this, "系统正常");
				}
			
			}
			break;
		case R.id.btn_experience:
			btn_right.setVisibility(View.VISIBLE);
			layout_experience.setVisibility(View.GONE);
			scrollView.setVisibility(View.VISIBLE);
			condition = CacheManager.getInstance(this).getVcDefault("plist.txt");
			updateUI();
			break;
		case R.id.btn_buy:
			Utils.showPhoneTips(this, Config.Hot_Line);
			break;
		default:
			break;
		}
	}
	private void getVc() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		fh.post(ApiConfig.getRequestUrl(ApiConfig.VC_Show_Url), params,new AjaxCallBack<String>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				mTasksView.stopCheck("已检测");
				swipeLayout.setRefreshing(false);
				super.onFailure(t, errorNo, strMsg);
			}

			@Override
			public void onStart() {
				super.onStart();
				mTasksView.startCheck("检测中");
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				mTasksView.stopCheck();
				try {
					if (!StringUtils.isNullOrEmpty(t)) {
						LogUtils.e(TAG, "t " +t);
						JSONObject json = new JSONObject(t);
						if (json.has("status")) {
							int status = json.getInt("status");
							if (1 == status) {
								if(json.has("result")) {
									condition.parser(json.getJSONObject("result"));
								}
							} else {
								if(json.has("message")) {
									String msg = json.getString("message");
									Utils.showToast(VehicleConditionActivity.this, msg);
								}
							}
						}
						updateUI();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		});
	}

	protected void updateUI() {
		swipeLayout.setRefreshing(false);
		if(condition!=null) {
			text_oil_status.setText((condition.getFuel()<condition.getTtl()?"省油":"高油耗"));
			text_oil_status.setTextColor((condition.getFuel()<condition.getTtl()?getResources().getColor(R.color.green_color):getResources().getColor(R.color.red)));
			text_oil_cost.setText(condition.getPrice());
			text_range_cost.setText(condition.getDis()+"");
			if(2==condition.getStatus()) {
				img_car.setImageResource(R.drawable.car_running);
				AnimationDrawable animationDrawable = (AnimationDrawable) img_car.getDrawable();
				animationDrawable.start();
			}else if(3==condition.getStatus()){
				img_car.setImageResource(R.drawable.ic_car_offline);
			}else {
				img_car.setImageResource(R.drawable.ic_car_park);
			}
			if(condition.getList().size()>=4) {
				btn_power_sys.setText(condition.getList().get(0).getTitle());
				btn_under_sys.setText(condition.getList().get(1).getTitle());
				btn_body_sys.setText(condition.getList().get(2).getTitle());
				btn_signal_sys.setText(condition.getList().get(3).getTitle());
			}
			
			
			
			if(condition.getList().size()>0) {
				int count = 0;
				for(CarSysItem item : condition.getList()) {
					count+=item.getSize();
				}
				if(count>0) {
					mTasksView.setText("亚健康");
				}else {
					mTasksView.setText("正常");
				}
			}else {
				mTasksView.setText("正常");
			}

			processProgress();
		}
	}

	private void processProgress() {
		layout_vc_bg.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.setMargins(Utils.dipToPixels(this, 2), 0, Utils.dipToPixels(this, 2), 0);
		ImageView view = null;
		if(condition!=null) {
			if(0==condition.getSpeed()) {
				TypedArray arr = getResources().obtainTypedArray(R.array.vc_idling);
				int count = (int) Math.round(condition.getFuel()*8/20);
				for(int i=0;i<(arr.length()-count);i++) {
					view = new ImageView(this);
					view.setImageResource(R.drawable.ic_vc_bg);
					layout_vc_bg.addView(view,params);
				}

				for(int i=count-1;i>=0;i--) {
					view = new ImageView(this);
					view.setImageResource(arr.getResourceId(i,0));
					layout_vc_bg.addView(view,params);
				}
				for(int i=0;i<18;i++) {
					view = new ImageView(this);
					view.setImageResource(R.drawable.ic_vc_bg);
					layout_vc_bg.addView(view,params);
				}
			}else {
				int count = (int) Math.round(condition.getFuel()*18/40);
				for(int i=0;i<8;i++) {
					view = new ImageView(this);
					view.setImageResource(R.drawable.ic_vc_bg);
					layout_vc_bg.addView(view,params);
				}

				TypedArray arr = getResources().obtainTypedArray(R.array.vc_normal_run);
				for(int i=0;i<count;i++) {
					view = new ImageView(this);
					view.setImageResource(arr.getResourceId(i, 0));
					layout_vc_bg.addView(view,params);
				}

				for(int i=0;i<(arr.length()-count);i++) {
					view = new ImageView(this);
					view.setImageResource(R.drawable.ic_vc_bg);
					layout_vc_bg.addView(view,params);
				}
			}
		}
	}

}
