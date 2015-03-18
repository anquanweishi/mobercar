package com.xtrd.obdcar.setting;


import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.Config;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class PwdActivity extends BaseActivity {
	
	protected static final String TAG = "PwdActivity";
	private EditText edit_pwd;
	private EditText edit_new_pwd;
	private EditText edit_confirm_pwd;

	public PwdActivity() {
		layout_id = R.layout.activity_pwd;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_pwd, R.string.btn_reset, 0);
		initView();
	}

	private void initView() {
		edit_pwd = (EditText)findViewById(R.id.edit_pwd);
		edit_new_pwd = (EditText)findViewById(R.id.edit_new_pwd);
		edit_confirm_pwd = (EditText)findViewById(R.id.edit_confirm_pwd);
		edit_confirm_pwd.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& KeyEvent.ACTION_DOWN == event.getAction()) {
					edit_pwd.clearFocus();
					resetPwd();
					return true;
				}

				return false;
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			resetPwd();
			break;

		default:
			break;
		}
	}

	private void resetPwd() {
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showToast(this, getResources().getString(R.string.network_unavailable_tips));
			return;
		}
		
		String pwd = edit_pwd.getText().toString();
		String new_pwd = edit_new_pwd.getText().toString();
		String confirm_pwd = edit_confirm_pwd.getText().toString();
		if(StringUtils.isNullOrEmpty(pwd)) {
			Utils.showToast(this, "请输入原密码");
			return;
		}
		if(StringUtils.isNullOrEmpty(new_pwd)) {
			Utils.showToast(this, "请输入新密码");
			return;
		}
		if(new_pwd.length()<Config.USER_MIN_LIMIT) {
			Utils.showToast(this, getResources().getString(R.string.text_pwd_min_limit));
			return;
		}
		
		if(!new_pwd.equals(confirm_pwd)) {
			Utils.showToast(this, "新密码输入不一致");
			return;
		}
		
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.OldPassword, pwd);
		params.put(ParamsKey.NewPassword, new_pwd);
		
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Pwd_Update_Url),params,new AjaxCallBack<String>() {

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
									Utils.showToast(PwdActivity.this, getResources().getString(R.string.info_update_success));
									finish();
								}else {
									msg = json.getString("message");
									Utils.showToast(PwdActivity.this, msg);
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
				Utils.showToast(PwdActivity.this, getResources().getString(R.string.tips_pwd_set_fail));
				dismissLoading();
			}

		});
	}

}
