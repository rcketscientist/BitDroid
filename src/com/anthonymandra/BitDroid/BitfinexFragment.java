package com.anthonymandra.BitDroid;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public abstract class BitfinexFragment extends SherlockFragment {
	private static final String TAG = BitfinexFragment.class.getSimpleName();

	protected static final String BITFINEX_URL = "https://api.bitfinex.com";
	protected static final String API_PATH = "/v1/";
	protected static final String REQUEST_KEY = "request";
	protected static final String NONCE_KEY = "nonce";
	protected static final String API_KEY = "X-BFX-APIKEY";
	protected static final String SIGNATURE_KEY = "X-BFX-SIGNATURE";
	protected static final String PAYLOAD_KEY = "X-BFX-PAYLOAD";

	private static final String KEYSTORE_API = "api";
	private static final String KEYSTORE_SECRET = "secret";

	protected static String ALGORITHM = "HmacSHA384";
	protected static final Executor EXECUTER = Executors.newCachedThreadPool();

	private static final byte[] SALT = { (byte) 0x22, (byte) 0x33, (byte) 0x10,
			(byte) 0x05, (byte) 0xde, (byte) 0x44, (byte) 0x11, (byte) 0x12, };

	private static String password;
	
	//TODO: Need keymanager
	protected static void initialize(Context c)
	{
		checkPassCode(c);
//		checkApiKeys(c);
	}
	
	protected static void checkPassCode(Context c)
	{
		if (hasPassword())
			return;

		showPasscodeDialog(c);
		if (hasKey())
			storeApiKey(c);	//TODO: Ghetto popups order isn't guaranteed so make sure we store
		else
			loadApiKey(c);
	}
	
	protected static void checkApiKeys(Context c)
	{
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(c);
		if (hasKey())
			return;
		
		if (sp.contains(KEYSTORE_API))
		{
			loadApiKey(c);
		}
		else
		{
			showApiDialog(c);
		}
	}
	
	protected static void showApiDialog(final Context c)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(c);

		alert.setTitle(R.string.apiTitle);
		alert.setMessage(R.string.apiMessage);
        		
		LayoutInflater inf = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout myLayout = (LinearLayout) inf.inflate(R.layout.api_layout, null);
		
		final EditText apiText = (EditText) myLayout.findViewById(R.id.apiText);
		final EditText secretText = (EditText) myLayout.findViewById(R.id.secretText);
		final Button apiButton = (Button) myLayout.findViewById(R.id.getApiButton);
		alert.setView(myLayout);
		apiButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent viewIntent = new Intent("android.intent.action.VIEW", 
						Uri.parse("https://www.bitfinex.com/account/api"));
				c.startActivity(viewIntent); 
			}
		});		

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			key = apiText.getText().toString();
			secret = secretText.getText().toString();
			storeApiKey(c);
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		  }
		});

		alert.show();
	}

	protected static boolean hasKey()
	{
		return key != null;
	}
	
	protected static boolean hasPassword()
	{
		return password != null;
	}
	
	protected static void showPasscodeDialog(final Context c)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(c);

		alert.setTitle(R.string.passwordTitle);
		alert.setMessage(R.string.passwordMessage);

		// Set an EditText view to get user input 
		final EditText input = new EditText(c);
