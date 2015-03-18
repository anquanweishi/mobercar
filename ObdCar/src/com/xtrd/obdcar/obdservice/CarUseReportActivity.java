package com.xtrd.obdcar.obdservice;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.Config;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.CacheManager;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.CarUseReport;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.DateDialog;
import com.xtrd.obdcar.view.TabContent.OnTabPageChangeListener;
import com.xtrd.obdcar.view.TabWidget;

public class CarUseReportActivity extends BaseActivity implements OnTabPageChangeListener{

	protected static final String TAG = "CarUseReportActivity";
	private LinearLayout tabContainer;
	private int tabCount;
	private ArrayList<TabWidget> tabs = new ArrayList<TabWidget>();
	private int currentTabIndex = 0;

	private LinearLayout layout_experience;
	private Button btn_experience;
	private Button btn_buy;
	private TextView text_tips;
	private TextView text_tips_desc;
	
	
	private ScrollView scrollView;
	private LinearLayout layout_date;
	private TextView text_start_date;
	private TextView text_end_date;
	private Button btn_query;
	private TextView text_fuel_amount;
	private TextView text_legend;
	private TextView text_time;
	private TextView text_cost;
	private TextView text_speed;
	private TextView text_trouble_count;
	private TextView text_alarm_count;
	private LinearLayout layout_lineview_one;
	private LinearLayout layout_lineview_two;
	private LinearLayout layout_lineview_three;
	private TextView text_fast_turbo;
	private TextView text_brake_count;
	private TextView text_race_count;
	private LinearLayout layout_bottom;
	
	private String start_date,end_date;
	protected CarUseReport report;

