package com.anthonymandra.bitfinex;

import org.json.JSONArray;
import org.json.JSONObject;

public class Balance {
	
	public double tradingBtcAmount;
	public double tradingUsdAmount;	
	public double depositBtcAmount;
	public double depositUsdAmount;
	public double exchangeBtcAmount;
	public double exchangeUsdAmount;
	
	public double tradingBtcAvailable;
	public double tradingUsdAvailable;	
	public double depositBtcAvailable;
	public double depositUsdAvailable;
	public double exchangeBtcAvailable;
	public double exchangeUsdAvailable;
	
	public Balance(JSONArray balances)
	{
		try
		{
			for (int i = 0; i < balances.length(); ++i)
			{
				JSONObject balance = balances.getJSONObject(i);
				String type = balance.getString(Tags.TYPE);
				String currency = balance.getString(Tags.CURRENCY);
								
				if (type.equals(Tags.TRADING) && currency.equals(Tags.USD))
				{
					tradingUsdAmount = balance.getDouble(Tags.AMOUNT);
					tradingUsdAvailable = balance.getDouble(Tags.AVAILABLE);
				}
				else if (type.equals(Tags.TRADING) && currency.equals(Tags.BTC))
				{
					tradingBtcAmount = balance.getDouble(Tags.AMOUNT);
					tradingBtcAvailable = balance.getDouble(Tags.AVAILABLE);
				}
				else if (type.equals(Tags.DEPOSIT) && currency.equals(Tags.USD))
				{
					depositUsdAmount = balance.getDouble(Tags.AMOUNT);
					depositUsdAvailable = balance.getDouble(Tags.AVAILABLE);
				}
				else if (type.equals(Tags.DEPOSIT) && currency.equals(Tags.BTC))
				{
					depositBtcAmount = balance.getDouble(Tags.AMOUNT);
					depositBtcAvailable = balance.getDouble(Tags.AVAILABLE);
				}
				else if (type.equals(Tags.EXCHANGE) && currency.equals(Tags.USD))
				{
					exchangeUsdAmount = balance.getDouble(Tags.AMOUNT);
					exchangeUsdAvailable = balance.getDouble(Tags.AVAILABLE);
				}
				else if (type.equals(Tags.EXCHANGE) && currency.equals(Tags.BTC))
				{
					exchangeBtcAmount = balance.getDouble(Tags.AMOUNT);
					exchangeBtcAvailable = balance.getDouble(Tags.AVAILABLE);
				}			
				
			}
		}
		catch(Exception e){}
	}

}
