package com.xtrd.obdcar.view;

import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.wheel.WheelMain;

public class DateDialog extends Dialog implements android.view.View.OnClickListener{

	private Context context;
	private ImageButton btn_no, btn_ok;
	private OnClickListener onPositiveClickListener;
	private LinearLayout date_view;
	private WheelMain wheelMain;
	
	private boolean hasDay = true;//是否显示到日子
	private Date showDate;
	private boolean showTime = false;//是否显示精确到时分秒
	public DateDialog(Context context) {
		super(context);
		this.context = context;
	}
	
	public DateDialog(Context context,boolean hasDay,Date showDate) {
		super(context);
		this.context = context;
		this.hasDay = hasDay;
		this.showDate = showDate;
	}
	
	public DateDialog(Context context,Date showDate) {
		super(context);
		this.context = context;
		this.showDate = showDate;
	}
	
	public DateDialog(Context context,Date showDate,boolean showTime) {
		super(context);
		this.context = context;
		this.showDate = showDate;
		this.showTime  = showTime;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(
				R.layout.layout_date_dialog, null);
		btn_no = (ImageButton) viewGroup.findViewById(R.id.btn_no);
		btn_ok = (ImageButton) viewGroup.findViewById(R.id.btn_ok);
		btn_no.setOnClickListener(this);
		btn_ok.setOnClickListener(this);

		date_view = (LinearLayout) viewGroup.findViewById(R.id.date_view);
		wheelMain = new WheelMain(date_view,hasDay,showTime);
		wheelMain.screenheight = Utils.getScreenWidth(context);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(showDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int h = calendar.get(Calendar.HOUR_OF_DAY);
		int m = calendar.get(Calendar.MINUTE);
		if(showTime) {
			wheelMain.initDateTimePicker(year, month, day, h, m);
		}else {
			wheelMain.initDateTimePicker(year,month,day);
		}
		
		
		LayoutParams layoutParams = new LayoutParams(
				Utils.getScreenWidth(context), LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 0, 0, Utils.dipToPixels(context, 10));
		setContentView(viewGroup, layoutParams);

		

		Window window = getWindow();
//		window.setGravity(Gravity.LEFT | Gravity.TOP);
		window.setGravity(Gravity.CENTER);
		window.setBackgroundDrawableResource(android.R.color.transparent);

		WindowManager.LayoutParams manager = window.getAttributes();
		manager.x = 0;
//		manager.y = Utils.getScreenHeight(context)
//				+ Utils.dipToPixels(context, 10);
		manager.y = Utils.getScreenHeight(context);
		Animation animation = AnimationUtils.loadAnimation(context,
				R.anim.push_down_in);
		viewGroup.setAnimation(animation);
		window.setAttributes(manager);

	}
	

	public DateDialog setPositiveButton(OnClickListener onClickListener) {
		this.onPositiveClickListener = onClickListener;
		return this;
	}

	public interface OnClickListener {
		void onClick(String date);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			if (onPositiveClickListener != null) {
				onPositiveClickListener.onClick(wheelMain.getTime());
			}
			dismiss();
			break;
		case R.id.btn_no:
			dismiss();
			break;

		default:
			break;
		}

	}

	@Override
	public void show() {
		super.show();
	}
		
}
