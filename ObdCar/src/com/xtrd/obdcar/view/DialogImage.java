package com.xtrd.obdcar.view;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.log.LogUtils;

@SuppressLint("NewApi")
public class DialogImage extends Dialog {

	private Context context;
	private ImageView btn;
	private int resId;
	private ImageView image;

	private DialogImage(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	private DialogImage(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	public DialogImage(Context context,ImageView btn,int resId) {
		super(context);
		this.btn = btn;
		this.resId = resId;
		init(context);
	}

	private void init(Context context) {
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		image = new ImageView(context);
		image.setImageResource(resId);
		setContentView(image, new ViewGroup.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		
		Window window = getWindow();
		window.setGravity(Gravity.CENTER);
		window.setWindowAnimations(R.style.dialog_anim);
		window.setBackgroundDrawableResource(android.R.color.transparent);

		WindowManager.LayoutParams manager=window.getAttributes();
		window.setAttributes(manager);
	}

	@Override
	public void show() {
		if(this!=null) {
			LogUtils.e("PayDialog", "show is running");
			super.show();
		}
	}
	
	@Override
	public void dismiss() {
		LogUtils.e("PayDialog", "dismiss is running");
		super.dismiss();
	}

}
