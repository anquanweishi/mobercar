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
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

public class ObdDialog extends Dialog implements android.view.View.OnClickListener{

	private Context context;
	private TextView text_title,text_msg;
	private Button btn_left, btn_right;
	private OnClickListener onPositiveClickListener;
	private OnClickListener onNegativeClickListener;
	private String p_text, n_text,title,msg;
	
	public ObdDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(false);
		ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(
				R.layout.obd_dialog, null);
		text_title = (TextView)viewGroup.findViewById(R.id.text_title);
		text_msg = (TextView)viewGroup.findViewById(R.id.text_msg);
		btn_left = (Button) viewGroup.findViewById(R.id.btn_left);
		btn_right = (Button) viewGroup.findViewById(R.id.btn_right);
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		LayoutParams layoutParams = new LayoutParams(
				Utils.getScreenWidth(context)-Utils.dipToPixels(context, 80), LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 0, 0, Utils.dipToPixels(context, 10));
		setContentView(viewGroup, layoutParams);

		

		Window window = getWindow();
//		window.setGravity(Gravity.LEFT | Gravity.TOP);
		window.setGravity(Gravity.CENTER);
		window.setBackgroundDrawableResource(android.R.color.transparent);

//		WindowManager.LayoutParams manager = window.getAttributes();
//		manager.x = 0;
//		manager.y = Utils.getScreenHeight(context)
//				+ Utils.dipToPixels(context, 10);
//		Animation animation = AnimationUtils.loadAnimation(context,
//				R.anim.push_down_in);
//		viewGroup.setAnimation(animation);
//		window.setAttributes(manager);

	}
	
	public ObdDialog setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public ObdDialog setMessage(String msg) {
		this.msg = msg;
		return this;
	}

	public ObdDialog setPositiveButton(String btn_text,
			OnClickListener onClickListener) {
		this.onPositiveClickListener = onClickListener;
		p_text = btn_text;
		return this;
	}

	public ObdDialog setNegativeButton(String btn_text,
			OnClickListener onClickListener) {
		this.onNegativeClickListener = onClickListener;
		n_text = btn_text;
		return this;
	}

	public interface OnClickListener {
		void onClick(DialogInterface dialog, int which);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			if (onPositiveClickListener != null) {
				onPositiveClickListener.onClick(this, R.id.btn_left);
				dismiss();
			}
			break;
		case R.id.btn_right:
			if (onPositiveClickListener != null) {
				onNegativeClickListener.onClick(this, R.id.btn_right);
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
		if (btn_left != null) {
			btn_left.setText(p_text);
		}
		if (btn_right!= null) {
			btn_right.setText(n_text);
		}
		
		if(text_title!=null) {
			text_title.setText(title);
		}
		if(text_msg!=null) {
			text_msg.setText(msg);
		}
	}
	public void updateMessage(String msg) {
		if(text_msg!=null) {
			text_msg.setText(msg);
		}
	}
		
}
