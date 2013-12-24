package com.anthonymandra.bitfinex.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.anthonymandra.BitDroid.R;

public class TradeView extends LinearLayout {

	public TradeView(Context context) {
		super(context);
		((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.trade_layout, this, true);
	}

	public TradeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.trade_layout, this, true);
			
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TradeView, 0, 0);

  		String buy = a.getString(R.styleable.TradeView_buyButton);
  		String sell = a.getString(R.styleable.TradeView_sellButton);
  		
  		if (buy != null)
  			((Button)findViewById(R.id.buyButton)).setText(buy);
  		if (sell != null)
  			((Button)findViewById(R.id.sellButton)).setText(sell);
  		
  		((Spinner)findViewById(R.id.sellType)).setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position > 0)
					findViewById(R.id.sellLimitationText).setVisibility(View.VISIBLE);
				else
					findViewById(R.id.sellLimitationText).setVisibility(View.GONE);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
  		
  		((Spinner)findViewById(R.id.buyType)).setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position > 0)
					findViewById(R.id.buyLimitationText).setVisibility(View.VISIBLE);
				else
					findViewById(R.id.buyLimitationText).setVisibility(View.GONE);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});

  		a.recycle();
	}

}
