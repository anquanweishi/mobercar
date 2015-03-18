package com.xtrd.obdcar.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.tumi.R;

public class AuthManageActivity extends BaseActivity {
	
	private LinearLayout layout_auth_sina;
	private LinearLayout layout_auth_tencent;
	private LinearLayout layout_auth_renren;
	private LinearLayout layout_auth_douban;

	public AuthManageActivity() {
		layout_id = R.layout.activity_auth_manage;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.set_item_auth, 0, 0);
		initView();
		regClick();
	}

	

	private void initView() {
		layout_auth_sina = (LinearLayout)findViewById(R.id.layout_auth_sina);
		layout_auth_tencent = (LinearLayout)findViewById(R.id.layout_auth_tencent);
		layout_auth_renren = (LinearLayout)findViewById(R.id.layout_auth_renren);
		layout_auth_douban = (LinearLayout)findViewById(R.id.layout_auth_douban);
	}
	
	private void regClick() {
		layout_auth_sina.setOnClickListener(this);
		layout_auth_tencent.setOnClickListener(this);
		layout_auth_renren.setOnClickListener(this);
		layout_auth_douban.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.layout_auth_sina:
			
			break;
		case R.id.layout_auth_tencent:
			
			break;
		case R.id.layout_auth_renren:
			
			break;
		case R.id.layout_auth_douban:
			
			break;

		default:
			break;
		}
	}

}
