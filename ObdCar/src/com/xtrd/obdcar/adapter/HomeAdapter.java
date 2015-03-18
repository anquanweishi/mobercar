package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.BaseTabActivity;
import com.xtrd.obdcar.HomeActivity;
import com.xtrd.obdcar.IllegalActivity;
import com.xtrd.obdcar.carcheck.CarCheckActivity;
import com.xtrd.obdcar.db.RuleBreakOpenHelper;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.CarInfo;
import com.xtrd.obdcar.entity.ViolationResult;
import com.xtrd.obdcar.illegal.IllegalResultActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class HomeAdapter extends BaseAdapter {

	/** 环境信息 **/
	private Context context;
	private CarInfo car;
	private int[] colors = new int[]{R.color.home_violet_color,R.color.home_red_color,
			R.color.home_green_color,R.color.home_blue_color,R.color.home_yellow_color};

	public HomeAdapter(Context context, CarInfo car) {
		this.context = context;
		this.car = car;
	}

	public int getCount() {
		return (car!=null&&0==car.getFuelAmount())?4:5;
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder view = null;
		if (convertView == null) {
			view = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_home_list_item, null);
			view.view = (View) convertView.findViewById(R.id.view);
			view.layout_left = (LinearLayout) convertView.findViewById(R.id.layout_left);
			view.textView1 = (TextView) convertView.findViewById(R.id.textView1);
			view.img_arrow = (ImageView) convertView.findViewById(R.id.img_arrow);
			view.textView2 = (TextView) convertView.findViewById(R.id.textView2);
			convertView.setTag(view);
		} else {
			view = (ViewHolder) convertView.getTag();
		}
		//		view.view.setBackgroundColor(colors[position]);
		view.view.setBackgroundColor(context.getResources().getColor(colors[position]));
		processBlue(view,car,position);
		return convertView;
	}



	private void processBlue(ViewHolder view,final CarInfo car,final int position) {
		if(car==null) {
			return;
		}
		String format = "";
		String value = "";
		SpannableString s = null;
		view.img_arrow.setVisibility(View.GONE);
		view.textView2.setVisibility(View.VISIBLE);
		view.textView1.setClickable(false);
		view.textView1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		if(4==getCount()) {
			switch (position) {
			case 0:
				if(0==car.getDistance()) {
					value = car.getCoolantTemperature()+"";
					format = String.format(context.getResources().getString(R.string.text_coolantTemperature),value);
					s = new SpannableString(format);
					s.setSpan(new ForegroundColorSpan(Color.parseColor("#114450")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					view.textView1.setText(s);
				}else {
					value = car.getDistance()+"";
					format = String.format(context.getResources().getString(R.string.text_distance),value);
					s = new SpannableString(format);
					s.setSpan(new ForegroundColorSpan(Color.parseColor("#114450")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					view.textView1.setText(s);
				}
			
				value = car.getFuelConsumption100()+"";
				format = String.format(2==car.getCarStatus()?context.getResources().getString(R.string.text_instant_fuelConsumption100):context.getResources().getString(R.string.text_fuelConsumption100),value);
				s = new SpannableString(format);
				s.setSpan(new ForegroundColorSpan(Color.parseColor("#114450")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				view.textView2.setText(s);
				break;
			case 1:
				value = car.getTroubleCodeNumber()==0?"正常":car.getTroubleCodeNumber()+"个故障";
				format = String.format(context.getResources().getString(R.string.text_troubleCodeNumber),value);
				s = new SpannableString(format);
				s.setSpan(new UnderlineSpan(), format.indexOf(value), format.indexOf(value)+(car.getTroubleCodeNumber()==0?value.length():(car.getTroubleCodeNumber()+"").length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				s.setSpan(new ForegroundColorSpan(car.getTroubleCodeNumber()==0?Color.parseColor("#114450"):Color.parseColor("#ff1900")),  format.indexOf(value), format.indexOf(value)+(car.getTroubleCodeNumber()==0?value.length():(car.getTroubleCodeNumber()+"").length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				view.textView1.setMovementMethod(LinkMovementMethod.getInstance());
				view.textView1.setText(s);
				view.textView1.setClickable(true);
				view.textView1.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(position==1) {
							Intent intent = new Intent(context,CarCheckActivity.class);
							intent.putExtra("troubleNum", car.getTroubleCodeNumber());
							context.startActivity(intent);
						}
					}
				});
				value = car.getBatteryRemain()+"";
				LogUtils.e("电瓶电压", "value " + value);
				if(!StringUtils.isNullOrEmpty(value)) {
					format = String.format(context.getResources().getString(R.string.text_battery_remain),value);
					s = new SpannableString(format);
					s.setSpan(new ForegroundColorSpan((11<car.getBatteryRemain()&&15>car.getBatteryRemain())?Color.parseColor("#114450"):Color.parseColor("#ff1900")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					view.textView2.setText(s);
				}
				break;
			case 2:
				value = car.getDrivingScore()+"";
				format = String.format(context.getResources().getString(R.string.text_drivingScore),value);
				s = new SpannableString(format);
				s.setSpan(new ForegroundColorSpan(car.getDrivingScore()<60?context.getResources().getColor(R.color.red):Color.parseColor("#114450")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				view.textView1.setText(s);
				value = car.getDrivingScoreRank()+"%";
				format = String.format(context.getResources().getString(R.string.text_drivingScoreRank),value);
				s = new SpannableString(format);
				s.setSpan(new ForegroundColorSpan(car.getDrivingScoreRank()<60?context.getResources().getColor(R.color.red):Color.parseColor("#114450")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				view.textView2.setText(s);
				break;
			case 3:
				view.img_arrow.setVisibility(View.VISIBLE);
				view.textView1.setClickable(true);
				if(!RuleBreakOpenHelper.getInstance(context).isDateExist(SettingLoader.getVehicleId(context),SettingLoader.getIllegalCity(context))&&
						StringUtils.isNullOrEmpty(SettingLoader.getQueryDate(context))) {
					view.textView1.setTextColor(Color.parseColor("#114450"));
					view.textView1.setText("违章查询");
					view.textView1.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if(3==position) {
								if(!RuleBreakOpenHelper.getInstance(context).isDateExist(SettingLoader.getVehicleId(context),SettingLoader.getIllegalCity(context))&&
										StringUtils.isNullOrEmpty(SettingLoader.getQueryDate(context))) {
									Intent intent = new Intent(context,IllegalActivity.class);
									intent.putExtra("from", true);
									context.startActivity(intent);
//									((BaseTabActivity)((HomeActivity)context).getParent()).setCurrentTab(3);
								}
							}
						}
					});
				}else {
					view.textView2.setVisibility(View.GONE);
					final ArrayList<ViolationResult> list = RuleBreakOpenHelper.getInstance(context).getResult(SettingLoader.getVehicleId(context),SettingLoader.getIllegalCity(context));
					processInfo(view.textView1,list);
					view.textView1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vio_location, 0, 0, 0);
					view.textView1.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if(3==position) {
								if(RuleBreakOpenHelper.getInstance(context).isDateExist(SettingLoader.getVehicleId(context),SettingLoader.getIllegalCity(context))&&
										!StringUtils.isNullOrEmpty(SettingLoader.getQueryDate(context))) {
									Intent intent = new Intent(context,IllegalResultActivity.class);
									intent.putExtra("list", list);
									intent.putExtra("area", SettingLoader.getIllegalCity(context));
									context.startActivity(intent);
								}else {
									Intent intent = new Intent(context,IllegalActivity.class);
									intent.putExtra("from", true);
									context.startActivity(intent);
//									((BaseTabActivity)((HomeActivity)context).getParent()).setCurrentTab(3);
								}
							}
						}
					});
				}
				break;
			default:
				break;
			}
		}else {
			switch (position) {
			case 0:
				if(0==car.getDistance()) {
					value = car.getCoolantTemperature()+"";
					format = String.format(context.getResources().getString(R.string.text_coolantTemperature),value);
					s = new SpannableString(format);
					s.setSpan(new ForegroundColorSpan(Color.parseColor("#114450")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					view.textView1.setText(s);
				}else {
					value = car.getDistance()+"";
					format = String.format(context.getResources().getString(R.string.text_distance),value);
					s = new SpannableString(format);
					s.setSpan(new ForegroundColorSpan(Color.parseColor("#114450")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					view.textView1.setText(s);
				}

				value = car.getFuelConsumption100()+"";
				format = String.format(2==car.getCarStatus()?context.getResources().getString(R.string.text_instant_fuelConsumption100):context.getResources().getString(R.string.text_fuelConsumption100),value);
				s = new SpannableString(format);
				s.setSpan(new ForegroundColorSpan(Color.parseColor("#114450")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				view.textView2.setText(s);
				break;
			case 1:
				value = car.getFuelAmount()+"";
				format = String.format(context.getResources().getString(R.string.text_fuelAmount),value);
				s = new SpannableString(format);
				s.setSpan(new ForegroundColorSpan(car.getFuelAmount()>10?Color.parseColor("#114450"):Color.parseColor("#ff1900")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				view.textView1.setText(s);
				value = car.getContinueDistance()+"";
				format = String.format(context.getResources().getString(R.string.text_continueDistance),value);
				s = new SpannableString(format);
				s.setSpan(new ForegroundColorSpan(Color.parseColor("#114450")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				view.textView2.setText(s);
				break;
			case 2:
				value = car.getTroubleCodeNumber()==0?"正常":car.getTroubleCodeNumber()+"个故障";
				format = String.format(context.getResources().getString(R.string.text_troubleCodeNumber),value);
				s = new SpannableString(format);
				s.setSpan(new UnderlineSpan(), format.indexOf(value), format.indexOf(value)+(car.getTroubleCodeNumber()==0?value.length():(car.getTroubleCodeNumber()+"").length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				s.setSpan(new ForegroundColorSpan(car.getTroubleCodeNumber()==0?Color.parseColor("#114450"):Color.parseColor("#ff1900")),  format.indexOf(value), format.indexOf(value)+(car.getTroubleCodeNumber()==0?value.length():(car.getTroubleCodeNumber()+"").length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				view.textView1.setMovementMethod(LinkMovementMethod.getInstance());
				view.textView1.setText(s);
				view.textView1.setClickable(true);
				view.textView1.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(position==2) {
							Intent intent = new Intent(context,CarCheckActivity.class);
							intent.putExtra("troubleNum", car.getTroubleCodeNumber());
							context.startActivity(intent);
						}
					}
				});
				value = car.getBatteryRemain()+"";
				LogUtils.e("电瓶电压", "value " + value);
				if(!StringUtils.isNullOrEmpty(value)) {
					format = String.format(context.getResources().getString(R.string.text_battery_remain),value);
					s = new SpannableString(format);
					s.setSpan(new ForegroundColorSpan((11<car.getBatteryRemain()&&15>car.getBatteryRemain())?Color.parseColor("#114450"):Color.parseColor("#ff1900")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					view.textView2.setText(s);
				}
				break;
			case 3:
				value = car.getDrivingScore()+"";
				format = String.format(context.getResources().getString(R.string.text_drivingScore),value);
				s = new SpannableString(format);
				s.setSpan(new ForegroundColorSpan(car.getDrivingScore()<60?context.getResources().getColor(R.color.red):Color.parseColor("#114450")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				view.textView1.setText(s);
				value = car.getDrivingScoreRank()+"%";
				format = String.format(context.getResources().getString(R.string.text_drivingScoreRank),value);
				s = new SpannableString(format);
				s.setSpan(new ForegroundColorSpan(car.getDrivingScoreRank()<60?context.getResources().getColor(R.color.red):Color.parseColor("#114450")), format.indexOf(value), format.indexOf(value)+value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				view.textView2.setText(s);
				break;
			case 4:
				view.img_arrow.setVisibility(View.VISIBLE);
				view.textView1.setClickable(true);
				if(!RuleBreakOpenHelper.getInstance(context).isDateExist(SettingLoader.getVehicleId(context),SettingLoader.getIllegalCity(context))&&
						StringUtils.isNullOrEmpty(SettingLoader.getQueryDate(context))) {
					view.textView1.setTextColor(Color.parseColor("#114450"));
					view.textView1.setText("违章查询");
					view.textView1.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if(4==position) {
								if(!RuleBreakOpenHelper.getInstance(context).isDateExist(SettingLoader.getVehicleId(context),SettingLoader.getIllegalCity(context))&&
										StringUtils.isNullOrEmpty(SettingLoader.getQueryDate(context))) {
//									Intent intent = new Intent(context,IllegalActivity.class);
//									intent.putExtra("from", true);
//									context.startActivity(intent);
									((BaseTabActivity)((HomeActivity)context).getParent()).setCurrentTab(3);
								}
							}
						}
					});
				}else {
					view.textView2.setVisibility(View.GONE);
					final ArrayList<ViolationResult> list = RuleBreakOpenHelper.getInstance(context).getResult(SettingLoader.getVehicleId(context),SettingLoader.getIllegalCity(context));
					processInfo(view.textView1,list);
					view.textView1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vio_location, 0, 0, 0);
					view.textView1.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if(4==position) {
								if(RuleBreakOpenHelper.getInstance(context).isDateExist(SettingLoader.getVehicleId(context),SettingLoader.getIllegalCity(context))&&
										!StringUtils.isNullOrEmpty(SettingLoader.getQueryDate(context))) {
									Intent intent = new Intent(context,IllegalResultActivity.class);
									intent.putExtra("list", list);
									intent.putExtra("area", SettingLoader.getIllegalCity(context));
									context.startActivity(intent);
								}else {
//									Intent intent = new Intent(context,IllegalActivity.class);
//									intent.putExtra("from", true);
//									context.startActivity(intent);
									((BaseTabActivity)((HomeActivity)context).getParent()).setCurrentTab(3);
								}
							}
						}
					});
				}
				
				break;

			default:
				break;
			}
		}
	}

	private void processInfo(TextView textView, ArrayList<ViolationResult> list) {
		if(list==null||list.size()==0) {
			textView.setText(SettingLoader.getIllegalCity(context) + "		无违章记录（截至日期" + SettingLoader.getQueryDate(context)+")");
		}else {
			int fen=0;
			for(ViolationResult result : list) {
				fen+=result.getFen();
			}
			String str = String.format(context.getResources().getString(R.string.text_home_vio_result), SettingLoader.getIllegalCity(context)+"  ",list.size(),fen,SettingLoader.getQueryDate(context));

			SpannableString s = new SpannableString(str);
			s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)), str.indexOf(String.valueOf(list.size())), str.indexOf(String.valueOf(list.size()))+String.valueOf(list.size()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)), str.indexOf(String.valueOf(fen)), str.indexOf(String.valueOf(fen))+String.valueOf(fen).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			textView.setText(s);
		}
	}

	class ViewHolder {
		public View view;
		public LinearLayout layout_left;
		public TextView textView1;
		public ImageView img_arrow;
		public TextView textView2;
	}

	public void setData(CarInfo car) {
		this.car = car;
	}

}
