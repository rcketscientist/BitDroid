package com.anthonymandra.bitfinex.widget;

import com.anthonymandra.BitDroid.R;
import com.anthonymandra.bitfinex.Balance;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class BalancesTable extends LinearLayout {

	TextView tradingUsdAvailable;
	TextView tradingUsdAmount;
	TextView tradingBtcAvailable;
	TextView tradingBtcAmount;

	TextView exchangeUsdAvailable;
	TextView exchangeUsdAmount;
	TextView exchangeBtcAvailable;
	TextView exchangeBtcAmount;

	TextView depositUsdAvailable;
	TextView depositUsdAmount;
	TextView depositBtcAvailable;
	TextView depositBtcAmount;
	
	TableLayout balances;
	ExpandableRefreshHeader balancesHeader;

	public BalancesTable(Context context, AttributeSet attrs) {
		super(context, attrs);

		((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.balances_layout, this, true);

		tradingUsdAvailable = ((TextView) findViewById(R.id.tradingAvailableUsd));
		tradingUsdAmount = ((TextView) findViewById(R.id.tradingAmountUsd));
		tradingBtcAvailable = ((TextView) findViewById(R.id.tradingAvailableBtc));
		tradingBtcAmount = ((TextView) findViewById(R.id.tradingAmountBtc));

		exchangeUsdAvailable = ((TextView) findViewById(R.id.exchangeAvailableUsd));
		exchangeUsdAmount = ((TextView) findViewById(R.id.exchangeAmountUsd));
		exchangeBtcAvailable = ((TextView) findViewById(R.id.exchangeAvailableBtc));
		exchangeBtcAmount = ((TextView) findViewById(R.id.exchangeAmountBtc));

		depositUsdAvailable = ((TextView) findViewById(R.id.depositAvailableUsd));
		depositUsdAmount = ((TextView) findViewById(R.id.depositAmountUsd));
		depositBtcAvailable = ((TextView) findViewById(R.id.depositAvailableBtc));
		depositBtcAmount = ((TextView) findViewById(R.id.depositAmountBtc));
		
		balances = (TableLayout) findViewById(R.id.balanceTable);
		balancesHeader = (ExpandableRefreshHeader) findViewById(R.id.balanceHeader);
		
		HeaderClickListener hcl = new HeaderClickListener();
		balancesHeader.setOnClickListenerHeader(hcl);
	}
	
	private class HeaderClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			if (balances.getVisibility() == View.GONE)
			{
				balances.setVisibility(View.VISIBLE);
				balancesHeader.expand();
			}
			else
			{
				balances.setVisibility(View.GONE);
				balancesHeader.collapse();
			}
		}
	}

	public void updateBalances(Balance balance) {
		if (balance == null)
			return;
		
		tradingUsdAvailable.setText(String.format("%.2f",
				balance.tradingUsdAvailable));
		tradingUsdAmount.setText(String
				.format("%.2f", balance.tradingUsdAmount));
		tradingBtcAvailable.setText(String.format("%.3f",
				balance.tradingBtcAvailable));
		tradingBtcAmount.setText(String
				.format("%.3f", balance.tradingBtcAmount));

		exchangeUsdAvailable.setText(String.format("%.2f",
				balance.exchangeUsdAvailable));
		exchangeUsdAmount.setText(String.format("%.2f",
				balance.exchangeUsdAmount));
		exchangeBtcAvailable.setText(String.format("%.3f",
				balance.exchangeBtcAvailable));
		exchangeBtcAmount.setText(String.format("%.3f",
				balance.exchangeBtcAmount));

		depositUsdAvailable.setText(String.format("%.2f",
				balance.depositUsdAvailable));
		depositUsdAmount.setText(String
				.format("%.2f", balance.depositUsdAmount));
		depositBtcAvailable.setText(String.format("%.3f",
				balance.depositBtcAvailable));
		depositBtcAmount.setText(String
				.format("%.3f", balance.depositBtcAmount));
	}
	
	public void setOnClickListenerRefresh(OnClickListener listener)
	{
		balancesHeader.setOnClickListenerRefresh(listener);
	}

}
