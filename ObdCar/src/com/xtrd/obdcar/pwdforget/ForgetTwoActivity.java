package com.xtrd.obdcar.pwdforget;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class ForgetTwoActivity extends BaseActivity {
	protected static final String TAG = "ForgetTwoActivity";
	private static final int ID_FIND = 1;
	private ImageView active_tow_img,active_three_img;
	private View active_tow_view,active_three_view;
	private TextView active_one_text,active_tow_text,active_three_text;
	private EditText edit_code;
	private TextView text_account;
	private Button btn_auth;
	private CountDownTimer timer;
	private boolean isRunning = true;//定时器是否正在进行
	private int countTime = 60000,interval = 1000;
	
	public ForgetTwoActivity() {
		layout_id = R.layout.activity_forget_two;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0,R.drawable.btn_back_bg,R.string.title_pwd_forget,R.string.btn_next,0);
		initStatusView();
		initView();
		countDown(); 
	}


	private void initStatusView() {
		active_tow_img = (ImageView)findViewById(R.id.active_tow_img);
		active_tow_view = (View)findViewById(R.id.active_tow_view);
		active_three_view = (View)findViewById(R.id.active_three_view);
		active_three_img = (ImageView)findViewById(R.id.active_three_img);
		active_one_text = (TextView)findViewById(R.id.active_one_text);
		active_tow_text = (TextView)findViewById(R.id.active_tow_text);
		active_three_text = (TextView)findViewById(R.id.active_three_text);
		active_one_text.setText(getResources().getString(R.string.text_forget_account));
		active_tow_text.setText(getResources().getString(R.string.text_forget_confirm));
		active_three_text.setText(getResources().getString(R.string.text_forget_finish));
		active_tow_img.setImageResource(R.drawable.ic_active_solid);
		active_tow_text.setTextColor(getResources().getColor(R.color.top_bar_color));
	}

	private void initView() {
		edit_code = (EditText)findViewById(R.id.edit_code);
		text_account = (TextView)findViewById(R.id.text_account);
		btn_auth = (Button)findViewById(R.id.btn_auth);
		btn_auth.setOnClickListener(this);
		String account = getIntent().getStringExtra("account");
		if(Utils.isEmail(account)) {
			text_account.setText(String.format(getResources().getString(R.string.authcode_text_email), account));
		}else {
			text_account.setText(String.format(getResources().getString(R.string.authcode_text_phone), account));
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(timer!=null) {
			timer.cancel();
			timer = null;
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
			checkAuthCode();
			break;
		case R.id.btn_auth:
			if(!isRunning) {
				authCodeGet();
				isRunning = true;
			}
			break;
		default:
			break;
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
				isRunning = false;
				btn_auth.setEnabled(true);
				btn_auth.setText(getString(R.string.auth_code_get));
			}
		}.start();
		btn_auth.setEnabled(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK==resultCode) {
			switch (requestCode) {
			case ID_FIND:
				setResult(RESULT_OK);
				finish();
				break;

			default:
				break;
			}
		}
	}
	

	/**
	 * 验证激活码
	 */
	private void checkAuthCode() {
		final String input = edit_code.getText().toString();
		if(StringUtils.isNullOrEmpty(input)) {
			Utils.showToast(this, getResources().getString(R.string.authcode_text_hint));
			return;
		}
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Value, getIntent().getStringExtra("account"));
		params.put(ParamsKey.Code, input);
		
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Vilidate_Code_Url), params,new AjaxCallBack<String>() {
			
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
								Intent intent = new Intent(ForgetTwoActivity.this,ForgetThreeActivity.class);
								intent.putExtra("account", getIntent().getStringExtra("account"));
								intent.putExtra("random", json.getString("result"));
								startActivityForResult(intent,ID_FIND);
							}else {
								String msg = json.getString("message");
								Utils.showToast(ForgetTwoActivity.this, msg);
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
				super.onFailure(t, errorNo, strMsg);
			}
			
		});
	}

	/**
	 * 重新获取激活码
	 */
	private void authCodeGet() {
		
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Value, getIntent().getStringExtra("account"));
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Send_Code_Url), params,new AjaxCallBack<String>() {
			
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
						JSONObject json = new JSONObject(t);
						if(json!=null) {
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									Utils.showToast(ForgetTwoActivity.this, getResources().getString(R.string.tips_auth_code_send));
									countDown();
								}else {
									isRunning = false;
									String msg = json.getString("message");
									Utils.showToast(ForgetTwoActivity.this, msg);
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
				Utils.showToast(ForgetTwoActivity.this, getResources().getString(R.string.tips_auth_code_send_fail));
				super.onFailure(t, errorNo, strMsg);
			}
			
		});
	}
}
