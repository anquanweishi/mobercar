package com.xtrd.obdcar.vc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.tumi.R;

/**
 * 专家问答
 * 
 * @author Administrator
 * 
 */
public class VCCheckActivity extends BaseActivity {

	private TextView text_code;
	private TextView text_desc;
	private Button btn_onekey_help;
	private Button btn_order_shop;

	public VCCheckActivity() {
		layout_id = R.layout.activity_vc_check;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, 0, R.string.title_trouble_code, 0, 0);
		initView();
		regClick();

	}

	

	private void initView() {
		text_code = (TextView) findViewById(R.id.text_code);
		text_desc = (TextView) findViewById(R.id.text_desc);
		btn_onekey_help = (Button)findViewById(R.id.btn_onekey_help);
		btn_order_shop = (Button)findViewById(R.id.btn_order);
	}

	private void regClick() {
		btn_onekey_help.setOnClickListener(this);
		btn_order_shop.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		default:
			break;
		}
	}
}
