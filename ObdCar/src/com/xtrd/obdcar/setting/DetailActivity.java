package com.xtrd.obdcar.setting;


import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.CarOwner;
import com.xtrd.obdcar.passport.ChooseBranchActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class DetailActivity extends BaseActivity {
	
	protected static final String TAG = "DetailActivity";
	private static final int ID_BRANCH = 1;
	private static final int ID_CARD = 2;
	private LinearLayout layout_car_model,layout_card_type;
	private TextView text_car_model,text_card_type;
	private EditText text_card_number,text_owner_name,
						text_owner_phone,text_owner_email,text_owner_address;
	private CarOwner owner = null;
	public DetailActivity() {
		layout_id = R.layout.activity_detail;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,getIntent().getStringExtra("plateNumber"), R.string.btn_finish, 0);
		initView();
		text_car_model.setText(getIntent().getStringExtra("branch")+getIntent().getStringExtra("series"));
		regClick();
		getDefaultCar();
	}


	private void regClick() {
		layout_car_model.setOnClickListener(this);
		layout_card_type.setOnClickListener(this);
		text_owner_address.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& KeyEvent.ACTION_DOWN == event.getAction()) {
					updateOwnerInfo();
					return true;
				}
				return false;
			}
		});
	}

	private void initView() {
		layout_car_model = (LinearLayout)findViewById(R.id.layout_car_model);
		text_car_model = (TextView)findViewById(R.id.text_car_model);
		layout_card_type = (LinearLayout)findViewById(R.id.layout_card_type);
		text_card_type = (TextView)findViewById(R.id.text_card_type);
		text_card_number = (EditText)findViewById(R.id.edit_card_num);
		text_owner_name = (EditText)findViewById(R.id.text_owner_name);
		text_owner_phone = (EditText)findViewById(R.id.text_owner_phone);
		text_owner_email = (EditText)findViewById(R.id.text_owner_email);
		text_owner_address = (EditText)findViewById(R.id.text_owner_address);
		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			updateOwnerInfo();
			break;
		case R.id.layout_car_model:
			Intent intent = new Intent(this,ChooseBranchActivity.class);
			intent.putExtra("from", true);
			startActivityForResult(intent, ID_BRANCH);
			break;
		case R.id.layout_card_type:
			Intent cintent = new Intent(this,CardTypeActivity.class);
			startActivityForResult(cintent, ID_CARD);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK==resultCode) {
			switch (requestCode) {
			case ID_BRANCH:
				if(owner==null) {
					owner = new CarOwner();
				}
				int modelId = data.getIntExtra("modelId", 0);
				owner.setModelId(modelId);
				text_car_model.setText(data.getStringExtra("value"));
				break;
			case ID_CARD:
				if(owner==null) {
					owner = new CarOwner();
				}
				String code = data.getStringExtra("code");
				owner.setIdType(code);
				text_card_type.setText(data.getStringExtra("value"));
				break;

			default:
				break;
			}
		}
	}

	protected void updateUI() {
		if(owner==null) {
			return;
		}
		text_card_type.setText(getCardType(owner.getIdType()));
		text_card_number.setText(owner.getIdNumber());
		text_owner_name.setText(owner.getName());
		text_owner_phone.setText(owner.getTelephone());
		text_owner_email.setText(owner.getEmail());
		text_owner_address.setText(owner.getAddress());
		
	}
	
	private String getCardType(String idType) {
		if("A".equalsIgnoreCase(idType)) {
			return "身份证";
		}else if("B".equalsIgnoreCase(idType)) {
			return "军官证";
		}else if("C".equalsIgnoreCase(idType)) {
			return "士兵证";
		}else {
			return "其他";
		}
		
	}

	private void getDefaultCar() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, getIntent().getIntExtra("vehicleId", 0)+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Owner_Url),params, new AjaxCallBack<String>(){
			private int status = 0;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				LogUtils.e(TAG, t);
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								status  = json.getInt("status");
								if(1==status) {
									if(json.has("result")) {
										json = json.getJSONObject("result");
										if(owner==null) {
											owner = new CarOwner();
										}
										owner.parser(json);
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
				LogUtils.e(TAG, "error msg "+strMsg);
				dismissLoading();
				Utils.showToast(DetailActivity.this, getResources().getString(R.string.data_load_fail));
				super.onFailure(t, errorNo, strMsg);
			}

		});

	}

	private void updateOwnerInfo() {
		if(owner==null) {
			owner = new CarOwner();
		}
		
		owner.setIdType(!StringUtils.isNullOrEmpty(owner.getIdType())?owner.getIdType():"A");
		owner.setIdNumber(text_card_number.getText().toString());
		owner.setName(text_owner_name.getText().toString());
		owner.setTelephone(text_owner_phone.getText().toString());
		owner.setEmail(text_owner_email.getText().toString());
		owner.setAddress(text_owner_address.getText().toString());
		
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, getIntent().getIntExtra("vehicleId", 0)+"");
		params.put(ParamsKey.Reg_ModelId, owner.getModelId()+"");
		params.put(ParamsKey.Owner_Name, owner.getName());
		params.put(ParamsKey.Owner_IdType,owner.getIdType());
		params.put(ParamsKey.Owner_IdNumber, owner.getIdNumber());
		params.put(ParamsKey.Owner_Telephone, owner.getTelephone());
		params.put(ParamsKey.Owner_Email, owner.getEmail());
		params.put(ParamsKey.Owner_Address, owner.getAddress());
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Owner_Update_Url),params, new AjaxCallBack<String>(){
			private int status = 0;
			private String msg = null;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				LogUtils.e(TAG, t);
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								status  = json.getInt("status");
								if(1==status) {
									Utils.showToast(DetailActivity.this, R.string.info_update_success);
									Intent intent = new Intent();
									intent.putExtra("infoupdate", true);
									setResult(RESULT_OK, intent);
									finish();
								}else {
									msg = json.getString("message");
									Utils.showToast(DetailActivity.this, msg);
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
				LogUtils.e(TAG, "error msg "+strMsg);
				dismissLoading();
				Utils.showToast(DetailActivity.this, getResources().getString(R.string.data_load_fail));
				super.onFailure(t, errorNo, strMsg);
			}

		});

	}
}
