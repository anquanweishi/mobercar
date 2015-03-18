package com.xtrd.obdcar.view.slidmenu;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class MenuView extends FrameLayout {

	private FrameLayout mContainer;

	public MenuView(Context context) {
		super(context);
		init();
	}

	public MenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MenuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		mContainer.measure(widthMeasureSpec, heightMeasureSpec);
//		Log.e("ad", "widthMeasureSpec----------------" + widthMeasureSpec);
//		Log.e("ad", "heightMeasureSpec----------------" + heightMeasureSpec);
//	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);
		final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0,
				height);
		final int menuWidth = getChildMeasureSpec(widthMeasureSpec, 0,
				mContainer.getWidth());
//		Log.e("ad", "menuWidth----------------" + menuWidth);
//		Log.e("ad", "contentHeight----------------" + contentHeight);
		mContainer.measure(menuWidth, contentHeight);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		final int height = b - t;
		mContainer.layout(0, 0, ContentView.MENU_WIDTH, height);
	}

	private void init() {
		mContainer = new FrameLayout(getContext());
		super.addView(mContainer);
	}

	public void setView(View v) {
		if (mContainer.getChildCount() > 0) {
			mContainer.removeAllViews();
		}
		mContainer.addView(v);
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		View child;
		for (int i = 0; i < getChildCount(); i++) {
			child = getChildAt(i);
			child.setFocusable(true);
			child.setClickable(true);
		}
	}

}
