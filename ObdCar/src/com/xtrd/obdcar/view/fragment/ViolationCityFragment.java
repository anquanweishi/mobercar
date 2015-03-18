package com.xtrd.obdcar.view.fragment;

import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.adapter.ViolationCityAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.ViolationCity;
import com.xtrd.obdcar.illegal.IllegalCityActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.ObdDialog;
import com.xtrd.obdcar.view.slidmenu.SlidingMenu;

public class ViolationCityFragment {
	
	protected static final String TAG = "LeftFragment";
	private SlidingMenu mSlidingMenu;
	private FrameLayout layout_left;
	private ListView listview;
	private IllegalCityActivity activity;
	private ArrayList<ViolationCity> list = new ArrayList<ViolationCity>();
	private ViolationCityAdapter adapter;
	private ViolationCity mViolation;
	private TextView choose_text;

	public void setSlideMenu(SlidingMenu mSlidingMenu, IllegalCityActivity activity) {
		this.mSlidingMenu = mSlidingMenu;
		this.activity = activity;
	}

	public void initView(View menu, Context context) {
		layout_left = (FrameLayout)menu.findViewById(R.id.layout_left);
		listview = (ListView)menu.findViewById(R.id.listView);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ViolationCity location = list.get(position);
				choose_text.setText(mViolation.getProvince()+" " +location.getCityName());
				showChooseDialog(location);
			}
		});
	}

	

	protected void showChooseDialog(final ViolationCity city) {
		ObdDialog obdDialog = new ObdDialog(activity)
		.setTitle("提示信息")
		.setMessage("确定选择   " + mViolation.getProvince()+ " " + city.getCityName())
		.setPositiveButton("重选", new ObdDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				choose_text.setText(mViolation.getProvince());
			}
		})
		.setNegativeButton("确定", new ObdDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.putExtra("value", city.getCityName());
				intent.putExtra("city", city);
				activity.setResult(Activity.RESULT_OK, intent);
				activity.finish();
			}
		});
		obdDialog.show();
		
	}

	public void setData(TextView choose_text, ViolationCity location) {
		this.choose_text = choose_text;
		choose_text.setText(location.getProvince());
		this.mViolation = location;
		getViolationCity(location.getProvinceCode());
		
	}

	private void getViolationCity(String privinceCode) {
		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Province, privinceCode);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Violation_City_Url),params, new AjaxCallBack<String>(){
			private int status = 0;

			@Override
			public void onStart() {
				activity.showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				activity.dismissLoading();
				LogUtils.e(TAG, ""+t);
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							status  = json.getInt("status");
							if(1==status) {
								JSONArray jsonArray = json.getJSONArray("result");
								if(jsonArray!=null&&jsonArray.length()>0) {
									list.clear();
									ViolationCity loc = null;
									for(int i=0;i<jsonArray.length();i++) {
										loc = new ViolationCity();
										loc.parser((JSONObject)jsonArray.opt(i));
										list.add(loc);
									}
								}
							}
						}
						updateUI();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				LogUtils.e(TAG,"errorNo " + errorNo + "strMsg " + strMsg);
				activity.dismissLoading();
				super.onFailure(t, errorNo, strMsg);
			}

		});
	}
	
	
	protected void updateUI() {
		if(adapter==null) {
			adapter = new ViolationCityAdapter(activity,list);
			listview.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}

	}
}
