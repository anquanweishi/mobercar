package com.xtrd.obdcar.vc;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.tumi.R;
/**
 * 专家问答
 * @author Administrator
 *
 */
public class TroubleCodeDetailActivity extends BaseActivity {
	

	private TextView text_desc;
	private TextView text_code;
	private TextView text_name;

	public TroubleCodeDetailActivity() {
		layout_id = R.layout.activity_trouble_code_detail;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_vc_detail, 0,0);
		initView();
		
	}

	private void initView() {
		text_code = (TextView)findViewById(R.id.text_code);
		text_name = (TextView)findViewById(R.id.text_name);
		text_desc = (TextView)findViewById(R.id.text_desc);
		
		Intent intent = getIntent();
		text_code.setText(intent.getStringExtra("code"));
		text_name.setText(intent.getStringExtra("name"));
		text_desc.setText(intent.getStringExtra("desc"));
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
