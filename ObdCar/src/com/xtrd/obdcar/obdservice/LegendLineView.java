package com.xtrd.obdcar.obdservice;


import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.entity.FuelReport;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

public class LegendLineView {
	private static Context mContext;

	/**
	 * 柱状图
	 * @param context
	 * @param list
	 * @param currentTabIndex 
	 * @param sameyear 
	 * @return
	 */
	public static GraphicalView getBarView(Context context,ArrayList<FuelReport> list,ArrayList<String> xLabels, int currentTabIndex, boolean sameyear) {
		mContext = context;
		String[] titles = new String[] { "里程"};
		
		double[] xValues = new double[xLabels.size()];
		double[] yValues = new double[xLabels.size()];
		
		for (int i = 0; i < xLabels.size(); i++) {
			xValues[i] = (double)i;
		}
		FuelReport report = null;
		double maxVelocity = 0;
		for(int i=0;i<xLabels.size();i++) {
			if(i<list.size()) {
				report = list.get(i);
			}
			yValues[getPosition(report,xLabels,currentTabIndex,sameyear)] = report.getDistance();
			if(maxVelocity<report.getDistance()) {
				maxVelocity = report.getDistance();
			}
		}
		int[] colors = new int[] {XtrdApp.getAppContext().getResources().getColor(R.color.report_color)};
		XYMultipleSeriesRenderer renderer = buildRenderer(colors);
		
		if(currentTabIndex==1) {
			int i=0;
			if(xLabels.size()%2==0) {
				i = 1;
			}
			for(;i<xLabels.size();i+=2) {
				renderer.addXTextLabel(i+1, xLabels.get(i));
			}
		}else {
			for(int i=0;i<xLabels.size();i++) {
				renderer.addXTextLabel(i+1, xLabels.get(i));
			}
		}
		
		setChartSettings(renderer, "", "日期", "里程(公里)", 0, xLabels.size()+1, 0, maxVelocity+1, Color.BLACK, Color.BLACK);
		renderer.setXLabels(0);
		renderer.setYLabels(10);
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
	    renderer.setBarSpacing(Utils.dipToPixels(context, 5));
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setZoomRate(1.0f);
	    renderer.setZoomEnabled(false,false);
	    renderer.setBarWidth(Utils.dipToPixels(XtrdApp.getAppContext(), 8));
		renderer.setZoomButtonsVisible(false);
		renderer.setPanLimits(new double[] { 0, xLabels.size(), 0, maxVelocity});
		renderer.setZoomLimits(new double[] { 0, xLabels.size(), 0, maxVelocity});

		//把图表变成 View的方法， 
		GraphicalView view= ChartFactory.getBarChartView(context, buildDataset(titles, xValues, yValues), renderer,Type.DEFAULT);
		view.setBackgroundResource(R.drawable.item_bg);
		return view;
	}
	
	
	private static int getPosition(FuelReport report,ArrayList<String> xLabels, int currentTabIndex, boolean sameyear) {
		String str = "";
		switch (currentTabIndex) {
		case 0:
			for(int i=0;i<xLabels.size();i++) {
				str = xLabels.get(i);
				if(str.contains(report.getMonth()+"-"+report.getDay())) {
					return i;
				}
			}
			break;
		case 1:
			for(int i=0;i<xLabels.size();i++) {
				str = xLabels.get(i);
				if(str.contains(report.getDay())) {
					return i;
				}
			}
			break;
		case 2:
			if(sameyear) {
				for(int i=0;i<xLabels.size();i++) {
					str = xLabels.get(i);
					if(str.contains(report.getYear()+"-"+report.getMonth())) {
						return i;
					}
				}
			}else {
				for(int i=0;i<xLabels.size();i++) {
					str = xLabels.get(i);
					if(str.contains(report.getYear())) {
						return i;
					}
				}
			}
			break;

		default:
			break;
		}
		return 0;
	}


	private static XYMultipleSeriesRenderer buildRenderer(int[] colors) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setInScroll(true);
		renderer.setAntialiasing(true);
		setRenderer(renderer, colors);
		return renderer;
	}

	private static void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors) {
		renderer.setAxisTitleTextSize(mContext.getResources().getDimension(R.dimen.report_text_size));
		renderer.setChartTitleTextSize(mContext.getResources().getDimension(R.dimen.report_text_size));
		renderer.setLabelsTextSize(mContext.getResources().getDimension(R.dimen.report_label_size));
		renderer.setLegendTextSize(mContext.getResources().getDimension(R.dimen.report_text_size));
		renderer.setMargins(new int[] { Utils.dipToPixels(mContext, 50),  Utils.dipToPixels(mContext, 40), Utils.dipToPixels(mContext, 40), Utils.dipToPixels(mContext, 40) });
		renderer.setMarginsColor(Color.WHITE);
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			renderer.addSeriesRenderer(r);
		}
	}

	private static void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}

	private static XYMultipleSeriesDataset buildDataset(String[] titles, double[] xValues, double[] yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, titles, xValues, yValues, 0);
		return dataset;
	}

	private static void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, double[] xValues, double[] yValues, int scale) {
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale);
			double[] xV = xValues;
			double[] yV = yValues;
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k]+1, yV[k]);
			}
			dataset.addSeries(series);
		}
	}
}
