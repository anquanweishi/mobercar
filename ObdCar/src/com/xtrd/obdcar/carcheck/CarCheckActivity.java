package com.xtrd.obdcar.carcheck;

import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.HealthAdapter;
import com.xtrd.obdcar.adapter.HealthScroolAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.Config;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.CarInfo;
import com.xtrd.obdcar.entity.CarTrouble;
import com.xtrd.obdcar.passport.LoginActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.MyProgressView;
import com.xtrd.obdcar.view.ObdDialog;

public class CarCheckActivity extends BaseActivity {
	protected static final String TAG = "CarCheckActivity";
	private MyProgressView mTasksView;
	private int mTotalProgress = 100;
	private int mCurrentProgress = 0;
	private ToggleButton btn_toggle;
	private TextView text_car_info;
	private TextView text_car_break;
	private ListView listView;
	private ListView mListView;

	private ArrayList<CarTrouble> list = new ArrayList<CarTrouble>();
	private int vehicleId;
	private TextView text_result;
	private CarInfo car;
	private long troubleNum;//故障数量

	public CarCheckActivity() {
		layout_id = R.layout.activity_check;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean from = getIntent().getBooleanExtra("from", false);
		if(from) {
			initTitle(0, R.drawable.btn_back_bg, R.string.title_car_check, 0,0);
		}else {
			initTitle(0, R.drawable.btn_back_bg, R.string.title_car_check, 0,R.drawable.ic_search_bg);
		}
		vehicleId = getIntent().getIntExtra("vehicleId", 0);
		troubleNum = getIntent().getLongExtra("troubleNum", 0);
		initView();
		registerClick();
		if(!SettingLoader.hasLogin(this)) {
			Intent intent = new Intent(this,LoginActivity.class);
			startActivityForResult(intent, 1);
			return;
		}
		if(troubleNum>0) {
			getTrouble();
		}else {
			if(from) {
				getDetail(getIntent().getStringExtra("dataId"));
			}else {
				getCarInfo();
			}
		}
	}


	private void registerClick() {
		btn_toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					text_car_info.setTextColor(getResources().getColor(R.color.gray));
					text_car_break.setTextColor(getResources().getColor(R.color.black));
				}else {
					text_car_info.setTextColor(getResources().getColor(R.color.black));
					text_car_break.setTextColor(getResources().getColor(R.color.gray));
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK==resultCode) {
			switch (requestCode) {
			case 1:
				finish();
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
		case R.id.btn_right:
			Intent intent = new Intent(this,VehicleActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	private void initView() {
		mListView = (ListView)findViewById(R.id.mListView);
		mTasksView = (MyProgressView) findViewById(R.id.tasks_view);
		mTasksView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//				if(!car.isOnline()) {
				//					Utils.showToast(CarCheckActivity.this, getResources().getString(R.string.tips_car_offline));
				//					return;
				//				}
				//				if(!car.isDriving()) {
				//					Utils.showToast(CarCheckActivity.this, getResources().getString(R.string.tips_car_stop));
				//					return;
				//				}
				//
				//				getCheckResult();
				getCarInfo();
			}
		});
		LinearLayout.LayoutParams params = (LayoutParams) mTasksView.getLayoutParams();
		params.height = Utils.getScreenHeight(this)/2 - Utils.dipToPixels(this,100);
		mTasksView.setLayoutParams(params);

		text_result = (TextView)findViewById(R.id.text_result);
		text_car_info = (TextView)findViewById(R.id.text_car_info);
		btn_toggle = (ToggleButton)findViewById(R.id.btn_toggle);
		text_car_break = (TextView)findViewById(R.id.text_car_break);
		listView = (ListView)findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(troubleNum>0&&!StringUtils.isNullOrEmpty(list.get(position).getDetail().getFaultCode())) {
					Intent intent = new Intent(CarCheckActivity.this,TroubleResultActivity.class);
					intent.putParcelableArrayListExtra("list", list);
					intent.putExtra("position", position);
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 是否显示故障
	 * @param b
	 */
	protected void updateUI(boolean trouble) {
		//滚动listview
		if(mListView.getAdapter()==null) {
			HealthScroolAdapter adapter = new HealthScroolAdapter(this, list);
			adapter.setTrouble(trouble);
			mListView.setAdapter(adapter);
		}else {
			((HealthScroolAdapter)mListView.getAdapter()).setTrouble(trouble);
			((HealthScroolAdapter)mListView.getAdapter()).notifyDataSetChanged();
		}

		updateScroolList(trouble);
	}

	protected void updateCar() {
		if(car.isOnline()) {
			if(car.isDriving()) {
				getCheckResult();
			}else {
				new ObdDialog(this).setTitle("温馨提示")
				.setMessage("车辆处于停车状态，查询最近一次车况")
				.setPositiveButton(getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton("确定", new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						getDetail(null);
					}
				}).show();
			}
		}else {
			new ObdDialog(this).setTitle("温馨提示")
			.setMessage("车辆OBD处于离线状态，查询最近一次车况")
			.setPositiveButton(getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.setNegativeButton("确定", new ObdDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					getDetail(null);
				}
			}).show();

		}
	}

