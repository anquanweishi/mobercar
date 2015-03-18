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
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.UserEntity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class InfoActivity extends BaseActivity {

	protected static final String TAG = "InfoActivity";
	private static final int ID_ADDRESS = 1;
	private TextView text_account;
	private EditText edit_nick;
	private TextView edit_address;
	private EditText edit_email;
	private EditText edit_phone;
	private UserEntity user = new UserEntity();
	public InfoActivity(){
		layout_id = R.layout.activity_info;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.set_item_info, R.string.btn_reset, 0);

		initView();
		getUserInfo();
	}


	private void initView() {
		text_account = (TextView)findViewById(R.id.text_account);
		edit_address = (TextView)findViewById(R.id.text_address);
		edit_nick = (EditText)findViewById(R.id.text_nick);
		edit_address.setOnClickListener(this);
		edit_phone = (EditText)findViewById(R.id.text_phone);
		edit_email = (EditText)findViewById(R.id.text_email);
		edit_email.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& KeyEvent.ACTION_DOWN == event.getAction()) {
					updateUserInfo();
					return true;
				}

				return false;
			}
		});
	}
	
	protected void updateUI() {
		Crashlytics.setString("username", user.getUserName());
		
		text_account.setText(user.getUserName());
		edit_nick.setText(user.getNickName());
		edit_address.setText(user.getAreaName());
		edit_email.setText(user.getEmail());
		edit_phone.setText(user.getTelephone());
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			if(!NetUtils.IsNetworkAvailable()) {
				Utils.showToast(this, getResources().getString(R.string.network_unavailable_tips));
				return;
			}
			updateUserInfo();
			break;
		case R.id.text_address:
			Intent intent = new Intent(this,AddressActivity.class);
			startActivityForResult(intent, ID_ADDRESS);
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
			case ID_ADDRESS:
				String areaCode = data.getStringExtra("areaCode");
				String value = data.getStringExtra("value");
				if(!StringUtils.isNullOrEmpty(areaCode)) {
					user.setAreaCode(areaCode);
				}
				edit_address.setText(value);
				break;

			default:
				break;
			}
		}
	}
	
	private void getUserInfo() {
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}
		
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.User_Get_Url),new AjaxCallBack<String>() {

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
								if(1==status) {
									if(json.has("result")) {
										json = json.getJSONObject("result");
										user.parser(json);
									}
								}else {
									msg = json.getString("message");
									Utils.showToast(InfoActivity.this, msg);
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
				Utils.showToast(InfoActivity.this,getResources().getString(R.string.data_load_fail));
				dismissLoading();
			}

		});
	}


	private void updateUserInfo() {
		String nick = edit_nick.getText().toString();
		String email = edit_email.getText().toString();
		String phone = edit_phone.getText().toString();
		
		if(StringUtils.isNullOrEmpty(email)) {
			Utils.showToast(this, R.string.hint_owner_email);
			return;
		}
		
		if(!Utils.isEmail(email)) {
			Utils.showToast(this, R.string.text_email_invalid);
			return;
		}
		
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.NickName, nick);
		params.put(ParamsKey.AreaCode, user.getAreaCode());
		params.put(ParamsKey.Email, email);
		params.put(ParamsKey.Telephone, phone);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.User_Update_Url),params,new AjaxCallBack<String>() {
			
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
								if(1==status) {
									Utils.showToast(InfoActivity.this, getResources().getString(R.string.info_update_success));
									finish();
								}else {
									msg = json.getString("message");
									Utils.showToast(InfoActivity.this, msg);
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
				Utils.showToast(InfoActivity.this, getResources().getString(R.string.tips_info_set_fail));
				dismissLoading();
			}
			
		});
	}
	

}
