package com.xtrd.obdcar.pwdforget;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.Config;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class ForgetThreeActivity extends BaseActivity {
	protected static final String TAG = "ForgetThreeActivity";
	private ImageView active_tow_img,active_three_img;
	private View active_tow_view,active_three_view;
	private TextView active_one_text,active_tow_text,active_three_text;

	private EditText edit_new_pwd;
	private EditText edit_confirm_pwd;

	public ForgetThreeActivity() {
		layout_id = R.layout.activity_forget_three;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0,R.drawable.btn_back_bg,R.string.title_pwd_forget,R.string.btn_finish,0);
		initStatusView();
		initView();
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
		active_tow_view.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
		active_three_view.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
		active_tow_text.setTextColor(getResources().getColor(R.color.top_bar_color));
	}

	private void initView() {
		edit_new_pwd = (EditText)findViewById(R.id.edit_new_pwd);
		edit_confirm_pwd = (EditText)findViewById(R.id.edit_confirm_pwd);
		edit_confirm_pwd.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& KeyEvent.ACTION_DOWN == event.getAction()) {
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
		
		String new_pwd = edit_new_pwd.getText().toString();
		String confirm_pwd = edit_confirm_pwd.getText().toString();
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
		params.put(ParamsKey.Value, getIntent().getStringExtra("account"));
		params.put(ParamsKey.Pwd, new_pwd);
		params.put(ParamsKey.Random, getIntent().getStringExtra("random"));
		
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
									Utils.showToast(ForgetThreeActivity.this, getResources().getString(R.string.info_update_success));
									setResult(RESULT_OK);
									finish();
								}else {
									msg = json.getString("message");
									Utils.showToast(ForgetThreeActivity.this, msg);
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
				Utils.showToast(ForgetThreeActivity.this, getResources().getString(R.string.tips_pwd_set_fail));
				dismissLoading();
			}

		});
	}
}
