package com.xtrd.obdcar.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

public class DashBoardView extends LinearLayout {
	private Context context;
	private int res;
	private int degree;
	private String value;
	private String unit;

	public DashBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public DashBoardView(Context context,int res,int degree,String value,String unit) {
		super(context);
		this.context = context;
		this.res = res;
		this.degree = degree;
		this.value = value;
		this.unit = unit;
		init();
	}

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		
		FrameLayout frame = new FrameLayout(context);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		ImageView bg = new ImageView(context);
		bg.setImageResource(res);
		frame.addView(bg, params);
		ImageView pointer = new ImageView(context);
		pointer.setImageResource(R.drawable.ic_point_pointer);
		RotateAnimation ratate = new RotateAnimation(5,degree, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		ratate.setDuration(500);
		ratate.setFillAfter(true);
		pointer.startAnimation(ratate);
		frame.addView(pointer, params);
		ImageView top = new ImageView(context);
		top.setImageResource(R.drawable.ic_point);
		frame.addView(top, params);
		
		FrameLayout.LayoutParams tvLP = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tvLP.gravity = Gravity.CENTER;
		tvLP.setMargins(0, Utils.dipToPixels(context, 20), 0, 0);
		TextView tvalue = new TextView(context);
		tvalue.setText(value);
		tvalue.setTextColor(Color.parseColor("#6af9fd"));
		tvalue.setTextSize(18.f);
		frame.addView(tvalue,tvLP);
		
		addView(frame,new LinearLayout.LayoutParams(Utils.dipToPixels(context, 100),Utils.dipToPixels(context, 100)));
		
		LinearLayout.LayoutParams tparams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tparams.gravity = Gravity.CENTER_HORIZONTAL;
		
		TextView textview = new TextView(context);
		textview.setText(unit);
		textview.setTextColor(Color.parseColor("#6af9fd"));
		textview.setTextSize(16.f);
		addView(textview,tparams);
	}

}
