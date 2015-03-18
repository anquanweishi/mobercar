package com.xtrd.obdcar.view;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.entity.Weather;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class WeatherView extends LinearLayout {

	private Weather weather;
	private FinalBitmap fb;
	private int count = 3;//未来三天天气情况

	public WeatherView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public WeatherView(Context context, Weather weather) {
		super(context);
		this.weather = weather;
		initView(context);
	}

	private void initView(Context context) {
		
		if(weather==null) {
			return;
		}
		
		fb = FinalBitmap.create(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		params.gravity = Gravity.CENTER;
		params.setMargins(Utils.dipToPixels(context, 5), 0, Utils.dipToPixels(context, 5), 0);
	
		String[] imgs = new String[count];
		if(!StringUtils.isNullOrEmpty(weather.getDayPictureUrl())) {
			String[] split = weather.getDayPictureUrl().split(",");
			for(int i=0;i<split.length;i++) {
				imgs[i] = split[i];
			}
		}
		String[] temps = new String[3];
		if(!StringUtils.isNullOrEmpty(weather.getTemperature())) {
			String[] split = weather.getTemperature().split(",");
			for(int i=0;i<split.length;i++) {
				temps[i] = split[i];
			}
		}
		/*String[] weathers = new String[3];
		if(!StringUtils.isNullOrEmpty(weather.getWeather())) {
			String[] split = weather.getWeather().split(",");
			for(int i=0;i<split.length;i++) {
				weathers[i] = split[i];
			}
		}*/
		
		String[] weathers = new String[]{"今天","明天","后天"};
		
		for(int i=0;i<count;i++) {
			LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_weather_item, null);
			addView(view,params);
			updateView(view,imgs[i],temps[i],weathers[i]);
		}
	}

	private void updateView(View view, String img, String temp, String weather) {
		ImageView img_weather = (ImageView)view.findViewById(R.id.img_weather);
		TextView text_title = (TextView)view.findViewById(R.id.text_title);
		TextView text_desc = (TextView)view.findViewById(R.id.text_desc);
		fb.display(img_weather, img);
		text_title.setText(weather);
		text_desc.setText(temp);
	}

}
