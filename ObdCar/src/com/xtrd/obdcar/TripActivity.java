package com.xtrd.obdcar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.active.CarListActivity;
import com.xtrd.obdcar.adapter.TripAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.Trip;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.passport.LoginActivity;
import com.xtrd.obdcar.trip.DateTripActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class TripActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

	protected static final String TAG = "TripActivity";
	private SwipeRefreshLayout swipeLayout;
	private ListView listView;
	private TripAdapter adapter;
	private ArrayList<Trip> list = new ArrayList<Trip>();
	private boolean refresh = false;
	private TextView tips_text;
	private boolean reload = false;
	private boolean hasMore = false;//是否有更多

	public Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case XtrdApp.ID_SET_DEFAULT:
				reload = true;
				list.clear();
				break;
			default:
				break;
			}
		}

	};



	public TripActivity() {
		layout_id = R.layout.activity_trip;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,(SettingLoader.hasLogin(this)&&SettingLoader.hasBindBox(this))?getResources().getString(R.string.title_trip):"体验账号-"+getResources().getString(R.string.title_trip), 0,R.drawable.ic_search_bg);

		initView();

		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}
		XtrdApp.addHandler(handler);
		
		if(SettingLoader.hasLogin(this)&&SettingLoader.hasBindBox(this)) {
			startRefresh();
		}else {
			getSimulationDate();
		}
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
	protected void onResume() {
		super.onResume();
		if(reload) {
			startRefresh();
			reload = false;
		}
	}

	private void initView() {

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light,
				R.color.holo_orange_light, R.color.holo_red_light);
		tips_text = (TextView)findViewById(R.id.tips_text);
		listView = (ListView)findViewById(R.id.listView);
		listView.setDividerHeight(0);
		listView.setPadding(0, Utils.dipToPixels(this, 10), 0, 0);
		listView.setSelector(android.R.color.transparent);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				refresh = false;
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if(hasMore) {
							getTripInfo();
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
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
		case R.id.btn_right:
			if(!SettingLoader.hasLogin(this)) {
				Intent intent = new Intent(this,LoginActivity.class);
				startActivity(intent);
				return;
			}
			if(!SettingLoader.hasBindBox(this)) {
				Intent intent = new Intent(this,CarListActivity.class);
				startActivity(intent);
				return;
			}
			Intent intent = new Intent(this,DateTripActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		if(!SettingLoader.hasLogin(this)) {
			Utils.showToast(this, R.string.tips_no_login);
			swipeLayout.setRefreshing(false);
			return;
		}
		if(!SettingLoader.hasBindBox(this)) {
			Utils.showToast(this, R.string.tips_no_box);
			swipeLayout.setRefreshing(false);
			return;
		}
		refresh = true;
		getTripInfo();
	}
	
	/**
	 * 获取模拟gps数据
	 */
	private void getSimulationDate() {
		list.clear();
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.TYPE, "trip");
		NetRequest.requestUrl(this,ApiConfig.getRequestUrl(ApiConfig.Simulate_Url), params,new NetRequest.NetCallBack() {
			
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
									if(array!=null&&array.length()>0) {
										Trip info = null;
										for(int i=0;i<array.length();i++) {
											info = new Trip();
											info.parser((JSONObject)array.opt(i));
											list.add(info);
										}
									}
								}
							}else {
								String msg = json.getString("message");
								Utils.showToast(TripActivity.this, msg);
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
				Utils.showToast(TripActivity.this, "数据加载失败");
			}
		});
	}
	
	private void getTripInfo() {
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			swipeLayout.setRefreshing(false);
			return;
		}
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		if(refresh) {
			if(list!=null&&list.size()>0) {
				if(2==list.get(0).getCarStatus()) {//1代表停车，2代表行驶，3代表离线
					if(list.size()<2){
						params.put(ParamsKey.MaxId,(list.get(0).getTripId()-1)+"");//往上看
					}else {
						params.put(ParamsKey.MaxId,list.get(1).getTripId()+"");//往上看
					}
				}else {
					params.put(ParamsKey.MaxId,list.get(0).getTripId()+"");//往上看
				}
			}
		}else {
			if(list!=null&&list.size()>0) {
				params.put(ParamsKey.MinId,list.get(list.size()-1).getTripId()+"");//往上看
			}
		}

		//		params.put(ParamsKey.PageSize, "30");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Trip_URL) , params,new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				tips_text.setVisibility(View.GONE);
				if(!refresh) {
					showLoading();
				}
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				if(!refresh) {
					dismissLoading();
				}
				LogUtils.e(TAG, t.toString());
				swipeLayout.setRefreshing(false);
				if("<".equals(String.valueOf(t.charAt(0)))) {
					showLoginDialog();
				}else {
					try {
						if(!StringUtils.isNullOrEmpty(t)) {
							JSONObject json = new JSONObject(t);
							int errorCode = 0;
							if(json.has("errorCode")) {
								errorCode = json.getInt("errorCode");
							}
							if(ApiConfig.InvilidSession==errorCode) {
								showLoginDialog();
							}else {
								if(json.has("status")) {
									int status = json.getInt("status");
									if(1==status) {
										if(json.has("result")) {
											json = json.getJSONObject("result");
											if(json.has("data")) {
												JSONArray array = json.getJSONArray("data");
												if(array!=null&&array.length()>0) {
													Trip trip = null;
													if(refresh) {
														removeRuningTrip();
													}
													for(int i=0;i<array.length();i++) {
														trip = new Trip();
														trip.parser((JSONObject)array.opt(i));
														if(!hasItem(trip.getTripId())) {
															if(2==trip.getCarStatus()) {
																list.add(0,trip);
															}else {
																list.add(trip);
															}
														}
													}
													hasMore = true;
												}
											}
										}else {
											hasMore = false;
										}
									}else {
										msg = json.getString("message");
										Utils.showToast(TripActivity.this, msg);
									}	
								}
							}
						}
						updateUI();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				swipeLayout.setRefreshing(false);
				if(!refresh) {
					dismissLoading();
				}
				Utils.showToast(TripActivity.this,getResources().getString(R.string.data_load_fail));
			}

		});
	}

	/**
	 * 移除正在行驶的trip
	 */
	protected void removeRuningTrip() {
		if(list!=null&&list.size()>0) {
			if(2==list.get(0).getCarStatus()) {
				list.remove(0);
			}
		}
	}

	private boolean hasItem(int tripId) {
		if(list!=null&&list.size()>0) {
			Trip trip = null;
			for(int i=0;i<list.size();i++) {
				trip = list.get(i);
				if(tripId==trip.getTripId()) {
					return true;
				}
			}
		}
		return false;
	}

	protected void updateUI() {
		if(list!=null&&list.size()>0) {
			tips_text.setVisibility(View.GONE);
			if(listView.getAdapter()==null) {
				adapter = new TripAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
		}else {
			tips_text.setVisibility(View.VISIBLE);
			tips_text.setText(getResources().getString(R.string.tips_no_trip));
		}
	}
}
