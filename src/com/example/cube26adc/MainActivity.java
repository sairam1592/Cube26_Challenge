package com.example.cube26adc;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	String internet_success, internet_failure;
	Intent intent;
	ArrayList<String> stationList = null;
	private static String url = "http://cube26-1337.0x10.info/stations";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		stationList = new ArrayList<String>();
		((Button) findViewById(R.id.button_enter))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// TODO Auto-generated method stub
						onEnterButtonClick();
					}
				});
	}

	public void onEnterButtonClick() {

		if(ConnectionCheck()){
			new MyClientTask(url.toString(), MainActivity.this).execute();	
		}else{
			Toast.makeText(MainActivity.this,
					"Kindly check internet connection.",
					Toast.LENGTH_SHORT).show();
		}
		

		// intent = new Intent(MainActivity.this, DetailsSelection.class);
		// intent.putExtra("food_select_page",
		// "Kindly choose the source and destination");
		// startActivity(intent);
	}

	private class MyClientTask extends AsyncTask<Void, Void, Void> {

		String url;
		private ProgressDialog dialog;
		Context context = null;

		MyClientTask(String myUrl, Context contex) {
			url = myUrl;
			context = contex;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			JSONParser jParser = new JSONParser();
			JSONArray jArr = jParser.getJsonArrayfromUrl(url);

			try {
				Log.i("MainActivity", "Contents" + jArr.toString());
				for (int i = 0; i < jArr.length(); i++) {

					stationList.add(jArr.getString(i));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			dialog.dismiss();
			Log.i("JSON", "List is:" + stationList.toString());
			if (stationList.size() != 0) {
				intent = new Intent(MainActivity.this, DetailsSelection.class);
				intent.putExtra("Detail_Page",
						"Kindly choose the source and destination");
				intent.putExtra("Station_List", stationList);
				startActivity(intent);
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean ConnectionCheck() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		// Get network info - WIFI internet access

		NetworkInfo info = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean isWifiConn = info.isConnected();

		info = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileConn = info.isConnected();

		if (isWifiConn) {
			System.out.println("Wifi data connected:" + isWifiConn);
			return true;
		} else if (isMobileConn) {
			System.out.println("Mobile data connected:" + isMobileConn);
			return true;
		} else {
			System.out.println("No connection is present!");
			return false;
		}

	}
}
