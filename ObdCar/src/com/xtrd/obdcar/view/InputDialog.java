package com.xtrd.obdcar.view;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

public class InputDialog extends Dialog implements android.view.View.OnClickListener{

	private Context context;
	private TextView dialog_title;
	private EditText edit_input;
	private TextView text_desc;
	private TextView text_unit;
	private TextView btn_dialog_left;
	private TextView btn_dialog_right;

	private OnClickListener onPositiveClickListener;
	private OnClickListener onNegativeClickListener;
	
	private String p_text, n_text,title,unit,msg;
	private int type;
	private int length;
	private InputDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	private InputDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	public InputDialog(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// linearLayout.setBackgroundResource(R.drawable.bg_toast);
		LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.layout_dialog_input, null);
//		linearLayout.getBackground().setAlpha(127);
		dialog_title = (TextView)linearLayout.findViewById(R.id.dialog_title);
		edit_input = (EditText)linearLayout.findViewById(R.id.edit_input);
		text_desc = (TextView)linearLayout.findViewById(R.id.text_desc);
		text_unit = (TextView)linearLayout.findViewById(R.id.title_unit);
		btn_dialog_left = (TextView)linearLayout.findViewById(R.id.btn_dialog_left);
		btn_dialog_right = (TextView)linearLayout.findViewById(R.id.btn_dialog_right);
		btn_dialog_left.setOnClickListener(this);
		btn_dialog_right.setOnClickListener(this);
		
		linearLayout.setGravity(Gravity.CENTER);
		setContentView(linearLayout, new ViewGroup.LayoutParams(Utils.getScreenWidth(context)-Utils.dipToPixels(context,100), android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		Window window = this.getWindow();
		window.setBackgroundDrawableResource(android.R.color.transparent);
		LayoutParams params = window.getAttributes();// 获取LayoutParams
		window.setGravity(Gravity.CENTER );
//		params.x = PayForApp.getScreenWidth() / 2
//				- Utils.dipToPixels(context, 50);
////		params.y = PayForApp.getScreenHeight() * 3 / 4;
//		params.y = PayForApp.getScreenHeight() / 2;
		window.setAttributes(params);// 设置生效
	}

	public InputDialog setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public InputDialog setUnit(String unit) {
		this.unit = unit;
		return this;
	}
	public InputDialog setMessage(String msg) {
		this.msg = msg;
		return this;
	}
	
	public InputDialog setInputType(int type) {
		this.type = type;
		return this;
	}
	
	public InputDialog setMaxLength(int length) {
		this.length = length;
		return this;
	}


	public InputDialog setPositiveButton(String btn_text,
			OnClickListener onClickListener) {
		this.onPositiveClickListener = onClickListener;
		p_text = btn_text;
		return this;
	}

	public InputDialog setNegativeButton(String btn_text,
			OnClickListener onClickListener) {
		this.onNegativeClickListener = onClickListener;
		n_text = btn_text;
		return this;
	}

	public interface OnClickListener {
		void onClick(DialogInterface dialog, int which);
		void onClick(DialogInterface dialog, int which,String input);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dialog_left:
			if (onPositiveClickListener != null) {
				onPositiveClickListener.onClick(this, R.id.btn_dialog_left,edit_input.getText().toString());
				dismiss();
			}
			break;
		case R.id.btn_dialog_right:
			if (onPositiveClickListener != null) {
				onNegativeClickListener.onClick(this, R.id.btn_dialog_right);
				dismiss();
			}
			break;

		default:
			break;
		}

	}

	@Override
	public void show() {
		if(context instanceof Activity&&!((Activity)context).isFinishing()) {
			super.show();
		}
		if (btn_dialog_left != null) {
			btn_dialog_left.setText(p_text);
		}
		if (btn_dialog_right!= null) {
			btn_dialog_right.setText(n_text);
		}
		
		if(dialog_title!=null) {
			dialog_title.setText(title);
		}
		if(text_desc!=null) {
			text_desc.setText(msg);
		}
		if(text_unit!=null) {
			text_unit.setText(unit);
		}
		if(edit_input!=null) {
			edit_input.setInputType(type);
		}
	}


	
}
