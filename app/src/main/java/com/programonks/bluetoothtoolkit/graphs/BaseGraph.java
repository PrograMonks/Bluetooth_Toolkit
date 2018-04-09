/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 PrograMonks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.programonks.bluetoothtoolkit.graphs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;
import android.widget.LinearLayout;

import com.programonks.bluetoothtoolkit.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.util.concurrent.TimeUnit;

/**
 * Base class for all the graphs
 *
 * @author PrograMonks
 */
public abstract class BaseGraph {
    public static final float LINE_WEIGHT = 4f;
    public static final boolean IS_GRAPH_MOVABLE_BY_TOUCH_HORIZONTAL = false;
    public static final boolean IS_GRAPH_MOVABLE_BY_TOUCH_VERTICAL = false;
    public static final boolean IS_GRAPH_ZOOMABLE_BY_TOUCH_HORIZONTAL = false;
    public static final boolean IS_GRAPH_ZOOMABLE_BY_TOUCH_VERTICAL = false;
    public static final int MARGIN_LEFT = 110;
    public static final int MARGIN_RIGHT = 70;

    private final Context context;
    private GraphicalView mChart;
    protected final XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    protected final XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private long mStartTime = 0;

    private double mStartingXAxisMin;
    private double mStartingXAxisMax;
    private double mStartingYAxisMin;
    private double mStartingYAxisMax;

    protected void setAxisStartingPoints() {
        mRenderer.setXAxisMin(mStartingXAxisMin);
        mRenderer.setXAxisMax(mStartingXAxisMax);
        mRenderer.setYAxisMin(mStartingYAxisMin);
        mRenderer.setYAxisMax(mStartingYAxisMax);
    }

    protected void setStartingPositions(double xMin, double xMax, double yMin,
                                        double yMax) {
        mStartingXAxisMin = xMin;
        mStartingXAxisMax = xMax;
        mStartingYAxisMin = yMin;
        mStartingYAxisMax = yMax;
    }

    protected BaseGraph(Context context, View view) {
        this.context = context;
        addChartToLayout(view);
    }

    protected void initChart() {
        setCommonRendererValues();
    }

    protected void clearGraph() {
        setCommonRendererValues();
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    public void startTimer() {
        if (mStartTime == 0) {
            mStartTime = System.nanoTime();
        }
    }

    protected void addChartToLayout(View view) {
        LinearLayout chartLayout = (LinearLayout) view
                .findViewById(R.id.chart_layout);

        mChart = ChartFactory.getCubeLineChartView(context, mDataset,
                mRenderer, 0.2f);
        chartLayout.addView(mChart);
    }

    protected void paintChart() {
        checkGraphHorizontalAutoMovement();
        checkVerticalGraphAutoMovement();
        mChart.repaint();
    }

    protected void setCommonRendererValues() {
        // graph styling
        mRenderer.setLabelsColor(Color.BLUE);
        mRenderer.setLabelsTextSize(25);
        mRenderer.setAxisTitleTextSize(30);
        mRenderer.setAxesColor(Color.BLACK);
        mRenderer.setMargins(new int[]{0, MARGIN_LEFT, 75, MARGIN_RIGHT});
        mRenderer.setMarginsColor(Color.parseColor("#00FFA500"));
        mRenderer.setGridColor(Color.parseColor("#00000000")); // CD7F32

        // X Axis and label
        mRenderer.setXTitle("Time (Minutes)");
        mRenderer.setXAxisMax(1.1);
        mRenderer.setXAxisMin(0);
        mRenderer.setXLabelsColor(Color.BLACK); // color of numbers on label X
        mRenderer.setXLabels(7);

        // Y Axis and label
        mRenderer.setYAxisAlign(Align.LEFT, 0);
        mRenderer.setYLabelsAlign(Align.RIGHT, 0);
        mRenderer.setYLabelsPadding(15);
        mRenderer.setYLabelsVerticalPadding(-8);
        mRenderer.setYLabelsColor(0, Color.BLACK); // colour of numbers on label
        mRenderer.setYLabels(6);

        // user interaction with chart
        mRenderer.setZoomButtonsVisible(false);
        mRenderer.setShowLegend(true);
        mRenderer.setFitLegend(true);
        mRenderer.setLegendTextSize(30f);
        mRenderer.setShowGrid(true);
    }

    public double calculateElapsedTime() {
        long mEndTime = System.nanoTime();
        long mElapsedTime = mEndTime - mStartTime;

        double sec = (double) TimeUnit.SECONDS.convert(mElapsedTime, TimeUnit.NANOSECONDS);

        sec = sec / 60;
        return sec;
    }

    /*
     * Min --> graph minimum view point
     * Max --> graph maximum view point
     * newMax --> max point of where the line is right now
     * newMin --> where should the  minimum point be moved
     */
    public void checkGraphHorizontalAutoMovement() {
        double max = mRenderer.getXAxisMax();
        double min = mRenderer.getXAxisMin();
        if (!((max == Double.MAX_VALUE || max == -Double.MAX_VALUE)
                && (min == Double.MAX_VALUE || min == -Double.MAX_VALUE))) {

            /*
            new max is the latest X value from our horizontal axis series
             */
            double newMax = mDataset.getSeriesAt(0).getMaxX();
            if (newMax >= max) {
                // move graph to the right
                mRenderer.setXAxisMax(newMax);
            }
        }
    }

    public void checkVerticalGraphAutoMovement() {
        double max = mRenderer.getYAxisMax();
        double min = mRenderer.getYAxisMin();

        /*
        Check if the user scrolled/zoomed/panned the graph or if it's on 'auto'
         */
        if (!((max == Double.MAX_VALUE || max == -Double.MAX_VALUE)
                && (min == Double.MAX_VALUE || min == -Double.MAX_VALUE))) {

            double currentHighestGraphValue = getGraphCurrentHighestValue();

            if (currentHighestGraphValue >= max) {
                mRenderer.setYAxisMax(currentHighestGraphValue);
            }
        }
    }

    private double getGraphCurrentHighestValue() {
        double newMax = mDataset.getSeriesAt(0).getMaxY();
        /*
        check which graph line has the biggest value in this way we can know the greatest number
        and display it on the Y axis
         */
        for (int j = 0; j < mDataset.getSeriesCount(); j++) {
            if (newMax < mDataset.getSeriesAt(j).getMaxY()) {
                newMax = mDataset.getSeriesAt(j).getMaxY();
            }
        }
        return newMax;
    }
}
