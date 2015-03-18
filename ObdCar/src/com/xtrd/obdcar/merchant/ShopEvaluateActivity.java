package com.xtrd.obdcar.merchant;


import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class ShopEvaluateActivity extends BaseActivity {

	protected static final String TAG = "ShopEvaluateActivity";
	private EditText edit_input;
	private int type;

	public ShopEvaluateActivity() {
		layout_id = R.layout.activity_evaluate;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = getIntent().getIntExtra("up",0);
		initTitle(0, R.drawable.btn_back_bg, 1==type?R.string.title_up:R.string.title_down, R.string.btn_publish, 0);
		initView();
	}

	private void initView() {
		edit_input = (EditText)findViewById(R.id.edit_input);
		edit_input.setHint(1==type?getResources().getString(R.string.merchant_comment_up_hint):getResources().getString(R.string.merchant_comment_down_hint));
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
			evaluate();
			break;
		default:
			break;
		}
	}

	private void evaluate() {
		String content = edit_input.getText().toString();
		if(StringUtils.isNullOrEmpty(content)) {
			Utils.showToast(this, getResources().getString(R.string.feedback_null));
			return;
		}
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.MERCHANTID, getIntent().getIntExtra(ParamsKey.MERCHANTID,-1)+"");
		params.put(ParamsKey.TYPE, type+"");
		params.put(ParamsKey.EVALUATE_CONTENT, content);
		NetRequest.requestUrl(this,
				ApiConfig.getRequestUrl(ApiConfig.Evaluate_Url), params,
				new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				Utils.showToast(ShopEvaluateActivity.this, getResources().getString(R.string.feedback_suc));
				finish();
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(ShopEvaluateActivity.this, getResources().getString(R.string.feedback_suc));
				finish();
			}
		});
	
	}
}
