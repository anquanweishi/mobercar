package com.xtrd.obdcar.setting;

import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.adapter.DefaultCarAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.CarInfo;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.ObdDialog;

public class DefaultCarActivity extends BaseActivity {
	protected static final String TAG = "DefaultCarActivity";
	private ListView listView;
	private ArrayList<CarInfo> list = new ArrayList<CarInfo>();
	private boolean from;

	public DefaultCarActivity() {
		layout_id = R.layout.activity_car_default;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		from = getIntent().getBooleanExtra("from", false);//true 默认车辆 false 解除绑定

		initTitle(0, R.drawable.btn_back_bg, from?R.string.set_item_default_car:R.string.set_item_car_unbind, 0, 0);
		initView();
		getDefaultCar();
	}



	private void initView() {
		listView = (ListView)findViewById(R.id.listView);
		DefaultCarAdapter adapter = new DefaultCarAdapter(this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(from) {
					showSetDialog(list.get(position));
				}else {
					showUnBind(list.get(position));
				}
			}
		});

	}

	protected void showSetDialog(final CarInfo carInfo) {
		new ObdDialog(this)
		.setTitle("设置默认车辆")
		.setMessage("确认设置该车辆为默认车辆?")
		.setPositiveButton(getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.setNegativeButton(getResources().getString(R.string.btn_confirm), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				setDefaultCar(carInfo);
			}
		}).show();
	}

	protected void updateUI() {
		((DefaultCarAdapter)listView.getAdapter()).notifyDataSetChanged();
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

	private void getDefaultCar() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		fh.get(ApiConfig.getRequestUrl(ApiConfig.Car_List_Url), new AjaxCallBack<String>(){
			private int status = 0;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, t);
				dismissLoading();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
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
				dismissLoading();
				Utils.showToast(DefaultCarActivity.this, getResources().getString(R.string.data_load_fail));
				super.onFailure(t, errorNo, strMsg);
			}

		});

	}

	private void setDefaultCar(final CarInfo carInfo) {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, carInfo.getVehicleId()+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Default_Car_Set_Url), params, new AjaxCallBack<String>(){
			private int status = 0;
			private String msg;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								status  = json.getInt("status");
								if(1==status) {
									XtrdApp.sendMsg(XtrdApp.ID_SET_DEFAULT);
									getCarInfo(carInfo.getVehicleId());
									Utils.showToast(DefaultCarActivity.this,getResources().getString(R.string.info_car_set_success));
									SettingLoader.saveDefaultCar(DefaultCarActivity.this, carInfo);
									for(CarInfo info : list) {
										info.setChecked(false);
									}
									carInfo.setChecked(true);
									updateUI();
									finish();
								}else {
									msg = json.getString("message");
									Utils.showToast(DefaultCarActivity.this,msg);
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
				dismissLoading();
				Utils.showToast(DefaultCarActivity.this, getResources().getString(R.string.tips_default_car_set_fail));
				super.onFailure(t, errorNo, strMsg);
			}

		});

	}

	/**
	 * 获取车辆信息
	 * @param vehicleId
	 */
	private void getCarInfo(long vehicleId) {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, vehicleId+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Info_Url) ,params, new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, "default info "+t.toString());
				CarInfo car = new CarInfo();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								json = json.getJSONObject("result");
								car .parser(json);
							}else {
								msg = json.getString("message");
								Utils.showToast(DefaultCarActivity.this, msg);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				SettingLoader.setCarLatLng(DefaultCarActivity.this,car.getLatitude(),car.getLongitude(),true);
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				updateUI();
				super.onFailure(t, errorNo, strMsg);
			}

		});
	}


	private void showUnBind(final CarInfo carInfo) {
		if(SettingLoader.getVehicleId(this).equals(carInfo.getVehicleId()+"")) {
			Utils.showToast(this, "不能解除绑定的默认车辆哦！");
			return;
		}

		new ObdDialog(this).setTitle(getResources().getString(R.string.set_item_car_unbind))
		.setMessage("确定解除当前车辆绑定吗？")
		.setPositiveButton(getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		})
		.setNegativeButton(getResources().getString(R.string.btn_confirm), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				unbindCar(carInfo);
			}
		}).show();

	}

	protected void unbindCar(final CarInfo carInfo) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, carInfo.getVehicleId()+"");
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Unbind_Car_Url),params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String t) {
				LogUtils.e(TAG, t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								Utils.showToast(DefaultCarActivity.this,getResources().getString(R.string.info_unbind_suc));
								list.remove(carInfo);
								updateUI();
							}else {
								if(0==status) {
									String message = json.getString("message");
									Utils.showToast(DefaultCarActivity.this, message);
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(DefaultCarActivity.this, getResources().getString(R.string.tips_unbind_fail));
			}
		});
	}

}
