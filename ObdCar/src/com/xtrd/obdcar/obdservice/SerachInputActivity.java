package com.xtrd.obdcar.obdservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class SerachInputActivity extends BaseActivity {
	
	private EditText edit_input;
	private Button btn_select;

	public SerachInputActivity() {
		layout_id = R.layout.activity_search_input;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_prefer, 0, 0);
		initView();
		
	}

	private void initView() {
		edit_input = (EditText)findViewById(R.id.text_date);
		btn_select = (Button)findViewById(R.id.btn_select);
		btn_select.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_select:
			String input = edit_input.getText().toString();
			if(StringUtils.isNullOrEmpty(input)) {
				Utils.showToast(this, getResources().getString(R.string.tips_no_input));
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("input", input);
			setResult(RESULT_OK, intent);
			finish();
			break;

		default:
			break;
		}
	}

}
