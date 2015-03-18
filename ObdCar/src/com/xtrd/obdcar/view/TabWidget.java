package com.xtrd.obdcar.view;



import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

public class TabWidget extends LinearLayout implements Checkable {
	private Context context;
	private TextView tabTitle;
	private RadioButton tabIcon;
	private int iconRes;
	private String title;
	
	private boolean checked=false;

	public TabWidget(Context context,String title,int iconRes) {
		super(context);
		this.context = context;
		this.iconRes = iconRes;
		this.title = title;
		init();
	}


	public TabWidget(Context context,String title) {
		super(context);
		this.context = context;
		this.title = title;
		init();
	}

	private void init() {
		this.setGravity(Gravity.CENTER);
		this.setOrientation(HORIZONTAL);
		this.setFocusable(true);
		this.setClickable(true);
//		this.setPadding(Utils.dipToPixels(context, 10), 0, Utils.dipToPixels(context, 10), 0);
		LinearLayout.LayoutParams iconLP = null;
		tabIcon = new RadioButton(context);
		if(iconRes!=0){
			tabIcon.setButtonDrawable(iconRes);
			tabIcon.setVisibility(View.VISIBLE);
			
			Drawable drawable = context.getResources().getDrawable(iconRes);
			if(drawable!=null) {
				iconLP = new LinearLayout.LayoutParams(drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
			}else {
				iconLP = new LinearLayout.LayoutParams(Utils.dipToPixels(context, 20),
							Utils.dipToPixels(context, 20));
			}
		}else {
			tabIcon.setVisibility(View.GONE);
			iconLP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		}

		
		iconLP.setMargins(0, 0, Utils.dipToPixels(context, 5), 0);
		iconLP.gravity=Gravity.CENTER;
		
		tabTitle = new TextView(context);
		tabTitle.setGravity(Gravity.CENTER);
		tabTitle.setTextColor(context.getResources().getColor(R.color.font_car_add_color));
//		tabTitle.setTextAppearance(context, R.style.font_main_tab_default);
		tabTitle.setText(title);
		
		this.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				setChecked(hasFocus);
			}
		});
		
		this.addView(tabIcon, iconLP);
		this.addView(tabTitle);

	}

	@Override
	public void setChecked(boolean checked) {
		if(this.checked!=checked){
			if(iconRes!=0) {
				tabIcon.setChecked(checked);
			}
//			tabTitle.setTextAppearance(context, checked ? R.style.font_main_tab_checked : R.style.font_main_tab_default);
			this.checked=checked;
		}
	}

	@Override
	public boolean isChecked() {
		return checked;
	}

	@Override
	public void toggle() {
		setChecked(!checked);
	}


	public void setTitleColor(int color) {
		tabTitle.setTextColor(color);
	}

}
