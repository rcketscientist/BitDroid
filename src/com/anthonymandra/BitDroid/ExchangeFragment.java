package com.anthonymandra.BitDroid;

import java.util.Timer;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * Created by amand_000 on 11/24/13.
 */
public class ExchangeFragment extends TradeFragment{

	private Timer priceTimer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		
		View view = inflater.inflate(R.layout.exchange_layout, container, false);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		lookupViews();
		attachButtons();
	}
	
	private void lookupViews()
	{	
	}
	
	private void attachButtons()
	{
		getView().findViewById(R.id.buyButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String quantity = getQuantity();
				String constraint = getBuyConstraint();
				String buyType = getBuyType();
				
				AlertDialog.Builder b = new Builder(getActivity());
				b.setTitle("New order confirmation")
					.setMessage("Place a " + buyType + 
						" buy order for " + quantity + 
						" shares with a constraint of " + constraint + "?" )
					.setPositiveButton("Yes", new BuyConfirmClickListener())
					.setNegativeButton("NO", null)
					.create().show();
			}
		});
				
		getView().findViewById(R.id.sellButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String quantity = getQuantity();
				String constraint = getSellConstraint();
				String sellType = getSellType();
				
				AlertDialog.Builder b = new Builder(getActivity());
				b.setTitle("New order confirmation")
					.setMessage("Place a " + sellType + 
						" buy order for " + quantity + 
						" shares with a constraint of " + constraint + "?" )
					.setPositiveButton("Yes", new SellConfirmClickListener())
					.setNegativeButton("NO", null)
					.create().show();
			}
		});
	}
	
	private class BuyConfirmClickListener implements DialogInterface.OnClickListener
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			placeOrder(true, false);			
		}
		
	}
	
	private class SellConfirmClickListener implements DialogInterface.OnClickListener
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			placeOrder(false, false);
		}
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		priceTimer = new Timer();
		priceTimer.scheduleAtFixedRate(new PriceTimer(), 0, 5000);
	}

	@Override
	public void onPause() {
		super.onPause();
		priceTimer.cancel();
	}
	
}
