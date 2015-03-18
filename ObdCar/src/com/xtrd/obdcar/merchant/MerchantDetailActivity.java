package com.xtrd.obdcar.merchant;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.PrivilegeAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.Merchant;
import com.xtrd.obdcar.nearby.PoiLineActivity;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.reservation.ReservationActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.MMAlert;
import com.xtrd.obdcar.view.MyListView;

public class MerchantDetailActivity extends BaseActivity {
	private ImageView img_icon;
	private TextView text_name;
	private RatingBar ratingbar;
	private TextView text_fen;
	private TextView text_address;
	private TextView text_gold;
	private TextView text_auth;
	private LinearLayout layout_phone;
	private LinearLayout layout_map;
	private LinearLayout layout_comment;
	private ImageView btn_select;
	private LinearLayout layout_items;
	private TextView text_introduce;
	private LinearLayout layout_imgs;


	private Merchant merchant = new Merchant();
	private MyListView listView;
	private LinearLayout layout_comment_up,layout_comment_down;


	public MerchantDetailActivity() {
		layout_id = R.layout.activity_merchant_detail;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_merchant_detail, R.string.btn_order,0);
		initView();
		regClick();

		getMerchantDetail();
	}


	private void initView() {
		img_icon = (ImageView)findViewById(R.id.img_icon);
		text_name = (TextView)findViewById(R.id.text_name);
		ratingbar = (RatingBar)findViewById(R.id.ratingbar);
		text_fen = (TextView)findViewById(R.id.text_fen);
		text_address = (TextView)findViewById(R.id.text_address);
		text_gold = (TextView)findViewById(R.id.text_gold);
		text_auth = (TextView)findViewById(R.id.text_auth);

		listView = (MyListView)findViewById(R.id.listView);

		layout_phone = (LinearLayout)findViewById(R.id.layout_phone);
		layout_map = (LinearLayout)findViewById(R.id.layout_map);
		layout_comment = (LinearLayout)findViewById(R.id.layout_comment);

		btn_select = (ImageView)findViewById(R.id.btn_select);
		layout_items = (LinearLayout)findViewById(R.id.layout_items);
		text_introduce = (TextView)findViewById(R.id.text_introduce);
		layout_imgs = (LinearLayout)findViewById(R.id.layout_imgs);

		//
		layout_comment_up = (LinearLayout)findViewById(R.id.layout_comment_up);
		layout_comment_down = (LinearLayout)findViewById(R.id.layout_comment_down);
	}

	private void regClick() {
		layout_phone.setOnClickListener(this);
		layout_map.setOnClickListener(this);
		layout_comment.setOnClickListener(this);
		layout_comment_up.setOnClickListener(this);
		layout_comment_down.setOnClickListener(this);
		btn_select.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			if(SettingLoader.hasLogin(this)) {
				Intent intent = new Intent(this,ReservationActivity.class);
				intent.putExtra(ParamsKey.MERCHANTID, merchant.getId());
				intent.putExtra("merchantName", merchant.getName());
				startActivity(intent);
			}else {
				Utils.showToast(this, R.string.tips_no_login);
			}
			
			break;
		case R.id.layout_phone:
			showPhoneDialog();
			break;
		case R.id.layout_map:
			Intent map = new Intent(this,PoiLineActivity.class);
			map.putExtra("endLat", merchant.getLatitude());
			map.putExtra("endLng", merchant.getLongitude());
			map.putExtra("endTitle", merchant.getName());
			map.putExtra("convert", false);
			startActivity(map);

			break;
		case R.id.layout_comment:
			Intent comment = new Intent(this,CommentActivity.class);
			comment.putExtra(ParamsKey.MERCHANTID, merchant.getId());
			startActivity(comment);
			break;

		case R.id.layout_comment_up:
			if(SettingLoader.hasLogin(this)) {
				Intent up = new Intent(this,ShopEvaluateActivity.class);
				up.putExtra("merchant", merchant.getName());
				up.putExtra(ParamsKey.MERCHANTID, merchant.getId());
				up.putExtra("up", 1);
				startActivity(up);
			}else {
				Utils.showToast(this, R.string.tips_no_login);
			}

			break;
		case R.id.layout_comment_down:
			if(SettingLoader.hasLogin(this)) {
				Intent down = new Intent(this,ShopEvaluateActivity.class);
				down.putExtra("merchant", merchant.getName());
				down.putExtra(ParamsKey.MERCHANTID, merchant.getId());
				down.putExtra("up", 0);
				startActivity(down);
			}else {
				Utils.showToast(this, R.string.tips_no_login);
			}
		
			break;
		case R.id.btn_select:
			if(SettingLoader.hasLogin(this)) {
				storeShop(1==merchant.getIsFav()?"0":"1");
			}else {
				Utils.showToast(this, R.string.tips_no_login);
			}
			break;
		default:
			break;
		}
	}

	private void showPhoneDialog() {
		if(!StringUtils.isNullOrEmpty(merchant.getPhone())) {
			final String[] phones = merchant.getPhone().split(",");
			MMAlert.showAlert(this, "联系商家", phones, null, new MMAlert.OnAlertSelectId() {

				@Override
				public void onClick(int whichButton) {
					if(whichButton<phones.length) {
						Utils.showPhoneTips(MerchantDetailActivity.this, phones[whichButton]);
					}
				}
			});
		}
	}

	private void getMerchantDetail() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID, getIntent().getIntExtra(ParamsKey.MERCHANTID, 0)+"");
		NetRequest.requestUrl(this,
				ApiConfig.getRequestUrl(ApiConfig.Merchant_Detail_Url), params,
				new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if (!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if (json.has("status")) {
							int status = json.getInt("status");
							if (1 == status) {
								if (json.has("result")) {
									json = json.getJSONObject("result");
									merchant.parser(json);
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
			}
		});
	}

	private void updateFav(){
		if(1==merchant.getIsFav()) {
			btn_select.setImageResource(R.drawable.ic_sel);
		}else {
			btn_select.setImageResource(R.drawable.ic_unsel);
		}
	}

	protected void updateUI() {
		FinalBitmap fb = FinalBitmap.create(this);
		fb.display(img_icon, merchant.getImgUrl());
		text_name.setText(merchant.getName());
		text_address.setText(merchant.getAddress());
		ratingbar.setRating(merchant.getScore());
		text_fen.setText(merchant.getScore()+"分");

		if(1==merchant.getGold()) {
			text_gold.setVisibility(View.VISIBLE);
		}else {
			text_gold.setVisibility(View.GONE);
		}
		if(1==merchant.getReal()) {
			text_auth.setVisibility(View.VISIBLE);
		}else {
			text_auth.setVisibility(View.GONE);
		}

		updateFav();

		PrivilegeAdapter adapter = new PrivilegeAdapter(this, merchant.getList());
		listView.setAdapter(adapter);

		processItems(merchant.getItems());
		text_introduce.setText(merchant.getDescription());
		processImgs(merchant.getImgs());

	}

	private void processItems(ArrayList<String> items) {
		if(items==null||items.size()==0) {
			return;
		}
		layout_items.setVisibility(View.VISIBLE);
		int line = items.size()%3==0?items.size()/3:items.size()/3+1;

		LinearLayout.LayoutParams cparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((Utils.getScreenWidth(this)-Utils.dipToPixels(this, 20))/3,LayoutParams.WRAP_CONTENT);
		//		params.weight = 1;
		LinearLayout child = null;
		TextView textView = null;
		for(int i=0;i<line;i++) {
			child = new LinearLayout(this);
			child.setOrientation(LinearLayout.HORIZONTAL);
			for(int j=0;j<3;j++) {
				if((j+i*3)<items.size()) {
					textView = new TextView(this);
					textView.setText(items.get(j+i*3));
					textView.setTextColor(getResources().getColor(R.color.white));
					textView.setCompoundDrawablePadding(Utils.dipToPixels(this, 10));
					textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_white_solid, 0, 0, 0);
					child.addView(textView,params);
				}else {
					textView = new TextView(this);
					child.addView(textView,params);
				}
			}
			layout_items.addView(child,cparams);
		}
	}

	/**
	 * 处理图片
	 * @param imgs
	 */
	private void processImgs(ArrayList<String> imgs) {
		if(imgs==null||imgs.size()==0) {
			return;
		}
		FinalBitmap fb = FinalBitmap.create(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		params.setMargins(0, Utils.dipToPixels(this, 10), 0, Utils.dipToPixels(this, 10));
		ImageView imageView = null;
		for(int i=0;i<imgs.size();i++) {
			imageView = new ImageView(this);
			fb.display(imageView, imgs.get(i));
			layout_imgs.addView(imageView,params);
		}
	}

	/**
	 * 收藏
	 * @param type
	 */
	private void storeShop(final String type) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.MERCHANTID, getIntent().getIntExtra(ParamsKey.MERCHANTID, 0)+"");
		params.put(ParamsKey.TYPE, type);
		NetRequest.requestUrl(this,
				ApiConfig.getRequestUrl(ApiConfig.Store_Merchant_Url), params,
				new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if (!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if (json.has("status")) {
							int status = json.getInt("status");
							if (1 == status) {
								Utils.showToast(MerchantDetailActivity.this, "1".equals(type)?"收藏成功":"取消收藏成功");
								merchant.setIsFav(1==merchant.getIsFav()?0:1);
							} else {
								Utils.showToast(MerchantDetailActivity.this, "1".equals(type)?"收藏失败":"取消收藏失败");
							}
						}
						updateFav();
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


}
