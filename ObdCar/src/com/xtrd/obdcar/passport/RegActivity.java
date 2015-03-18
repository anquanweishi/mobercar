package com.xtrd.obdcar.passport;

import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.BaseTabActivity;
import com.xtrd.obdcar.WebLoadActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class RegActivity extends BaseActivity {
	private int countTime = 60000,interval = 1000;
	private static final String TAG = "RegActivity";
	private EditText edit_phone,edit_pwd,edit_auth;
	private Button btn_auth,btn_commit;
	private CheckBox checkBox;
	private CountDownTimer timer;
	private boolean isRunning;

	private boolean authphone = false;


	public RegActivity() {
		layout_id = R.layout.activity_reg;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0,R.drawable.btn_back_bg,R.string.title_reg,0,0);

		initView();
		regListener();
	}


	private void regListener() {
		btn_auth.setOnClickListener(this);
		btn_commit.setOnClickListener(this);
		edit_phone.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus&&(edit_pwd.hasFocus()||edit_auth.hasFocus())) {
					authPhone();
				}
			}
		});
		
		edit_auth.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!StringUtils.isNullOrEmpty(edit_phone.getText().toString())&&!StringUtils.isNullOrEmpty(edit_pwd.getText().toString())) {
					btn_commit.setEnabled(true);
				}
			}
		});
	}



	private void initView() {
		edit_phone = (EditText)findViewById(R.id.edit_phone);
		edit_pwd = (EditText)findViewById(R.id.edit_pwd);
		edit_auth = (EditText)findViewById(R.id.edit_auth);
		btn_auth = (Button)findViewById(R.id.btn_auth);
		btn_commit = (Button)findViewById(R.id.btn_commit);
		checkBox = (CheckBox)findViewById(R.id.checkBox);
		TextView text_auth_right = (TextView)findViewById(R.id.text_auth_right);
		SpannableString s = new SpannableString(getResources().getString(R.string.auth_right));
		s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.top_bar_color)), s.length()-4, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new ClickableSpan() {

			@Override
			public void onClick(View widget) {
				Intent intent = new Intent(RegActivity.this,WebLoadActivity.class);
				intent.putExtra("title", "服务条款");
				intent.putExtra("url", ApiConfig.APP_Service_Right);
				startActivity(intent);

			}
		}, s.length()-4, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		text_auth_right.setMovementMethod(LinkMovementMethod.getInstance());
		text_auth_right.setText(s);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_commit:
			reg();
			break;
		case R.id.btn_auth:
			String phone = edit_phone.getText().toString();
			if(StringUtils.isNullOrEmpty(phone)) {
				Utils.showToast(this, getResources().getString(R.string.tips_phone_null));
				return;
			}
			if(!Utils.isMobile(phone)) {
				Utils.showToast(this, getResources().getString(R.string.text_phone_tips));
				return;
			}
			if(!StringUtils.isNullOrEmpty(edit_phone.getText().toString())&&!StringUtils.isNullOrEmpty(edit_pwd.getText().toString())) {
				btn_commit.setEnabled(true);
			}
			
			if(!isRunning) {
				authCodeGet(phone);
				isRunning = true;
			}
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(timer!=null) {
			timer.cancel();
			timer = null;
		}
	}


	private void countDown() {
		if(timer!=null) {
			timer.cancel();
			timer = null;
		}
		timer  = new CountDownTimer(countTime,interval) {

			@Override
			public void onTick(long millisUntilFinished) {
				btn_auth.setText(String.format(getString(R.string.btn_auth_text_for_countdown),millisUntilFinished / 1000));
			}

			@Override
			public void onFinish() {
				btn_auth.setText(getString(R.string.auth_code_get));
				isRunning = false;
				btn_auth.setEnabled(true);
			}
		}.start();
		btn_auth.setEnabled(false);
	}

	/**
	 * 验证码获取
	 * @param phone 
	 */
	private void authCodeGet(String phone) {

		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Reg_Telephone, phone);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.AUTH_CODE_URL), params,new AjaxCallBack<String>() {

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
						JSONObject json = new JSONObject(t);
						if(json!=null) {
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									Utils.showToast(RegActivity.this, getResources().getString(R.string.tips_auth_code_send));
									countDown();
								}else {
									isRunning = false;
									String msg = json.getString("message");
									Utils.showToast(RegActivity.this, msg);
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
				isRunning = false;
				dismissLoading();
				Utils.showToast(RegActivity.this, getResources().getString(R.string.tips_auth_code_send_fail));
				super.onFailure(t, errorNo, strMsg);
			}

		});
	}


	protected void authPhone() {
		String phone = edit_phone.getText().toString();
		if(StringUtils.isNullOrEmpty(phone)) {
			Utils.showToast(this, getResources().getString(R.string.tips_phone_null));
			return;
		}

		if(!Utils.isMobile(phone)) {
			Utils.showToast(this, getResources().getString(R.string.text_phone_tips));
			return;
		}

		if(!StringUtils.isNullOrEmpty(phone)) {
			FinalHttp fh = new FinalHttp();
			AjaxParams params = new AjaxParams();
			params.put(ParamsKey.Phone, phone);
			fh.post(ApiConfig.getRequestUrl(ApiConfig.Vilidate_Phone_Url), params,new AjaxCallBack<String>() {


				@Override
				public void onSuccess(String t) {
					LogUtils.e(TAG, t.toString());
					try {
						if(!StringUtils.isNullOrEmpty(t)) {
							JSONObject json = new JSONObject(t);
							if(json!=null) {
								if(json.has("status")) {
									int status = json.getInt("status");
									if(1==status) {
										if(json.has("result")) {
											authphone = json.getBoolean("result");
											if(authphone) {
												Utils.showToast(RegActivity.this, getResources().getString(R.string.tips_auth_phone_vilid));
											}
										}
									}
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					super.onSuccess(t);
				}
			});
		}
	}



	/**
	 * 注册
	 * @param user
	 * @param pwd
	 */
	private void reg() {
		final String phone = edit_phone.getText().toString();
		if(StringUtils.isNullOrEmpty(phone)) {
			Utils.showToast(this, getResources().getString(R.string.tips_phone_null));
			return;
		}

		final String pwd = edit_pwd.getText().toString();
		if(StringUtils.isNullOrEmpty(pwd)) {
			Utils.showToast(this, getResources().getString(R.string.text_pwd_null));
			edit_pwd.requestFocus();
			return;
		}

		String code = edit_auth.getText().toString();
		if(StringUtils.isNullOrEmpty(code)) {
			Utils.showToast(this, getResources().getString(R.string.tips_auth_code_null));
			edit_auth.requestFocus();
			return;
		}

		if(!checkBox.isChecked()) {
			Utils.showToast(this, getResources().getString(R.string.text_service_right));
			return;
		}


		FinalHttp fh = new FinalHttp();
		final AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Reg_Telephone, phone);
		params.put(ParamsKey.Reg_Password, pwd);
		params.put(ParamsKey.Reg_Code,code);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Reg_Url), params,new AjaxCallBack<String>() {

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
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								login(phone,pwd);
							}else {
								String msg = json.getString("message");
								Utils.showToast(RegActivity.this, msg);
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
				dismissLoading();
				Utils.showToast(RegActivity.this, getResources().getString(R.string.tips_reg_fail));
				super.onFailure(t, errorNo, strMsg);
			}

		});

	}
	/**
	 * 登录
	 * @param user
	 * @param pwd
	 */
	private void login(final String username, String pwd) {
		SettingLoader.setLoginName(RegActivity.this, username,null);
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}
		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Login_Username, "A"+username);
		params.put(ParamsKey.Login_Password, pwd);
		//post cookieStore 设置
		final CookieStore cookieStore = new BasicCookieStore();
		fh.configCookieStore(cookieStore);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Login_Url), params,new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				LogUtils.e(TAG, t.toString());

				PreferencesCookieStore store = new PreferencesCookieStore(RegActivity.this);
				List<Cookie> cookies = cookieStore.getCookies();
				if(!cookies.isEmpty()){
					for(int i = 0; i < cookies.size();i++){
						Cookie cookie = cookies.get(i);
						Log.e("cookie",cookie.getName()+" "+cookie.getValue());
						store.addCookie(cookie);
					}
				}
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								if(json.has("result")) {
									String result = json.getString("result");
									SettingLoader.setLoginName(RegActivity.this, username,result);
								}
								SettingLoader.setHasLogin(RegActivity.this, true);
								Utils.showToast(RegActivity.this, getResources().getString(R.string.login_suc));
								Intent intent = new Intent(RegActivity.this,BaseTabActivity.class);
								startActivity(intent);
								finish();
							}else {
								String message = json.getString("message");
								Utils.showToast(RegActivity.this, message);
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
				dismissLoading();
				Utils.showToast(RegActivity.this,getResources().getString(R.string.tips_login_fail));
				super.onFailure(t, errorNo, strMsg);
			}

		});
	}
}