	public CarUseReportActivity() {
		layout_id = R.layout.activity_car_use_report;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0,R.drawable.btn_back_bg,R.string.text_car_report,0,0);
		initTab();
		initView();
		if(SettingLoader.hasLogin(this)&&SettingLoader.hasBindBox(this)) {
			getCarUseReport("0");
		}else {
			report = CacheManager.getInstance(this).getCarUseDefault("data_caruse_report.txt");
			updateUI();
		}

	}

	private void initTab() {
		tabContainer = (LinearLayout) findViewById(R.id.tab_container);

		// 计算tab数量,和tab的最小宽度
		tabCount = 3;
		String[] tabTitles = new String[] {"最近一次","最近一周","自定义"};
		for (int i = 0; i < tabCount; i++) {
			// 将tab项,添加到tabbar
			TabWidget tab = new TabWidget(this, tabTitles[i]);
			tab.setTitleColor(getResources().getColor(R.color.white));
			tab.setChecked(i == 0 ? true : false);
			// tabId==subject里的id
			tab.setId(i);
			LinearLayout.LayoutParams tabLP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					Utils.dipToPixels(this, 35));
			if(i==tabCount-1) {
				tabLP.setMargins(0, 0, 0, 0);
			}else {
				tabLP.setMargins(0, 0, Utils.dipToPixels(this, 10), 0);
			}
			tabLP.weight = 1;
			if(0==i) {
				tab.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
			}else {
				tab.setBackgroundColor(getResources().getColor(R.color.blue_bg_for_btn));
			}
			tab.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!SettingLoader.hasLogin(CarUseReportActivity.this)) {
						Utils.showToast(CarUseReportActivity.this, getResources().getString(R.string.tips_no_login));
						return;
					}
					
					if(!SettingLoader.hasBindBox(CarUseReportActivity.this)){
						Utils.showToast(CarUseReportActivity.this, getResources().getString(R.string.tips_no_box));
						return;
					}
					onTabPageChange(v.getId());
				}
			});
			tabs.add(tab);
			tabContainer.addView(tab, tabLP);
		}
	}
	
	

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_select:
			start_date = text_start_date.getText().toString();
			end_date = text_end_date.getText().toString();
			if(TimeUtils.compare(start_date,end_date,"yyyy-MM")) {
				getCarUseReport(null);
			}else {
				Utils.showToast(CarUseReportActivity.this, "起始日期应早于结束日期");
			}
			break;
		case R.id.text_start_date:
			start_date = text_start_date.getText().toString();
			showDateDialog(true,start_date);
			break;
		case R.id.text_end_date:
			end_date = text_end_date.getText().toString();
			showDateDialog(false,end_date);
			break;
		case R.id.btn_experience:
			layout_experience.setVisibility(View.GONE);
			scrollView.setVisibility(View.VISIBLE);
			report = CacheManager.getInstance(this).getCarUseDefault("data_caruse_report.txt");
			updateUI();
			break;
		case R.id.btn_buy:
			Utils.showPhoneTips(this, Config.Hot_Line);
			break;
		default:
			break;
		}
	}


	private void showDateDialog(final boolean start, String date) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
			Date parse = format.parse(date);
			new DateDialog(this,false,parse)
			.setPositiveButton(new DateDialog.OnClickListener() {

				@Override
				public void onClick(String date) {
					if(start) {
						start_date = date;
						text_start_date.setText(date);
					}else {
						if(TimeUtils.compare(start_date,date,"yyyy-MM")) {
							end_date = date;
							text_end_date.setText(date);
						}else {
							Utils.showToast(CarUseReportActivity.this, "起始日期应早于结束日期");
						}
					}
				}
			}).show();
		} catch (ParseException e) {
			e.printStackTrace();
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
		scrollView = (ScrollView)findViewById(R.id.scrollView);
		layout_bottom = (LinearLayout)findViewById(R.id.layout_bottom);

		
		text_fuel_amount = (TextView)findViewById(R.id.text_fuel_amount);
		text_legend = (TextView)findViewById(R.id.text_legend);
		text_time = (TextView)findViewById(R.id.text_time);
		text_cost = (TextView)findViewById(R.id.text_cost);
		text_speed = (TextView)findViewById(R.id.text_speed);
		text_trouble_count = (TextView)findViewById(R.id.text_trouble_count);
		text_alarm_count = (TextView)findViewById(R.id.text_alarm_count);
		layout_lineview_one = (LinearLayout)findViewById(R.id.layout_lineview_one);
		layout_lineview_two = (LinearLayout)findViewById(R.id.layout_lineview_two);
		layout_lineview_three = (LinearLayout)findViewById(R.id.layout_lineview_three);

		text_fast_turbo = (TextView)findViewById(R.id.text_fast_turbo);
		text_brake_count = (TextView)findViewById(R.id.text_brake_count);
		text_race_count = (TextView)findViewById(R.id.text_race_count);



		layout_date = (LinearLayout)findViewById(R.id.layout_date);
		layout_date.setVisibility(View.GONE);
		text_start_date = (TextView)findViewById(R.id.text_start_date);
		text_end_date = (TextView)findViewById(R.id.text_end_date);
		btn_query = (Button)findViewById(R.id.btn_select);
		text_start_date.setOnClickListener(this);
		text_end_date.setOnClickListener(this);
		btn_query.setOnClickListener(this);
		
		
		if(SettingLoader.hasLogin(this)&&SettingLoader.hasBindBox(this)&&!StringUtils.isNullOrEmpty(SettingLoader.getVehicleId(this))) {
			layout_experience.setVisibility(View.GONE);
			scrollView.setVisibility(View.VISIBLE);
			layout_bottom.setVisibility(View.VISIBLE);
		}else {
			layout_experience.setVisibility(View.VISIBLE);
			layout_bottom.setVisibility(View.GONE);
			scrollView.setVisibility(View.GONE);
			if(SettingLoader.hasLogin(this)) {
				text_tips.setText("对不起，您还没有登录");
			}
			if(!SettingLoader.hasBindBox(this)) {
				text_tips.setText("对不起，您还没有购买盒子");
			}
		}

	}


	public void getCarUseReport(String day) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		if(!StringUtils.isNullOrEmpty(day)) {
			params.put(ParamsKey.Days, day);
		}else {
			params.put(ParamsKey.StartTime, TimeUtils.getFirstDatebyMonth(start_date) +" 00:00:00");
			params.put(ParamsKey.EndTime,TimeUtils.getLastDatebyMonth(end_date) +" 23:59:59");
		}
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Car_Use_Url), params, new NetRequest.NetCallBack() {

			private String msg;

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								report = new CarUseReport();
								if(json.has("result")) {
									report.parser(json.getJSONObject("result"));
								}
								
								if(json.has("datas")) {
									report.parserUnits(json.getJSONObject("datas"));
								}
							}else {
								msg = json.getString("message");
								Utils.showToast(CarUseReportActivity.this, msg);
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
				updateReport();
			}
		});
	}



	protected void updateReport() {
		try {
			LinearLayout.LayoutParams LParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,Utils.dipToPixels(this, 240));
			if(report!=null&&report.getDistances()!=null&&report.getDisUnits()!=null) {
				layout_lineview_one.setVisibility(View.VISIBLE);
				layout_lineview_one.addView(PieChartView.getDistancePieView(this, report.getDistances(),report.getDisUnits()),LParams);
			}else {
				layout_lineview_one.setVisibility(View.GONE);
				layout_lineview_one.removeAllViews();
			}
			if(report!=null&&report.getTimes()!=null&&report.getTimeUnits()!=null) {
				layout_lineview_two.setVisibility(View.VISIBLE);
				layout_lineview_two.addView(PieChartView.getTimePieView(this, report.getTimes(),report.getTimeUnits()),LParams);
			}else {
				layout_lineview_two.setVisibility(View.GONE);
				layout_lineview_two.removeAllViews();
			}
			if(report!=null&&report.getTemperatures()!=null&&report.getTempUnits()!=null) {
				layout_lineview_three.setVisibility(View.VISIBLE);
				layout_lineview_three.addView(PieChartView.getTempPieView(this, report.getTemperatures(),report.getTempUnits()),LParams);
			}else {
				layout_lineview_three.setVisibility(View.GONE);
				layout_lineview_three.removeAllViews();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void updateUI() {
		if(report!=null) {
			text_fuel_amount.setText(report.getFuelAvg()+"");
			text_legend.setText(report.getDistance()+"");
			text_time.setText(report.getSumTime());
			text_cost.setText(report.getPrice()+"元");
			text_speed.setText(report.getSpeedTop()+"公里/时");
			text_trouble_count.setText(report.getFaultNum()+"");
			text_alarm_count.setText(report.getAlarmNum()+"");

			text_fast_turbo.setText(report.getAcc()+"");
			text_brake_count.setText(report.getDec()+"");
			text_race_count.setText(report.getWhe()+"");
		}
		updateReport();

	}

	/**
	 * 当tab切换时调用,更新tab的icon和文字的属性
	 * 
	 * @param index
	 */
	private void refreshTabState(int index) {
		for (int i = 0; i < tabCount; i++) {
			tabs.get(i).setBackgroundColor(getResources().getColor(R.color.blue_bg_for_btn));
			tabs.get(i).setChecked(index == i ? true : false);
		}

		tabs.get(index).setBackgroundColor(getResources().getColor(R.color.top_bar_color));
	}


	@Override
	public void onTabPageChange(int index) {
		refreshTabState(index);
		if (index != currentTabIndex) {
			loadContent(index);
		}
		currentTabIndex = index;
	}

	/**
	 * 加载相应tab索引的tab内容
	 * 
	 * @param tabIndex
	 */
	private void loadContent(int tabIndex) {
		
		switch (tabIndex) {
		case 0:
			layout_date.setVisibility(View.GONE);
			getCarUseReport("0");
			break;
		case 1:
			Calendar calendar = Calendar.getInstance();
			int maximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			LogUtils.e(TAG, "maxinum " + maximum);
			layout_date.setVisibility(View.GONE);
			getCarUseReport("7");
			break;
		case 2:
			layout_date.setVisibility(View.VISIBLE);
			start_date = TimeUtils.getPreMonth(2,null);
			end_date = TimeUtils.getCurrentMonth();
			text_start_date.setText(start_date);
			text_end_date.setText(end_date);
			getCarUseReport(null);
			break;
		default:
			break;
		}
	}

	@Override
	public void onTabPageSlide(int distanceX) {
	}
}
