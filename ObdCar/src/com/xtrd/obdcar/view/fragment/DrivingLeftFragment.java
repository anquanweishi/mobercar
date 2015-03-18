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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.LocationExpandAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.ObdLocation;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.ObdDialog;
import com.xtrd.obdcar.view.slidmenu.SlidingMenu;

public class DrivingLeftFragment {
	
	protected static final String TAG = "LeftFragment";
	private SlidingMenu mSlidingMenu;
	private FrameLayout layout_left;
	private ExpandableListView expandlist;
	private BaseActivity activity;
	private ArrayList<ObdLocation> list = new ArrayList<ObdLocation>();
	private LocationExpandAdapter adapter;
	private ObdLocation mLocation,mCity;
	private TextView choose_text;

	public void setSlideMenu(SlidingMenu mSlidingMenu, BaseActivity activity) {
		this.mSlidingMenu = mSlidingMenu;
		this.activity = activity;
	}

	public void initView(View menu, Context context) {
		layout_left = (FrameLayout)menu.findViewById(R.id.layout_left);
		expandlist = (ExpandableListView)menu.findViewById(R.id.expandlist);
		expandlist.setGroupIndicator(null);
		
		expandlist.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				ObdLocation location = mCity = list.get(groupPosition);
				choose_text.setText(mLocation.getDisplayName()+" " +location.getDisplayName());
				if(0==location.getHasChildren()) {
					showChooseDialog(location,null);
				}else {
					getCityById(location);
				}
				
				return false;
			}
		});
		
		expandlist.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				ObdLocation location = list.get(groupPosition).getList().get(childPosition);
				choose_text.setText(mLocation.getDisplayName() + " " + list.get(groupPosition).getDisplayName()+" " +location.getDisplayName());
				showChooseDialog(list.get(groupPosition),location);
				return false;
			}
		});
	}

	

	public void showChooseDialog(final ObdLocation city, final ObdLocation district) {
		ObdDialog obdDialog = new ObdDialog(activity)
		.setTitle("提示信息")
		.setMessage("确定选择   " + mLocation.getDisplayName()+ " " + city.getDisplayName() + " " + (district!=null?district.getDisplayName():""))
		.setPositiveButton("重选", new ObdDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mCity = null;
				choose_text.setText(mLocation.getDisplayName());
			}
		})
		.setNegativeButton("确定", new ObdDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				choose_text.setText(mLocation.getDisplayName()+ " " + city.getDisplayName() + " " + (district!=null?district.getDisplayName():""));
				Intent intent = new Intent();
				intent.putExtra("areaCode", (district!=null?district.getAreaCode():city.getAreaCode()));
				intent.putExtra("value", choose_text.getText().toString());
				activity.setResult(Activity.RESULT_OK, intent);
				activity.finish();
			}
		});
		obdDialog.show();
		
	}

	public void setData(TextView choose_text, ObdLocation location) {
		this.choose_text = choose_text;
		choose_text.setText(location.getDisplayName());
		this.mLocation = location;
		getAreaById(location.getAreaCode());
		
	}

	private void getAreaById(String areaCode) {
		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Area_Code, areaCode);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.City_Url),params, new AjaxCallBack<String>(){
			private int status = 0;

			@Override
			public void onStart() {
				activity.showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				activity.dismissLoading();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							status  = json.getInt("status");
							if(1==status) {
								JSONArray jsonArray = json.getJSONArray("result");
								if(jsonArray!=null&&jsonArray.length()>0) {
									list.clear();
									ObdLocation loc = null;
									for(int i=0;i<jsonArray.length();i++) {
										loc = new ObdLocation();
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
			adapter = new LocationExpandAdapter(activity,list);
			expandlist.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}

	}
	
	
	protected void getCityById(final ObdLocation location) {
		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Area_Code, location.getAreaCode());
		fh.post(ApiConfig.getRequestUrl(ApiConfig.City_Url),params, new AjaxCallBack<String>(){
			private int status = 0;

			@Override
			public void onStart() {
				activity.showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				activity.dismissLoading();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							status  = json.getInt("status");
							if(1==status) {
								JSONArray jsonArray = json.getJSONArray("result");
								if(jsonArray!=null&&jsonArray.length()>0) {
									ObdLocation loc = null;
									location.getList().clear();
									for(int i=0;i<jsonArray.length();i++) {
										loc = new ObdLocation();
										loc.parser((JSONObject)jsonArray.opt(i));
										location.getList().add(loc);
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
				activity.dismissLoading();
				super.onFailure(t, errorNo, strMsg);
			}

		});
	}

	public ObdLocation getChooseCity() {
		return mCity;
	}
}
