package com.xtrd.obdcar.nearby;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.NearCategoryAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.entity.Category;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
/**
 * 来源之一 加油提醒  进入附近搜索加油站
 * 
 * @author Administrator
 *
 */
public class NearCategoryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
	private SwipeRefreshLayout swipeLayout;
	private ListView listView;
	private TextView tips_text;

	private ArrayList<Category> list = new ArrayList<Category>();
	private NearCategoryAdapter adapter;
	private double latitude;
	private double longitude;

	
	public NearCategoryActivity() {
		layout_id = R.layout.activity_cate_detail;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,getResources().getString(R.string.title_nearby) , 0, 0);
		initView();
		startRefresh();
	}


	private void initView() {
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light,
				R.color.holo_orange_light, R.color.holo_red_light);
		listView = (ListView)findViewById(R.id.listView);
		listView.setDivider(null);
		listView.setDividerHeight(0);
		listView.setSelector(android.R.color.transparent);
		tips_text = (TextView)findViewById(R.id.tips_text);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK==resultCode) {
			switch (requestCode) {
			case 1:
				if(data.getBooleanExtra("finish", false)) {
					latitude = data.getDoubleExtra("latitude", 0);
					longitude = data.getDoubleExtra("longitude", 0);
					if(adapter!=null) {
						adapter.setLatLng(latitude, longitude);
						adapter.notifyDataSetChanged();
					}
				}
				break;

			default:
				break;
			}
		}
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

	@Override
	public void onRefresh() {
		list.clear();
		getCategorys();
	}
	
	
	private void getCategorys() {
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Near_Category_Url), new NetRequest.NetCallBack() {

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
										Category category = null;
										for(int i=0;i<array.length();i++) {
											category = new Category();
											category.parser(array.optJSONObject(i));
											list.add(category);
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

	protected void updateUI() {
		swipeLayout.setRefreshing(false);
		if(list!=null&&list.size()>0) {
			if(adapter==null) {
				adapter = new NearCategoryAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
		}else {
			tips_text.setText(getResources().getString(R.string.tips_category_no_data));
			
		}
	}
}
