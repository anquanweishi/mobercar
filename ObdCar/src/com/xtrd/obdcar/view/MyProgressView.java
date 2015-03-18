package com.xtrd.obdcar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

/**
 * @author naiyu(http://snailws.com)
 * @version 1.0
 */
public class MyProgressView extends View {

	// 画背景实心圆的画笔
	private Paint mBgCirclePaint;
	// 画实心圆的画笔
	private Paint mCirclePaint;
	// 画圆环的画笔
	private Paint mRingPaint;
	// 画字体的画笔
	private Paint mTextPaint;
	// 结束画字体的画笔
	private Paint mEndTextPaint;
	// 背景圆形颜色
	private int mBgCircleColor;
	// 圆形颜色
	private int mCircleColor;
	// 圆环颜色
	private int mRingColor;
	// 背景半径
	private float mBgRadius;
	// 半径
	private float mRadius;
	// 圆环半径
	private float mRingRadius;
	// 圆环宽度
	private float mStrokeWidth;
	// 圆心x坐标
	private int mXCenter;
	// 圆心y坐标
	private int mYCenter;
	// 字的长度
	private float mTxtWidth;
	// 字的高度
	private float mTxtHeight;
	// 结束字的长度
	private float mEndTxtWidth;
	// 结束字的高度
	private float mEndTxtHeight;
	// 总进度
	private int mTotalProgress = 100;
	// 当前进度
	private int mProgress;
	
	private Context context;
	//进度条文本
	private String txt;
	//结束文本
	private String endText = "重新检测";
	private boolean complete = false;

	public MyProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// 获取自定义的属性
		initAttrs(context, attrs);
		initVariable();
		android.view.ViewGroup.LayoutParams params = new android.view.ViewGroup.LayoutParams(Utils.getScreenWidth(context),Utils.getScreenHeight(context)-Utils.dipToPixels(context, 200));
		setLayoutParams(params);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.MyProgressView, 0, 0);
		mBgRadius = Utils.dipToPixels(context, 80);
		mRadius = Utils.dipToPixels(context, 50);
		mStrokeWidth = Utils.dipToPixels(context, 15);
		mBgCircleColor = typeArray.getColor(R.styleable.MyProgressView_bgColor, 0xFFFFFFFF);
		mCircleColor = typeArray.getColor(R.styleable.MyProgressView_circleColor, 0xFFFFFFFF);
		mRingColor = typeArray.getColor(R.styleable.MyProgressView_ringColor, 0xFFFFFFFF);
		
		mRingRadius = mRadius + mStrokeWidth / 2;
	}

	private void initVariable() {
		mBgCirclePaint = new Paint();
		mBgCirclePaint.setAntiAlias(true);
		mBgCirclePaint.setColor(mBgCircleColor);
		mBgCirclePaint.setStyle(Paint.Style.FILL);
		
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(mCircleColor);
		mCirclePaint.setStyle(Paint.Style.FILL);
		
		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(mRingColor);
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint.setStrokeWidth(mStrokeWidth);
		
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setARGB(255, 255, 255, 255);
		mTextPaint.setTextSize(mRadius / 2);
		
		mEndTextPaint = new Paint();
		mEndTextPaint.setAntiAlias(true);
		mEndTextPaint.setStyle(Paint.Style.FILL);
		mEndTextPaint.setARGB(255, 255, 255, 255);
		mEndTextPaint.setTextSize(mRadius / 4);
		
		FontMetrics fm = mTextPaint.getFontMetrics();
		mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);
		
		FontMetrics efm = mEndTextPaint.getFontMetrics();
		mEndTxtHeight = (int) Math.ceil(efm.descent - efm.ascent);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {

		mXCenter = Utils.getScreenWidth(context) / 2;
		mYCenter = Utils.getScreenHeight(context) / 2 - Utils.dipToPixels(context, 200);
		
		canvas.drawCircle(mXCenter, mYCenter, mBgRadius, mBgCirclePaint);
		canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);
		
		if (mProgress > 0 ) {
			RectF oval = new RectF();
			oval.left = (mXCenter - mRingRadius);
			oval.top = (mYCenter - mRingRadius);
			oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
			oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
			canvas.drawArc(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint); //
//			canvas.drawCircle(mXCenter, mYCenter, mRadius + mStrokeWidth / 2, mRingPaint);
			mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());
			canvas.drawText(txt, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4, mTextPaint);
			if(complete) {
				mEndTxtWidth = mEndTextPaint.measureText(endText, 0, endText.length());
				canvas.drawText(endText, mXCenter - mEndTxtWidth / 2, mYCenter + mTxtHeight, mEndTextPaint);
			}
		}
	}
	
	public void setProgress(int progress) {
		complete = false;
		mProgress = progress;
		txt = mProgress + "%";
//		invalidate();
		postInvalidate();
	}

	public void updateComplete(String txt) {
		mTextPaint.setTextSize(mRadius / 2);
		complete = true;
		this.txt = txt;
		postInvalidate();
	}
	public void updateFirst(String txt) {
		mTextPaint.setTextSize(mRadius / 3);
		this.txt = txt;
		postInvalidate();
	}
}
