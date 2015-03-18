package com.xtrd.obdcar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imgquery.AQuery;
import com.xtrd.obdcar.active.CarListActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.ServiceEntity;
import com.xtrd.obdcar.nearby.NearShopActivity;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.obdservice.FindLocationActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.ObdDialog;

public class ServiceActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
	protected static final String TAG = "ServiceActivity";
	private GridView gridView;
	private AQuery mAQuery;
	private ArrayList<ServiceEntity> list = new ArrayList<ServiceEntity>();
	private TextView tips_text;
	private SwipeRefreshLayout swipeLayout;

	public ServiceActivity() {
		layout_id = R.layout.activity_service;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, 0, R.string.title_service, 0, 0);

		mAQuery = new AQuery(this);
		initView();

		JSONArray array = SettingLoader.getLocalService(this);
		ServiceEntity entity = null;
		if(array!=null&&array.length()>0) {
			for(int i=0;i<array.length();i++) {
				entity = new ServiceEntity();
				entity.parser(array.optJSONObject(i));
				list.add(entity);
			}
		}
		if(list==null||list.size()==0) {
			startRefresh();
		}else {
			updateUI();
		}
	}

	public void startRefresh() {
		try {
			Method refresh = null;
			Method[] methods = SwipeRefreshLayout.class.getDeclaredMethods();
			for (Method method : methods) {
				if ("startRefresh".equals(method.getName())) {
					refresh = method;
				}
			}
			if (refresh != null) {
				refresh.setAccessible(true);
				refresh.invoke(swipeLayout);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		if(getParent()!=null) {
			getParent().onBackPressed();
		}
	}


	private void checkService() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.App_Version, Utils.getLocalAppVersion(this));
		NetRequest.requestUrlNoDialog(this, ApiConfig.getRequestUrl(ApiConfig.Module_List_Url),params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if (!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								JSONArray array = json.getJSONArray("result");
								SettingLoader.saveService(ServiceActivity.this,array);
								ServiceEntity entity = null;
								for(int i=0;i<array.length();i++) {
									entity = new ServiceEntity();
									entity.parser(array.optJSONObject(i));
									list.add(entity);
								}
							}
						}
						updateUI();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				swipeLayout.setRefreshing(false);
				Utils.showToast(ServiceActivity.this,getResources().getString(R.string.data_load_fail));
			}
		});
	}

	private void initView() {
		tips_text = (TextView)findViewById(R.id.tips_text);

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light,
				R.color.holo_orange_light, R.color.holo_red_light);
		gridView = (GridView)findViewById(R.id.gridView);
		gridView.setSelector(android.R.color.transparent);
		gridView.setBackgroundColor(getResources().getColor(R.color.white));
		gridView.setVerticalSpacing(Utils.dipToPixels(this, 10));
		gridView.setHorizontalSpacing(Utils.dipToPixels(this, 10));
		gridView.setPadding(Utils.dipToPixels(this, 10), Utils.dipToPixels(this, 10), Utils.dipToPixels(this, 10), Utils.dipToPixels(this, 10));
		gridView.setNumColumns(3);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ServiceEntity entity = list.get(position);
				if(FindLocationActivity.class.getName().equals(entity.getDest().getName())) {
					gotoMap(entity);
				}else if(NearShopActivity.class.getName().equals(entity.getDest().getName())){
					Intent intent = new Intent(ServiceActivity.this,entity.getDest());
					intent.putExtra("keyword", "加油站");
					intent.putExtra("fromService", true);
					startActivity(intent);
				}else {
					Intent intent = new Intent(ServiceActivity.this,entity.getDest());
					intent.putExtra("title", entity.getName());
					intent.putExtra("from", true);
					startActivity(intent);
				}
			}
		});

	}


	protected void gotoMap(final ServiceEntity entity) {
		if(!NetUtils.isWifiNet(this)) {
			new ObdDialog(this).setTitle("温馨提示")
			.setMessage("您的手机当前网络环境不是wifi，确定查看地图吗？")
			.setPositiveButton(getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.setNegativeButton("确定", new ObdDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(ServiceActivity.this,entity.getDest());
					intent.putExtra("title", entity.getName());
					startActivity(intent);
				}
			}).show();
		}else {
			if(!SettingLoader.hasLogin(ServiceActivity.this)) {
				Utils.showToast(ServiceActivity.this, getResources().getString(R.string.tips_no_login));
				return;
			}
			
			if(!SettingLoader.hasBindBox(ServiceActivity.this)){
				showSimulateDialog();
				return;
			}
			Intent intent = new Intent(ServiceActivity.this,entity.getDest());
			intent.putExtra("title", entity.getName());
			startActivity(intent);
		}
	}

	private void showSimulateDialog() {
		ObdDialog dialog = new ObdDialog(this).setTitle("温馨提示")
				.setMessage("您还没绑定盒子，请先绑定。")
				.setPositiveButton(getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.setNegativeButton("绑定盒子", new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(ServiceActivity.this,CarListActivity.class);
						startActivity(intent);
					}
				});
		if(!dialog.isShowing()) {
			dialog.show();
		}
	}

	protected void updateUI() {
		swipeLayout.setRefreshing(false);
		if(list==null||list.size()==0) {
			tips_text.setVisibility(View.VISIBLE);
		}else {
			tips_text.setVisibility(View.GONE);
			if(gridView.getAdapter()==null) {
				ListAdapter adapter = new ListAdapter();
				gridView.setAdapter(adapter);
			}else {
				((ListAdapter)gridView.getAdapter()).notifyDataSetChanged();
			}
		}
	}


	final class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list!=null?list.size():0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ServiceEntity item = list.get(position);
			ViewHolder view = null;
			if (convertView == null) {
				view = new ViewHolder();
				convertView = LayoutInflater.from(ServiceActivity.this).inflate(R.layout.layout_service_item, null);
				view.layout_service = (LinearLayout)convertView.findViewById(R.id.layout_service);
				view.img_service = (ImageView) convertView.findViewById(R.id.img_service);
				view.text_service = (TextView) convertView.findViewById(R.id.text_service);
				convertView.setTag(view);
			} else {
				view = (ViewHolder) convertView.getTag();
			}
			mAQuery.id(view.img_service)
			.width(Utils.getScreenWidth(ServiceActivity.this)/2)
			.image(item.getRes());
			view.text_service.setText(item.getName());
			return convertView;
		}

		class ViewHolder {
			LinearLayout layout_service;
			ImageView img_service;
			TextView text_service;
		}
	}


	@Override
	public void onRefresh() {
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}
		list.clear();
		tips_text.setVisibility(View.GONE);
		checkService();
	}
}
