package com.anthonymandra.BitDroid;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Timer;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.anthonymandra.bitfinex.Position;
import com.anthonymandra.bitfinex.widget.ExpandableRefreshHeader;


/**
 * Created by amand_000 on 11/24/13.
 */
public class MarginFragment extends TradeFragment{
	
	private TableLayout positionsTable;
	
	private ArrayList<Position> positions = new ArrayList<Position>();
	
	private Timer priceTimer;
	
	private LayoutInflater inflater;
	
	private ExpandableRefreshHeader positionHeader;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.margin_layout, container, false);		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		lookupViews();
		attachButtons();
	}

	@Override
	public void onResume() {
		super.onResume();
		priceTimer = new Timer();
		priceTimer.scheduleAtFixedRate(new PriceTimer(), 0, 5000);
		new PositionsTask().executeOnExecutor(EXECUTER);
	}

	@Override
	public void onPause() {
		super.onPause();
		priceTimer.cancel();
	}

	private void lookupViews()
	{
		positionsTable = (TableLayout) getView().findViewById(R.id.positionsTable);
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
		
		PositionsClickListener pcl = new PositionsClickListener();		
		positionHeader = (ExpandableRefreshHeader) getView().findViewById(R.id.positionHeader);
		positionHeader.setOnClickListenerHeader(pcl);
		positionHeader.setOnClickListenerRefresh(new OnClickListener()  {
			
			@Override
			public void onClick(View v) {
				new PositionsTask().executeOnExecutor(EXECUTER);
			}
		});
	}
	
	private class BuyConfirmClickListener implements DialogInterface.OnClickListener
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			placeOrder(true, true);			
		}
		
	}
	
	private class SellConfirmClickListener implements DialogInterface.OnClickListener
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			placeOrder(false, true);
		}
		
	}
	
	private class PositionsClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			if (positionsTable.getVisibility() == View.GONE)
			{
				positionsTable.setVisibility(View.VISIBLE);
				positionHeader.expand();
			}
			else
			{
				positionsTable.setVisibility(View.GONE);
				positionHeader.collapse();
			}
		}
		
	}
	
	private void getPositions()
	{
		try {
			String request = "/v1/positions";
			String URL = BITFINEX_URL + request;
			Header[] headers = prepareHeaders(request, new JSONObject(), true);
			String result = getHttpResult(URL, headers);
			if (result == null)
				return;
			JSONArray pArray = new JSONArray(result);
			
			positions.clear();
			for (int i = 0; i < pArray.length(); ++i)
			{						
				positions.add(new Position(pArray.getJSONObject(i)));				
			}	
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
	}
	
	private class PositionsTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			getPositions();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			positionsTable.removeAllViews();
			
			View header = inflater.inflate(R.layout.position_row, null);
			header.setBackgroundResource(R.color.neutral);
			positionsTable.addView(header);
			for (Position p : positions)
			{
				View row = inflater.inflate(R.layout.position_row, null);
				row.setTag(p.id);
				
				TextView pair = ((TextView)row.findViewById(R.id.positionPair));
				pair.setText(p.Pair);
				pair.setTypeface(Typeface.DEFAULT);
				
				TextView amount = ((TextView)row.findViewById(R.id.positionAmount));
				amount.setText(String.format("%.1f", p.Amount));
				amount.setTypeface(Typeface.DEFAULT);
				
				TextView price = ((TextView)row.findViewById(R.id.positionPrice));
				price.setText(String.format("%.2f", p.Price));
				price.setTypeface(Typeface.DEFAULT);
							
				TextView profitCash = ((TextView)row.findViewById(R.id.positionProfitCash));
				profitCash.setText(String.format("%.2f", p.ProfitCash));
				profitCash.setTextColor(p.ProfitCash < 0 ? getResources().getColor(R.color.loss) : getResources().getColor(R.color.profit));
				profitCash.setTypeface(Typeface.DEFAULT);
				
				TextView profitPercent = ((TextView)row.findViewById(R.id.positionProfitPercent));
				profitPercent.setText(String.format("%.3f", p.ProfitPercent));
				profitPercent.setTextColor(p.ProfitPercent < 0 ? getResources().getColor(R.color.loss) : getResources().getColor(R.color.profit));
				profitPercent.setTypeface(Typeface.DEFAULT);
				
				TextView swap = ((TextView)row.findViewById(R.id.positionSwap));
				swap.setText(String.format("%.2f", p.Swap));
				swap.setTextColor(p.Swap < 0 ? getResources().getColor(R.color.loss) : getResources().getColor(R.color.profit));
				swap.setTypeface(Typeface.DEFAULT);
				
				positionsTable.addView(row);
			}
		}	
	}

	@Override
	protected void setBidPrice(double price) {
		double delta = currentBid / price;
		if (delta < 1 && delta > 0.05 || delta > 1 && delta > 1.05)
			new PositionsTask().executeOnExecutor(EXECUTER);
		super.setBidPrice(price);
	}

	@Override
	protected void setAskPrice(double price) {
		double delta = currentAsk / price;
		if (delta < 1 && delta > 0.05 || delta > 1 && delta > 1.05)
			new PositionsTask().executeOnExecutor(EXECUTER);
		super.setAskPrice(price);
	}
	
}
