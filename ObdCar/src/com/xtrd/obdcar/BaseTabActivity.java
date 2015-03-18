package com.xtrd.obdcar;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class BaseTabActivity extends TabActivity {

	public static final String TAG = "BaseTabActivity";

	private static TabHost tabHost;

	private LinearLayout myTab;
	private static View[] tabs;
	private static TextView[] titles;
	private static ImageView[] tabIcons;
	private static LinearLayout[] items;
	private String[] tags;
	private static int curPage = 0;
	private static Context context;
	private boolean from;

	OnClickListener tabOnClicklistener;

	private boolean isExitAppToastShowed;


	private static Resources resources;


	public Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case XtrdApp.ID_TAB_RESET:
				setCurrentTab(msg.arg1);
				break;
			default:
				break;
			}
		}

	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_tab_layout);
		XtrdApp.allActivity.add(this);
		from = getIntent().getBooleanExtra("fromNotify", false);
		context = this;
		resources = XtrdApp.getAppContext().getResources();
		XtrdApp.addHandler(handler);
		initLayout();
		setCurrentTab(getIntent().getIntExtra("currentTab", 0));

	}

	public void setCurrentTab(int index) {
		LogUtils.e(TAG, "index is " + index);
		if(index>=0&&index<5) {
			onTabChange(index);
		}
	}

	@Override
	public void onBackPressed() {
		if (isExitAppToastShowed) {
			XtrdApp.exitApp();
		} else {
			isExitAppToastShowed = true;
			Utils.showToast(BaseTabActivity.this,
					getString(R.string.toast_exit_app));
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (Utils.hasSmartBar()) {
			int orientation = getResources().getConfiguration().orientation;
			if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				if (ev.getY() > 127) {
					isExitAppToastShowed = false;
				}
			} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
				if (ev.getY() < 1184) {
					isExitAppToastShowed = false;
				}
			}
		} else {
			isExitAppToastShowed = false;
		}
		return super.dispatchTouchEvent(ev);
	}

	@SuppressWarnings("deprecation")
	private void initLayout() {

		tabHost = this.getTabHost();

		int[] tabTextResIds = new int[] { R.string.BaseTabActivity_Home,
				R.string.BaseTabActivity_Trip,
				R.string.BaseTabActivity_Service,
				R.string.BaseTabActivity_MainTain,
				R.string.BaseTabActivity_PersonlCenter };

		int[] tabIconResIds = new int[] { R.drawable.tab_btn_home_normal,
				R.drawable.tab_btn_trip_normal,
				R.drawable.tab_btn_service_normal,
				R.drawable.tab_btn_illegal_normal,
				R.drawable.tab_btn_myself_normal };

		Class[] tabIntentClasses = new Class[] { HomeActivity.class,
				VehicleConditionActivity.class, 
				ServiceActivity.class, MainTainActivity.class,
				PersonalActivity.class };

		tags = new String[tabTextResIds.length];

		for (int i = 0; i < tabTextResIds.length; i++) {
			tags[i] = getResources().getString(tabTextResIds[i]);
		}

		items = new LinearLayout[tabTextResIds.length];
		tabs = new View[tabTextResIds.length];
		titles = new TextView[tabTextResIds.length];
		tabIcons = new ImageView[tabTextResIds.length];
		myTab = (LinearLayout) findViewById(R.id.mytabs);
		tabOnClicklistener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				int tabIndex = v.getId();
//				tabHost.setCurrentTabByTag(tags[tabIndex]);
				onTabChange(tabIndex);

			}
		};

		createTabs(tabTextResIds, tabIconResIds, tags, tabIntentClasses, myTab);
	}

	protected void onTabChange(final int tabIndex) {
		tabHost.setCurrentTabByTag(tags[tabIndex]);

		for (int i = 0; i < tabs.length; i++) {
			tabs[i].setBackgroundColor(0);
			items[i].setBackgroundResource(0);
		}

		switch (tabIndex) {
		case 0:
			items[0].setBackgroundColor(getResources().getColor(R.color.tab_item_click_color));
			break;
		case 1:
			items[1].setBackgroundColor(getResources().getColor(R.color.tab_item_click_color));
			break;
		case 2:
			items[2].setBackgroundColor(getResources().getColor(R.color.tab_item_click_color));
			break;
		case 3:
			items[3].setBackgroundColor(getResources().getColor(R.color.tab_item_click_color));
			break;
		case 4:
			items[4].setBackgroundColor(getResources().getColor(R.color.tab_item_click_color));
			break;

		default:
			break;
		}
		curPage = tabIndex;
	}

	@SuppressWarnings("rawtypes")
	private void createTabs(int[] tabTextResIds, int[] tabIconResIds,
			String[] tags, Class[] tabIntentClasses, LinearLayout myTab) {
		for (int i = 0; i < tabTextResIds.length; i++) {
			View view = View.inflate(BaseTabActivity.this,
					R.layout.base_tab_item, null);
			LinearLayout tabItem = (LinearLayout) view
					.findViewById(R.id.tab_item);
			items[i] = tabItem;
			TextView textView = (TextView) view
					.findViewById(R.id.tab_textview_title);
			textView.setText(getResources().getString(tabTextResIds[i]));
			textView.setTextColor(getResources().getColor(
					R.color.white));

			titles[i] = textView;
			ImageView iv = ((ImageView) view
					.findViewById(R.id.tab_imageview_icon));
			iv.setImageResource(tabIconResIds[i]);
			tabIcons[i] = iv;

			Intent tabIntent = new Intent(this, tabIntentClasses[i]);
			if (from) {
				if (i == 0) {
					tabIntent.putExtra("fromNotify", from);
				}
			}
			
			TabHost.TabSpec spec = tabHost.newTabSpec(tags[i])
					.setIndicator(tags[i]).setContent(tabIntent);
			tabHost.addTab(spec);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					Utils.getScreenWidth(this) / 5, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
			view.setLayoutParams(params);
			myTab.addView(view);
			view.setOnClickListener(tabOnClicklistener);
			view.setId(i);
			tabs[i] = view;
		}
	}
}
