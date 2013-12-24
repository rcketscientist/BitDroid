package com.anthonymandra.BitDroid;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.StockChartView;
import org.stockchart.core.Appearance.Gradient;
import org.stockchart.core.Area;
import org.stockchart.core.Axis;
import org.stockchart.core.Axis.Side;
import org.stockchart.core.AxisRange;
import org.stockchart.core.AxisRange.ViewValues;
import org.stockchart.indicators.EmaIndicator;
import org.stockchart.misc.DateTimeScaleValuesProvider;
import org.stockchart.misc.RoundNumbersScaleValuesProvider;
import org.stockchart.points.BarPoint;
import org.stockchart.points.StockPoint;
import org.stockchart.series.BarSeries;
import org.stockchart.series.LinearSeries;
import org.stockchart.series.StockSeries;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.anthonymandra.bitfinex.Candle;

public class ChartFragment extends BitfinexFragment {
	
	public static final String FRAGMENT_TAG = "ChartFragment";
	private static final long DAY = 1000 * 60 * 60 * 24;
	private static final long WEEK = DAY * 7;
	private static final long MONTH = DAY * 30;
	private static final long YEAR = DAY * 365;
	
	protected long latestUpdate;
	protected String currentSymbol;
//	List<Candle> candles = new ArrayList<Candle>();
	protected Timer candleTimer;
	protected StockChartView chart;
	protected Area area;
	protected StockSeries candleSeries;
	protected BarSeries volumeSeries;
	public static final String CHART_NAME = "chart";
	private static long period = DAY;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//        View view = inflater.inflate(R.layout.chart_layout, container, false);
        View view = inflater.inflate(R.layout.temp_chart_layout, container, false);  //Temporary while API is broken
        
