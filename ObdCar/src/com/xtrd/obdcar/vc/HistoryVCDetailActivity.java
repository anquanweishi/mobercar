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
public class HistoryVCDetailActivity extends BaseActivity {
	private TextView text_title,text_time,text_desc,text_continue_time;

	public HistoryVCDetailActivity() {
		layout_id = R.layout.activity_history_vc_detail;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_vc_detail, 0,0);
		initView();
		
	}

	private void initView() {
		text_title = (TextView)findViewById(R.id.text_title);
		text_time = (TextView)findViewById(R.id.text_time);
		text_desc = (TextView)findViewById(R.id.text_desc);
		text_continue_time = (TextView)findViewById(R.id.text_continue_time);
		
		Intent intent = getIntent();
		text_title.setText(intent.getStringExtra("name"));
		text_time.setText(intent.getStringExtra("time"));
		text_desc.setText(intent.getStringExtra("desc"));
		text_continue_time.setText(intent.getStringExtra("stand"));
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
