package com.xtrd.obdcar.obdservice;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

public class PieChartView {
	private static Context mContext;
	private static int[] ones = new int[]{Color.parseColor("#1fa6f8"),Color.parseColor("#00cf9b"),Color.parseColor("#0067d0"),Color.parseColor("#0b86ee")};
	private static int[] twos = new int[]{Color.parseColor("#00badb"),Color.parseColor("#ce7f00"),Color.parseColor("#241ff8"),Color.parseColor("#8800ce"),Color.parseColor("#ed3b0b")};
	private static int[] threes = new int[]{Color.parseColor("#0067d0"),Color.parseColor("#00cf9b"),Color.parseColor("#1fa6f8")};

	/**
	 * 饼状图
	 * @param context
	 * @param units 
	 * @param list
	 * @param currentTabIndex 
	 * @return
	 */
	public static GraphicalView getDistancePieView(Context context,double[] values, String[] units) {
		//把图表变成 View的方法， 
		mContext = context;
		CategorySeries series = new CategorySeries("总里程（%）");
		int[] colors = null;
		try {
			if(values!=null) {
				colors = new int[values.length];
				for(int i=0;i<values.length;i++) {
					series.add(units[i], values[i]);
					colors[i] = ones[i];
				}
				DefaultRenderer renderer=buildDistanceRenderer(colors);
				
				return ChartFactory.getPieChartView(context,series, renderer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
//		view.setBackgroundResource(R.drawable.item_bg);
		return null;
	}


	private static DefaultRenderer buildDistanceRenderer(int[] colors) {
		DefaultRenderer renderer = new DefaultRenderer();

		renderer.setLegendTextSize(mContext.getResources().getDimension(R.dimen.report_text_size));//设置左下角表注的文字大小
		//renderer.setZoomButtonsVisible(true);//设置显示放大缩小按钮  
		renderer.setZoomEnabled(false);//设置不允许放大缩小.  
		renderer.setChartTitleTextSize(mContext.getResources().getDimension(R.dimen.report_text_size));//设置图表标题的文字大小
		renderer.setChartTitle("总里程（%）");//设置图表的标题  默认是居中顶部显示
		renderer.setLabelsTextSize(mContext.getResources().getDimension(R.dimen.report_label_size));//饼图上标记文字的字体大小
		//renderer.setLabelsColor(Color.WHITE);//饼图上标记文字的颜色
		renderer.setPanEnabled(false);//设置是否可以平移
		renderer.setLabelsColor(mContext.getResources().getColor(R.color.top_bar_color));
		renderer.setDisplayValues(true);//是否显示值
		//不显示底部说明
		renderer.setShowLegend(false);
		renderer.setClickEnabled(true);//设置是否可以被点击
		renderer.setMargins(new int[] { Utils.dipToPixels(mContext, 50),  Utils.dipToPixels(mContext, 40), Utils.dipToPixels(mContext, 40), Utils.dipToPixels(mContext, 40) });
		//margins - an array containing the margin size values, in this order: top, left, bottom, right
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}
	/**
	 * 饼状图
	 * @param context
	 * @param units 
	 * @param list
	 * @param currentTabIndex 
	 * @return
	 */
	public static GraphicalView getTimePieView(Context context,double[] values, String[] units) {
		//把图表变成 View的方法， 
		mContext = context;
		CategorySeries series = new CategorySeries("总时间（%）");
		int[] colors = null;
		try {
			if(values!=null) {
				colors = new int[values.length];
				for(int i=0;i<values.length;i++) {
					series.add(units[i], values[i]);
					colors[i] = twos[i];
				}
				DefaultRenderer renderer=buildTimeRenderer(colors);
				return ChartFactory.getPieChartView(context,series, renderer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}


	private static DefaultRenderer buildTimeRenderer(int[] colors) {
		DefaultRenderer renderer = new DefaultRenderer();

		renderer.setLegendTextSize(mContext.getResources().getDimension(R.dimen.report_text_size));//设置左下角表注的文字大小
		//renderer.setZoomButtonsVisible(true);//设置显示放大缩小按钮  
		renderer.setZoomEnabled(false);//设置不允许放大缩小.  
		renderer.setChartTitleTextSize(mContext.getResources().getDimension(R.dimen.report_text_size));//设置图表标题的文字大小
		renderer.setChartTitle("总时间（%）");//设置图表的标题  默认是居中顶部显示
		renderer.setLabelsTextSize(mContext.getResources().getDimension(R.dimen.report_label_size));//饼图上标记文字的字体大小
		//renderer.setLabelsColor(Color.WHITE);//饼图上标记文字的颜色
		renderer.setPanEnabled(false);//设置是否可以平移
		renderer.setLabelsColor(mContext.getResources().getColor(R.color.top_bar_color));
		renderer.setDisplayValues(true);//是否显示值
		//不显示底部说明
		renderer.setShowLegend(false);
		renderer.setClickEnabled(true);//设置是否可以被点击
		renderer.setMargins(new int[] { Utils.dipToPixels(mContext, 50),  Utils.dipToPixels(mContext, 40), Utils.dipToPixels(mContext, 40), Utils.dipToPixels(mContext, 40) });
		//margins - an array containing the margin size values, in this order: top, left, bottom, right
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}
	/**
	 * 饼状图
	 * @param context
	 * @param units 
	 * @param list
	 * @param currentTabIndex 
	 * @return
	 */
	public static GraphicalView getTempPieView(Context context,double[] values, String[] units) {
		//把图表变成 View的方法， 
		mContext = context;
		CategorySeries series = new CategorySeries("总时间（%）");
		int[] colors = null;
		try {
			if(values!=null) {
				colors = new int[values.length];
				for(int i=0;i<values.length;i++) {
					series.add(units[i], values[i]);
					colors[i] = threes[i];
				}
				DefaultRenderer renderer=buildTempRenderer(colors);
				return ChartFactory.getPieChartView(context,series, renderer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	private static DefaultRenderer buildTempRenderer(int[] colors) {
		DefaultRenderer renderer = new DefaultRenderer();

		renderer.setLegendTextSize(mContext.getResources().getDimension(R.dimen.report_text_size));//设置左下角表注的文字大小
		//renderer.setZoomButtonsVisible(true);//设置显示放大缩小按钮  
		renderer.setZoomEnabled(false);//设置不允许放大缩小.  
		renderer.setChartTitleTextSize(mContext.getResources().getDimension(R.dimen.report_text_size));//设置图表标题的文字大小
		renderer.setChartTitle("总时间（%）");//设置图表的标题  默认是居中顶部显示
		renderer.setLabelsTextSize(mContext.getResources().getDimension(R.dimen.report_label_size));//饼图上标记文字的字体大小
		//renderer.setLabelsColor(Color.WHITE);//饼图上标记文字的颜色
		renderer.setPanEnabled(false);//设置是否可以平移
		renderer.setLabelsColor(mContext.getResources().getColor(R.color.top_bar_color));
		renderer.setDisplayValues(true);//是否显示值
		//不显示底部说明
		renderer.setShowLegend(false);
		renderer.setClickEnabled(true);//设置是否可以被点击
		renderer.setMargins(new int[] { Utils.dipToPixels(mContext, 50),  Utils.dipToPixels(mContext, 40), Utils.dipToPixels(mContext, 40), Utils.dipToPixels(mContext, 40) });
		//margins - an array containing the margin size values, in this order: top, left, bottom, right
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

}
