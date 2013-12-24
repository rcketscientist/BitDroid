package com.anthonymandra.bitfinex;

import org.json.JSONObject;

public class Position {

	public int id;
	public long Timestamp;
	public String Pair;
	public double Amount;
	public double Price;
	public double ProfitCash;
	public double ProfitPercent;
	public String Status;
	public double Swap;
	
	public Position(JSONObject position)
	{
		try
		{
			Amount = position.getDouble(Tags.AMOUNT);
			id = position.getInt(Tags.ID);
			Price = position.getDouble(Tags.BASE);
			Pair = position.getString(Tags.SYMBOL);
			ProfitCash = position.getDouble(Tags.PROFIT);
			ProfitPercent = ProfitCash / (Price * Math.abs(Amount)) * 100;
			Status = position.getString(Tags.STATUS);				
			Swap = position.getDouble(Tags.SWAP);
			Timestamp = position.getLong(Tags.TIMESTAMP);
		}
		catch(Exception e){}
	}
}
