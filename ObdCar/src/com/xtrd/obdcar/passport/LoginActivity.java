package com.xtrd.obdcar.passport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.BaseTabActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.CarOpenHelper;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.CarInfo;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.pwdforget.ForgetOneActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.MMAlert;

public class LoginActivity extends BaseActivity {
	private static final String TAG = "LoginActivity";
	private LinearLayout layout_text;
	private TextView text_user;
	private LinearLayout layout_edit;
	private View view_interval;

	private LinearLayout mainview;
	private EditText edit_user;
	private ImageView btn_user_clear;
	private EditText edit_pwd;
	private ImageView btn_pwd_clear;
	private Button btn_login,btn_forget;
	private TextView btn_reg;


	public LoginActivity() {
		layout_id = R.layout.activity_login;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0,R.drawable.btn_back_bg,R.string.title_login,R.string.btn_change,0);

		initView();
		regListener();
	}


	private void regListener() {
		btn_user_clear.setOnClickListener(this);
		btn_pwd_clear.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		btn_reg.setOnClickListener(this);
		btn_forget.setOnClickListener(this);

		edit_user.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(s.length()>0) {
					btn_user_clear.setVisibility(View.VISIBLE);
				}else {
					btn_user_clear.setVisibility(View.INVISIBLE);
				}
			}
		});
		edit_pwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(s.length()>0) {
					btn_pwd_clear.setVisibility(View.VISIBLE);
				}else {
					btn_pwd_clear.setVisibility(View.INVISIBLE);
				}
			}
		});
		edit_pwd.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& KeyEvent.ACTION_DOWN == event.getAction()) {
					edit_pwd.clearFocus();
					login();
					return true;
				}

				return false;
			}
		});
	}


	private void initView() {
		layout_text = (LinearLayout)findViewById(R.id.layout_text);
		text_user = (TextView)findViewById(R.id.text_user);
		layout_edit =(LinearLayout)findViewById(R.id.layout_edit);
		view_interval = (View)findViewById(R.id.view_interval);

		mainview = (LinearLayout)findViewById(R.id.mainview);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mainview.getLayoutParams();
		params.height = Utils.getScreenHeight(this)-Utils.getStatusBarHeight(getWindow()) - Utils.dipToPixels(this, 200);
		mainview.setLayoutParams(params);
		edit_user = (EditText)findViewById(R.id.edit_user);
		btn_user_clear = (ImageView)findViewById(R.id.btn_user_clear);
		edit_pwd = (EditText)findViewById(R.id.edit_pwd);
		btn_pwd_clear = (ImageView)findViewById(R.id.btn_pwd_clear);
		btn_login = (Button)findViewById(R.id.btn_login);
		btn_forget = (Button)findViewById(R.id.btn_pwd_forget);
		btn_reg = (TextView)findViewById(R.id.btn_reg);

		TextView text_change_svr = (TextView)findViewById(R.id.textView2);
		text_change_svr.setOnClickListener(this);

		updateView();
	}

	public void updateView() {
		if(StringUtils.isNullOrEmpty(SettingLoader.getLoginName(this))) {
			layout_text.setVisibility(View.GONE);
			layout_edit.setVisibility(View.VISIBLE);
			view_interval.setVisibility(View.VISIBLE);
			edit_user.setText("");
			edit_pwd.setText("");
		}else {
			layout_text.setVisibility(View.VISIBLE);
			layout_edit.setVisibility(View.GONE);
			view_interval.setVisibility(View.GONE);
			text_user.setText(SettingLoader.getLoginName(this));
		}
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			onBackPressed();
			break;
		case R.id.btn_right:
			SettingLoader.clearLoginName(this);
			updateView();
			break;
		case R.id.btn_login:
			login();
			break;
		case R.id.btn_reg:
			Intent intent = new Intent(this,RegActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.btn_pwd_forget:
			/*String user = (!StringUtils.isNullOrEmpty(SettingLoader.getLoginName(this))?SettingLoader.getLoginName(this):edit_user.getText().toString());
			if(StringUtils.isNullOrEmpty(user)) {
				Utils.showToast(this, "请输入用户名");
				return;
			}
			showTipDialog(user);*/
			Intent forget = new Intent(this,ForgetOneActivity.class);
			startActivity(forget);
			break;
		case R.id.btn_user_clear:
			edit_user.setText("");
			break;
		case R.id.btn_pwd_clear:
			edit_pwd.setText("");
			break;
		case R.id.textView2:
//			showSvrChange();
			break;

		default:
			break;
		}
		super.onClick(v);
	}


	/**
	 * 登录
	 * @param user
	 * @param pwd
	 */
	private void login() {
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showToast(this, getResources().getString(R.string.network_unavailable_tips));
			return;
		}
		String user = edit_user.getText().toString();
		if(StringUtils.isNullOrEmpty(user)) {
			user = SettingLoader.getLoginName(this);
		}
		String pwd = edit_pwd.getText().toString();
		if(StringUtils.isNullOrEmpty(user)) {
			Utils.showToast(this, "请输入用户名");
			return;
		}
		if(StringUtils.isNullOrEmpty(pwd)) {
			Utils.showToast(this, "请输入密码");
			return;
		}
		SettingLoader.setLoginName(LoginActivity.this,user,null);

		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Login_Username, "D"+user);
		params.put(ParamsKey.Login_Password, pwd);
		//post cookieStore 设置
		final CookieStore cookieStore = new BasicCookieStore();
		fh.configCookieStore(cookieStore);
		LogUtils.e(TAG, "params " + params);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Login_Url), params,new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				LogUtils.e(TAG, t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								PreferencesCookieStore store = new PreferencesCookieStore(LoginActivity.this);
								List<Cookie> cookies = cookieStore.getCookies();
								if(!cookies.isEmpty()){
									for(int i = 0; i < cookies.size();i++){
										Cookie cookie = cookies.get(i);
										Log.e("cookie",cookie.getName()+" "+cookie.getValue());
										store.addCookie(cookie);
									}
								}
								if(json.has("result")) {
									String result = json.getString("result");
									SettingLoader.setLoginName(LoginActivity.this,null,result);
									checkJpushReg(result);
								}
								SettingLoader.setHasLogin(LoginActivity.this,true);
								XtrdApp.sendMsg(XtrdApp.ID_LOGIN);
								uploadCars();
								Utils.showToast(LoginActivity.this, getResources().getString(R.string.login_suc));
								
								if(getIntent().getBooleanExtra("retain", false)) {//登录失效弹出登录框需要保留
									Intent intent = new Intent(LoginActivity.this,BaseTabActivity.class);
									startActivity(intent);
								}
								finish();
							}else {
								String message = json.getString("message");
								Utils.showToast(LoginActivity.this, message);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				dismissLoading();
				SettingLoader.clearLoginName(LoginActivity.this);
				Utils.showToast(LoginActivity.this,getResources().getString(R.string.tips_login_fail));
				super.onFailure(t, errorNo, strMsg);
			}

		});

	}

	protected void uploadCars() {
		final CarOpenHelper openHelper = CarOpenHelper.getInstance(this);
		ArrayList<CarInfo> localCars = openHelper.getLocalCars();
		String value = "";
		for(CarInfo info : localCars) {
			value+=info.getPlateNumber()+","+info.getModelId()+","+info.getDefaultFuelTypeId()+","+info.getDrivingAreaCode()+","+info.getDistance()+";";
		}
		
		if(StringUtils.isNullOrEmpty(value)) {
			return;
		}
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Active_Vehicles,value);
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Active_Add_Car_Url) ,params,new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json!=null) {
							if(json.has("status")) {
								int status = json.getInt("status");
								if (1 == status) {
									openHelper.deleteCars();
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
			}
		});
	}


	/**
	 * 检查极光reg_id
	 */
	private void checkJpushReg(String channel) {
		LogUtils.e(TAG, "jpush regid "+JPushInterface.getRegistrationID(this));
		if(!StringUtils.isNullOrEmpty(JPushInterface.getRegistrationID(this))) {
			Set<String> tags = new HashSet<String>();
			tags.add(channel);
			JPushInterface.setAliasAndTags(this, SettingLoader.getLoginName(this),tags, new TagAliasCallback() {

				@Override
				public void gotResult(int arg0, String arg1, Set<String> arg2) {
					LogUtils.e(TAG, arg0 + " " + arg1 + " " + arg2);
				}
			});
			FinalHttp fh = new FinalHttp();
			AjaxParams params = new AjaxParams();
			params.put(ParamsKey.RegistrationId, JPushInterface.getRegistrationID(this));
			params.put(ParamsKey.Alias, SettingLoader.getLoginName(this));
			params.put(ParamsKey.Tag, channel);
			params.put(ParamsKey.DeviceType, ParamsKey.Android);
			fh.post(ApiConfig.getRequestUrl(ApiConfig.Jpush_Reg_Url) ,params, new AjaxCallBack<String>() {

				private String msg;

				@Override
				public void onSuccess(String t) {
					LogUtils.e(TAG,"login jpush"+t.toString() + "  " + SettingLoader.getLoginName(LoginActivity.this));
					try {
						if(!StringUtils.isNullOrEmpty(t)) {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									LogUtils.e(TAG,"push login suc");
								}else {
									msg = json.getString("message");
									LogUtils.e(TAG, msg);
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					super.onSuccess(t);
				}
				
				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					LogUtils.e(TAG, "error msg "+strMsg);
				}
			});
		}
	}


	private void showSvrChange() {
		String[] items = new String[]{"192.168.1.200","inforstack","app.sevoh.com","xtrd.sevoh.com"};
		MMAlert.showAlert(this, "服务器选择", items, null, new MMAlert.OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				switch (whichButton) {
				case 0: 
					SettingLoader.setSvr(LoginActivity.this, ApiConfig.TEST_API_URL);
					break;
				case 1: 
					SettingLoader.setSvr(LoginActivity.this, ApiConfig.BEIJING_API_URL);
					break;
				case 2: 
					SettingLoader.setSvr(LoginActivity.this, ApiConfig.JILIN_API_URL);
					break;
				case 3: 
					SettingLoader.setSvr(LoginActivity.this, ApiConfig.RELEASE_JILIN_API_URL);
					break;
				default:
					break;
				}
			}

		});
	}

}
