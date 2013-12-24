package com.anthonymandra.BitDroid;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonymandra.bitfinex.Balance;
import com.anthonymandra.bitfinex.Order;
import com.anthonymandra.bitfinex.Tags;
import com.anthonymandra.bitfinex.widget.BalancesTable;
import com.anthonymandra.bitfinex.widget.ExpandableRefreshHeader;
import com.anthonymandra.bitfinex.widget.OrderDialog;
import com.anthonymandra.bitfinex.widget.OrderRow;

/**
 * Created by amand_000 on 11/24/13.
 */
public abstract class TradeFragment extends BitfinexFragment {
	private static final String TAG = TradeFragment.class.getSimpleName();

	protected Balance balance;

	protected double currentBid = 0;
	protected double currentAsk = 0;
	TextView bidText;
	TextView askText;
	private BalancesTable balanceTable;

	private ArrayList<Order> orders = new ArrayList<Order>();
	private TableLayout ordersTable;
	private ExpandableRefreshHeader orderHeader;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		balanceTable = (BalancesTable) getView().findViewById(
				R.id.balancesTable);

		balanceTable.setOnClickListenerRefresh(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new BalanceTask().executeOnExecutor(EXECUTER);
			}
		});

		lookupViews();
		attachButtons();
	}

	private void lookupViews() {
		ordersTable = (TableLayout) getView().findViewById(R.id.ordersTable);
	}

	private void attachButtons() {
		OrdersClickListener ocl = new OrdersClickListener();
		orderHeader = (ExpandableRefreshHeader) getView().findViewById(
				R.id.ordersHeader);
		orderHeader.setOnClickListenerHeader(ocl);
		orderHeader.setOnClickListenerRefresh(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new OrdersTask().executeOnExecutor(EXECUTER);
			}
		});
	}

	@Override
	public void onResume() {
		// Set initial bid/ask
		super.onResume();
		bidText = ((TextView) getView().findViewById(R.id.bidText));
		askText = ((TextView) getView().findViewById(R.id.askText));
		new BalanceTask().executeOnExecutor(EXECUTER);
		new OrdersTask().executeOnExecutor(EXECUTER);
	}

	private class PostOrderTask extends AsyncTask<Boolean, Void, Void> {
		@Override
		protected Void doInBackground(Boolean... params) {
			postOrder(params[0], params[1]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			updateTicker();
		}
	}

	private class OrdersClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (ordersTable.getVisibility() == View.GONE) {
				ordersTable.setVisibility(View.VISIBLE);
				orderHeader.expand();
			} else {
				ordersTable.setVisibility(View.GONE);
				orderHeader.collapse();
			}
		}

	}

	private class CancelOrderTask extends AsyncTask<Integer, Void, Void> {
		@Override
		protected Void doInBackground(Integer... params) {
			cancelO(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// updateTicker();
		}
	}

	public void cancelOrder(int orderId) {
		new CancelOrderTask().executeOnExecutor(EXECUTER, orderId);
	}

	private void cancelO(int orderId) {
		try {
			String request = "/v1/order/cancel";
			String URL = BITFINEX_URL + request;
			JSONObject payload = new JSONObject();
			payload.put("order_id", orderId);
			Header[] headers;

			headers = prepareHeaders(request, payload, true);
			String result = postHttpRequest(URL, headers);
			final boolean isCancelled;
			if (result == null) {
				isCancelled = false;
			} else {
				isCancelled = new JSONObject(result)
						.getBoolean(Tags.IS_CANCELLED);
			}
			String message = orderId + ": ";
			if (isCancelled) {
				message += getString(R.string.cancelSuccess);
				new OrdersTask().executeOnExecutor(EXECUTER);
			} else {
				message += getString(R.string.cancelFail);
			}
			final String m = message;
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					Toast.makeText(getActivity(), m, Toast.LENGTH_LONG).show();
				}
			});
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

	private void getOrders() {
		try {
			String request = "/v1/orders";
			String URL = BITFINEX_URL + request;
			Header[] headers = prepareHeaders(request, new JSONObject(), true);
			String result = getHttpResult(URL, headers);
			if (result == null)
				return;
			JSONArray pArray = new JSONArray(result);

			orders.clear();
			for (int i = 0; i < pArray.length(); ++i) {
				orders.add(new Order(pArray.getJSONObject(i)));
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

	private class OrdersTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			getOrders();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			ordersTable.removeAllViews();

			View header = ((LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE)).inflate(
					R.layout.order_row, null);
			header.setBackgroundResource(R.color.neutral);
			ordersTable.addView(header);
			for (final Order o : orders) {
				OrderRow row = new OrderRow(getActivity(), o);
				row.setClickable(true);
				row.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final OrderDialog od = new OrderDialog(getActivity(), o);
						od.setOnCancelOrderListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								cancelOrder(od.getOrder().id);
								od.dismiss();
							}
						});
						od.show();
					}
				});
				ordersTable.addView(row);
			}
		}
	}

	public void placeOrder(boolean isBuy, boolean isMargin) {
		new PostOrderTask().executeOnExecutor(EXECUTER, isBuy, isMargin);
	}

	protected String getQuantity() {
		return ((EditText) getView().findViewById(R.id.quantityTransaction))
				.getText().toString();
	}

	protected String getBuyType() {
		return (String) ((Spinner) getView().findViewById(R.id.buyType))
				.getSelectedItem();
	}

	protected String getSellType() {
		return (String) ((Spinner) getView().findViewById(R.id.sellType))
				.getSelectedItem();
	}

	protected String getBuyConstraint() {
		return ((EditText) getView().findViewById(R.id.buyLimitationText))
				.getText().toString();
	}

	protected String getSellConstraint() {
		return ((EditText) getView().findViewById(R.id.sellLimitationText))
				.getText().toString();
	}

	private void postOrder(boolean isBuy, boolean isMargin) {
		String side = isBuy ? Tags.BUY : Tags.SELL;
		int typeId = isBuy ? R.id.buyType : R.id.sellType;
		int type = ((Spinner) getView().findViewById(typeId))
				.getSelectedItemPosition();
		String constraint = isBuy ? getBuyConstraint() : getSellConstraint();

		try {
			JSONObject payload = new JSONObject();

			payload.put(Tags.SYMBOL, "btcusd");
			payload.put(Tags.AMOUNT, "1");
			payload.put(Tags.PRICE, "1");
			payload.put(Tags.EXCHANGE, "all"); // TODO: Implement routing
			payload.put(Tags.SIDE, "buy");
			payload.put(Tags.TYPE, "limit");

			// payload.put("symbol", "btcusd");
			// payload.put("amount", "100");
			// payload.put("price", "1.00000000");
			// payload.put("exchange", "all"); // TODO: Implement routing
			// payload.put("side", "buy");
			// payload.put("type", "limit");
			// payload.put(Tags.IS_HIDDEN, "false");

			// switch (type)
			// {
			// case 0:
			// payload.put(Tags.TYPE, isMargin ? Tags.MARGIN_MARKET_TYPE :
			// Tags.EXCHANGE_MARKET_TYPE);
			// break;
			// case 1:
			// payload.put(Tags.TYPE, isMargin ?
			// "LIMIT"/*Tags.MARGIN_LIMIT_TYPE*/ : Tags.EXCHANGE_LIMIT_TYPE);
			// payload.put(Tags.PRICE, Double.valueOf(constraint));
			// break;
			// case 2:
			// payload.put(Tags.TYPE, isMargin ? Tags.MARGIN_STOP_TYPE :
			// Tags.EXCHANGE_STOP_TYPE);
			// payload.put(Tags.PRICE, constraint);
			// break;
			// case 3:
			// payload.put(Tags.TYPE, isMargin ? Tags.MARGIN_TRAILING_STOP_TYPE
			// : Tags.EXCHANGE_TRAILING_STOP_TYPE);
			// payload.put(Tags.PRICE, constraint);
			// break;
			// case 4:
			// payload.put(Tags.TYPE, isMargin ? Tags.MARGIN_FILL_OR_KILL_TYPE :
			// Tags.EXCHANGE_FILL_OR_KILL_TYPE);
			// payload.put(Tags.PRICE, constraint);
			// break;
			// default:
			// Log.e(TAG, "WTF");
			// }

			String request = "/v1/order/new";
			String URL = BITFINEX_URL + request;
			Header[] headers = prepareHeaders(request, payload, true);
			String result = postHttpRequest(URL, headers);
			if (result == null)
				return;

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

	protected class PriceTimer extends TimerTask {
		@Override
		public void run() {
			new UpdateTickerTask().executeOnExecutor(EXECUTER);
		}
	}

	private class UpdateTickerTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			getTicker();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			updateTicker();
		}
	}

	protected class BalanceTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			getBalances();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			balanceTable.updateBalances(balance);
		}
	}

	protected void updateTicker() {
		bidText.setText("" + currentBid);
		askText.setText("" + currentAsk);
	}

	private void getTicker() {
		JSONObject jObject = getJSONObject(BITFINEX_URL + "/v1/ticker/btcusd");
		if (jObject == null)
			return;

		try {
			currentAsk = jObject.getDouble(Tags.ASK);
			currentBid = jObject.getDouble(Tags.BID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void getBalances() {
		try {
			String request = "/v1/balances";
			String URL = BITFINEX_URL + request;
			Header[] headers = prepareHeaders(request, new JSONObject(), true);
			String result = getHttpResult(URL, headers);
			if (result == null)
				return;
			JSONArray pArray = new JSONArray(result);

			balance = new Balance(pArray);

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
	
	protected void setBidPrice(double price)
	{
		currentBid = price;
	}
	
	protected void setAskPrice(double price)
	{
		currentAsk = price;
	}
}
