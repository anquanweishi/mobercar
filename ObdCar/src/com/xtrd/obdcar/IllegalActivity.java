package com.xtrd.obdcar;

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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.RuleBreakOpenHelper;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.ViolationCity;
import com.xtrd.obdcar.entity.ViolationResult;
import com.xtrd.obdcar.illegal.IllegalCityActivity;
import com.xtrd.obdcar.illegal.IllegalPlateActivity;
import com.xtrd.obdcar.illegal.IllegalResultActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.ImageUtils;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.DialogImage;
import com.xtrd.obdcar.view.PlateChooseDialog;

public class IllegalActivity extends BaseActivity {

	protected static final String TAG = "IllegalActivity";
	private static final int ID_CITY = 1;
	private static final int ID_PLATE = 2;
	private LinearLayout layout_loc_plate;
	private ImageView img_plate;

	private TextView title_plate;
	private LinearLayout layout_plate;
	private TextView car_plate_area;
	private EditText edit_plate;

	private LinearLayout layout_drive_area;
	private LinearLayout layout_engine;
	private LinearLayout layout_vin;
	private LinearLayout layout_resiter;
	private LinearLayout layout_plate_type;
	private TextView text_drive_area;
	private TextView text_plate;
	private EditText edit_vin;
	private ImageView btn_vin_refer;
	private EditText edit_engine;
	private EditText edit_register;
	private ImageView btn_engine_refer;
	private String areaCode,plateType="02";
	protected boolean reload = false;
	private String plate = "";
	
	private ViolationCity vioCity;
	

