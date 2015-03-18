package com.xtrd.obdcar.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.Trip;
import com.xtrd.obdcar.trip.DriveScoreActivity;
import com.xtrd.obdcar.trip.FuelActivity;
import com.xtrd.obdcar.trip.TripMapActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.LocationDecoder;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.ObdDialog;

public class TripAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Trip> list;

	public TripAdapter(Context context,ArrayList<Trip> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public Trip getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder view = null;
		if (convertView == null) {
			view = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_trip_item, null);
			view.view = (View) convertView.findViewById(R.id.view);
			view.img_running = (ImageView) convertView.findViewById(R.id.img_running);
			view.text_start_hour = (TextView) convertView.findViewById(R.id.text_start_hour);
			view.text_start_day = (TextView) convertView.findViewById(R.id.text_start_day);
			view.text_start_year = (TextView) convertView.findViewById(R.id.text_start_year);
			view.text_end_hour = (TextView) convertView.findViewById(R.id.text_end_hour);
			view.text_end_day = (TextView) convertView.findViewById(R.id.text_end_day);
			view.text_end_year = (TextView) convertView.findViewById(R.id.text_end_year);
			view.text_start_location = (TextView) convertView.findViewById(R.id.text_start_location);
			view.text_end_location = (TextView) convertView.findViewById(R.id.text_end_location);
			view.text_cost_oil = (TextView) convertView.findViewById(R.id.text_cost_oil);
			view.text_price_oil = (TextView) convertView.findViewById(R.id.text_price_oil);
			view.text_duration = (TextView) convertView.findViewById(R.id.text_duration);
			view.text_distance = (TextView) convertView.findViewById(R.id.text_distance);
			view.text_idle = (TextView) convertView.findViewById(R.id.text_idle);
			view.text_speed_top = (TextView) convertView.findViewById(R.id.text_speed_top);
			view.text_speed_avg = (TextView) convertView.findViewById(R.id.text_speed_avg);
			view.btn_trip = (LinearLayout) convertView.findViewById(R.id.btn_trip);
			view.btn_score = (LinearLayout) convertView.findViewById(R.id.btn_score);
			view.btn_fuel = (LinearLayout) convertView.findViewById(R.id.btn_fuel);
			convertView.setTag(view);
		} else {
			view = (ViewHolder) convertView.getTag();
		}

		final Trip trip = list.get(position);
		updateUI(view,trip);

		OnClickListener click = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = null;
				switch (v.getId()) {
				case R.id.btn_trip:
					if(trip.getStartGps()==null) {
						Utils.showToast(context, "车辆位置信息有误");
					}else {
						gotoMap(trip);
					}
					break;
				case R.id.btn_score:
					if(!SettingLoader.hasLogin(context)) {
						Utils.showToast(context, context.getResources().getString(R.string.tips_no_login));
						return;
					}
					if(!SettingLoader.hasBindBox(context)) {
						Utils.showToast(context, context.getResources().getString(R.string.tips_no_box));
						return;
					}
					intent = new Intent(context,DriveScoreActivity.class);
					intent.putExtra("startGps", trip.getStartGps());
					intent.putExtra("endGps", trip.getEndGps());
					intent.putExtra("tripId", trip.getTripId());
					intent.putExtra("duation", trip.getDuration());
					intent.putExtra("distance", trip.getDistance());
					intent.putExtra("drivescore", trip.getDrivingScore());
					context.startActivity(intent);
					break;
				case R.id.btn_fuel:
					if(!SettingLoader.hasLogin(context)) {
						Utils.showToast(context, context.getResources().getString(R.string.tips_no_login));
						return;
					}
					if(!SettingLoader.hasBindBox(context)) {
						Utils.showToast(context, context.getResources().getString(R.string.tips_no_box));
						return;
					}
					intent = new Intent(context,FuelActivity.class);
					intent.putExtra("startTime", trip.getStartGps().getGpsTime());
					intent.putExtra("endTime", trip.getEndGps().getGpsTime());
					intent.putExtra("distance", trip.getDistance());
					intent.putExtra("duration", trip.getDuration());
					intent.putExtra("fuelAmount", trip.getFuelConsumption100());
					intent.putExtra("idleDuration", trip.getIdleDuration());
					intent.putExtra("topSpeed", trip.getSpeedTop());
					intent.putExtra("avgSpeed", trip.getSpeedAvg());
					context.startActivity(intent);
					break;

				default:
					break;
				}
			}
		};
		view.btn_trip.setOnClickListener(click);
		view.btn_score.setOnClickListener(click);
		view.btn_fuel.setOnClickListener(click);

		return convertView;
	}


	protected void gotoMap(final Trip trip) {
		if(!NetUtils.isWifiNet(context)) {
			new ObdDialog(context).setTitle("温馨提示")
			.setMessage("您的手机当前网络环境不是wifi，确定查看地图吗？")
			.setPositiveButton(context.getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.setNegativeButton("确定", new ObdDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(context,TripMapActivity.class);
					intent.putExtra("displaySingle", true);
					intent.putExtra("tripId", trip.getTripId());
					intent.putExtra("carStatus", trip.getCarStatus());
					if(trip.getStartGps()!=null) {
						intent.putExtra("sLat", trip.getStartGps().getLatitude());
						intent.putExtra("sLon", trip.getStartGps().getLongitude());
						intent.putExtra("starttime", trip.getStartGps().getGpsTime());
					}
					if(trip.getEndGps()!=null) {
						intent.putExtra("eLat", trip.getEndGps().getLatitude());
						intent.putExtra("eLon", trip.getEndGps().getLongitude());
						intent.putExtra("endtime", trip.getEndGps().getGpsTime());
					}
					context.startActivity(intent);
				}
			}).show();
		}else {
			Intent intent = new Intent(context,TripMapActivity.class);
			intent.putExtra("displaySingle", true);
			intent.putExtra("tripId", trip.getTripId());
			intent.putExtra("carStatus", trip.getCarStatus());
			if(trip.getStartGps()!=null) {
				intent.putExtra("sLat", trip.getStartGps().getLatitude());
				intent.putExtra("sLon", trip.getStartGps().getLongitude());
				intent.putExtra("starttime", trip.getStartGps().getGpsTime());
			}
			if(trip.getEndGps()!=null) {
				intent.putExtra("eLat", trip.getEndGps().getLatitude());
				intent.putExtra("eLon", trip.getEndGps().getLongitude());
				intent.putExtra("endtime", trip.getEndGps().getGpsTime());
			}
			context.startActivity(intent);
		}
		
	}

	private void updateUI(final ViewHolder view,final Trip trip) {
		
		if(2==trip.getCarStatus()) {
			view.img_running.setVisibility(View.VISIBLE);
		}else {
			view.img_running.setVisibility(View.GONE);
		}
		
		processOilPriceAndCost(view.text_cost_oil, "耗油：",trip.getFuelAmount()+"升");
		processOilPriceAndCost(view.text_price_oil, "油费：",trip.getPrice()+"元");
		
		if(trip.getGpsEndTime()!=null) {
			Date date = TimeUtils.getTimeByStr(trip.getGpsEndTime());
			if(date!=null) {
				view.text_end_hour.setText(TimeUtils.formatDate2Hour(date));
				view.text_end_day.setText(TimeUtils.formatDate2Day(date));
				view.text_end_year.setText(TimeUtils.formatDate2Year(date));
			}else {
				view.text_end_hour.setText("");
				view.text_end_day.setText("");
				view.text_end_year.setText("");
			}
		}else {
			view.text_end_hour.setText("");
			view.text_end_day.setText("");
			view.text_end_year.setText("");
		}
		if(trip.getGpsStartTime()!=null) {
			Date start = TimeUtils.getTimeByStr(trip.getGpsStartTime());
			if(start!=null) {
				view.text_start_hour.setText(TimeUtils.formatDate2Hour(start));
				view.text_start_day.setText(TimeUtils.formatDate2Day(start));
				view.text_start_year.setText(TimeUtils.formatDate2Year(start));
			}else {
				view.text_start_hour.setText("");
				view.text_start_day.setText("");
				view.text_start_year.setText("");
			}
		}else {
			view.text_start_hour.setText("");
			view.text_start_day.setText("");
			view.text_start_year.setText("");
		}
		view.text_start_location.setText("");
		view.text_end_location.setText("");
		if(StringUtils.isNullOrEmpty(trip.getSlocation())) {
			if(trip.getStartGps()!=null) {
				LocationDecoder.decodeLocation(trip.getStartGps().getLatitude(), trip.getStartGps().getLongitude(),true,
						new LocationDecoder.LocationCallBack() {
					@Override
					public void callback(String str, AddressComponent addressComponent) {
						view.text_start_location.setText(str);
						trip.setSlocation(str);
					}
				});
			}
		}else {
			view.text_start_location.setText(trip.getSlocation());
		}
		
		if(StringUtils.isNullOrEmpty(trip.getElocation())) {
			if(trip.getEndGps()!=null) {
				LocationDecoder.decodeLocation(trip.getEndGps().getLatitude(), trip.getEndGps().getLongitude(),true,
						new LocationDecoder.LocationCallBack() {
					@Override
					public void callback(String str, AddressComponent addressComponent) {
						view.text_end_location.setText(str);
						trip.setElocation(str);
					}
				});
			}

		}else {
			view.text_end_location.setText(trip.getElocation());
		}
		
		int durationLength = TimeUtils.getTimeLength(trip.getDuration());
		processDuration(view.text_duration, TimeUtils.autoFormat(trip.getDuration()),durationLength);
		processInfo(view.text_distance, trip.getDistance()+"","公里");
		String idle = TimeUtils.autoFormat(trip.getIdleDuration());
		int idleLength = TimeUtils.getTimeLength(trip.getIdleDuration());
		processGreen(view.text_idle,"怠速：",idle,idleLength);
		processGreen(view.text_speed_top,context.getResources().getString(R.string.text_top_speed),trip.getSpeedTop()+"");
		processGreen(view.text_speed_avg,context.getResources().getString(R.string.text_avg_speed),trip.getSpeedAvg()+"");
		((TextView)view.btn_score.getChildAt(0)).setText((int)trip.getDrivingScore()+"");
		if(60<trip.getDrivingScore()) {
			((TextView)view.btn_score.getChildAt(0)).setBackgroundResource(R.drawable.ic_score_good_bg);
		}else {
			((TextView)view.btn_score.getChildAt(0)).setBackgroundResource(R.drawable.ic_score_bad_bg);
		}
		((TextView)view.btn_fuel.getChildAt(0)).setText((int)trip.getFuelConsumption100()+"");
	}

	private void processInfo(TextView view,String date, String res) {
		SpannableString s = new SpannableString(date+res);
		s.setSpan(new ForegroundColorSpan(Color.parseColor("#606060")), 0, date.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.2f), 0, date.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(s);
	}
	
	private void processOilPriceAndCost(TextView view,String perfix, String end) {
		SpannableString s = new SpannableString(perfix+end);
		s.setSpan(new ForegroundColorSpan(Color.parseColor("#606060")), perfix.length(), (perfix+end).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(s);
	}

	
	private void processDuration(TextView view,String str, int length) {
		SpannableString s = new SpannableString(str);
		s.setSpan(new ForegroundColorSpan(Color.parseColor("#606060")), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.2f), 0, length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(s);
	}

	private void processGreen(TextView view,String res, String data) {
		String str = String.format(res, data);
		SpannableString s = new SpannableString(str);
		s.setSpan(new ForegroundColorSpan(Color.parseColor("#3c9276")), str.indexOf(data), str.indexOf(data)+data.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.2f), str.indexOf(data), str.indexOf(data)+data.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(s);
	}

	/**
	 * 处理怠速
	 * @param view
	 * @param res
	 * @param svrData
	 * @param svrDataLength
	 */
	private void processGreen(TextView view,String res, String svrData, int svrDataLength) {
		String str = res+svrData;
		SpannableString s = new SpannableString(str);
		s.setSpan(new ForegroundColorSpan(Color.parseColor("#3c9276")), str.indexOf(svrData), str.indexOf(svrData)+svrDataLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.2f), str.indexOf(svrData), str.indexOf(svrData)+svrDataLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(s);
	}




	class ViewHolder {
		
		View view;
		ImageView img_running;
		TextView text_start_hour;
		TextView text_start_day;
		TextView text_start_year;
		TextView text_end_hour;
		TextView text_end_day;
		TextView text_end_year;
		TextView text_start_location;
		TextView text_end_location;
		TextView text_price_oil;
		TextView text_cost_oil;
		TextView text_distance;
		TextView text_duration;
		TextView text_idle;
		TextView text_speed_top;
		TextView text_speed_avg;
		LinearLayout btn_trip;
		LinearLayout btn_score;
		LinearLayout btn_fuel;
	}


}
