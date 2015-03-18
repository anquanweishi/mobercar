package com.xtrd.obdcar.setting;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class FeedbackActivity extends BaseActivity {

	protected static final String TAG = "FeedbackActivity";
	private EditText edit_input;
	private EditText edit_email;

	public FeedbackActivity() {
		layout_id = R.layout.activity_feedback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_feedback, R.string.btn_commit, 0);
		initView();
	}

	private void initView() {
		edit_email = (EditText)findViewById(R.id.edit_email);
		edit_input = (EditText)findViewById(R.id.edit_input);
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
				Utils.showNetTips(this);
				return;
			}
			
			feedback();
			break;
		default:
			break;
		}
	}

	private void feedback() {
		String email = edit_email.getText().toString();
		if(!StringUtils.isNullOrEmpty(email)&&!Utils.isEmail(email)) {
			Utils.showToast(this, R.string.text_email_invalid);
			return;
		}
		String content = edit_input.getText().toString();
		if(StringUtils.isNullOrEmpty(content)) {
			Utils.showToast(this, getResources().getString(R.string.feedback_null));
			return;
		}
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		final AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Feedback_Contact, email);
		params.put(ParamsKey.Feedback_Content, content);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Feedback_Url), params,new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, t.toString());
				dismissLoading();
				Utils.showToast(FeedbackActivity.this, getResources().getString(R.string.feedback_suc));
				finish();
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				dismissLoading();
				Utils.showToast(FeedbackActivity.this, getResources().getString(R.string.feedback_suc));
				finish();
				super.onFailure(t, errorNo, strMsg);
			}

		});
	}
}
