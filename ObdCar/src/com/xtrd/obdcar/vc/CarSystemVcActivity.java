package com.xtrd.obdcar.vc;


import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.entity.CarItem;
import com.xtrd.obdcar.merchant.MerchantActivity;
import com.xtrd.obdcar.obdservice.OneKeyHelpActivity;
import com.xtrd.obdcar.tumi.R;

public class CarSystemVcActivity extends BaseActivity {

	protected static final String TAG = "CarSystemVcActivity";
	private TextView text_code;
	private TextView text_desc;
	private LinearLayout layout_introduce;
	private Button btn_onekey_help;
	private Button btn_order;
	
	private ArrayList<CarItem> list = null;

	public CarSystemVcActivity() {
		layout_id = R.layout.activity_vc_check;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String title = getIntent().getStringExtra("title");
		initTitle(0, R.drawable.btn_back_bg,title, 0, 0);
		list = getIntent().getParcelableArrayListExtra("data");
		
		initView();
		updateUI();

	}



	private void initView() {
		text_code = (TextView)findViewById(R.id.text_code);
		text_desc = (TextView)findViewById(R.id.text_desc);
		layout_introduce = (LinearLayout)findViewById(R.id.layout_introduce);
		btn_onekey_help = (Button)findViewById(R.id.btn_onekey_help);
		btn_order = (Button)findViewById(R.id.btn_order);
		btn_onekey_help.setOnClickListener(this);
		btn_order.setOnClickListener(this);
	}
	
	private void updateUI() {
		if(list!=null&&list.size()>0) {
			text_code.setText(list.get(0).getCode());
			text_desc.setText(list.get(0).getName());
			
			layout_introduce.removeAllViews();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			TextView textview = null;
			for(int i=0;i<list.size();i++) {
				textview = new TextView(this);
				textview.setText(list.get(i).getDescription());
				textview.setTextColor(getResources().getColor(R.color.top_bar_color));
				layout_introduce.addView(textview,params);
			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_onekey_help:
			intent = new Intent(this,OneKeyHelpActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_order:
			intent = new Intent(this,MerchantActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
