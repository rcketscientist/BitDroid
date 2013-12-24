package com.anthonymandra.bitfinex.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anthonymandra.BitDroid.R;
import com.anthonymandra.bitfinex.Order;

public class OrderRow extends LinearLayout {

	public OrderRow(Context context) {
		super(context);
		initOrderRow(context);
	}

	public OrderRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		initOrderRow(context);
	}
	
	public OrderRow(Context context, Order o) {
		super(context);
		initOrderRow(context);
		setOrder(o);
	}
	
	private void initOrderRow(Context context)
	{
		((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.order_row, this, true);
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}
	
	public void setOrder(Order o)
	{
		if (o == null)
			return;
		
		setTag(o.id);
		
		if (o.isSell)
		{
			setBackgroundResource(R.color.red_price);
		}
		else
		{
			setBackgroundResource(R.color.green_price);
		}
		
		TextView id = ((TextView) findViewById(R.id.orderId));
		id.setText("" + o.id);
		id.setTypeface(Typeface.DEFAULT);
		
		TextView pair = ((TextView) findViewById(R.id.orderPair));
		pair.setText(o.Pair);
		pair.setTypeface(Typeface.DEFAULT);
		
		TextView type = ((TextView) findViewById(R.id.orderType));
		type.setText(o.Type);
		type.setTypeface(Typeface.DEFAULT);
		
		TextView amount = ((TextView) findViewById(R.id.orderAmount));
		amount.setText(String.format("%.4f", o.Amount));
		amount.setTypeface(Typeface.DEFAULT);
		
		TextView price = ((TextView) findViewById(R.id.orderPrice));
		price.setText(String.format("%.2f", o.Price));
		price.setTypeface(Typeface.DEFAULT);
		
//		TextView route = ((TextView)row.findViewById(R.id.orderRoute));
//		route.setText(String.format("%.2f", o.Route));
//		route.setTypeface(Typeface.DEFAULT);

//		double time = 0;
//		String label = " sec";
//		double deltaSeconds = System.currentTimeMillis() / 1000 - o.Timestamp;
//		if (deltaSeconds > 3600)
//		{
//			time = (int)(deltaSeconds / 3600);
//			label = " hr";
//		}
//		else if (deltaSeconds > 60)
//		{
//			time = (int)(deltaSeconds / 60);
//			label = " min";
//		}
//						
//		TextView age = ((TextView)row.findViewById(R.id.orderAge));
//		age.setText(time + label);
//		age.setTypeface(Typeface.DEFAULT);
//
//		TextView status = ((TextView)row.findViewById(R.id.orderStatus));
//		status.setText(o.Status);
//		status.setTypeface(Typeface.DEFAULT);
	}

}
