package com.xtrd.obdcar.trip;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

import com.xtrd.obdcar.entity.Velocity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;

public class LineView {
	private static Context mContext;
	

	public static GraphicalView getLineView(Context context,ArrayList<Velocity> list) {
		mContext = context;
		String[] titles = new String[] { "速度"};

		double[] xValues = new double[list.size()];
		double[] yValues = new double[list.size()];

		for (int i = 0; i < list.size(); i++) {
			xValues[i] = (double)i;
		}
		Velocity velocity = null;
		double maxVelocity = 0;
		for(int i=0;i<list.size();i++) {
			velocity = list.get(i);
			yValues[i] = velocity.getVelocity();
			if(maxVelocity<velocity.getVelocity()) {
				maxVelocity = velocity.getVelocity();
			}
		}
		int[] colors = new int[] { Color.BLUE};
		XYMultipleSeriesRenderer renderer = buildRenderer(colors);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
		}
		
		renderer.addXTextLabel(0, TimeUtils.formatDate2Hour(TimeUtils.getTimeByStr(list.get(0).getTime())));
		renderer.addXTextLabel(list.size(), TimeUtils.formatDate2Hour(TimeUtils.getTimeByStr(list.get(list.size()-1).getTime())));
		
		setChartSettings(renderer, "速度时间曲线图", "时间", "速度(公里/小时)", 0, list.size(), 0, maxVelocity+2, mContext.getResources().getColor(R.color.font_car_add_color),  mContext.getResources().getColor(R.color.font_car_add_color));
		renderer.setXLabels(0);
		renderer.setFitLegend(true);
		renderer.setYLabels(10);
		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);

		renderer.setZoomButtonsVisible(false);
		renderer.setPanLimits(new double[] { 0, list.size(), 0, maxVelocity});
		renderer.setZoomLimits(new double[] { 0, list.size(), 0, maxVelocity});

		//把图表变成 View的方法， 
		GraphicalView view= ChartFactory.getLineChartView(context, buildDataset(titles, xValues, yValues), renderer);
		view.setBackgroundResource(R.drawable.item_bg);
		return view;
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
			r.setLineWidth(Utils.dipToPixels(mContext, 2));
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
		renderer.setXLabelsColor(labelsColor);
		renderer.setYLabelsColor(0, labelsColor);
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
