package com.xtrd.obdcar.self;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.adapter.MySubscripitionAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.GasStation;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.MyListView;

public class MySubscriptionActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
	private SwipeRefreshLayout swipeLayout;
	private MyListView listView;
	private Button btn_subscribe; 
	private LinearLayout layout_no_subscription;
	private MySubscripitionAdapter adapter;
	private ArrayList<GasStation> list = new ArrayList<GasStation>();
	private Button btn_subscribe_more;
	private RelativeLayout layout_list;
	private boolean refresh = true;
	protected boolean hasMore = true;

	public MySubscriptionActivity() {
		layout_id = R.layout.activity_my_sub_station;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,
				R.string.title_my_realtime,R.string.text_real_oil_manage,0);
		initView();
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
		list.clear();
		getRealPrice();
	}

	private void initView() {
		btn_right.setVisibility(View.GONE);

		layout_list = (RelativeLayout)findViewById(R.id.layout_list);
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light,
				R.color.holo_orange_light, R.color.holo_red_light);
		listView = (MyListView)findViewById(R.id.listView);
		btn_subscribe = (Button)findViewById(R.id.btn_subscribe);
		layout_no_subscription = (LinearLayout)findViewById(R.id.layout_no_subscription);
		btn_subscribe.setOnClickListener(this);
		btn_subscribe_more = (Button)findViewById(R.id.btn_subscribe_more);
		btn_subscribe_more.setOnClickListener(this);

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if(hasMore ) {
							refresh = false;
							getRealPrice();
						}else {
							Utils.showToast(MySubscriptionActivity.this, R.string.has_no_more_data);
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});

		adapter = new MySubscripitionAdapter(this, list);
		listView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			if(list==null||list.size()==0) {
				Utils.showToast(this, R.string.tips_no_subscription);
				return;
			}
			adapter.setEdit(!adapter.isEdit());
			if(adapter.isEdit()) {
				btn_subscribe_more.setVisibility(View.GONE);
				((TextView)btn_right.getChildAt(0)).setText("完成");
			}else {
				((TextView)btn_right.getChildAt(0)).setText("管理");
				btn_subscribe_more.setVisibility(View.VISIBLE);
			}
			adapter.notifyDataSetChanged();
			break;
		case R.id.btn_subscribe:
			Intent intent = new Intent(MySubscriptionActivity.this,SubscriptionActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_subscribe_more:
			Intent more = new Intent(MySubscriptionActivity.this,SubscriptionActivity.class);
			startActivity(more);
			break;
		default:
			break;
		}
	}

	private void getRealPrice() {
		if(SettingLoader.getCarLatLng(this)==null) {
			showLoading();
			XtrdApp.getInstance().getCurrentLocation(new XtrdApp.LocationCallBack(){

				@Override
				public void callback(double longitude, double latitude,
						String city) {
					AjaxParams params = new AjaxParams();
					params.put(ParamsKey.LONGITUDE,longitude+"");
					params.put(ParamsKey.LATITUDE,latitude+"");
					if(refresh) {
						if(list!=null&&list.size()>0) {
							params.put(ParamsKey.MaxTime, list.get(0).getTime());
						}
					}else {
						if(list!=null&&list.size()>0) {
							params.put(ParamsKey.MinTime, list.get(list.size()-1).getTime());
						}
					}

					NetRequest.requestUrl(MySubscriptionActivity.this, ApiConfig.getRequestUrl(ApiConfig.My_Subscrption_Url), params, new NetRequest.NetCallBack() {

						@Override
						public void sucCallback(String str) {
							try {
								if(!StringUtils.isNullOrEmpty(str)) {
									JSONObject json = new JSONObject(str);
									if(json.has("status")) {
										int status = json.getInt("status");
										if(1==status) {
											if(json.has("result")) {
												JSONArray array = json.getJSONArray("result");
												GasStation item = null;
												for(int i=0;i<array.length();i++) {
													item = new GasStation();
													item.parser(array.optJSONObject(i));
													list.add(item);
												}
												hasMore = true;
											}else {
												hasMore = false;
											}
										}else {
											hasMore = false;
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

						}
					});
				}});
		}else {
			AjaxParams params = new AjaxParams();
			params.put(ParamsKey.LONGITUDE,SettingLoader.getCarLatLng(this).longitude+"");
			params.put(ParamsKey.LATITUDE,SettingLoader.getCarLatLng(this).latitude+"");

			if(refresh ) {
				if(list!=null&&list.size()>0) {
					params.put(ParamsKey.MinTime, list.get(0).getTime());
				}
			}else {
				if(list!=null&&list.size()>0) {
					params.put(ParamsKey.MinTime, list.get(list.size()-1).getTime());
				}
			}


			NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.My_Subscrption_Url), params, new NetRequest.NetCallBack() {

				@Override
				public void sucCallback(String str) {
					try {
						if(!StringUtils.isNullOrEmpty(str)) {
							JSONObject json = new JSONObject(str);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									if(json.has("result")) {
										JSONArray array = json.getJSONArray("result");
										GasStation item = null;
										for(int i=0;i<array.length();i++) {
											item = new GasStation();
											item.parser(array.optJSONObject(i));
											list.add(item);
										}
									}
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
					swipeLayout.setRefreshing(false);
				}
			});
		}
	}

	protected void updateUI() {
		swipeLayout.setRefreshing(false);
		if(list==null||list.size()==0) {
			layout_no_subscription.setVisibility(View.VISIBLE);
			layout_list.setVisibility(View.GONE);
			btn_right.setVisibility(View.GONE);
		}else {
			btn_right.setVisibility(View.VISIBLE);
			layout_no_subscription.setVisibility(View.GONE);
			layout_list.setVisibility(View.VISIBLE);
			adapter.notifyDataSetChanged();
		}

	}
}
