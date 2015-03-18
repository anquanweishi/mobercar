package com.xtrd.obdcar.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.xtrd.obdcar.utils.Utils;


public class TabContent extends FrameLayout {

	private Scroller scroller;
	private Context context;
	private int mMaximumVelocity;
	private OnTabPageChangeListener onTabContentChangeListener;
	private boolean slideEnabled=true;
	
	public TabContent(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TabContent(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TabContent(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		scroller = new Scroller(context);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	@Override
	public void onLayout(boolean changed, int l, int t, int r, int b) {
		int childCount = getChildCount();
		super.onLayout(changed, l, t, r * childCount, b);
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.layout(i * getWidth(), 0, (i + 1) * getWidth(), getHeight());
		}
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), 0);
			postInvalidate();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int x = (int) ev.getRawX();
		int y = (int) ev.getRawY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = x;
			downY = y;
			tempX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(x - downX) - Math.abs(y - downY) > 10) {
				return true;
			}
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	private int downX = 0;
	private int downY = 0;
	private int tempX = 0;
	private int oritation = 0;
	private float velocity;
	private VelocityTracker mVelocityTracker;
	private boolean snap = true;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!snap){
			return false;
		}
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		int x = (int) event.getRawX();
		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = x;
			downY = y;
			tempX = downX;
			break;
		case MotionEvent.ACTION_MOVE:
			int distanceX = -(x - tempX);
			oritation = (x - downX);
			tempX = x;
			if (slideEnabled&&Math.abs(tempX - downX) > Utils.getScreenWidth(context)/20 && Math.abs(tempX - downX) > Math.abs(event.getRawY() - downY)) {
				scrollBy((int) distanceX, 0);
				if (onTabContentChangeListener != null) {
					onTabContentChangeListener.onTabPageSlide(distanceX);
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
			velocity = velocityTracker.getXVelocity();
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			snapToDestination();
			break;
		}
		return true;
	}

	public void scrollToPage(int page) {

		final int delta = page * (getWidth()) - getScrollX();
		int time = 0;
		if (velocity != 0) {
			time = (int) (delta / velocity * 1000);
			time = Math.abs(time) * 3;
		}
		velocity=0;
		time = time > 500 || time <= 0 ? 500 : time;
		scroller.startScroll(getScrollX(), 0, delta, 0, time);
		invalidate();
		if (onTabContentChangeListener != null) {
			onTabContentChangeListener.onTabPageChange(page);
		}
	}

	private void snapToDestination() {
		int page = getScrollX() / getWidth();
		int x = getScrollX() % getWidth();
		x = Math.abs(x);
		if (oritation < 0) {
			page += x >= getWidth() / 4 ? 1 : 0;
			if (page > getChildCount() - 1) {
				page = getChildCount() - 1;
			}
		} else if (oritation > 0) {
			if (page != 0) {
				page += x > getWidth() * 3 / 4 ? 1 : 0;
			}
		}
		scrollToPage(page);
	}

	public void setOnTabContentChangeListener(OnTabPageChangeListener onTabContentChangeListener) {
		this.onTabContentChangeListener = onTabContentChangeListener;
	}

	public void setTabContentChangeListener(OnTabPageChangeListener tabContentChangeListener) {
		this.onTabContentChangeListener = tabContentChangeListener;
	}
	
	public boolean isSlideEnabled() {
		return slideEnabled;
	}

	public void setSlideEnabled(boolean slideEnabled) {
		this.slideEnabled = slideEnabled;
	}

	public interface OnTabPageChangeListener {
		void onTabPageChange(int index);

		void onTabPageSlide(int distanceX);
	}

	public void setSnapToDestination(boolean snap) {
		this.snap  = snap;
	}

}
