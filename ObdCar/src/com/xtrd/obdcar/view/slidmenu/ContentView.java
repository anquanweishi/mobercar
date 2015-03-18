package com.xtrd.obdcar.view.slidmenu;


import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.utils.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class ContentView extends ViewGroup {
	
	/**菜单宽度*/
	public static final int MENU_WIDTH = Utils.getScreenWidth(XtrdApp.getAppContext()) - Utils.dipToPixels(XtrdApp.getAppContext(), 100);
	/**速率采样间隔*/
	private static final int SNAP_VELOCITY = 1000;
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	
	public int mTouchState = TOUCH_STATE_REST;

	private FrameLayout mContainer;
	private Scroller mScroller;
	/**用于采集当手指离开屏幕时的滑动速率，本例中并未实际用到*/
	private VelocityTracker mVelocityTracker;
	/**系统所能识别的最小滑动距离*/
	private int mTouchSlop;
	/**手指点击屏幕的最初位置*/
	private float mLastMotionX;
	private float mLastMotionY;
	

	public ContentView(Context context) {
		super(context);
		init();
	}

	public ContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ContentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mContainer.measure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		final int height = b - t;
		mContainer.layout(0, 0, width, height);
	}

	private void init() {
		mContainer = new FrameLayout(getContext());
		mScroller = new Scroller(getContext());
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		super.addView(mContainer);
	}

	public void setView(View v) {
		if (mContainer.getChildCount() > 0) {
			mContainer.removeAllViews();
		}
		mContainer.addView(v);
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();
				if (oldX != x || oldY != y) {
					scrollTo(x, y);
				}
				// Keep on drawing until the animation has finished.
				invalidate();
				return;
			}
		}
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(MENU_WIDTH<ev.getX()) {
			return super.onInterceptTouchEvent(ev);
		}
		
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			Log.e("ad", "return true");
			return true;
		}

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// Remember location of down touch
			mLastMotionX = x;
			mLastMotionY = y;

			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			Log.e("ad", "onInterceptTouchEvent  ACTION_DOWN  mTouchState=="
					+ (mTouchState == TOUCH_STATE_SCROLLING));

			break;
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(x - mLastMotionX);
			final int yDiff = (int) Math.abs(y - mLastMotionY);

			final int touchSlop = mTouchSlop;
			boolean xMoved = xDiff > touchSlop;
			boolean yMoved = yDiff > touchSlop;

			if (xMoved || yMoved) {
				if (xMoved) {
					// Scroll if the user moved far enough along the X axis
					mTouchState = TOUCH_STATE_SCROLLING;
					Log.e("ad",
							"onInterceptTouchEvent  ACTION_MOVE  mTouchState=="
									+ (mTouchState == TOUCH_STATE_SCROLLING));
					enableChildrenCache();
				}
			}
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			// Release the drag
			clearChildrenCache();
			mTouchState = TOUCH_STATE_REST;
			Log.e("ad", "onInterceptTouchEvent  ACTION_UP  mTouchState=="
					+ (mTouchState == TOUCH_STATE_SCROLLING));
			break;
		}

		/*
		 * The only time we want to intercept motion events is if we are in the
		 * drag mode.
		 */
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(MENU_WIDTH<ev.getX()) {
			return super.onTouchEvent(ev);
		}
		
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			mLastMotionY = y;
			if (getScrollX() == -MENU_WIDTH && mLastMotionX < MENU_WIDTH) {
				return false;
			}
			Log.e("ad", "ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(x - mLastMotionX);
			final int yDiff = (int) Math.abs(y - mLastMotionY);
			final int touchSlop = mTouchSlop;
			boolean xMoved = xDiff > touchSlop;
			if (xMoved) {
				mTouchState = TOUCH_STATE_SCROLLING;
				Log.e("ad", "onInterceptTouchEvent  ACTION_MOVE  mTouchState=="
						+ (mTouchState == TOUCH_STATE_SCROLLING));
				enableChildrenCache();
			}
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final float deltaX = mLastMotionX - x;
				mLastMotionX = x;
				float oldScrollX = getScrollX();
				float scrollX = oldScrollX + deltaX;
				final float leftBound = 0;
				final float rightBound = -MENU_WIDTH;
				if (scrollX > leftBound) {
					scrollX = leftBound;
				} else if (scrollX < rightBound) {
					scrollX = rightBound;
				}
				scrollTo((int) scrollX, getScrollY());

			}
			Log.e("ad", "ACTION_MOVE");
			break;
		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(SNAP_VELOCITY);
				int velocityX = (int) velocityTracker.getXVelocity();
				int oldScrollX = getScrollX();
				Log.e("ad", "oldScrollX  ==  " + oldScrollX);
				int dx = 0;
				if (oldScrollX < -MENU_WIDTH/2) {
					dx = -MENU_WIDTH - oldScrollX;
				} else {
					dx = -oldScrollX;
				}
				smoothScrollTo(dx);
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}else{
				close();
			}
			Log.e("ad", "ACTION_UP");
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.e("ad", "ACTION_CANCEL");
			mTouchState = TOUCH_STATE_REST;
		}

		return true;
	}

	void toggle() {
		int oldScrollX = getScrollX();
		if (oldScrollX == 0) {
			smoothScrollTo(-MENU_WIDTH);
		} else if (oldScrollX == -MENU_WIDTH) {
			smoothScrollTo(MENU_WIDTH);
		}
	}
	
	void close() {
		int oldScrollX = getScrollX();
		if (oldScrollX == -MENU_WIDTH) {
			smoothScrollTo(MENU_WIDTH);
		}
	}

	void smoothScrollTo(int dx) {
		int duration = 500;
		int oldScrollX = getScrollX();
		mScroller.startScroll(oldScrollX, getScrollY(), dx, getScrollY(),
				duration);
		invalidate();
	}

	void enableChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(true);
		}
	}

	void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(false);
		}
	}

	/**
	 * true 没有显示
	 * @return
	 */
	public boolean isDisplay() {
		return getScrollX()==0;
	}

}
