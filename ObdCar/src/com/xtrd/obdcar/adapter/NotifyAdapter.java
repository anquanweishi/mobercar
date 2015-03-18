package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.NotifyMessage;
import com.xtrd.obdcar.maintain.MainTainAddActivity;
import com.xtrd.obdcar.merchant.MerchantActivity;
import com.xtrd.obdcar.merchant.MerchantDetailActivity;
import com.xtrd.obdcar.oil.OilReportActivity;
import com.xtrd.obdcar.self.NearGasStationActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.vc.ProfessorQAActivity;
import com.xtrd.obdcar.vc.TroubleCodeActivity;

public class NotifyAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<NotifyMessage> list;

	public NotifyAdapter(Context context, ArrayList<NotifyMessage> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public NotifyMessage getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final NotifyMessage info = getItem(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.layout_notify_item, null);
			viewHolder.layout_car_list_item = (LinearLayout) convertView
					.findViewById(R.id.layout_notify_item);
			viewHolder.text_title = (TextView) convertView
					.findViewById(R.id.text_title);
			viewHolder.text_content = (TextView) convertView
					.findViewById(R.id.text_content);
			viewHolder.text_time = (TextView) convertView
					.findViewById(R.id.text_time);
			viewHolder.btn_one = (Button) convertView
					.findViewById(R.id.btn_one);
			viewHolder.btn_two = (Button) convertView
					.findViewById(R.id.btn_two);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		OnClickListener click = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_one:
					Intent intent = null;
					if(1 == info.getType()) {
						intent = new Intent(context,TroubleCodeActivity.class);
						intent.putExtra(ParamsKey.Code, info.getKey());
					}else if (3 == info.getType()) {
						intent = new Intent(context,MerchantDetailActivity.class);
						intent.putExtra(ParamsKey.MERCHANTID, !StringUtils.isNullOrEmpty(info.getKey())?Integer.parseInt(info.getKey()):0);
					} else if (4 == info.getType()) {
						intent = new Intent(context, MerchantActivity.class);
					} else if (5 == info.getType()) {
						intent = new Intent(context,NearGasStationActivity.class);
					} else if (8 == info.getType()) {
						intent = new Intent(context, OilReportActivity.class);
					}else if(41 == info.getType()) {
						intent = new Intent(context,ProfessorQAActivity.class);
					}
					context.startActivity(intent);
					break;
				case R.id.btn_two:
					if (4 == info.getType()) {
						intent = new Intent(context,MainTainAddActivity.class);
						context.startActivity(intent);
					}
					break;

				default:
					break;
				}
			}
		};
		viewHolder.btn_one.setOnClickListener(click);
		viewHolder.btn_two.setOnClickListener(click);

		updateView(viewHolder, info);
		return convertView;
	}

	private void updateView(final ViewHolder viewHolder,
			final NotifyMessage info) {
		if (info != null) {
			viewHolder.text_title.setText(info.getTitle());
			viewHolder.text_content.setText(info.getContent());
			viewHolder.text_time.setText(info.getTime());
			// 1=OBD故障码提醒，2=车辆报警提醒，3=4S促销提醒，4=保养提醒，5=加油提醒，6=援助，7=防盗报警 8=加油登记 41提问专家
			if(1 == info.getType()) {
				viewHolder.btn_one.setVisibility(View.VISIBLE);
				viewHolder.btn_one.setText("故障详情");
				viewHolder.btn_two.setVisibility(View.GONE);
			}else if (3 == info.getType()) {
				viewHolder.btn_one.setVisibility(View.VISIBLE);
				viewHolder.btn_one.setText("查看");
				viewHolder.btn_two.setVisibility(View.GONE);
			} else if (4 == info.getType()) {
				viewHolder.btn_one.setVisibility(View.VISIBLE);
				viewHolder.btn_two.setVisibility(View.VISIBLE);
				viewHolder.btn_one.setText("预约保养");
				viewHolder.btn_two.setText("已经保养");
			} else if (5 == info.getType()) {
				viewHolder.btn_one.setVisibility(View.VISIBLE);
				viewHolder.btn_one.setText("加油喽");
				viewHolder.btn_two.setVisibility(View.GONE);
			} else if (8 == info.getType()) {
				viewHolder.btn_one.setVisibility(View.VISIBLE);
				viewHolder.btn_one.setText("上报油价");
				viewHolder.btn_two.setVisibility(View.GONE);
			} else if(41 == info.getType()){
				viewHolder.btn_one.setVisibility(View.VISIBLE);
				viewHolder.btn_one.setText("提问专家");
				viewHolder.btn_two.setVisibility(View.GONE);
			}else {
				viewHolder.btn_one.setVisibility(View.GONE);
				viewHolder.btn_two.setVisibility(View.GONE);
			}
		}
	}

	final static class ViewHolder {

		public LinearLayout layout_car_list_item;
		public TextView text_title;
		public TextView text_content;
		public TextView text_time;
		public Button btn_one;
		public Button btn_two;
	}

	// 使用快捷分享完成分享（请务必仔细阅读位于SDK解压目录下Docs文件夹中OnekeyShare类的JavaDoc）
	/**
	 * ShareSDK集成方法有两种</br>
	 * 1、第一种是引用方式，例如引用onekeyshare项目，onekeyshare项目再引用mainlibs库</br>
	 * 2、第二种是把onekeyshare和mainlibs集成到项目中，本例子就是用第二种方式</br> 请看“ShareSDK
	 * 使用说明文档”，SDK下载目录中 </br> 或者看网络集成文档
	 * http://wiki.mob.com/Android_%E5%BF%AB%E9%
	 * 80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
	 * 3、混淆时，把sample或者本例子的混淆代码copy过去，在proguard-project.txt文件中
	 * 
	 * 
	 * 平台配置信息有三种方式： 1、在我们后台配置各个微博平台的key
	 * 2、在代码中配置各个微博平台的key，http://mob.com/androidDoc
	 * /cn/sharesdk/framework/ShareSDK.html
	 * 3、在配置文件中配置，本例子里面的assets/ShareSDK.conf,
	 */
	private void showShare(boolean silent, String platform, boolean captureView) {
		final OnekeyShare oks = new OnekeyShare();

		oks.setNotification(R.drawable.ic_launcher, context.getResources()
				.getString(R.string.app_name));
		// oks.setAddress("12345678901");
		oks.setTitle("车保姆应用分享");
		oks.setTitleUrl("http://sevoh.com/");
		oks.setText("车保姆servant of vehice 缩写为：sevoh，是由矽通瑞达科技（北京）有限责任公司研发的一款对于车联网的一项专属成果。主要针对行车安全及车况检测的一款手机软件！将车的每一行程记录，通过手机查询给出每次行程的数据分析与统计！");
		View view = new View(context);
		view.setBackgroundColor(Color.TRANSPARENT);

		if (captureView) {
			oks.setViewToShare(view);
		} else {
			oks.setImageUrl("http://sevoh.com/images/sevoh-android2.png");
		}
		oks.setUrl("http://sevoh.com/");
		oks.setComment("分享");
		oks.setSite("车保姆");
		oks.setSiteUrl("http://sevoh.com/");
		oks.setVenueName("sevoh");
		oks.setVenueDescription("This is a beautiful app!");
		oks.setSilent(silent);
		oks.setShareFromQQAuthSupport(true);
		oks.setTheme(OnekeyShareTheme.CLASSIC);

		if (platform != null) {
			oks.setPlatform(platform);
		}

		// 令编辑页面显示为Dialog模式
		oks.setDialogMode();

		// 在自动授权时可以禁用SSO方式
		// oks.disableSSOWhenAuthorize();

		// 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
		// oks.setCallback(new OneKeyShareCallback());

		// 去自定义不同平台的字段内容
		// oks.setShareContentCustomizeCallback(new ShareContentCustomize());

		// 去除注释，演示在九宫格设置自定义的图标
		// Bitmap logo = BitmapFactory.decodeResource(menu.getResources(),
		// R.drawable.ic_launcher);
		// String label = menu.getResources().getString(R.string.app_name);
		// OnClickListener listener = new OnClickListener() {
		// public void onClick(View v) {
		// String text = "Customer Logo -- ShareSDK " +
		// ShareSDK.getSDKVersionName();
		// Toast.makeText(menu.getContext(), text, Toast.LENGTH_SHORT).show();
		// oks.finish();
		// }
		// };
		// oks.setCustomerLogo(logo, label, listener);

		// 去除注释，则快捷分享九宫格中将隐藏新浪微博和腾讯微博
		// oks.addHiddenPlatform(SinaWeibo.NAME);
		// oks.addHiddenPlatform(TencentWeibo.NAME);

		// 为EditPage设置一个背景的View
		oks.setEditPageBackground(view);
		oks.show(context);
	}

}
