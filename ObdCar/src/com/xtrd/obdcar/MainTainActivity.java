package com.xtrd.obdcar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xtrd.obdcar.active.AddCarActivity;
import com.xtrd.obdcar.adapter.GuidePageAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.maintain.MainTainAddActivity;
import com.xtrd.obdcar.maintain.MainTainItemActivity;
import com.xtrd.obdcar.maintain.MaintainRecoderActivity;
import com.xtrd.obdcar.merchant.MerchantActivity;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.passport.LoginActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.InputDialog;

public class MainTainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

	private static final int Interval_Time = 2*1000;
	private SwipeRefreshLayout swipeLayout;
	private LinearLayout layout_distance;
	private Button btn_correct;
	private TextView text_maintain_time;
	private Button btn_order;
	private LinearLayout layout_pre_maintain;
	private TextView text_pre_time;
	private LinearLayout layout_dis_cost;
	private TextView text_maintain_distance;
	private TextView text_maintain_cost;
	private Button btn_maintain_period;

	private ViewPager layout_gallery;
	private TextView text_shop_promotion;
	private ArrayList<View> pageViews;
	private LinearLayout layout_points;

	private Button btn_maintian_item;
	private Button btn_maintian_recoder;
	private ImageButton btn_maintain_add;

	private Timer timer;
	private int current = 0;



	public MainTainActivity() {
		layout_id = R.layout.activity_maintain;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, 0, R.string.title_maintain, 0,0);
		initView();
		regClick();

		startRefresh();
	}
	
	public void startRefresh() {
		try {
			Method refresh = null;
			Method[] methods = SwipeRefreshLayout.class.getDeclaredMethods();
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
		getMainTain(SettingLoader.getVehicleId(this));
	}

	private void initView() {
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light,
				R.color.holo_orange_light, R.color.holo_red_light);
		
		
		btn_correct = (Button)findViewById(R.id.btn_correct);
		layout_distance = (LinearLayout)findViewById(R.id.layout_distance);

		text_maintain_time = (TextView)findViewById(R.id.text_maintain_time);
		btn_order = (Button)findViewById(R.id.btn_order);

		layout_pre_maintain = (LinearLayout)findViewById(R.id.layout_pre_maintain);
		text_pre_time = (TextView)findViewById(R.id.text_pre_time);
		
		layout_dis_cost = (LinearLayout)findViewById(R.id.layout_dis_cost);
		text_maintain_distance = (TextView)findViewById(R.id.text_maintain_distance);
		text_maintain_cost = (TextView)findViewById(R.id.text_maintain_cost);

		btn_maintain_period = (Button)findViewById(R.id.btn_maintain_period);

		layout_gallery = (ViewPager)findViewById(R.id.layout_gallery);
		text_shop_promotion = (TextView)findViewById(R.id.text_shop_promotion);
		layout_points = (LinearLayout)findViewById(R.id.layout_points);

		btn_maintian_item = (Button)findViewById(R.id.btn_maintian_item);
		btn_maintian_recoder = (Button)findViewById(R.id.btn_maintian_recoder);
		btn_maintain_add = (ImageButton)findViewById(R.id.btn_maintain_add);

	}

	private void regClick() {
		btn_correct.setOnClickListener(this);
		btn_order.setOnClickListener(this);
		btn_maintain_period.setOnClickListener(this);
		btn_maintian_item.setOnClickListener(this);
		btn_maintian_recoder.setOnClickListener(this);
		btn_maintain_add.setOnClickListener(this);

	}
	
	@Override
	public void onBackPressed() {
		if(getParent()!=null) {
			getParent().onBackPressed();
		}
	}
	
	private void showCorrectDialog() {
		if(!SettingLoader.hasLogin(this)) {
			Intent intent = new Intent(this,LoginActivity.class);
			startActivity(intent);
			return;
		}
		new InputDialog(this)
		.setTitle("里程校准")
		.setMessage("设置爱车行驶的公里数")
		.setUnit("公里")
		.setInputType(InputType.TYPE_CLASS_NUMBER)
		.setPositiveButton("保存设置", new InputDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
			@Override
			public void onClick(DialogInterface dialog, int which, String input) {
				updateRange(input);
				setRange(input);
			}
		})
		.setNegativeButton("取消设置", new InputDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}

			@Override
			public void onClick(DialogInterface dialog, int which, String input) {

			}
		})
		.show();
	}

	protected void setRange(String distance) {
		if(StringUtils.isNullOrEmpty(distance)) {
			return;
		}

		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.Active_Distance, distance);
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Maintain_DISTANCE_Zhouqi_Set_Url), params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								Utils.showToast(MainTainActivity.this, "设置成功");
							}else {
								Utils.showToast(MainTainActivity.this, "设置失败");
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(MainTainActivity.this, "设置失败");
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_correct:
			if(!SettingLoader.hasLogin(this)) {
				Intent intent = new Intent(this,LoginActivity.class);
				startActivity(intent);
				return;
			}

			if(StringUtils.isNullOrEmpty(SettingLoader.getVehicleId(this))) {
				Intent intent = new Intent(this,AddCarActivity.class);
				startActivity(intent);
				return;
			}
			showCorrectDialog();
			break;
		case R.id.btn_order:
			gotoNext(MerchantActivity.class);
			break;
		case R.id.btn_maintain_period:
			showPeriodDialog();
			break;
		case R.id.btn_maintian_item:
			gotoNext(MainTainItemActivity.class);
			break;
		case R.id.btn_maintian_recoder:
			gotoNext(MaintainRecoderActivity.class);
			break;
		case R.id.btn_maintain_add:
			gotoNext(MainTainAddActivity.class);
			break;

		default:
			break;
		}
	}


	private void gotoNext(Class dest) {

		if(!SettingLoader.hasLogin(this)) {
			Intent intent = new Intent(this,LoginActivity.class);
			startActivity(intent);
			return;
		}

		if(StringUtils.isNullOrEmpty(SettingLoader.getVehicleId(this))) {
			Intent intent = new Intent(this,AddCarActivity.class);
			startActivity(intent);
			return;
		}

		Intent intent = new Intent(this,dest);
		startActivity(intent);
	}



	private void updateRange(String input) {
		if(!StringUtils.isNullOrEmpty(input)) {
			for(int i=0;i<layout_distance.getChildCount();i++) {
				TextView edit = (TextView) layout_distance.getChildAt(layout_distance.getChildCount()-1-i);
				if(i<input.length()) {
					edit.setText(input.charAt(input.length()-1-i)+"");
				}else {
					edit.setText("");
				}
			}
		}else {
			Utils.showToast(this, getResources().getString(R.string.tips_no_input));
		}
	}

	private void showPeriodDialog() {
		if(!SettingLoader.hasLogin(this)) {
			Intent intent = new Intent(this,LoginActivity.class);
			startActivity(intent);
			return;
		}

		new InputDialog(this)
		.setTitle("保养周期")
		.setMessage("设置爱车保养的周期公里数")
		.setUnit("公里")
		.setMaxLength(7)
		.setInputType(InputType.TYPE_CLASS_NUMBER)
		.setPositiveButton("保存设置", new InputDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
			@Override
			public void onClick(DialogInterface dialog, int which, String input) {
				setMaintainPeriod(input);
			}
		})
		.setNegativeButton("取消设置", new InputDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}

			@Override
			public void onClick(DialogInterface dialog, int which, String input) {

			}
		})
		.show();
	}


	protected void setMaintainPeriod(String distance) {
		if(StringUtils.isNullOrEmpty(distance)) {
			Utils.showToast(this, "请输入保养公里数");
			return;
		}

		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.Active_Distance, distance);
		params.put(ParamsKey.TYPE, "0");
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Maintain_Zhouqi_Set_Url), params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								Utils.showToast(MainTainActivity.this, "设置成功");
							}else {
								Utils.showToast(MainTainActivity.this, "设置失败");
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(MainTainActivity.this, "设置失败");
			}
		});
	}

	private void getMainTain(String vehicleId) {
		AjaxParams params = new AjaxParams();
		if(!StringUtils.isNullOrEmpty(vehicleId)) {
			params.put(ParamsKey.VEHICLEID, vehicleId);
		}
		params.put(ParamsKey.CITY, SettingLoader.getCurrentCity(this));
		NetRequest.requestUrlNoDialog(this, ApiConfig.getRequestUrl(ApiConfig.Maintain_Url), params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				swipeLayout.setRefreshing(false);
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								updateUI(json.getJSONObject("result"));
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
	 * 
	 * @param response jsonObject
	 */
	private void updateUI(JSONObject jsonObject) {
		try {
			if(jsonObject!=null) {
				if(jsonObject.has("curDistance")) {
					updateRange(jsonObject.getLong("curDistance")+"");
				}
				if(jsonObject.has("subDistance")) {
					text_maintain_time.setVisibility(View.VISIBLE);
					String remainTime = jsonObject.getString("subDistance");
					String str = String.format(getResources().getString(R.string.text_maintain_time_tip), remainTime);
					SpannableString s = new SpannableString(str);
					s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.maintain_blue_text_color)), str.indexOf(remainTime),str.indexOf(remainTime)+remainTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					s.setSpan(new  RelativeSizeSpan(2),str.indexOf(remainTime),str.indexOf(remainTime)+remainTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					text_maintain_time.setText(s);
				}else {
					text_maintain_time.setVisibility(View.GONE);
				}
				//上次保养
				if(jsonObject.has("time")&&jsonObject.has("distance")&&jsonObject.has("price")) {
					layout_pre_maintain.setVisibility(View.VISIBLE);
					if(jsonObject.has("time")&&!"null".equals(jsonObject.getString("time"))) {
						text_pre_time.setVisibility(View.VISIBLE);
						text_pre_time.setText(String.format(getResources().getString(R.string.text_pre_maintain_time), jsonObject.getString("time")));
					}else {
						text_pre_time.setVisibility(View.GONE);
					}
					layout_dis_cost.setVisibility(View.VISIBLE);
					if(jsonObject.has("distance")) {
						text_maintain_distance.setText(jsonObject.getString("distance")+"公里");
					}
					if(jsonObject.has("price")) {
						text_maintain_cost.setText(jsonObject.getString("price")+"元");
					}
				}else {
					layout_dis_cost.setVisibility(View.GONE);
				}
				

				if(jsonObject.has("shows")) {
					processGallery(jsonObject.getJSONArray("shows"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void processGallery(final JSONArray jsonArray) {
		pageViews = new ArrayList<View>();
		View image = null;
		LinearLayout.LayoutParams pLP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		pLP.setMargins(Utils.dipToPixels(this, 5), 0, Utils.dipToPixels(this, 5), 0);
		ImageView point = null;
		layout_points.removeAllViews();
		for (int i = 0; i < jsonArray.length(); i++) {
			image = new View(this);
			pageViews.add(image);
			point = new ImageView(this);
			if(i==0) {
				point.setImageResource(R.drawable.ic_solid_point);
			}else {
				point.setImageResource(R.drawable.ic_dash_point);
			}
			layout_points.addView(point,pLP);
		}
		displayInfo(jsonArray,0);
		layout_gallery.setAdapter(new GuidePageAdapter(this,jsonArray,pageViews));
		layout_gallery.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				for(int i=0;i<layout_points.getChildCount();i++) {
					ImageView view = (ImageView) layout_points.getChildAt(i);
					view.setImageResource(R.drawable.ic_dash_point);
				}

				displayInfo(jsonArray,arg0);
				ImageView view = (ImageView) layout_points.getChildAt(arg0);
				view.setImageResource(R.drawable.ic_solid_point);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		if(jsonArray!=null&&jsonArray.length()>0) {
			if(timer!=null) {
				return;
			}
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							layout_gallery.setCurrentItem(current%jsonArray.length());
							current++;
						}
					});
				}
			}, Interval_Time, Interval_Time);
		}
	}

	/**
	 * 显示优惠信息
	 */
	private void displayInfo(JSONArray array,int position) {
		try {
			text_shop_promotion.setText(array.getJSONObject(position).getString("disCount"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(timer!=null) {
			timer.cancel();
		}
	}
}
