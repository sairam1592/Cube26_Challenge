package com.example.cube26adc;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsSelection extends Activity implements
		OnItemSelectedListener {

	private static final String TAG_Id = "_id";
	private static final String TAG_TrainNo = "trainNo";
	private static final String TAG_TrianName = "trainName";
	private static final String TAG_LineNo = "islno";
	private static final String TAG_StationCode = "stationCode";
	private static final String TAG_StationName = "stationName";
	private static final String TAG_ArrivalTime = "arrivalTime";
	private static final String TAG_DepartureTime = "departureTime";
	private static final String TAG_Dist = "distance";
	private static final String TAG_srcCode = "sourceStationCode";
	private static final String TAG_srcStatName = "sourceStationName";
	private static final String TAG_destStCode = "destinationStationCode";
	private static final String TAG_destStName = "destinationStationName";

	JSONArray user = null;
	ArrayList<HashMap<String, String>> srcDestList;
	ListView lv = null;
	String text;
	ArrayList<String> station_List = null;
	Spinner source, destination;
	String src_item;
	String dest_item;
	AlertDialog.Builder alert;
	AlertDialog dialog;
	String id_;
	Button fetch;
	TextView countValue, countLabel;
	SimpleAdapter adapter=null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_layout);
		Intent i = getIntent();
		text = i.getStringExtra("Detail_Page");
		countLabel = (TextView) findViewById(R.id.textView4);
		countValue = (TextView) findViewById(R.id.textView_count);
		countLabel.setVisibility(View.INVISIBLE);
		countValue.setVisibility(View.INVISIBLE);
		station_List = (ArrayList<String>) i
				.getSerializableExtra("Station_List");
		srcDestList = new ArrayList<HashMap<String, String>>();
		Toast.makeText(DetailsSelection.this, text, Toast.LENGTH_LONG).show();
		lv = (ListView) findViewById(R.id.listView_list);
		fillSpinners(station_List);

		((Button) findViewById(R.id.button_srcdestdetails))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// TODO Auto-generated method stub
						onSourceDestSelected();
					}
				});
		listViewItemSelect();

	}
	

	public void fillSpinners(ArrayList<String> stationList) {
		// source spinner fillUp
		source = (Spinner) findViewById(R.id.spinner_source);
		ArrayAdapter<String> adapter_source = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, stationList);
		adapter_source
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		source.setAdapter(adapter_source);
		source.setOnItemSelectedListener(this);

		// Destination spinner fillUp

		destination = (Spinner) findViewById(R.id.spinner_destination);
		ArrayAdapter<String> adapter_destination = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, stationList);
		adapter_destination
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		destination.setAdapter(adapter_destination);
		destination.setOnItemSelectedListener(this);

	}

	public void onSourceDestSelected() {
		if (!source.getSelectedItem().toString().equalsIgnoreCase("")
				&& !destination.getSelectedItem().toString()
						.equalsIgnoreCase("")) {
			if (ConnectionCheck()) {
				new MyClientTask(source.getSelectedItem().toString(),
						destination.getSelectedItem().toString(),
						DetailsSelection.this).execute();
			} else {
				Toast.makeText(DetailsSelection.this,
						"Please check internet connection.", Toast.LENGTH_LONG)
						.show();
			}

		} else {
			Toast.makeText(
					DetailsSelection.this,
					"Please select source and destination data to proceed further.",
					Toast.LENGTH_LONG).show();
		}

	}

	private class MyClientTask extends AsyncTask<Void, Void, Void> {

		String strSource, strDest;
		String url;
		private ProgressDialog dialog;
		Context context = null;

		MyClientTask(String src, String dest, Context contex) {
			strSource = src;
			strDest = dest;
			context = contex;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setCancelable(false);
			dialog.setMessage("Please wait while we fetch the train details..");
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			url = "http://cube26-1337.0x10.info/trains?source="
					+ strSource.replace(" ", "%20") + "&destination="
					+ strDest.replace(" ", "%20") + "";
			srcDestList.clear();
			JSONParser jParser = new JSONParser();
			JSONArray jsonArr = jParser.getJsonArrayfromUrl(url);
			Log.i("Detail Section", jsonArr.toString());
			try {

				if (jsonArr.length() > 0) {

					for (int i = 0; i < jsonArr.length(); i++) {

						JSONObject c = jsonArr.getJSONObject(i);
						String id = c.getString(TAG_Id);
						String trainNo = c.getString(TAG_TrainNo);
						String trainName = c.getString(TAG_TrianName);
						String lineNo = c.getString(TAG_LineNo);
						String stationCode = c.getString(TAG_StationCode);
						String stationName = c.getString(TAG_StationName);
						String arrivalTime = c.getString(TAG_ArrivalTime);
						String departureTime = c.getString(TAG_DepartureTime);
						String dist = c.getString(TAG_Dist);
						String srcCode = c.getString(TAG_srcCode);
						String srcName = c.getString(TAG_srcStatName);
						String destCode = c.getString(TAG_destStCode);
						String destName = c.getString(TAG_destStName);

						HashMap<String, String> details = new HashMap<String, String>();

						details.put(TAG_Id, id);
						details.put(TAG_TrainNo, trainNo);
						details.put(TAG_TrianName, trainName);
						details.put(TAG_LineNo, lineNo);
						details.put(TAG_StationCode, stationCode);
						details.put(TAG_StationName, stationName);
						details.put(TAG_ArrivalTime, arrivalTime);
						details.put(TAG_DepartureTime, departureTime);
						details.put(TAG_Dist, dist);
						details.put(TAG_srcCode, srcCode);
						details.put(TAG_srcStatName, srcName);
						details.put(TAG_destStCode, destCode);
						details.put(TAG_destStName, destName);

						srcDestList.add(details);
					}
				} else {

					HashMap<String, String> error_map = new HashMap<String, String>();
					error_map.put("Error", "no_data_found");
					srcDestList.add(error_map);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;

		}

		protected void onPostExecute(Void result) {
			dialog.dismiss();

			countLabel.setVisibility(View.VISIBLE);
			countValue.setVisibility(View.VISIBLE);
			
			if (srcDestList.get(0).containsKey("Error")) {
				showAlert("No details present for the given information.");
				lv.clearChoices();
				lv.setAdapter(adapter);
				countValue.setText("0");
				//adapter.notifyDataSetChanged();
			} else {
					adapter = new SimpleAdapter(DetailsSelection.this,
							srcDestList, R.layout.list_item, new String[] {
									TAG_TrainNo, TAG_TrianName,
									TAG_srcStatName, TAG_destStName, TAG_Id },
							new int[] { R.id.textView_no, R.id.textView_name,
									R.id.textView_from, R.id.textView_to,
									R.id.textView_id });
						lv.setAdapter(adapter);	
						adapter.notifyDataSetChanged();		
					
				
				countValue.setText(String.valueOf(srcDestList.size()));

			}
		}
	}

	public void listViewItemSelect() {

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// String item =(String) (lv.getItemAtPosition(position));
				HashMap<String, String> map = (HashMap<String, String>) lv
						.getItemAtPosition(position);
				id_ = map.get(TAG_Id);
				StringBuilder sb = new StringBuilder();
				sb.append("Hi user, Please find the train details below for your information:\n");
				sb.append("Train No:" + map.get(TAG_TrainNo));
				sb.append("\nTrain Name:" + map.get(TAG_TrianName));
				sb.append("\nLine No:" + map.get(TAG_LineNo));
				sb.append("\nStation Code:" + map.get(TAG_StationCode));
				sb.append("\nStation Name:" + map.get(TAG_StationName));
				sb.append("\nArrival Time:" + map.get(TAG_ArrivalTime));
				sb.append("\nDeparture Time:" + map.get(TAG_DepartureTime));
				sb.append("\nDistance:" + map.get(TAG_Dist));
				sb.append("\nSource:" + map.get(TAG_srcStatName));
				sb.append("\nDestination:" + map.get(TAG_destStName));

				showAlert(sb.toString());
			}
		});
	}

	public void showAlert(String content) {
		final Context context = this;
		alert = new AlertDialog.Builder(context);
		dialog = alert.create();
		dialog.setTitle("NOTE");
		dialog.setMessage(content);
		dialog.setButton(Dialog.BUTTON_NEGATIVE, "Close",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		dialog.show();
	}

	public void onShareClick() {
		String trainNo = null;
		String trainName = null;
		String lineNo = null;
		String stationCode = null;
		String stationName = null;
		String arrivalTime = null;
		String departureTime = null;
		String dist = null;
		String srcCode = null;
		String srcName = null;
		String destCode = null;
		String destName = null;

		if (srcDestList.size() > 0) {
			for (int i = 0; i < srcDestList.size(); i++) {
				if (!srcDestList.get(i).containsKey("Error")) {
					if (srcDestList.get(i).get(TAG_Id).toString()
							.contains(id_.toString())) {
						trainNo = srcDestList.get(i).get(TAG_TrainNo)
								.toString();
						trainName = srcDestList.get(i).get(TAG_TrianName)
								.toString();
						lineNo = srcDestList.get(i).get(TAG_LineNo).toString();
						stationCode = srcDestList.get(i).get(TAG_StationCode)
								.toString();
						stationName = srcDestList.get(i).get(TAG_StationName)
								.toString();
						arrivalTime = srcDestList.get(i).get(TAG_ArrivalTime)
								.toString();
						departureTime = srcDestList.get(i)
								.get(TAG_DepartureTime).toString();
						dist = srcDestList.get(i).get(TAG_Dist).toString();
						srcCode = srcDestList.get(i).get(TAG_srcCode)
								.toString();
						srcName = srcDestList.get(i).get(TAG_srcStatName)
								.toString();
						destCode = srcDestList.get(i).get(TAG_destStCode)
								.toString();
						destName = srcDestList.get(i).get(TAG_destStName)
								.toString();
						break;
					}
				} else {
					Toast.makeText(DetailsSelection.this,
							"No details present to share.", Toast.LENGTH_LONG)
							.show();

				}

			}
		} else {
			Toast.makeText(DetailsSelection.this,
					"Kindly fetch the details to proceed further",
					Toast.LENGTH_LONG).show();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Hi user, Please find the train details below for your information:\n");
		sb.append("Train No:" + trainNo);
		sb.append("\nTrain Name:" + trainName);
		sb.append("\nLine No:" + lineNo);
		sb.append("\nStation Code:" + stationCode);
		sb.append("\nStation Name:" + stationName);
		sb.append("\nArrival Time:" + arrivalTime);
		sb.append("\nDeparture Time:" + departureTime);
		sb.append("\nDistance:" + dist);
		sb.append("\nSource:" + srcName);
		sb.append("\nDestination:" + destName);
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_share:
			// share action
			onShareClick();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.spinner_source:
			break;
		case R.id.spinner_destination:
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

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
