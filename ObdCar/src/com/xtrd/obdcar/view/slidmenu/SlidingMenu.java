package com.xtrd.obdcar.view.slidmenu;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class SlidingMenu extends RelativeLayout {

	private MenuView mMenuView;
	private ContentView mContentView;

	public SlidingMenu(Context context) {
		super(context);
		init(context);
	}

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		LayoutParams behindParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mMenuView = new MenuView(context);
		addView(mMenuView, behindParams);
//		mMenuView.setView(mMenuView);

		LayoutParams aboveParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mContentView = new ContentView(context);
		addView(mContentView, aboveParams);
//		mContentView.setView(mContentView);
	}

	public void setMenu(View v) {
		mMenuView.setView(v);
		mMenuView.invalidate();
	}

	public void setContent(View v) {
		mContentView.setView(v);
		mContentView.invalidate();
	}

	public void showMenu() {
//		mContentView.scrollTo(-200, 0);
		mContentView.toggle();
	}
	
	public boolean isDisplay() {
		return mContentView.isDisplay();
	}
}