//		input.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			password = input.getText().toString();
			checkApiKeys(c);
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		  }
		});

		alert.show();
	}

	protected static void storeApiKey(Context c) {
		if (password == null)
			return;
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(c);

		try {
			sp.edit().putString(KEYSTORE_API, encrypt(key))
					.putString(KEYSTORE_SECRET, encrypt(secret)).commit();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	protected static void loadApiKey(Context c)
	{
		if (password == null)
			return;
		
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(c);
		try {
			key = decrypt(sp.getString(KEYSTORE_API, ""));
			secret = decrypt(sp.getString(KEYSTORE_SECRET, ""));
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected static String encrypt(String property) throws IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException,
			InvalidKeySpecException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException {
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher
				.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return Base64.encodeToString(
				pbeCipher.doFinal(property.getBytes("UTF-8")), Base64.DEFAULT);
	}

	private static String decrypt(String property)
			throws GeneralSecurityException, IOException {
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher
				.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return new String(pbeCipher.doFinal(Base64.decode(property,
				Base64.DEFAULT)), "UTF-8");
	}

	protected Header[] prepareHeaders(String request, JSONObject payload,
			boolean sign) throws UnsupportedEncodingException,
			NoSuchAlgorithmException, InvalidKeyException {
		if (!hasKey())
		{
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(getActivity(), "API keys not entered,  function not available", Toast.LENGTH_LONG);		
				}
			});
			return null;
		}
		try {
			payload.put(REQUEST_KEY, request);
			payload.put(NONCE_KEY, Long.toString(System.currentTimeMillis()));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] data = payload.toString().getBytes();
		String payload64 = Base64.encodeToString(data, Base64.NO_WRAP);

		Header[] headers;
		if (sign) {
			Mac mac = Mac.getInstance(ALGORITHM);
			SecretKeySpec sk = new SecretKeySpec(secret.getBytes(),
					mac.getAlgorithm());
			mac.init(sk);
			byte[] encrypted = mac.doFinal(payload64.getBytes());

			StringBuffer hash = new StringBuffer();

			for (byte b : encrypted) {
				String hex = Integer.toHexString(0xFF & b);
				if (hex.length() == 1) {
					hash.append('0');
				}
				hash.append(hex);
			}
			String signature = hash.toString();

			headers = new BasicHeader[3];
			headers[0] = new BasicHeader(API_KEY, key);
			headers[1] = new BasicHeader(SIGNATURE_KEY, signature);
			headers[2] = new BasicHeader(PAYLOAD_KEY, payload64);
		} else {
			headers = new BasicHeader[1];
			headers[0] = new BasicHeader(PAYLOAD_KEY, payload64);
		}

		return headers;
	}

	protected String getHttpResult(String URL) {
		HttpGet request = new HttpGet(URL);
		return getHttpResult(request);
	}

	protected String getHttpResult(String URL, Header[] headers) {
		HttpGet request = new HttpGet(URL);
		request.setHeaders(headers);
		return getHttpResult(request);
	}

	protected String postHttpRequest(String URL, Header[] headers) {
		HttpPost request = new HttpPost(URL);
		request.setHeaders(headers);
		return getHttpResult(request);
	}

	protected String getHttpResult(final HttpRequestBase request) {

		InputStream content = null;
		String result = null;
		HttpClient http = getNewHttpClient();// new DefaultHttpClient();
		try {
			HttpResponse response = http.execute(request);
			final StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				return EntityUtils.toString(response.getEntity());
			} else {
				// TODO: Should work on a better result system
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Log.w(TAG, "Error (" + request.getURI().getPath()
								+ "): " + statusLine.getReasonPhrase());
						Toast.makeText(
								getActivity(),
								"Error (" + request.getURI().getPath() + "): "
										+ statusLine.getReasonPhrase(),
								Toast.LENGTH_LONG).show();
					}
				});
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeSilently(content);
		}

		return result;
	}

	protected JSONArray getJSONArray(String URL) {
		String result = getHttpResult(URL);
		if (result == null)
			return null;

		try {
			return new JSONArray(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected JSONObject getJSONObject(String URL) {
		String result = getHttpResult(URL);
		if (result == null)
			return null;

		try {
			return new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected static void closeSilently(Closeable c) {
		if (c == null)
			return;
		try {
			c.close();
		} catch (IOException t) {
			Log.w(TAG, "close fail ", t);
		}
	}

	// TODO: MySSLSocketFactory and getNewHttpClient allow all certificates to
	// get around ssl error,
	// This should be fixed in produciton
	public class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

}
