package com.xtrd.obdcar.obdservice;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.Config;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.CacheManager;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.FuelInfo;
import com.xtrd.obdcar.entity.FuelReport;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.DateDialog;
import com.xtrd.obdcar.view.TabContent.OnTabPageChangeListener;
import com.xtrd.obdcar.view.TabWidget;

public class ReportActivity extends BaseActivity implements OnTabPageChangeListener{

	protected static final String TAG = "ReportActivity";
	private ArrayList<FuelReport> list = new ArrayList<FuelReport>();
	private int screenWidth;
	private ImageView tab_move;
	private HorizontalScrollView tabBar;
	private LinearLayout tabContainer;
	private int tabCount;
	private int minTabWidth;
	private ArrayList<TabWidget> tabs = new ArrayList<TabWidget>();
	private int currentTabIndex = 0;
	private int tabMoveWidth;
	private float tab_move_startX;

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
	private TextView text_avg_legend;
	private TextView text_avg_fuel;
	private TextView text_max_fuel;
	private TextView text_min_fuel;
	private LinearLayout layout_lineview_one;
	private LinearLayout layout_lineview_two;


	private ArrayList<String> xLabels = new ArrayList<String>();
	private String start_date="",end_date="";

	public ReportActivity() {
		layout_id = R.layout.activity_report;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0,R.drawable.btn_back_bg,R.string.text_oil_statistics,0,0);
		initTab();
		initView();

