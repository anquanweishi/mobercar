package com.xtrd.obdcar.view;


import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xtrd.obdcar.HomeActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.active.AddCarActivity;
import com.xtrd.obdcar.adapter.DefaultCarAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.CarOpenHelper;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.CarInfo;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class PopupSpinner {
	private View parent;
	private PopupWindow pupup;
	private LinearLayout layout;
	private Context context;
	private ListView listView;
	private ArrayList<CarInfo> list = new ArrayList<CarInfo>();
	private CallBack callBack;
	private DefaultCarAdapter adapter;

	public PopupSpinner(Context context, View parent, CallBack callBack) {
		this.parent = parent;
		this.context = context;
		this.callBack = callBack;
		if (pupup == null) {
			initPop();
		}
	}

	public void initPop() {
		LayoutInflater listInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = (LinearLayout) listInflater.inflate(R.layout.layout_popup, null);
		listView = (ListView)layout.findViewById(R.id.listView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.setMargins(0, Utils.dipToPixels(context, 10), 0, Utils.dipToPixels(context, 5));
		LinearLayout line = new LinearLayout(context);
		line.setGravity(Gravity.CENTER);
		TextView button = new TextView(context);
		button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_car_bg, 0, 0, 0);
		button.setText("添加爱车");
		button.setCompoundDrawablePadding(Utils.dipToPixels(context, 5));
		button.setTextColor(context.getResources().getColor(R.color.top_bar_color));
		line.addView(button,params);
		listView.addFooterView(line);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,AddCarActivity.class);
				((Activity)context).startActivityForResult(intent, 1);
				dismissPop();
			}
		});
		DefaultCarAdapter adapter = new DefaultCarAdapter(context, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(list.size()>position) {
					if(SettingLoader.hasLogin(context)) {
						setDefaultCar(list.get(position));
					}else {
						SettingLoader.saveDefaultCar(context, list.get(position));
						if(callBack!=null) {
							callBack.callback();
						}
					}
				}
				dismissPop();
			}
		});
		pupup = new PopupWindow(layout,Utils.dipToPixels(context, 200),Utils.dipToPixels(context, 240));
		pupup.getContentView().setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dismissPop();
			}
		});

		if(SettingLoader.hasLogin(context)) {
			getDefaultCar();
		}else {
			list.addAll(CarOpenHelper.getInstance(context).getLocalCars());
			updateUI();
		}
	}
	
	public interface CallBack {
		void callback();
	}



	public void showPopWindow() {
		pupup.showAsDropDown(parent,0,0);
		pupup.setFocusable(true);
		pupup.setTouchable(true);
		parent.invalidate();
		pupup.update();
	}

	public void dismissPop() {
		if (pupup != null) {
			pupup.dismiss();
			parent.invalidate();
		}
	}


	public boolean isShowing() {
		return pupup.isShowing();
	}

	private void getDefaultCar() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(context);
		fh.configCookieStore(store);
		fh.get(ApiConfig.getRequestUrl(ApiConfig.Car_List_Url), new AjaxCallBack<String>(){
			private int status = 0;

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							((HomeActivity)context).showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								status  = json.getInt("status");
								if(1==status) {
									JSONArray jsonArray = json.getJSONArray("result");
									if(jsonArray!=null&&jsonArray.length()>0) {
										CarInfo car = null;
										for(int i=0;i<jsonArray.length();i++) {
											car = new CarInfo();
											car.parser(jsonArray.optJSONObject(i));
											list.add(car);
										}
									}
								}
							}
							updateUI();
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
			}

		});

	}

	protected void updateUI() {
		if(adapter==null) {
			adapter = new DefaultCarAdapter(context,list);
			listView.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}

	}

	private void setDefaultCar(final CarInfo carInfo) {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(context);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, carInfo.getVehicleId()+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Default_Car_Set_Url), params, new AjaxCallBack<String>(){
			private int status = 0;
			private String msg;

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							((HomeActivity)context).showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								status  = json.getInt("status");
								if(1==status) {
									XtrdApp.sendMsg(XtrdApp.ID_SET_DEFAULT);
									Utils.showToast(context,context.getResources().getString(R.string.info_car_set_success));
									for(CarInfo info : list) {
										info.setChecked(false);
									}
									carInfo.setChecked(true);
									SettingLoader.saveDefaultCar(context, carInfo);
									if(callBack!=null) {
										callBack.callback();
									}
									updateUI();
								}else {
									msg = json.getString("message");
									Utils.showToast(context,msg);
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
			}

		});

	}

}