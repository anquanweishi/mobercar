package com.xtrd.obdcar.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

public class HelpActivity extends BaseActivity {

	private TextView textView;

	public HelpActivity() {
		layout_id = R.layout.activity_help;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,
				R.string.title_about, 0, 0);
		
		initView();
	}

	private void initView() {
		textView = (TextView)findViewById(R.id.textView1);
		textView.setText(String.format(getResources().getString(R.string.text_version), Utils.getLocalAppVersion(this)));
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
