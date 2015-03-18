package com.xtrd.obdcar;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.tumi.R;



public class GuideActivity extends Activity {
	private ViewPager viewPager;
	protected ArrayList<View> pageViews;
	private View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		viewPager = (ViewPager) findViewById(R.id.guidePages);
		pageViews = new ArrayList<View>();
		for (int i = 0; i < 3; i++) {
			view = inflater.inflate(R.layout.guide_item, null, true);
			pageViews.add(view);

		}

		viewPager.setAdapter(new GuidePageAdapter());
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
	}


	class GuidePageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(pageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(pageViews.get(arg1));
			updateUI(arg1,pageViews.get(arg1));
			return pageViews.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {
		}
	}

	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
		}
	}



	private void updateUI(int arg1, View view) {
		RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.guideview);
		if(0==arg1) {
			layout.setBackgroundResource(R.drawable.guide_one);
		}else if(1==arg1) {
			layout.setBackgroundResource(R.drawable.guide_two);
		}else if(2==arg1) {
			layout.setBackgroundResource(R.drawable.guide_three);
			Button btn_start = (Button)view.findViewById(R.id.btn_start);
			btn_start.setVisibility(View.VISIBLE);
			//			btn_start.getBackground().setAlpha(127);
			btn_start.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(GuideActivity.this,BaseTabActivity.class);
					startActivity(intent);
					SettingLoader.setFirstLaunch(GuideActivity.this, false);
					finish();
				}
			});
		}

	}


}
