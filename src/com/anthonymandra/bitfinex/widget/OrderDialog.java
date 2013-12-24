package com.anthonymandra.bitfinex.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anthonymandra.BitDroid.R;
import com.anthonymandra.bitfinex.Order;

public class OrderDialog extends Dialog {

	Order order;
	public OrderDialog(Context context, Order o) {
		super(context);
		
		setContentView(R.layout.order_dialog);
		setTitle(R.string.orderDetails);
		
		order = o;
		TextView id = ((TextView) findViewById(R.id.orderId));
		id.setText("" + o.id);
		
		TextView pair = ((TextView) findViewById(R.id.orderPair));
		pair.setText(o.Pair);
		
		TextView side = ((TextView) findViewById(R.id.orderSide));
		side.setText(o.isSell ? getContext().getString(R.string.sell) : getContext().getString(R.string.buy));
		
		TextView type = ((TextView) findViewById(R.id.orderType));
		type.setText(o.Type);
		
		TextView amount = ((TextView) findViewById(R.id.orderAmount));
		amount.setText(String.format("%.4f", o.Amount));
		
		TextView price = ((TextView) findViewById(R.id.orderPrice));
		price.setText(String.format("%.4f", o.Price));
		
		TextView route = ((TextView) findViewById(R.id.orderRoute));
		route.setText(o.Route);

		double time = 0;
		String label = " sec";
		double deltaSeconds = System.currentTimeMillis() / 1000 - o.Timestamp;
		if (deltaSeconds > 3600)
		{
			time = (int)(deltaSeconds / 3600);
			label = " hr";
		}
		else if (deltaSeconds > 60)
		{
			time = (int)(deltaSeconds / 60);
			label = " min";
		}

		TextView age = ((TextView) findViewById(R.id.orderAge));
		age.setText(time + label);

		TextView status = ((TextView) findViewById(R.id.orderStatus));
		status.setText(o.Status);
	}
	
	public void setOnCancelOrderListener(View.OnClickListener listener)
	{
		Button cancelOrder = (Button) findViewById(R.id.cancelButton);
		cancelOrder.setOnClickListener(listener);
	}
	
	public Order getOrder()
	{
		return order;
	}
}
