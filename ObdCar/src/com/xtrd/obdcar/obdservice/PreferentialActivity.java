package com.xtrd.obdcar.obdservice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.active.CarListActivity;
import com.xtrd.obdcar.adapter.GuidePageAdapter;
import com.xtrd.obdcar.adapter.MerchantAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.Merchant;
import com.xtrd.obdcar.passport.LoginActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.MyListView;
import com.xtrd.obdcar.view.ObdDialog;

public class PreferentialActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
	protected static final String TAG = " MerchantActivity";
	private static final int Interval_Time = 2*1000;

	private SwipeRefreshLayout swipeLayout;
	private MyListView listView;
	private ViewPager layout_gallery;
	private ArrayList<View> pageViews;
	private LinearLayout layout_points;

	private ArrayList<Merchant> list = new ArrayList<Merchant>();
	private boolean refresh = true;
	private TextView tips_text;
	protected double longitude;
	protected double latitude;
	protected boolean hasMore;
	private MerchantAdapter adapter;
	
	protected String currentCity;
	private String name;
	
	private Timer timer;
	private int current = 0;

	public PreferentialActivity() {
		layout_id = R.layout.activity_merchant;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_prefer, 0,R.drawable.ic_search_bg);
		initView();
		if(!SettingLoader.hasLogin(this)) {
			Intent intent = new Intent(this,LoginActivity.class);
			startActivityForResult(intent, 2);
			return;
		}
		
		if(!SettingLoader.hasBindBox(this)) {
			showSimulateDialog();
			return;
		}
		startRefresh();
		
	}

	private void showSimulateDialog() {
		ObdDialog dialog = new ObdDialog(this).setTitle("温馨提示")
				.setMessage("您还没绑定盒子，请先绑定。")
				.setPositiveButton(getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.setNegativeButton("绑定盒子", new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(PreferentialActivity.this,CarListActivity.class);
						startActivity(intent);
					}
				});
		if(!dialog.isShowing()) {
			dialog.show();
		}
	}

	private void initView() {

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light,
				R.color.holo_orange_light, R.color.holo_red_light);
		
		tips_text = (TextView) findViewById(R.id.tips_text);

		View view = LayoutInflater.from(this).inflate(
				R.layout.layout_merchant_top, null);
		listView = (MyListView) findViewById(R.id.listView);
		listView.addHeaderView(view);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				refresh = false;
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if (hasMore) {
							getMerchants(longitude, latitude);
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});

		layout_gallery = (ViewPager) findViewById(R.id.layout_gallery);
		layout_points = (LinearLayout) findViewById(R.id.layout_points);
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
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			Intent intent = new Intent(this, SerachInputActivity.class);
			startActivityForResult(intent, 1);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
			case 1:
				if (data != null) {
					name = data.getStringExtra("input");
					getLocation();
				}
				break;
			case 2:
				finish();
				break;

			default:
				break;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(timer!=null) {
			timer.cancel();
			timer = null;
		}
	}

	
	@Override
	public void onRefresh() {
		list.clear();
		getLocation();
	}

	private void getLocation() {
		showLoading();
		if(SettingLoader.getCarLatLng(this)!=null) {
			currentCity = SettingLoader.getCurrentCity(this);
			getMerchants(SettingLoader.getCarLatLng(this).longitude, SettingLoader.getCarLatLng(this).latitude);
		}else {
			XtrdApp.getInstance().getCurrentLocation(
					new XtrdApp.LocationCallBack() {
						
						@Override
						public void callback(double lon, double lat, String city) {
							longitude = lon;
							latitude = lat;
							currentCity = city;
							getMerchants(longitude, latitude);
						}
					});
		}
	}

	private void getMerchants(double longitude, double latitude) {
		
		if (!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.LONGITUDE, longitude + "");
		params.put(ParamsKey.LATITUDE, latitude + "");
		params.put(ParamsKey.CITY, currentCity);
		if (!StringUtils.isNullOrEmpty(name)) {
			params.put(ParamsKey.Reg_Name, name);
		}

		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);

		fh.post(ApiConfig.getRequestUrl(ApiConfig.Discount_Merchant_Url),
				params, new AjaxCallBack<String>() {

					@Override
					public void onSuccess(String str) {
						dismissLoading();
						LogUtils.e(TAG, "str " + str);
						try {
							if (!StringUtils.isNullOrEmpty(str)) {
								JSONObject json = new JSONObject(str);
								if (json.has("status")) {
									int status = json.getInt("status");
									if (1 == status) {
										if (refresh) {
											list.clear();
										}
										if (json.has("result")) {
											JSONArray array = json.getJSONArray("result");
											if (array != null && array.length() > 0) {
												Merchant merchant = null;
												for (int i = 0; i < array.length(); i++) {
													merchant = new Merchant();
													merchant.parser(array.optJSONObject(i));
													list.add(merchant);
												}
											}
										}
										if (json.has("datas")) {
											JSONArray array = json.getJSONArray("datas");
											if (array != null&& array.length() > 0) {
												processGallery(array);
											}
										}
									} else {
										Utils.showToast(PreferentialActivity.this,getResources().getString(R.string.tips_no_merchants));
									}
									updateUI();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						super.onSuccess(str);
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						dismissLoading();
						swipeLayout.setRefreshing(false);
						Utils.showToast(PreferentialActivity.this,getResources().getString(R.string.tips_no_merchants));
						super.onFailure(t, errorNo, strMsg);
					}
				});
	}

	protected void updateUI() {
		swipeLayout.setRefreshing(false);
		if (list != null && list.size() > 0) {
			tips_text.setVisibility(View.GONE);
			if (listView.getAdapter() == null) {
				adapter = new MerchantAdapter(this, list);
				listView.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		} else {
			tips_text.setVisibility(View.VISIBLE);
			tips_text.setText(getResources().getString(R.string.tips_no_merchants));
			listView.setVisibility(View.GONE);
			name = "";
		}

	}

	private void processGallery(final JSONArray jsonArray) {
		pageViews = new ArrayList<View>();
		View image = null;
		LinearLayout.LayoutParams pLP = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pLP.setMargins(0, 0, Utils.dipToPixels(this, 5), 0);
		ImageView point = null;
		layout_points.removeAllViews();
		for (int i = 0; i < jsonArray.length(); i++) {
			image = new View(this);
			pageViews.add(image);
			point = new ImageView(this);
			if (i == 0) {
				point.setImageResource(R.drawable.ic_solid_point);
			} else {
				point.setImageResource(R.drawable.ic_dash_point);
			}
			layout_points.addView(point, pLP);
		}

		layout_gallery.setAdapter(new GuidePageAdapter(this, jsonArray,
				pageViews));
		layout_gallery.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				for (int i = 0; i < layout_points.getChildCount(); i++) {
					ImageView view = (ImageView) layout_points.getChildAt(i);
					view.setImageResource(R.drawable.ic_dash_point);
				}

				ImageView view = (ImageView) layout_points.getChildAt(arg0);
				view.setImageResource(R.drawable.ic_solid_point);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

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

}
