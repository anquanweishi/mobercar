package com.xtrd.obdcar.pwdforget;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class ForgetOneActivity extends BaseActivity {
	protected static final String TAG = "ForgetOneActivity";
	protected static final int ID_FIND = 1;
	private ImageView active_tow_img,active_three_img;
	private View active_tow_view,active_three_view;
	private TextView active_one_text,active_tow_text,active_three_text;
	private EditText edit_info;
	

	public ForgetOneActivity() {
		layout_id = R.layout.activity_forget_one;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0,R.drawable.btn_back_bg,R.string.title_pwd_forget,R.string.btn_next,0);
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
	
	}

	private void initView() {
		edit_info = (EditText)findViewById(R.id.edit_info);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			authCodeRequest();
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
	 * 获取激活码
	 */
	private void authCodeRequest() {
		final String input = edit_info.getText().toString();
		if(StringUtils.isNullOrEmpty(input)) {
			Utils.showToast(this, getResources().getString(R.string.text_account_tips));
			return;
		}
		
		if(!Utils.isMobile(input)&&!Utils.isEmail(input)) {
			Utils.showToast(this, getResources().getString(R.string.text_account_invilidate));
			return;
		}
		
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Value, input);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Send_Code_Url), params,new AjaxCallBack<String>() {
			
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
								Intent intent = new Intent(ForgetOneActivity.this,ForgetTwoActivity.class);
								intent.putExtra("account", input);
								startActivityForResult(intent, ID_FIND);
							}else {
								String msg = json.getString("message");
								Utils.showToast(ForgetOneActivity.this, msg);
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
}
