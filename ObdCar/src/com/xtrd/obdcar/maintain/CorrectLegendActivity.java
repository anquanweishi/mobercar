package com.xtrd.obdcar.maintain;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class CorrectLegendActivity extends BaseActivity {
	private EditText edit_correct;
	private TextView text_unit;
	private TextView text_name;

	public CorrectLegendActivity() {
		layout_id = R.layout.activity_correct;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String title = getIntent().getStringExtra("title");
		initTitle(0, R.drawable.btn_back_bg, title, R.string.btn_reset, 0);
		String unit = getIntent().getStringExtra("unit");
		initView();
		if(getIntent().getBooleanExtra("type", false)) {
			edit_correct.setInputType(InputType.TYPE_CLASS_TEXT);
		}else {
			edit_correct.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		text_name.setText(title);
		if(!StringUtils.isNullOrEmpty(unit)) {
			text_unit.setVisibility(View.VISIBLE);
			text_unit.setText(getIntent().getStringExtra("unit"));
		}else {
			text_unit.setVisibility(View.GONE);
		}
		
	}

	private void initView() {
		edit_correct = (EditText)findViewById(R.id.edit_correct);
		text_unit = (TextView)findViewById(R.id.text_unit);
		text_name = (TextView)findViewById(R.id.text_name);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			String input = edit_correct.getText().toString();
			if(StringUtils.isNullOrEmpty(input)) {
				Utils.showToast(this,getResources().getString(R.string.tips_no_input));
				return;
			}
			
			if(InputType.TYPE_CLASS_NUMBER==edit_correct.getInputType()) {
				long range = Long.parseLong(input);
				if(range<=0) {
					Utils.showToast(this, getResources().getString(R.string.tips_less_than_zero));
					return;
				}
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
