package com.xtrd.obdcar.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.tumi.R;

public class LoadDialog extends ProgressDialog {

	private TextView tips_text;

	public LoadDialog(Context context, int theme) {
		super(context, theme);
	}

	public LoadDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.progress_load, null);
		tips_text = (TextView) linearLayout.findViewById(R.id.textView1);
		linearLayout.setGravity(Gravity.CENTER);
		setContentView(linearLayout, new ViewGroup.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

	}

}