	public Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case XtrdApp.ID_SET_DEFAULT:
				if(isFinishing()) {
					updateView();
				}else {
					reload  = true;
				}
				break;
			default:
				break;
			}
		}

	};




	public IllegalActivity() {
		layout_id = R.layout.activity_illegal;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_rule_break, R.string.btn_query, 0);

		initView();
		registerClick();
		updateView();
		XtrdApp.addHandler(handler);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(reload){
			updateView();
			reload = false;
		}

		if(StringUtils.isNullOrEmpty(edit_engine.getText().toString())&&!StringUtils.isNullOrEmpty(SettingLoader.getEngineNum(this))) {
			updateView();
		}
	}

	private void registerClick() {
		car_plate_area.setOnClickListener(this);
		layout_drive_area.setOnClickListener(this);
		layout_plate_type.setOnClickListener(this);
		btn_engine_refer.setOnClickListener(this);
		btn_vin_refer.setOnClickListener(this);
	}



	private void initView() {
		layout_loc_plate = (LinearLayout)findViewById(R.id.layout_loc_plate);
		img_plate = (ImageView)findViewById(R.id.img_plate);
		title_plate = (TextView)findViewById(R.id.title_plate);

		layout_plate = (LinearLayout)findViewById(R.id.layout_plate);
		car_plate_area = (TextView)findViewById(R.id.car_plate_area);
		edit_plate = (EditText)findViewById(R.id.edit_plate);

		layout_drive_area = (LinearLayout)findViewById(R.id.layout_drive_area);
		text_drive_area = (TextView)findViewById(R.id.text_drive_area);
		layout_plate_type = (LinearLayout)findViewById(R.id.layout_plate_type);
		text_plate = (TextView)findViewById(R.id.text_plate);
		layout_engine = (LinearLayout)findViewById(R.id.layout_engine);
		edit_engine = (EditText)findViewById(R.id.edit_engine);
		btn_engine_refer = (ImageView)findViewById(R.id.btn_engine_refer);
		layout_vin = (LinearLayout)findViewById(R.id.layout_vin);
		edit_vin = (EditText)findViewById(R.id.edit_vin);
		btn_vin_refer = (ImageView)findViewById(R.id.btn_vin_refer);
		layout_resiter = (LinearLayout)findViewById(R.id.layout_resiter);
		edit_register = (EditText)findViewById(R.id.edit_register);
	}

	private void updateView() {
		if(!StringUtils.isNullOrEmpty(SettingLoader.getCarPlate(this))) {
			layout_loc_plate.setVisibility(View.VISIBLE);
			layout_plate.setVisibility(View.GONE);
		}else {
			layout_loc_plate.setVisibility(View.GONE);
			layout_plate.setVisibility(View.VISIBLE);
		}

		ImageUtils.displayBranchImg(this, img_plate, SettingLoader.getBranchId(this));
		title_plate.setText(SettingLoader.getCarPlate(this));

		edit_engine.setText(SettingLoader.getEngineNum(this));
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			getViolation();
			break;
		case R.id.btn_vin_refer:
			showCarFrameTips();
			break;
		case R.id.btn_engine_refer:
			showEngineTips();
			break;
		case R.id.layout_drive_area:
			Intent intent = new Intent(this,IllegalCityActivity.class);
			startActivityForResult(intent, ID_CITY);
			break;
		case R.id.layout_plate_type:
			Intent pintent = new Intent(this,IllegalPlateActivity.class);
			startActivityForResult(pintent, ID_PLATE);
			break;
		case R.id.car_plate_area:
			showAreaDialog();
			break;

		default:
			break;
		}
	}

	private void showAreaDialog() {
		PlateChooseDialog dialog = new PlateChooseDialog(this, new PlateChooseDialog.CallBack() {

			@Override
			public void callback(String value) {
				car_plate_area.setText(value);
			}
		});
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK==resultCode) {
			switch (requestCode) {
			case ID_CITY:
				vioCity = data.getParcelableExtra("city");
				text_drive_area.setText(data.getStringExtra("value"));
				areaCode = vioCity.getCityCode();
				updateUI();
				break;
			case ID_PLATE:
				text_plate.setText(data.getStringExtra("value"));
				plateType = data.getIntExtra("plateType",0)+"";
				break;

			default:
				break;
			}
		}
	}


	private void updateUI() {
		if(vioCity.getEngine()==1) {
			layout_engine.setVisibility(View.VISIBLE);
			String enginenum="";
			if(0==vioCity.getEngineno()) {
				if(!StringUtils.isNullOrEmpty(SettingLoader.getEngineNum(this))) {
					enginenum = SettingLoader.getEngineNum(this);
				}
			}else {
				edit_engine.setHint(String.format(getResources().getString(R.string.text_car_engine_vio_hint), vioCity.getEngineno()));
				if(!StringUtils.isNullOrEmpty(SettingLoader.getEngineNum(this))) {
					enginenum = SettingLoader.getEngineNum(this).substring(SettingLoader.getEngineNum(this).length()-vioCity.getEngineno()>=0?SettingLoader.getEngineNum(this).length()-vioCity.getEngineno():0);
				}
			}
			edit_engine.setText(enginenum);
		}else {
			layout_engine.setVisibility(View.GONE);
		}

		if(vioCity.getVin()==1) {
			layout_vin.setVisibility(View.VISIBLE);
			String vinnum = "";
			if(0==vioCity.getVinno()) {
				if(!StringUtils.isNullOrEmpty(SettingLoader.getVinNum(this))) {
					vinnum = SettingLoader.getVinNum(this);
				}
			}else {
				edit_vin.setHint(String.format(getResources().getString(R.string.text_car_vin_vio_hint), vioCity.getVinno()));
				if(!StringUtils.isNullOrEmpty(SettingLoader.getVinNum(this))) {
					vinnum = SettingLoader.getVinNum(this).substring(SettingLoader.getVinNum(this).length()-vioCity.getVinno()>=0?SettingLoader.getVinNum(this).length()-vioCity.getVinno():0);
				}
			}
			edit_vin.setText(vinnum);
		}else {
			layout_vin.setVisibility(View.GONE);
		}


		if(vioCity.getRegist()==1) {
			layout_resiter.setVisibility(View.VISIBLE);
			if(0==vioCity.getRegist()) {
				edit_register.setHint(getResources().getString(R.string.text_car_regist_hint));
			}else {
				edit_register.setHint(String.format(getResources().getString(R.string.text_car_regist_vio_hint), vioCity.getRegist()));
			}
		}else {
			layout_resiter.setVisibility(View.GONE);
		}
	}




	private void showEngineTips() {
		DialogImage dialog = new DialogImage(this,btn_engine_refer,R.drawable.ic_engine_tips);
		dialog.show();
	}
	private void showCarFrameTips() {
		DialogImage dialog = new DialogImage(this,btn_vin_refer,R.drawable.ic_car_frame_tips);
		dialog.show();
	}


	private void getViolation() {
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}

		if(StringUtils.isNullOrEmpty(areaCode)) {
			Utils.showToast(this, getResources().getString(R.string.tips_violation_city));
			return;
		}
		if(StringUtils.isNullOrEmpty(title_plate.getText().toString())&&StringUtils.isNullOrEmpty(edit_plate.getText().toString())) {
			Utils.showToast(this, getResources().getString(R.string.tips_violation_plate));
			return;
		}

		if(!StringUtils.isNullOrEmpty(SettingLoader.getCarPlate(this))) {
			plate = SettingLoader.getCarPlate(this);
		}else {
			plate = car_plate_area.getText().toString()+edit_plate.getText().toString();
		}

		
		if(vioCity!=null) {
			if(vioCity.getEngine()==1) {
				if(StringUtils.isNullOrEmpty(edit_engine.getText().toString())) {
					Utils.showToast(this, getResources().getString(R.string.tips_violation_no_engine));
					return;
				}
			}

			if(vioCity.getVin()==1) {
				if(StringUtils.isNullOrEmpty(edit_vin.getText().toString())) {
					Utils.showToast(this, getResources().getString(R.string.tips_violation_vin));
					return;
				}
			}


			if(vioCity.getRegist()==1) {
				if(StringUtils.isNullOrEmpty(edit_register.getText().toString())) {
					Utils.showToast(this, getResources().getString(R.string.tips_violation_register));
					return;
				}
			}
		}
		
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.CITY, areaCode);
		params.put(ParamsKey.Hphm, plate);
		params.put(ParamsKey.Hpzl, plateType);
		params.put(ParamsKey.Engineno, edit_engine.getText().toString());
		params.put(ParamsKey.Classno, edit_vin.getText().toString());
		params.put(ParamsKey.Registno, edit_register.getText().toString());
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Violation_Query_Url) , params,new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				LogUtils.e(TAG, t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								SettingLoader.setQueryDate(IllegalActivity.this,TimeUtils.getCurrentTime());
								SettingLoader.setIllegalCityAndCode(IllegalActivity.this,text_drive_area.getText().toString(),areaCode);
								if(1==status) {
									if(json.has("result")){
										JSONArray array = json.getJSONArray("result");
										if(array!=null&&array.length()>0) {
											ArrayList<ViolationResult> slist = new ArrayList<ViolationResult>();
											ViolationResult violation = null;
											for(int i=0;i<array.length();i++) {
												violation = new ViolationResult();
												violation.parser((JSONObject)array.opt(i));
												slist.add(violation);
											}
											RuleBreakOpenHelper.getInstance(IllegalActivity.this).batchInfos(SettingLoader.getVehicleId(IllegalActivity.this), slist,SettingLoader.getIllegalCity(IllegalActivity.this));
											Intent intent = new Intent(IllegalActivity.this,IllegalResultActivity.class);
											intent.putExtra("list", slist);
											intent.putExtra(ParamsKey.CITY, areaCode);
											intent.putExtra(ParamsKey.Hphm, plate);
											intent.putExtra(ParamsKey.Hpzl, plateType);
											intent.putExtra(ParamsKey.Engineno, edit_engine.getText().toString());
											intent.putExtra(ParamsKey.Classno, edit_vin.getText().toString());
											intent.putExtra(ParamsKey.Registno, edit_register.getText().toString());
											intent.putExtra("area", text_drive_area.getText().toString());
											startActivity(intent);
										}else {
											Utils.showToast(IllegalActivity.this, getResources().getString(R.string.tips_no_break_rule));
										}
									}else {
										Utils.showToast(IllegalActivity.this, getResources().getString(R.string.tips_no_break_rule));
									}
								}else {
									msg = json.getString("message");
									Utils.showToast(IllegalActivity.this, msg);
								}
								XtrdApp.sendMsg(XtrdApp.ID_REFRESH_ILLEGAL);
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
				Utils.showToast(IllegalActivity.this, getResources().getString(R.string.data_load_fail));
				dismissLoading();
			}

		});
	}
}