	private void updateScroolList(final boolean trouble) {
		mCurrentProgress = 0;
		new AsyncTask<Void, Integer, Long>(){
			int position = 0;
			@Override
			protected Long doInBackground(Void... params) {
				if(list.size()>0) {
					int percent = mTotalProgress/list.size();
					while (mCurrentProgress < mTotalProgress) {	
						try {
							Thread.sleep(100);
						} catch (Exception e) {
							e.printStackTrace();
						}
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mListView.setSelection(position);
							}
						});

						position++;
						mCurrentProgress += percent;
						mTasksView.setProgress(mCurrentProgress);
					}
				}
				return null;
			}
			@Override
			protected void onPostExecute(Long result) {
				mListView.setVisibility(View.GONE);
				if(trouble) {
					mTasksView.updateFirst("重新检测");
				}else {
					mTasksView.updateComplete(getFen());
				}
				//正常view
				if(listView.getAdapter()==null) {
					HealthAdapter adapter = new HealthAdapter(CarCheckActivity.this, list);
					adapter.setTrouble(trouble);
					listView.setAdapter(adapter);
				}else {
					((HealthAdapter)listView.getAdapter()).setTrouble(trouble);
					((HealthAdapter)listView.getAdapter()).notifyDataSetChanged();
				}
				super.onPostExecute(result);
			}

			@Override
			protected void onPreExecute() {
				mListView.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}
		}.execute();
	}

	private String getFen() {
		int total = 100;
		for(CarTrouble car : list) {
			if("故障灯亮起".equals(car.getName())) {
				if(!"Off".equalsIgnoreCase(car.getValue())) {
					total-=50;
				}
			}
			if("动力负荷计算值".equals(car.getName())) {
				float value = Float.parseFloat(car.getValue()==null?"0":car.getValue());
				if (value < 0 || value > 100) {
					total -= 1;
				}
			}

			if("冷却液温度".equals(car.getName())) {
				float value = Float.parseFloat(car.getValue()==null?"0":car.getValue());
				if (value > 95 && value < 100) {
					total -= 1;
				}
				if (value >= 100) {
					total -= 5;
				}
			}
			if("控制模块电压".equals(car.getName())) {
				float value = Float.parseFloat(car.getValue()==null?"0":car.getValue());
				if (value >= 10 && value < 12.5) {
					total -= 1;
				}
				if (value < 10) {
					total -= 5;
				}
			}
			if("等效比指令".equals(car.getName())) {
				float value = Float.parseFloat(car.getValue()==null?"0":car.getValue());
				if (value >1.0 || value < 0.1) {
					total -= 1;
				}
			}

		}
		return total+"分";
	}

	/**
	 * 获取车辆故障
	 */
	private void getTrouble() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, vehicleId==0?SettingLoader.getVehicleId(this):vehicleId+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Fault_URL) ,params, new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				super.onStart();
				showLoading();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, "info "+t.toString());
				dismissLoading();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									list.clear();
									if(json.has("result")) {
										JSONArray array = json.getJSONArray("result");
										if(array!=null&&array.length()>0) {
											CarTrouble health = null;
											for(int i=0;i<array.length();i++) {
												health = new CarTrouble();
												health.parser(array.optJSONObject(i));
												list.add(health);
											}
										}
									}
									updateUI(true);
								}else {
									msg = json.getString("message");
									Utils.showToast(CarCheckActivity.this, msg);
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
				Utils.showToast(CarCheckActivity.this, getResources().getString(R.string.data_load_fail));
				dismissLoading();
			}

		});
	}
	/**
	 * 获取车况
	 */
	private void getCarInfo() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, vehicleId==0?SettingLoader.getVehicleId(this):vehicleId+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Info_Url) ,params, new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				super.onStart();
				showLoading();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, "info "+t.toString());
				dismissLoading();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									car = new CarInfo();
									json = json.getJSONObject("result");
									car.parser(json);
									updateCar();
								}else {
									msg = json.getString("message");
									Utils.showToast(CarCheckActivity.this, msg);
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
				Utils.showToast(CarCheckActivity.this, getResources().getString(R.string.data_load_fail));
				dismissLoading();
			}

		});
	}

	/**
	 * 体检
	 */
	private void getCheckResult() {
		FinalHttp fh = new FinalHttp();
		fh.configTimeout(Config.TIMEOUT_TIME);
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, vehicleId==0?SettingLoader.getVehicleId(this):vehicleId+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Health_Check_Url) ,params, new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				super.onStart();
				showLoading();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, "default "+t.toString());
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
									list.clear();
									if(json.has("result")) {
										JSONArray array = json.getJSONArray("result");
										if(array!=null&&array.length()>0) {
											CarTrouble health = null;
											for(int i=0;i<array.length();i++) {
												health = new CarTrouble();
												health.parser(array.optJSONObject(i));
												list.add(health);
											}
										}
									}
									updateUI(false);
								}else {
									msg = json.getString("message");
									Utils.showToast(CarCheckActivity.this, msg);
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
				Utils.showToast(CarCheckActivity.this, getResources().getString(R.string.tips_car_check_fail));
				dismissLoading();
			}

		});
	}
	private void getDetail(String dataId) {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, vehicleId==0?SettingLoader.getVehicleId(this):vehicleId+"");
		if(!StringUtils.isNullOrEmpty(dataId)) {
			params.put(ParamsKey.DataId, dataId);
		}

		fh.post(ApiConfig.getRequestUrl(ApiConfig.OBD_Detail_Check_Url) ,params, new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				LogUtils.e(TAG, "default "+t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									list.clear();
									if(json.has("result")) {
										JSONArray array = json.getJSONArray("result");
										if(array!=null&&array.length()>0) {
											CarTrouble health = null;
											for(int i=0;i<array.length();i++) {
												health = new CarTrouble();
												health.parser(array.optJSONObject(i));
												list.add(health);
											}
										}
									}
									updateUI(false);
								}else {
									msg = json.getString("message");
									Utils.showToast(CarCheckActivity.this, msg);
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
				Utils.showToast(CarCheckActivity.this, getResources().getString(R.string.data_load_fail));
				dismissLoading();
			}
		});
	}

}
