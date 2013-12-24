package com.anthonymandra.bitfinex;

import org.json.JSONObject;

public class Candle
{
	public long Start;
	public int Period;
	public double Open;
	public double Close;
	public double High;
	public double Low;
	public double Volume;
	
	public Candle(JSONObject candle)
	{

		try
		{
			Start = candle.getLong(Tags.START_AT);
			Period = candle.getInt(Tags.PERIOD);
			Open = candle.getDouble(Tags.OPEN);
			Close = candle.getDouble(Tags.CLOSE);
			High = candle.getDouble(Tags.HIGHEST);
			Low = candle.getDouble(Tags.LOWEST);
			Volume = candle.getDouble(Tags.VOLUME);
		}
		catch(Exception e){}
    	
	}
}
