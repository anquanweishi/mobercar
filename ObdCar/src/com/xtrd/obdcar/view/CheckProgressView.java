package com.xtrd.obdcar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtrd.obdcar.tumi.R;

/**
 * @version 1.0
 */
public class CheckProgressView extends FrameLayout {


	private Context context;
	private ImageView imageview;
	private ImageView progressImg;
	private TextView textImg;
	private Animation loadAnimation;
	private Callback callback;


	public CheckProgressView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CheckProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CheckProgressView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
//		setLayoutParams(new LayoutParams(width+Utils.dipToPixels(context, 10),height+Utils.dipToPixels(context, 10)));

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		imageview = new ImageView(context);
		imageview.setImageResource(R.drawable.ic_check_bg);
		addView(imageview,params);
		
		progressImg = new ImageView(context);
		progressImg.setImageResource(R.drawable.ic_check_progress);
		addView(progressImg,params);
		
		textImg = new TextView(context);
		textImg.setText("未检测");
		textImg.setGravity(Gravity.CENTER);
		textImg.setTextColor(context.getResources().getColor(R.color.text_gray_color));
		textImg.setBackgroundResource(R.drawable.ic_check_top);
		addView(textImg,params);
		textImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(callback!=null) {
					callback.callback();
				}
			}
		});
	}
	
	public void startCheck(String progress) {
		textImg.setText(progress);
		loadAnimation = AnimationUtils.loadAnimation(context, R.anim.check_progress);
		progressImg.startAnimation(loadAnimation);
	}
	
	public void stopCheck(String score) {
		if(loadAnimation!=null) {
			loadAnimation.cancel();
		}
		textImg.setText(score);
	}
	public void stopCheck() {
		if(loadAnimation!=null) {
			loadAnimation.cancel();
		}
	}
	
	public void setText(String score) {
		textImg.setText(score);
	}

	
	public interface Callback {
		void callback();
	}


	public void setCallback(Callback callback) {
		this.callback = callback;
	}
	
}