		for(int i=6;i>=0;i--) {
			xLabels.add(TimeUtils.getPreDay(i));
		}
		if(SettingLoader.hasLogin(this)&&SettingLoader.hasBindBox(this)) {
			getFuelForLatest();
			getFuelInfoforLatest();
		}else {
			list.addAll(CacheManager.getInstance(this).getReportDate("data_oil_report.txt"));
			updateReport();
			updateFuelInfo(CacheManager.getInstance(this).getReportTopData("data_oil_report_top.txt"));;

		}
	}

	private void initTab() {
		screenWidth = Utils.getScreenWidth(this);
		minTabWidth = screenWidth%3==0?screenWidth/3:(screenWidth+(3-screenWidth%3))/3;
		tab_move = (ImageView) findViewById(R.id.tab_move);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tab_move.getLayoutParams();
		params.width = minTabWidth;
		tab_move.setLayoutParams(params);
		tabBar = (HorizontalScrollView) findViewById(R.id.tab_bar);
		tabContainer = (LinearLayout) findViewById(R.id.tab_container);

		// 计算tab数量,和tab的最小宽度
		tabCount = 3;
		String[] tabTitles = new String[] {"最近一周","本月","自定义"};
		for (int i = 0; i < tabCount; i++) {
			// 将tab项,添加到tabbar
			TabWidget tab = new TabWidget(this, tabTitles[i]);
			tab.setChecked(i == 0 ? true : false);
			// tabId==subject里的id
			tab.setId(i);
			LinearLayout.LayoutParams tabLP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					Utils.dipToPixels(this, 35));
			tabLP.weight = 1;
			tab.setMinimumWidth(minTabWidth);
			tab.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!SettingLoader.hasLogin(ReportActivity.this)) {
						Utils.showToast(ReportActivity.this, getResources().getString(R.string.tips_no_login));
						return;
					}

					if(!SettingLoader.hasBindBox(ReportActivity.this)){
						Utils.showToast(ReportActivity.this, getResources().getString(R.string.tips_no_box));
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
			xLabels.clear();
			if(TimeUtils.isSameYear(start_date,end_date)) {
				for(int i=TimeUtils.getInterval(start_date,end_date);i>=0;i--) {
					xLabels.add(TimeUtils.getPreMonth(i,end_date));
				}
			}else {
				for(int i=TimeUtils.getIntervalYear(start_date,end_date);i>=0;i--) {
					xLabels.add(TimeUtils.getPreYear(i));
				}
			}
			
			if(TimeUtils.compare(start_date,end_date,"yyyy-MM")) {
				getFuelByDate(false);
				getFuelInfoByDate(false);
			}else {
				Utils.showToast(ReportActivity.this, "起始日期应早于结束日期");
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
			list.addAll(CacheManager.getInstance(this).getReportDate("data_oil_report.txt"));
			updateReport();
			updateFuelInfo(CacheManager.getInstance(this).getReportTopData("data_oil_report_top.txt"));;
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
							Utils.showToast(ReportActivity.this, "起始日期应早于结束日期");
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
		layout_date = (LinearLayout)findViewById(R.id.layout_date);
		layout_date.setVisibility(View.GONE);
		text_start_date = (TextView)findViewById(R.id.text_start_date);
		text_end_date = (TextView)findViewById(R.id.text_end_date);
		btn_query = (Button)findViewById(R.id.btn_select);
		text_start_date.setOnClickListener(this);
		text_end_date.setOnClickListener(this);
		btn_query.setOnClickListener(this);

		text_fuel_amount = (TextView)findViewById(R.id.text_fuel_amount);
		text_legend = (TextView)findViewById(R.id.text_legend);
		text_avg_legend = (TextView)findViewById(R.id.text_avg_legend);
		text_avg_fuel = (TextView)findViewById(R.id.text_avg_fuel);
		text_max_fuel = (TextView)findViewById(R.id.text_max_fuel);
		text_min_fuel = (TextView)findViewById(R.id.text_min_fuel);
		layout_lineview_one = (LinearLayout)findViewById(R.id.layout_lineview_one);
		layout_lineview_two = (LinearLayout)findViewById(R.id.layout_lineview_two);

		if(SettingLoader.hasLogin(this)&&SettingLoader.hasBindBox(this)) {
			layout_experience.setVisibility(View.GONE);
			scrollView.setVisibility(View.VISIBLE);
		}else {
			layout_experience.setVisibility(View.VISIBLE);
			scrollView.setVisibility(View.GONE);
			if(!SettingLoader.hasLogin(this)) {
				text_tips.setText("对不起，您还没有登录");
			}
			if(!SettingLoader.hasBindBox(this)) {
				text_tips.setText("对不起，您还没有购买盒子");
			}
		}
	}

	private void updateFuelInfo(FuelInfo fuelInfo) {
		text_fuel_amount.setText(String.format(getResources().getString(R.string.text_fuel_amount), fuelInfo.getFuelAmount()));
		text_legend.setText(String.format(getResources().getString(R.string.text_legend_amount), fuelInfo.getDistance()));
		text_avg_legend.setText(String.format(getResources().getString(R.string.text_avg_legend), fuelInfo.getAvgDistance()));
		text_avg_fuel.setText(String.format(getResources().getString(R.string.text_avg_fuel), fuelInfo.getAvgFC100()));
		text_max_fuel.setText(String.format(getResources().getString(R.string.text_max_fuel), fuelInfo.getMaxFC100()));
		text_min_fuel.setText(String.format(getResources().getString(R.string.text_min_fuel), fuelInfo.getMinFC100()));
	}

	protected void updateReport() {
		layout_lineview_one.removeAllViews();
		layout_lineview_two.removeAllViews();
		if(list!=null&&list.size()>0) {
			LinearLayout.LayoutParams LParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,Utils.dipToPixels(this, 240));
			layout_lineview_one.addView(LegendLineView.getBarView(this, list,xLabels,currentTabIndex,TimeUtils.isSameYear(start_date, end_date)),LParams);
			LinearLayout.LayoutParams FParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,Utils.dipToPixels(this, 240));
			layout_lineview_two.addView(FuelLineView.getBarView(this, list,xLabels,currentTabIndex,TimeUtils.isSameYear(start_date, end_date)),FParams);
		}else {
			Utils.showToast(this, "没有找到数据哦！");
		}
	}


	public void getFuelByDate(boolean current) {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		if(current) {
			params.put(ParamsKey.FromYear, getYear(TimeUtils.getCurrentMonth()));
			params.put(ParamsKey.FromMonth, getMonth(TimeUtils.getCurrentMonth()));
			params.put(ParamsKey.EndYear, getYear(TimeUtils.getCurrentMonth()));
			params.put(ParamsKey.EndMonth, getMonth(TimeUtils.getCurrentMonth()));
		}else {
			params.put(ParamsKey.FromYear, getYear(start_date));
			params.put(ParamsKey.FromMonth, getMonth(start_date));
			params.put(ParamsKey.EndYear, getYear(end_date));
			params.put(ParamsKey.EndMonth, getMonth(end_date));
		}
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Service_Fuel_Date_Url) ,params, new AjaxCallBack<String>() {

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
										JSONArray array = json.getJSONArray("result");
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
								}else {
									msg = json.getString("message");
									Utils.showToast(ReportActivity.this, msg);
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
				Utils.showToast(ReportActivity.this, getResources().getString(R.string.data_load_fail));
				dismissLoading();
			}

		});
	}

	/**
	 * 获取年份
	 * @param date
	 * @param pre
	 * @return
	 */
	private String getYear(String date) {
		if(StringUtils.isNullOrEmpty(date)){
			return "";
		}
		String[] split = date.split("-");
		if(split!=null) {
			return split[0];
		}
		return "";
	}
	/**
	 * 获取月份
	 * @param date
	 * @param pre
	 * @return
	 */
	private String getMonth(String date) {
		if(StringUtils.isNullOrEmpty(date)){
			return "";
		}
		String[] split = date.split("-");
		if(split!=null) {
			return split[1];
		}
		return "";
	}

	public void getFuelInfoByDate(boolean current) {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		if(current) {
			params.put(ParamsKey.FromYear, getYear(TimeUtils.getCurrentMonth()));
			params.put(ParamsKey.FromMonth, getMonth(TimeUtils.getCurrentMonth()));
			params.put(ParamsKey.EndYear, getYear(TimeUtils.getCurrentMonth()));
			params.put(ParamsKey.EndMonth, getMonth(TimeUtils.getCurrentMonth()));
		}else {
			params.put(ParamsKey.FromYear, getYear(start_date));
			params.put(ParamsKey.FromMonth, getMonth(start_date));
			params.put(ParamsKey.EndYear, getYear(end_date));
			params.put(ParamsKey.EndMonth, getMonth(end_date));
		}
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Service_Fuel_Date_Info_Url) ,params, new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				super.onStart();
				showLoading();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, "getFuelInfoByDate "+t.toString());
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
									FuelInfo fuelInfo = new FuelInfo();
									if(json.has("result")) {
										json = json.getJSONObject("result");
										fuelInfo.parser(json);
									}
									updateFuelInfo(fuelInfo);
								}else {
									msg = json.getString("message");
									Utils.showToast(ReportActivity.this, msg);
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
				Utils.showToast(ReportActivity.this, getResources().getString(R.string.data_load_fail));
				dismissLoading();
			}

		});
	}
	public void getFuelForLatest() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.Days, "7");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Service_Fuel_Days_Url) ,params, new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				LogUtils.e(TAG, "getFuelForLatest "+t.toString());
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
										JSONArray array = json.getJSONArray("result");
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
								}else {
									msg = json.getString("message");
									Utils.showToast(ReportActivity.this, msg);
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
				Utils.showToast(ReportActivity.this, getResources().getString(R.string.data_load_fail));
			}

		});
	}
	public void getFuelInfoforLatest() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.Days, "7");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Service_Fuel_Days_Info_Url) ,params, new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				LogUtils.e(TAG, "getFuelInfoforLatest "+t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									FuelInfo fuelInfo = new FuelInfo();
									if(json.has("result")) {
										json = json.getJSONObject("result");
										fuelInfo.parser(json);
									}
									updateFuelInfo(fuelInfo);
								}else {
									msg = json.getString("message");
									Utils.showToast(ReportActivity.this, msg);
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
				Utils.showToast(ReportActivity.this, getResources().getString(R.string.data_load_fail));
			}
		});
	}


	private int getTabWidth(int index) {
		if (index < tabs.size()) {
			TabWidget tab = tabs.get(index);
			return tab.getWidth();
		}
		return 0;
	}

	/**
	 * tab滑块的动画
	 * 
	 * @param index
	 */
	private void startTabAnimation(final int index) {
		int tabWidth = getTabWidth(index);
		tabMoveWidth = tab_move.getWidth();

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tab_move.getLayoutParams();
		//		tab_move_startX += params.leftMargin;
		tab_move.setLayoutParams(params);
		tabBar.postInvalidate();

		int animationDuration = 200;

		// 当要切换到的tab不在屏幕区域或部分在屏幕区域时执行下面的一段代码
		final int[] endLoc = new int[2];
		final TabWidget tab = tabs.get(index);
		tab.getLocationOnScreen(endLoc);
		int tabLeftPadding = tabBar.getPaddingLeft();
		if (endLoc[0] < tabLeftPadding || endLoc[0] + tabWidth > (screenWidth - tabLeftPadding)) {
			int x = 0;
			if (endLoc[0] < tabLeftPadding) {
				x = endLoc[0] - tabLeftPadding;
			} else if (endLoc[0] + tabWidth > screenWidth - tabLeftPadding) {
				x = endLoc[0] + tabWidth - (screenWidth - tabLeftPadding);
			}
			tabBar.smoothScrollBy(x, 0);
			animationDuration = 0;
		}

		// 开始tab滑块的动画
		final int endX = tabs.get(index).getLeft() + tabWidth / 2 - tabMoveWidth/2;
		TranslateAnimation tabBarAnimation = new TranslateAnimation(tab_move_startX, endX, 0, 0);
		tabBarAnimation.setDuration(animationDuration);
		tabBarAnimation.setFillEnabled(true);
		tabBarAnimation.setFillAfter(true);
		tab_move.startAnimation(tabBarAnimation);
		tab_move_startX = endX;

		tabBarAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画结束后,更新tab项的状态
				refreshTabState(index);
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

		});

	}

	/**
	 * 当tab切换时调用,更新tab的icon和文字的属性
	 * 
	 * @param index
	 */
	private void refreshTabState(int index) {
		for (int i = 0; i < tabCount; i++) {
			tabs.get(i).setChecked(index == i ? true : false);
		}
	}


	@Override
	public void onTabPageChange(int index) {
		startTabAnimation(index);
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
			xLabels.clear();
			for(int i=6;i>=0;i--) {
				xLabels.add(TimeUtils.getPreDay(i));
			}
			layout_date.setVisibility(View.GONE);
			getFuelForLatest();
			getFuelInfoforLatest();
			break;
		case 1:
			Calendar calendar = Calendar.getInstance();
			int maximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			LogUtils.e(TAG, "maxinum " + maximum);
			xLabels.clear();
			for(int i = 1;i<=maximum;i++) {
				if(i<10) {
					xLabels.add("0"+i);
				}else {
					xLabels.add(i+"");
				}
			}
			layout_date.setVisibility(View.GONE);
			getFuelByDate(true);
			getFuelInfoByDate(true);
			break;
		case 2:
			layout_date.setVisibility(View.VISIBLE);
			start_date = TimeUtils.getPreMonth(2,null);
			end_date = TimeUtils.getCurrentMonth();
			text_start_date.setText(start_date);
			text_end_date.setText(end_date);
			xLabels.clear();
			if(TimeUtils.isSameYear(start_date,end_date)) {
				for(int i=TimeUtils.getInterval(start_date,end_date);i>=0;i--) {
					xLabels.add(TimeUtils.getPreMonth(i,null));
				}
			}else {
				for(int i=TimeUtils.getIntervalYear(start_date,end_date);i>=0;i--) {
					xLabels.add(TimeUtils.getPreYear(i));
				}
			}
			getFuelByDate(false);
			getFuelInfoByDate(false);
			break;
		default:
			break;
		}
	}

	@Override
	public void onTabPageSlide(int distanceX) {
	}
}