        return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		WebView webView = (WebView) getView().findViewById(R.id.chartWeb);
		webView.loadUrl("http://bitcoinwisdom.com/markets/bitfinex/btcusd");
		webView.getSettings().setJavaScriptEnabled(true);
//		lookupViews();
//		attachButtons();
//		
//        if(savedInstanceState != null)
//        {
//        	// if we have something to restore, then restore it
//        	 String sss = savedInstanceState.getString("chart");
//        	 restoreChart(sss);        
//        }
//        else
//        {
//        	// if we have nothing to restore (i.e. first launch) then initialize
//        	newChart();
//        }

//        wireEvents();	
	}
	
	public void onSaveInstanceState (Bundle outState)
	{
//		try 
//		{
//			// if something changes (i.e. orientation) we want to 
//			// restore our state later. So, why don't we save it first?
//			outState.putString("chart", chart.save());
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public void onResume() {
		super.onResume();
//		candleTimer = new Timer();
//		candleTimer.scheduleAtFixedRate(new CandleTimer(), 0, 60000);
//		new CandlesTask().execute();
	}

	@Override
	public void onPause() {
		super.onPause();
//		candleTimer.cancel();
	}

	private void lookupViews()
	{
		chart = (StockChartView) getView().findViewById(R.id.stockChartView);
	}
	
	private void attachButtons()
	{
	}
	
	// this method is called when only once
	private void newChart()
	{			
		// initializing areas
		
		// first area:
        area = new Area();
        area.setName(CHART_NAME);
//        area.setTitle("Summer");
        area.getAppearance().setPrimaryFillColor(Color.rgb(0, 255, 255));
        area.getAppearance().setSecondaryFillColor(Color.GREEN);
        area.getAppearance().setGradient(Gradient.LINEAR_VERTICAL);
        area.getLeftAxis().setVisible(true);
        area.getLeftAxis().setLinesCount(2);
        area.getRightAxis().setScaleValuesProvider(new RoundNumbersScaleValuesProvider(0));
        // we don't need top axis...
        area.getTopAxis().setVisible(false);
        
        chart.getAreas().add(area);
//        s.getAreas().add(a2);             
//        s.getAreas().add(a3);        

        
        // creating stock series
    	candleSeries = new StockSeries();
//    	s1.getAppearance().setTextColor(Color.GREEN);
    	// despite each series has unique name, I recommend you to give them names
    	candleSeries.setName("stock");
    	candleSeries.getFallAppearance().setPrimaryFillColor(Color.BLACK);
    	candleSeries.getRiseAppearance().setPrimaryFillColor(Color.YELLOW);
    	// setting outline style and width to make our candlesticks look more attractive. 
    	// i like this combination more than others. I don't recommend setting outline width too
    	// large. 1...3f is enough.      	
//    	s1.setOutlineStyle(OutlineStyle.USE_DARKER_BODY_COLOR);
//    	s1.setOutlineWidth(2.0f);
    	
    	
    	// creating bar series for volume
    	volumeSeries = new BarSeries();
    	volumeSeries.setName("volume");    
    	
    	// attaching s2 to left axis
    	volumeSeries.setYAxisSide(Side.LEFT);
        // change the color of bar series
    	volumeSeries.getAppearance().setGradient(Gradient.LINEAR_VERTICAL);
        
    	// creating linear series for close prices            	
//	    s3 = new LinearSeries();
//	    s3.setName("line");
//	    s3.setPointsVisible(true);
//	    
//	    // attaching s3 to right axis
//	    s3.setYAxisSide(Side.RIGHT);        
        	    
	    // series done. let's add them to corresponding areas        
        area.getSeries().add(volumeSeries);
        area.getSeries().add(candleSeries);
        
        area.getAxis(candleSeries.getYAxisSide()).getAxisRange().setMargin(0.2f);
        
//        a2.getSeries().add(s3);
                
        // adding constant lines  (rulers) to the first area just for fun
//        a1.getLines().add(new Line(10,Axis.Side.BOTTOM));
//        a1.getLines().add(new Line(800,Axis.Side.RIGHT));
        // EMA indicator:
        LinearSeries ema = new LinearSeries();
        area.getSeries().add(ema);
        chart.getIndicators().add(new EmaIndicator(candleSeries, 3, ema));
        // The MACD indicator (to know for sure, when to buy or sell :) )
        // For the MACD we (typically) need:
        // 1. Lines x2
        // 2. Histogram x1
        // 3. Own area x1 
        // In theory, you can create only series and attach them to any area you want
        // But i prefer classical representation of the MACD
        // Here we go:       
//        LinearSeries macd = new LinearSeries();
//        LinearSeries signal = new LinearSeries();
//        BarSeries hist = new BarSeries();
//        a3.getSeries().add(macd);
//        a3.getSeries().add(signal);
//        a3.getSeries().add(hist);
        // cool zero level for the histogram
//        a3.getLines().add(new Line(0.0,Axis.Side.RIGHT));
        
        // add indicator
//        s.getIndicators().add(new MacdIndicator(s1,0,macd,signal,hist));
        
// 		  Bollinger bands indicator example
        
//        LinearSeries bbSma = new LinearSeries();
//        LinearSeries bbUpper = new LinearSeries();
//        LinearSeries bbLower = new LinearSeries();
//        a1.Series.add(bbSma);
//        a1.Series.add(bbUpper);
//        a1.Series.add(bbLower);
//        s.Indicators.add(new BollingerBandsIndicator(s1,3,bbSma,bbUpper,bbLower,5,2.0,3));
        
        // Stochastic indicator example
//        LinearSeries slowK = new LinearSeries();
//        slowK.getAppearance().setOutlineColor(Color.BLUE);
//        LinearSeries slowD = new LinearSeries();
//        
//        a3.getSeries().add(slowK);
//        a3.getSeries().add(slowD);
//        
//        s.getIndicators().add(new StochasticIndicator(s1,StockPoint.getValueIndex(PointValue.CLOSE),slowK,slowD));
        
        // the last thing we need, is to make all areas behave as one.
        // we need to set global range
        AxisRange ar = new AxisRange();
        ar.setMovable(true);
        ar.setZoomable(true);
        chart.enableGlobalAxisRange(Axis.Side.BOTTOM, ar); 
	}
	
	private void restoreChart(String sss)
	{
		// all we have to do is to restore our chart from JSON.
		// All series created automatically, so we need to find them by name
		try 
		{
			chart.load(sss);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		area = chart.findAreaByName(CHART_NAME);
//		a2 = s.findAreaByName("A2");
//		a3 = s.findAreaByName("A3");
		candleSeries = (StockSeries) area.findSeriesByName("stock");
		volumeSeries = (BarSeries) area.findSeriesByName("volume");
//		s3 = (LinearSeries)a1.findSeriesByName("line");
	}
	
	protected class CandleTimer extends TimerTask
    {
        @Override
        public void run() {
            new CandlesTask().executeOnExecutor(EXECUTER);
        }
    }
	
	private List<Candle> getCandles()
	{
		try {
			String request = "/v1/candles/btcusd";
			String URL = BITFINEX_URL + request;
			JSONObject payload = new JSONObject();
//			payload.put(Tags.TIMESTAMP, Long.toString(0));//System.currentTimeMillis() - period));
			
			Header[] headers = prepareHeaders(request, payload, false);
			String result = getHttpResult(URL, headers);
			if (result == null)
				return null;
			JSONArray pArray = new JSONArray(result);
			
			List<Candle> candles = new ArrayList<Candle>();
			for (int i = 0; i < pArray.length(); ++i)
			{						
				candles.add(new Candle(pArray.getJSONObject(i)));				
			}	
			return candles;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	protected void updateChart(List<Candle> candles)
	{   	
		
		long max = 0;
    	for (Candle candle : candles)
    	{ 
    		max = Math.max(max, candle.Start);
	    	StockPoint ss = new StockPoint();
	    	Date d = new Date(candle.Start); //This would be left end of period.  Mid is start + period/2
	    	ss.setID(d);	
	    	ss.setValues(candle.Open, candle.High, candle.Low, candle.Close);
	    	candleSeries.getPoints().add(ss);
	    	candleSeries.setLastValue( candle.Close); //sloppy
	    	
	    	BarPoint bp = volumeSeries.addPoint(0, candle.Volume);
	    	bp.setID(d);
	    	volumeSeries.setLastValue( candle.Volume); //sloppy
	    	
//	    	LinePoint lp = s3.addPoint(close);
//	    	lp.setID(id);
    	}
    	
    	area.getBottomAxis().setScaleValuesProvider(new DateTimeScaleValuesProvider(candleSeries));
    	
    	// explicit call recalc() to set auto view values. I know it will be called 
    	// again in onDraw method, but I haven't found better way
    	chart.recalc();
    	
    	// Assume we want to move view values to show us the most recent data  
    	// (scroll current view to the most right position). We call one of the overloaded 
    	// methods setViewValues. 
    	chart.getGlobalAxisRange(Axis.Side.BOTTOM).setViewValues(ViewValues.SCROLL_TO_LAST);

    	// don't forget to recalc indicators before invalidate
    	chart.recalcIndicators();
    	// don't forget to call invalidate when finish
    	chart.invalidate();
//    	
//		
//    	if(tttt < 100)
//    	{
//    		mHandler.postDelayed(this, 10);        	
//    	}       
    	
	}
	
	private class CandlesTask extends AsyncTask<Void, Void, List<Candle>>
	{
		@Override
		protected List<Candle> doInBackground(Void... params) {
			return getCandles();
		}

		@Override
		protected void onPostExecute(List<Candle> result) {
			updateChart(result);
		}	
	}
}
