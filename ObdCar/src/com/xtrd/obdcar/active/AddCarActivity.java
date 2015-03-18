package com.xtrd.obdcar.active;

import java.util.ArrayList;
import java.util.Iterator;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.BranchOpenHelper;
import com.xtrd.obdcar.db.CarOpenHelper;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.CarInfo;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.passport.ChooseBranchActivity;
import com.xtrd.obdcar.passport.ChooseDrivingActivity;
import com.xtrd.obdcar.passport.ChooseOilActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.PlateChooseDialog;

public class AddCarActivity extends BaseActivity {
	protected static final String TAG = "AddCarActivity";
	private static final int ID_OIL = 1;
	private static final int ID_MODEL = 2;
	private static final int ID_DRIVE = 3;
	private LinearLayout layout_branch;
	private TextView text_plate_area;
	private EditText edit_plate;
	private LinearLayout layout_add_car;
	private LinearLayout layout_oil;
	private LinearLayout layout_range_correct;
	private EditText edit_input;

	private int oilId = 30;
	private int modelId;
	private String drivingAreaCode;


	public AddCarActivity() {
		layout_id = R.layout.activity_car_add;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0,R.drawable.btn_back_bg,R.string.btn_add_car_text,R.string.btn_finish,0);
		initView();
		regClick();
	}

	private void regClick() {
		layout_branch.setOnClickListener(this);
		text_plate_area.setOnClickListener(this);
		layout_add_car.setOnClickListener(this);
		layout_oil.setOnClickListener(this);
	}

	private void initView() {
		layout_branch = (LinearLayout)findViewById(R.id.layout_branch);
		text_plate_area = (TextView)findViewById(R.id.car_plate_area);
		edit_plate = (EditText)findViewById(R.id.edit_plate);
		layout_add_car = (LinearLayout)findViewById(R.id.layout_add_car);
		layout_oil = (LinearLayout)findViewById(R.id.layout_oil_type);
		layout_range_correct = (LinearLayout)findViewById(R.id.layout_range_correct);
		edit_input = (EditText)findViewById(R.id.edit_input);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:

			authCarPlate();
			break;
		case R.id.car_plate_area:
			showAreaDialog();
			break;
		case R.id.layout_add_car:
			intent = new Intent(this, ChooseDrivingActivity.class);
			startActivityForResult(intent, ID_DRIVE);
			break;
		case R.id.layout_oil_type:
			intent = new Intent(this, ChooseOilActivity.class);
			startActivityForResult(intent, ID_OIL);
			break;
		case R.id.layout_branch:
			intent = new Intent(this, ChooseBranchActivity.class);
			startActivityForResult(intent, ID_MODEL);
			break;

		default:
			break;
		}
	}

	/**
	 * 添加车辆
	 */
	private void addCar() {
		if(StringUtils.isNullOrEmpty(edit_plate.getText().toString())) {
			Utils.showToast(this, "请输入车牌号");
			return;
		}

		if(0==modelId) {
			Utils.showToast(this, "请选择车型");
			return;
		}

		if(StringUtils.isNullOrEmpty(drivingAreaCode)) {
			Utils.showToast(this, "请选择行驶区域");
			return;
		}

		if(0==oilId) {
			Utils.showToast(this, "请选择燃油类型");
			return;
		}

		final String value = text_plate_area.getText().toString()+edit_plate.getText().toString().toUpperCase()+","+modelId+","+
				oilId+","+drivingAreaCode+","+(StringUtils.isNullOrEmpty(edit_input.getText().toString())?"0":edit_input.getText().toString());
		//没有登录添加到本地  登录则添加到服务器
		if(SettingLoader.hasLogin(this)) {
			AjaxParams params = new AjaxParams();
			params.put(ParamsKey.Active_Vehicles,value);
			NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Active_Add_Car_Url) ,params,new NetRequest.NetCallBack() {

				@Override
				public void sucCallback(String str) {
					try {
						if(!StringUtils.isNullOrEmpty(str)) {
							JSONObject json = new JSONObject(str);
							if(json!=null) {
								if(json.has("status")) {
									int status = json.getInt("status");
									if (1 == status) {
										Utils.showToast(AddCarActivity.this, getResources().getString(R.string.tips_add_car_suc));
										ArrayList<CarInfo> list = null;
										if(json.has("result")) {
											JSONArray array = json.getJSONArray("result");
											list = new ArrayList<CarInfo>();
											CarInfo item = null;
											for(int i=0;i<array.length();i++) {
												item = new CarInfo();
												item.parser(array.optJSONObject(i));
												list.add(item);
											}
											setDefaultCar(list.get(0).getVehicleId()+"");
										}
										CarInfo car = getCurrentCar(value);
										car.setVehicleId(list.get(0).getVehicleId());
										car.setBranchId(BranchOpenHelper.getInstance(AddCarActivity.this).getBranchId(car.getModelId()));
										car.setBranch(BranchOpenHelper.getInstance(AddCarActivity.this).getBranch(car.getModelId()));
										car.setSeries(BranchOpenHelper.getInstance(AddCarActivity.this).getSeries(car.getModelId()));
										SettingLoader.saveDefaultCar(AddCarActivity.this,car);
										if(!getIntent().getBooleanExtra("from", false)) {
											setResult(RESULT_OK);
										}else {//来源于添加车辆的且为激活
											Intent intent = new Intent(AddCarActivity.this,BoxActiveTwoActivity.class);
											intent.putExtra(ParamsKey.VEHICLEID, list!=null?list.get(0).getVehicleId():0);
											startActivity(intent);
										}
										finish();
									}else {
										Utils.showToast(AddCarActivity.this, getResources().getString(R.string.tips_add_car_fail));
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
					Utils.showToast(AddCarActivity.this, getResources().getString(R.string.tips_add_car_fail));
				}
			});
		}else {
			CarOpenHelper openHelper = CarOpenHelper.getInstance(AddCarActivity.this);
			CarInfo car = getCurrentCar(value);
			car.setBranchId(BranchOpenHelper.getInstance(AddCarActivity.this).getBranchId(car.getModelId()));
			car.setBranch(BranchOpenHelper.getInstance(AddCarActivity.this).getBranch(car.getModelId()));
			car.setSeries(BranchOpenHelper.getInstance(AddCarActivity.this).getSeries(car.getModelId()));
			openHelper.insertItem(car);
			SettingLoader.saveDefaultCar(AddCarActivity.this,car);
			Utils.showToast(AddCarActivity.this, getResources().getString(R.string.tips_add_car_suc));
			setResult(RESULT_OK);
			finish();
		}

	}

	protected CarInfo getCurrentCar(String value) {
		CarInfo info = new CarInfo();
		if(!StringUtils.isNullOrEmpty(value)) {
			String[] split = value.split(",");
			info.setPlateNumber(split[0]);
			info.setModelId(!StringUtils.isNullOrEmpty(split[1])?Integer.parseInt(split[1]):0);
			info.setDefaultFuelTypeId(!StringUtils.isNullOrEmpty(split[2])?Integer.parseInt(split[2]):0);
			info.setDrivingAreaCode(split[3]);
			info.setDrivingArea(((TextView)layout_add_car.getChildAt(1)).getText().toString());
			info.setDistance(!StringUtils.isNullOrEmpty(split[4])?Long.parseLong(split[4]):0);
		}
		return info;
	}

	private void showAreaDialog() {
		PlateChooseDialog dialog = new PlateChooseDialog(this, new PlateChooseDialog.CallBack() {

			@Override
			public void callback(String value) {
				text_plate_area.setText(value);
			}
		});
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(RESULT_OK==resultCode) {
			if(data==null) {
				return;
			}
			switch (requestCode) {
			case ID_MODEL:
				modelId = data.getIntExtra("modelId", 0);
				if(1==data.getIntExtra("support", 0)) {
					layout_range_correct.setVisibility(View.GONE);
				}else {
					layout_range_correct.setVisibility(View.VISIBLE);
				}
				((TextView)layout_branch.getChildAt(1)).setText(data.getStringExtra("value"));
				break;
			case ID_OIL:
				oilId  = data.getIntExtra("oilId",30);
				((TextView)layout_oil.getChildAt(1)).setText(data.getStringExtra("value"));
				break;
			case ID_DRIVE:
				drivingAreaCode = data.getStringExtra("areaCode");
				((TextView)layout_add_car.getChildAt(1)).setText(data.getStringExtra("value"));
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void authCarPlate() {
		String plate = text_plate_area.getText().toString()+edit_plate.getText().toString();
		if(StringUtils.isNullOrEmpty(plate)) {
			Utils.showToast(this, "请输入车牌号");
			return;
		}
		if(SettingLoader.hasLogin(this)) {
			AjaxParams params = new AjaxParams();
			params.put(ParamsKey.Pns,plate);
			NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Active_Verify_Url) ,params,new NetRequest.NetCallBack() {

				@Override
				public void sucCallback(String str) {
					try {
						if(!StringUtils.isNullOrEmpty(str)) {
							JSONObject json = new JSONObject(str);
							if(json!=null) {
								if(json.has("status")) {
									int status = json.getInt("status");
									if (1 == status) {
										if(json.has("result")) {
											json = json.getJSONObject("result");
											Iterator iterator = json.keys();
											if(iterator!=null) {
												if(iterator.hasNext()) {
													String key = (String) iterator.next();
													int auth = json.getInt(key);
													if(0==auth) {
														addCar();
													}else if(1==auth) {
														Utils.showToast(AddCarActivity.this,"已添加过该车");
													}else if(-1==auth) {
														Utils.showToast(AddCarActivity.this,"该车已被人添加");
													}
												}
											}
										}

									}else {
										Utils.showToast(AddCarActivity.this, getResources().getString(R.string.tips_add_car_fail));
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
					Utils.showToast(AddCarActivity.this, getResources().getString(R.string.tips_add_car_fail));
				}
			});
		}else {
			addCar();
		}

	}

	private void setDefaultCar(final String vehicleId) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, vehicleId);
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Default_Car_Set_Url), params,new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status  = json.getInt("status");
							if(1==status) {
								XtrdApp.sendMsg(XtrdApp.ID_SET_DEFAULT);
								LogUtils.e(TAG, "set default");
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
			}
		});
	}
}
