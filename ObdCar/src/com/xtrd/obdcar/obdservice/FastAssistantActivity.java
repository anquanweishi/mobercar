package com.xtrd.obdcar.obdservice;


import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.WebLoadActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.CacheConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.AccidentType;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.passport.LoginActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.ImageUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.ObdDialog;
import com.xtrd.obdcar.view.ObdListDialog;

public class FastAssistantActivity extends BaseActivity {

	private static final int CAMERA_REQUEST_CODE = 20;
	private static final int LOGIN_CODE = 21;
	private FrameLayout layout_type;
	private ListView listView;
	private EditText edit_input;


	private ArrayList<AccidentType> firstlist = new ArrayList<AccidentType>();
	private ArrayList<AccidentType> sencondlist = new ArrayList<AccidentType>();
	private ArrayList<AccidentType> list = new ArrayList<AccidentType>();
	private ListAdapter adapter;
	private LinearLayout layout_button;
	private TextView text_description;
	protected int level = 1;//1 第一级  2第二级 3第三级
	private int typeId;//事故类型


	public FastAssistantActivity() {
		layout_id = R.layout.activity_fast_assistant;
	}

	public Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case XtrdApp.ID_LOGIN:
				getIncidentType(null);
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_fast_assistant, R.string.text_photo_guide,0);
		XtrdApp.addHandler(handler);
		initView();
		regClick();
		if(SettingLoader.hasLogin(this)) {
			getIncidentType(null);
		}else {
			Intent intent = new Intent(this,LoginActivity.class);
			startActivityForResult(intent, LOGIN_CODE);
		}

	}


	private void initView() {
		layout_button = (LinearLayout)findViewById(R.id.layout_button);
		text_description = (TextView)findViewById(R.id.text_description);

		layout_type = (FrameLayout)findViewById(R.id.layout_type);
		listView = (ListView)findViewById(R.id.listView);

		edit_input =(EditText)findViewById(R.id.edit_input);

	}


	private void regClick() {
		layout_type.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			Intent intent = new Intent(this,WebLoadActivity.class);
			intent.putExtra("title", "拍照指南");
			intent.putExtra("url", ApiConfig.Paizhao_Url);
			startActivity(intent);
			break;
		case R.id.layout_type:
			showListDialog();
			break;

		default:
			break;
		}
	}

	private void showListDialog() {
		if(sencondlist.size()>0) {
			String[] datas = new String[sencondlist.size()];
			for(int i=0;i<sencondlist.size();i++) {
				datas[i] = sencondlist.get(i).getName();
			}
			new ObdListDialog(this)
			.setList(datas)
			.setItemButton(new ObdListDialog.OnClickListener() {

				@Override
				public void onClick(String value) {
					((TextView)layout_type.getChildAt(0)).setText(value);
					list.clear();
					if(adapter!=null) {
						adapter.notifyDataSetChanged();
					}
					int typeId = getTypeId(value);
					level = 3;
					getIncidentType(typeId+"");
				}
			})
			.show();
		}
	}

	private int getTypeId(String name) {
		for(AccidentType item : sencondlist) {
			if(name.equals(item.getName())) {
				return item.getId();
			}
		}
		return 0;
	}

	private void getIncidentType(String type) {

		AjaxParams params = new AjaxParams();
		if(!StringUtils.isNullOrEmpty(type)) {
			params.put(ParamsKey.TYPE, type);
		}
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Incident_Type_Url) ,params,new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json!=null) {
							if(json.has("status")) {
								int status = json.getInt("status");
								if (1 == status) {
									if(json.has("result")) {
										JSONArray array = json.getJSONArray("result");
										if(array!=null&&array.length()>0) {
											AccidentType item = null;
											for(int i=0;i<array.length();i++) {
												item = new AccidentType();
												item.parser(array.optJSONObject(i));
												if(1==level) {
													firstlist.add(item);
												}else if(2==level){
													sencondlist.add(item);
												}else {
													list.add(item);
												}
											}
										}
									}
								}
							}
						}
						if(1==level) {
							updateTopButton();
							if(firstlist!=null&&firstlist.size()>0) {
								getIncidentType(firstlist.get(0).getId()+"");
							}
							level = 2;
						}else if(2==level){
							if(sencondlist!=null&&sencondlist.size()>0) {
								((TextView)layout_type.getChildAt(0)).setText(sencondlist.get(0).getName());
								typeId = sencondlist.get(0).getId();								
								level = 3;
								getIncidentType(typeId+"");
							}
						}else if(3==level) {
							updateUI();
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
	 * 处理顶部按钮
	 */
	protected void updateTopButton() {
		layout_button.removeAllViews();
		if(firstlist!=null&&firstlist.size()>0) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dipToPixels(this, 300),LayoutParams.WRAP_CONTENT);
			params.weight = 1;
			Button button = null;
			for(int i=0;i<firstlist.size();i++) {
				button = new Button(this);
				button.setText(firstlist.get(i).getName());
				button.setTextColor(getResources().getColor(R.color.white));
				if(0==i) {
					button.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
				}else {
					button.setBackgroundColor(getResources().getColor(R.color.transparent));
				}
				button.setTextSize(16.f);
				button.setId(i);

				layout_button.addView(button,params);
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						switch (v.getId()) {
						case 0:
							(layout_button.findViewById(0)).setBackgroundColor(getResources().getColor(R.color.top_bar_color));
							(layout_button.findViewById(1)).setBackgroundColor(getResources().getColor(R.color.transparent));
							layout_type.setVisibility(View.VISIBLE);
							edit_input.setVisibility(View.GONE);
							text_description.setText(firstlist.get(0).getDescription());
							list.clear();
							if(adapter!=null) {
								adapter.notifyDataSetChanged();
							}
							level = 2;
							sencondlist.clear();
							getIncidentType(firstlist.get(0).getId()+"");
							break;
						case 1:
							(layout_button.findViewById(0)).setBackgroundColor(getResources().getColor(R.color.transparent));
							(layout_button.findViewById(1)).setBackgroundColor(getResources().getColor(R.color.top_bar_color));
							layout_type.setVisibility(View.GONE);
							edit_input.setVisibility(View.VISIBLE);
							listView.setVisibility(View.GONE);
							text_description.setText(firstlist.get(1).getDescription());
							list.clear();
							if(adapter!=null) {
								adapter.notifyDataSetChanged();
							}
							getIncidentType(firstlist.get(1).getId()+"");
							break;

						default:
							break;
						}
					}
				});
			}
			text_description.setText(firstlist.get(0).getDescription());
		}

	}

	protected void updateUI() {
		listView.setVisibility(View.VISIBLE);
		if(adapter==null) {
			adapter = new ListAdapter();
			listView.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}
	}

	private File currentFile = null;
	private String localFilename = null;
	/**
	 * 启动相机
	 */
	public void startCamera() {
		if (!Utils.isSDCardEnable()) {
			Utils.showToast(this,"请插入SD卡");
			return ;
		}
		if(Utils.isSDCardEnable()) {
			File filePath = CacheConfig.getRootFile();
			currentFile = new File(filePath,getFileName());
			Uri outputFileUri = Uri.fromFile(currentFile);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			intent.putExtra("autofocus", true); // 自动对焦
			intent.putExtra("fullScreen", false); // 全屏
			intent.putExtra("showActionIcons", false);
			startActivityForResult(intent, CAMERA_REQUEST_CODE);
		}else {
			Utils.showToast(this, getResources().getString(R.string.tips_no_sdcard));
		}
	}
	private String getFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
		return "IMG_" + format.format(date) + ".jpg";

	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(RESULT_OK==resultCode) {
			switch (requestCode) {
			case CAMERA_REQUEST_CODE:
				new AsyncTask<Void, Integer, Long>() {

					@Override
					protected Long doInBackground(Void... params) {
						try {
							if(currentFile!=null&&currentFile.exists()) {

								Bitmap thumbnails = ImageUtils.getScaleImage(currentFile.getAbsolutePath());
								if(thumbnails!=null) {
									currentFile.delete();
									currentFile = new File(CacheConfig.getRootFile(),getFileName());
									FileOutputStream fos = new FileOutputStream(currentFile);
									thumbnails.compress(CompressFormat.JPEG, 100, fos);
									fos.flush();
									fos.close();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}


					@Override
					protected void onPreExecute() {
						showLoading();
						super.onPreExecute();
					}


					@Override
					protected void onPostExecute(Long result) {
						XtrdApp.getInstance().getCurrentLocation(new XtrdApp.LocationDetailCallBack() {

							@Override
							public void callback(String address) {
								uploadImg(uploadId,address);
							}
						});
						super.onPostExecute(result);
					}

				}.execute();
				break;
			case LOGIN_CODE:
				finish();
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private int currentPosition;
	final class ListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return list!=null?list.size():0;
		}

		@Override
		public AccidentType getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final AccidentType item = getItem(position);
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(FastAssistantActivity.this).inflate(R.layout.layout_incident_item, null);
				viewHolder.gridview = (GridView) convertView.findViewById(R.id.gridView);
				viewHolder.text_time = (TextView)convertView.findViewById(R.id.text_time);
				viewHolder.layout_take_photo = (LinearLayout)convertView.findViewById(R.id.layout_take_photo);
				viewHolder.text_desc = (TextView)convertView.findViewById(R.id.text_desc);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			item.setPosition(position);
			viewHolder.text_time.setText(item.getDescription());
			viewHolder.text_desc.setText(item.getName());

			GridAdapter adapter = new GridAdapter(FastAssistantActivity.this, item);
			viewHolder.gridview.setAdapter(adapter);

			OnClickListener click = new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.layout_take_photo:
						typeId = item.getId();
						currentPosition = item.getPosition();
						LogUtils.e("TAG", " item.getLocalUrls() " + item.getLocalUrls().size()+" " + item.getNum());
						if(item.getLocalUrls().size()<item.getNum()) {
							startCamera();
						}else {
							Utils.showToast(FastAssistantActivity.this, "照片数量已经足够");
						}
						break;
					default:
						break;
					}
				}
			};
			viewHolder.layout_take_photo.setOnClickListener(click);

			return convertView;
		}

		class ViewHolder {

			public GridView gridview;
			public TextView text_time;
			public LinearLayout layout_take_photo;
			public TextView text_desc;
		}
	}



	private String uploadId = "";
	/**
	 * 上传
	 */
	private void uploadImg(String id,String address) {
		try {
			FinalHttp fh = new FinalHttp();
			fh.addHeader("Accept-Encoding", "GZIP");
			AjaxParams params = new AjaxParams();
			params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
			if(!StringUtils.isNullOrEmpty(id)) {
				params.put(ParamsKey.ID, id);
			}
			params.put(ParamsKey.TypeId,typeId+"");
			params.put(ParamsKey.AccLoc, address);
			if(!StringUtils.isNullOrEmpty(edit_input.getText().toString())) {
				params.put(ParamsKey.PhoneOther, edit_input.getText().toString());
			}
			
			if(!StringUtils.isNullOrEmpty(localFilename)) {
				params.put(ParamsKey.FileName, localFilename.substring(localFilename.lastIndexOf("/")+1));
			}
			params.put(ParamsKey.Phone, SettingLoader.getLoginName(this));
			params.put(ParamsKey.Picture, currentFile);
			PreferencesCookieStore store = new PreferencesCookieStore(this);
			fh.configCookieStore(store);
			fh.post(ApiConfig.getRequestUrl(ApiConfig.Incident_Upload_Url), params, new AjaxCallBack<String>() {

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					dismissLoading();
					Utils.showToast(FastAssistantActivity.this, "上传失败");
				}


				@Override
				public void onSuccess(String t) {
					dismissLoading();

					try {
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							if(1==json.getInt("status")) {
								if(json.has("result")) {
									json = json.getJSONObject("result");
									uploadId = json.getInt("id")+"";
									if(json.has("name")) {
										String svrUrl = json.getString("name");
										if(list.get(currentPosition)!=null) {
											list.get(currentPosition).addLocal(currentFile.getAbsolutePath());
											list.get(currentPosition).addSvr(svrUrl);
										}
									}
								}

								Utils.showToast(FastAssistantActivity.this, "上传成功");
								if(adapter!=null) {
									adapter.notifyDataSetChanged();
								}
								/*if(currentFile!=null) {
									currentFile.delete();
									currentFile = null;
								}*/
							}else {
								Utils.showToast(FastAssistantActivity.this, "上传失败");
							}
						}else {
							Utils.showToast(FastAssistantActivity.this, "上传失败");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					super.onSuccess(t);
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
			dismissLoading();
			Utils.showToast(FastAssistantActivity.this, "上传失败");
		}
	}


	final class GridAdapter extends BaseAdapter{

		private Context context;
		private AccidentType item;

		public GridAdapter(Context context,AccidentType item) {
			this.context = context;
			this.item = item;
		}

		@Override
		public int getCount() {
			return item!=null&&item.getLocalUrls()!=null?item.getLocalUrls().size():0;
		}

		@Override
		public String getItem(int position) {
			return item.getLocalUrls().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final String url = getItem(position);
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(FastAssistantActivity.this).inflate(R.layout.layout_img_item, null);
				viewHolder.imageView = convertView.findViewById(R.id.imageView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			OnClickListener click = new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.imageView:
						showChangeDialog(position);
						break;

					default:
						break;
					}
				}


			};
			viewHolder.imageView.setOnClickListener(click);
			ImageUtils.displayImg(context, viewHolder.imageView,url);

			return convertView;
		}

		private void showChangeDialog(final int position) {
			ObdDialog dialog = new ObdDialog(FastAssistantActivity.this).setTitle("温馨提示")
					.setMessage("您确认更换该照片吗？")
					.setPositiveButton(getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.setNegativeButton("更换", new ObdDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							item.getLocalUrls().remove(position);
							localFilename = item.getSvrUrls().get(position);
							item.getSvrUrls().remove(position);
							GridAdapter.this.notifyDataSetChanged();
							startCamera();
						}
					});
			if(!dialog.isShowing()) {
				dialog.show();
			}
		}

		class ViewHolder {
			public View imageView;
		}
	}
}
