package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.merchant.MerchantDetailActivity;

public class GuidePageAdapter extends PagerAdapter{
	private FinalBitmap fb; 
	private JSONArray jsonArray;
	private Context context;
	private ArrayList<View> pageViews;

	public GuidePageAdapter(Context context,JSONArray jsonArray,ArrayList<View> pageViews) {
		fb = FinalBitmap.create(context);
		this.context = context;
		this.pageViews = pageViews;
		this.jsonArray = jsonArray;
	}
	@Override
	public int getCount() {
		return pageViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(pageViews.get(arg1));
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(pageViews.get(arg1));
		try {
			JSONObject json = jsonArray.getJSONObject(arg1);
			fb.display(pageViews.get(arg1), json.getString("imgUrl"));
			final int id = json.getInt("id");
			pageViews.get(arg1).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,MerchantDetailActivity.class);
					intent.putExtra(ParamsKey.MERCHANTID, id);
					context.startActivity(intent);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return pageViews.get(arg1);
	}


}

