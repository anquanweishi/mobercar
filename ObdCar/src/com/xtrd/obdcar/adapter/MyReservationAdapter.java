package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.Reservation;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.MyGridView;
import com.xtrd.obdcar.view.ObdDialog;

public class MyReservationAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Reservation> list;
	private FinalBitmap fb;
	private MainTainAdapter adapter;

	public MyReservationAdapter(Context context,ArrayList<Reservation> list) {
		this.context = context;
		this.list = list;
		fb = FinalBitmap.create(context);
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public Reservation getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final Reservation info = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_reservation_item, null);
			viewHolder.layout_reservation_item = (LinearLayout) convertView.findViewById(R.id.layout_reservation_item);
			viewHolder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
			viewHolder.text_plate = (TextView) convertView.findViewById(R.id.text_plate);
			viewHolder.text_time = (TextView) convertView.findViewById(R.id.text_time);
			viewHolder.gridView = (MyGridView) convertView.findViewById(R.id.gridView);
			viewHolder.layout_wait_order = (LinearLayout) convertView.findViewById(R.id.layout_wait_order);
			viewHolder.btn_refresh = (ImageButton) convertView.findViewById(R.id.btn_refresh);
			viewHolder.btn_cancel = (ImageButton) convertView.findViewById(R.id.btn_cancel);
			viewHolder.btn_confirm = (ImageButton) convertView.findViewById(R.id.btn_confirm);
			viewHolder.layout_suc_order = (LinearLayout) convertView.findViewById(R.id.layout_suc_order);
			viewHolder.text_order_result = (TextView) convertView.findViewById(R.id.text_order_result);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		updateUI(viewHolder,info);

		OnClickListener click = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_refresh:

					break;
				case R.id.btn_cancel:
					showCancelDialog(info);
					break;


				default:
					break;
				}
			}
		};
		viewHolder.btn_cancel.setOnClickListener(click);

		return convertView;
	}

	protected void showCancelDialog(final Reservation info) {
		new ObdDialog(context).setTitle("取消预约")
		.setMessage("您确定取消本次预约吗？")
		.setPositiveButton(context.getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		})
		.setNegativeButton(context.getResources().getString(R.string.btn_confirm), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				cancelOrder(info);
			}
		})
		.show();
	}

	protected void cancelOrder(final Reservation info) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID, info.getOrderId());
		NetRequest.requestUrl(context,
				ApiConfig.getRequestUrl(ApiConfig.Cancel_Order_Url), params,
				new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if (!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if (json.has("status")) {
							int status = json.getInt("status");
							if (1 == status) {
								Utils.showToast(context, "取消成功");
								info.setStatus(2);
								MyReservationAdapter.this.notifyDataSetChanged();
							} else {
								if(json.has("message")) {
									String msg = json.getString("message");
									Utils.showToast(context, msg);
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

	private void updateUI(ViewHolder viewHolder, Reservation info) {
		if(info!=null) {
			fb.display(viewHolder.img_icon, info.getIcon());
			viewHolder.text_plate.setText(info.getPlateNumber());
			viewHolder.text_time.setText(info.getTime());
			processResult(viewHolder,info);

			if(viewHolder.gridView.getAdapter()==null) {
				adapter = new MainTainAdapter(context,info.getList());
				viewHolder.gridView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
		}
	}

	private void processResult(ViewHolder viewHolder,final Reservation info) {
		SpannableString s = null;
		if(0==info.getStatus()) {//发出申请
			viewHolder.layout_wait_order.setVisibility(View.VISIBLE);
			((TextView)viewHolder.layout_wait_order.getChildAt(1)).setText(String.format(context.getResources().getString(R.string.text_wait_order), info.getName()));
			viewHolder.layout_suc_order.setVisibility(View.GONE);
			if((System.currentTimeMillis()-TimeUtils.getTimeStampByStr(info.getTime()))>=120*10000) {
				viewHolder.btn_cancel.setVisibility(View.GONE);
			}else {
				viewHolder.btn_cancel.setVisibility(View.VISIBLE);
			}
		}else if(1==info.getStatus()){//成功预约
			viewHolder.layout_wait_order.setVisibility(View.GONE);
			viewHolder.layout_suc_order.setVisibility(View.VISIBLE);
			String clickStr = "联系商家："+info.getPhone();
			String str = String.format(context.getResources().getString(R.string.text_suc_order), info.getName(),info.getLastTime(),clickStr);

			s = new SpannableString(str);
			s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.top_bar_color)), str.indexOf(String.valueOf(clickStr)), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			s.setSpan(new ClickableSpan() {

				@Override
				public void onClick(View widget) {
					Utils.makePhone(context, info.getPhone());
				}
			},str.indexOf(String.valueOf(clickStr)), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			viewHolder.text_order_result.setMovementMethod(LinkMovementMethod.getInstance());
			viewHolder.text_order_result.setText(s);
		}else if(2==info.getStatus()){//取消预约
			viewHolder.layout_wait_order.setVisibility(View.GONE);
			viewHolder.layout_suc_order.setVisibility(View.VISIBLE);
			viewHolder.btn_confirm.setVisibility(View.GONE);
			viewHolder.text_order_result.setText(String.format(context.getResources().getString(R.string.text_cancel_order,info.getName())));
		}else if(3==info.getStatus()){//保养成功
			viewHolder.layout_wait_order.setVisibility(View.GONE);
			viewHolder.layout_suc_order.setVisibility(View.VISIBLE);
			viewHolder.text_order_result.setText(String.format(context.getResources().getString(R.string.text_finish_order),info.getLastTime(), info.getName()));
		}
	}

	final static class ViewHolder {

		public ImageButton btn_confirm;
		public LinearLayout layout_reservation_item;
		public ImageView img_icon;
		public TextView text_plate;
		public TextView text_time;
		public MyGridView gridView;
		public LinearLayout layout_wait_order;
		public ImageButton btn_refresh;
		public ImageButton btn_cancel;
		public LinearLayout layout_suc_order;
		public TextView text_order_result;

	}
}
