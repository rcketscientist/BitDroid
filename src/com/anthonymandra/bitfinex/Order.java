package com.anthonymandra.bitfinex;

import org.json.JSONObject;

public class Order {

	public int id;
	public long Timestamp;
	public String Pair;
	public String Type;
	public double Amount;
	public double Amount_Executed;
	public double Price;
	public String Route;
	public double Placed;
	public String Status;
	public boolean isSell;
	public boolean isActive;
	
	public Order(JSONObject order) {
		try
		{
			Amount = order.getDouble(Tags.ORIGINAL_AMOUNT);
			Amount_Executed = order.getDouble(Tags.EXECUTED_AMOUNT);
			id = order.getInt(Tags.ID);
			Pair = order.getString(Tags.SYMBOL);
			Price = order.getDouble(Tags.PRICE);
			isSell = order.getString(Tags.SIDE).equals("sell");
			isActive = order.getBoolean(Tags.IS_LIVE);
			Route = order.getString(Tags.EXCHANGE);
			Type = order.getString(Tags.TYPE);
			Timestamp = order.getLong(Tags.TIMESTAMP);
		}
		catch(Exception e){}
	}

}
