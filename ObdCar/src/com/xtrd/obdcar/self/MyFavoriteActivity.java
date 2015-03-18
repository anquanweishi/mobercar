package com.xtrd.obdcar.self;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.adapter.FavoriteAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.FavoriteGroup;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.view.MyExpandListView;

public class MyFavoriteActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

	private MyExpandListView listView;
	private TextView tips_text;
	private SwipeRefreshLayout swipeLayout;
	
	private FavoriteAdapter adapter;
	private ArrayList<FavoriteGroup> list = new ArrayList<FavoriteGroup>();

	public MyFavoriteActivity() {
		layout_id = R.layout.activity_my_fav;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.text_my_fav, 0,0);
		initView();

		startRefresh();
	}

	private void initView() {
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light,
				R.color.holo_orange_light, R.color.holo_red_light);
		listView = (MyExpandListView)findViewById(R.id.listView);
		tips_text = (TextView)findViewById(R.id.tips_text);
		listView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
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

		default:
			break;
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
	public void onRefresh() {
		list.clear();
		getMyFav();
	}

	private void getMyFav() {
		if(SettingLoader.getCarLatLng(this)==null) {
			showLoading();
			XtrdApp.getInstance().getCurrentLocation(new XtrdApp.LocationCallBack(){

				@Override
				public void callback(double longitude, double latitude,
						String city) {
					AjaxParams params = new AjaxParams();
					params.put(ParamsKey.LONGITUDE,longitude+"");
					params.put(ParamsKey.LATITUDE,latitude+"");
					NetRequest.requestUrl(MyFavoriteActivity.this, ApiConfig.getRequestUrl(ApiConfig.My_fav_Url), params, new NetRequest.NetCallBack() {

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
												FavoriteGroup item = null;
												for(int i=0;i<array.length();i++) {
													item = new FavoriteGroup();
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
						}
					});
				}});
		}else {
			AjaxParams params = new AjaxParams();
			params.put(ParamsKey.LONGITUDE,SettingLoader.getCarLatLng(this).longitude+"");
			params.put(ParamsKey.LATITUDE,SettingLoader.getCarLatLng(this).latitude+"");
			NetRequest.requestUrl(MyFavoriteActivity.this, ApiConfig.getRequestUrl(ApiConfig.My_fav_Url), params, new NetRequest.NetCallBack() {

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
											FavoriteGroup item = null;
											for(int i=0;i<array.length();i++) {
												item = new FavoriteGroup();
												item.parser(array.optJSONObject(i));
												list.add(item);
											}
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
			tips_text.setVisibility(View.VISIBLE);
			tips_text.setText(getResources().getString(R.string.tips_no_fav));
		}else {
			tips_text.setVisibility(View.GONE);
			if(adapter==null) {
				adapter = new FavoriteAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
			
			for(int i=0;i<list.size();i++) {
				listView.expandGroup(i);
			}
		}
		
	}

}
