package com.xtrd.obdcar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.entity.LimitAndOil;
import com.xtrd.obdcar.tumi.R;

public class CarLimitItem extends LinearLayout {
	
	private LimitAndOil item;
	public CarLimitItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public CarLimitItem(Context context,LimitAndOil item) {
		super(context);
		this.item = item;
		initView(context);
	}

	private void initView(Context context) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		params.gravity = Gravity.CENTER;
		
		setLayoutParams(params);
		
		LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_car_limit, null);
		((TextView)view.findViewById(R.id.text_title)).setText(item.getTitle());
		addView(view,params);
		
		updateLimit(view, item);
	}

	public void updateLimit(View view, LimitAndOil item) {
		

		if(item.getOneValue()!=null&&item.getOneValue().length>0) {
			((TextView)view.findViewById(R.id.text_today)).setText(item.getOneTitle());

			((TextView)view.findViewById(R.id.text_one)).setVisibility(View.VISIBLE);
			if(item.getOneValue().length>1) {
				((TextView)view.findViewById(R.id.text_one)).setText(item.getOneValue()[0]);
				((TextView)view.findViewById(R.id.text_two)).setVisibility(View.VISIBLE);
				((TextView)view.findViewById(R.id.text_two)).setText(item.getOneValue()[1]);
			}else {
				((TextView)view.findViewById(R.id.text_one)).setBackgroundResource(R.drawable.oil_price_item_bg);
				((TextView)view.findViewById(R.id.text_one)).setText(item.getOneValue()[0]);
				((TextView)view.findViewById(R.id.text_one)).setCompoundDrawablesWithIntrinsicBounds(0, 0, item.getOneRes(), 0);
				((TextView)view.findViewById(R.id.text_two)).setVisibility(View.GONE);
			}
		}
		if(item.getTwoValue()!=null&&item.getTwoValue().length>0) {
			((TextView)view.findViewById(R.id.text_tomorrow)).setText(item.getTwoTitle());
			((TextView)view.findViewById(R.id.text_three)).setVisibility(View.VISIBLE);
			if(item.getTwoValue().length>1) {
				((TextView)view.findViewById(R.id.text_three)).setText(item.getTwoValue()[0]);
				((TextView)view.findViewById(R.id.text_four)).setVisibility(View.VISIBLE);
				((TextView)view.findViewById(R.id.text_four)).setText(item.getTwoValue()[1]);
			}else {
				((TextView)view.findViewById(R.id.text_three)).setBackgroundResource(R.drawable.oil_price_item_bg);
				((TextView)view.findViewById(R.id.text_three)).setText(item.getTwoValue()[0]);
				((TextView)view.findViewById(R.id.text_three)).setCompoundDrawablesWithIntrinsicBounds(0, 0, item.getTwoRes(), 0);
				((TextView)view.findViewById(R.id.text_four)).setVisibility(View.GONE);
			}
		}
		
	}
}
