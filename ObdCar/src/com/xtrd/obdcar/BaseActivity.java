package com.xtrd.obdcar;

import net.tsz.afinal.http.PreferencesCookieStore;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.passport.LoginActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.view.LoadDialog;
import com.xtrd.obdcar.view.ObdDialog;

public class BaseActivity extends Activity implements OnClickListener {


	protected int layout_id;

	protected LinearLayout btn_left, btn_right;
	protected TextView title_text;

	private RelativeLayout layout_top_bar;

	private Object object = new Object();
	private LoadDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout_id);
		XtrdApp.allActivity.add(this);
		layout_top_bar = (RelativeLayout)findViewById(R.id.layout_top_bar);

		btn_left = (LinearLayout) findViewById(R.id.btn_left);
		if(btn_left!=null) {
			btn_left.setOnClickListener(this);
		}
		title_text = (TextView) findViewById(R.id.title_text);
		btn_right = (LinearLayout) findViewById(R.id.btn_right);
		if(btn_right!=null) {
			btn_right.setOnClickListener(this);
		}


	}

	/**
	 * 初始化标题  
	 * @param leftText
	 * @param leftDrawable
	 * @param title
	 * @param rightText
	 * @param rightDrawable
	 */
	protected void initTitle(int leftText, int leftDrawable, int title,
			int rightText, int rightDrawable) {
		if (leftText != 0) {
			btn_left.setVisibility(View.VISIBLE);
			((TextView)btn_left.getChildAt(0)).setText(getResources().getString(leftText));
		}
		if (leftDrawable != 0) {
			btn_left.setVisibility(View.VISIBLE);
			((TextView)btn_left.getChildAt(0)).setBackgroundResource(leftDrawable);
		}
		if (title != 0) {
			title_text.setText(title);
		}
		if (rightText != 0) {
			btn_right.setVisibility(View.VISIBLE);
			((TextView)btn_right.getChildAt(0)).setVisibility(View.VISIBLE);
			((TextView)btn_right.getChildAt(0)).setText(getResources().getString(rightText));
			((ImageView)btn_right.getChildAt(1)).setVisibility(View.GONE);
		}
		if (rightDrawable != 0) {
			btn_right.setVisibility(View.VISIBLE);
			((TextView)btn_right.getChildAt(0)).setVisibility(View.GONE);
			((ImageView)btn_right.getChildAt(1)).setVisibility(View.VISIBLE);
			((ImageView)btn_right.getChildAt(1)).setImageResource(rightDrawable);
		}
	}

	/**
	 * 初始化标题  右边按钮 文本 背景 和字体颜色可配置
	 * @param leftText
	 * @param leftDrawable
	 * @param title
	 * @param rightText
	 * @param rightDrawable
	 *@param rightTextColor
	 */
	protected void initTitle(int leftText, int leftDrawable, String title,
			int rightText, int rightDrawable) {
		if (leftText != 0) {
			btn_left.setVisibility(View.VISIBLE);
			((TextView)btn_left.getChildAt(0)).setText(getResources().getString(leftText));
		}
		if (leftDrawable != 0) {
			btn_left.setVisibility(View.VISIBLE);
			((TextView)btn_left.getChildAt(0)).setBackgroundResource(leftDrawable);
		}
		if (!StringUtils.isNullOrEmpty(title)) {
			title_text.setText(title);
		}

		if (rightText != 0) {
			btn_right.setVisibility(View.VISIBLE);
			((TextView)btn_right.getChildAt(0)).setVisibility(View.VISIBLE);
			((TextView)btn_right.getChildAt(0)).setText(getResources().getString(rightText));
			((ImageView)btn_right.getChildAt(1)).setVisibility(View.GONE);
		}
		if (rightDrawable != 0) {
			btn_right.setVisibility(View.VISIBLE);
			((TextView)btn_right.getChildAt(0)).setVisibility(View.GONE);
			((ImageView)btn_right.getChildAt(1)).setVisibility(View.VISIBLE);
			((ImageView)btn_right.getChildAt(1)).setImageResource(rightDrawable);
		}
	}


	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}



	@Override
	public void onClick(View v) {
	}


	public void showLoading() {
		synchronized (object) {
			if(progressDialog==null) {
				progressDialog = new LoadDialog(this);
			}

			if (!isFinishing()) {
				progressDialog.show();
			}
		}

	}

	public void showLoading(boolean cancel) {
		synchronized (object) {
			if(progressDialog==null) {
				progressDialog = new LoadDialog(this);
				progressDialog.setCancelable(cancel);
			}
			if (!isFinishing()) {
				progressDialog.show();
			}
		}
	}

	public void dismissLoading() {
		synchronized (object) {
			if (progressDialog != null) {
				try {
					progressDialog.dismiss();
					progressDialog = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}


	public void showLoginDialog() {
		ObdDialog dialog = new ObdDialog(this).setTitle("温馨提示")
				.setMessage("您的登录已失效，请重新登录。")
				.setPositiveButton(getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						PreferencesCookieStore store = new PreferencesCookieStore(BaseActivity.this);
						store.getCookies().clear();
						store.clear();
						SettingLoader.setHasLogin(BaseActivity.this, false);
					}
				})
				.setNegativeButton("确定", new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						PreferencesCookieStore store = new PreferencesCookieStore(BaseActivity.this);
						store.clear();
						Intent intent = new Intent(BaseActivity.this,LoginActivity.class);
						intent.putExtra("retain", true);
						startActivity(intent);
						finish();
					}
				});
		if(!dialog.isShowing()) {
			dialog.show();
		}
	}
}
