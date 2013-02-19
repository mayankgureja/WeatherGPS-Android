package com.example.weathergps;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.SimpleAdapter;

public class GetWeather extends ListActivity
{
	public static LinkedHashMap<String, String> WeatherInfo = new LinkedHashMap<String, String>();
	static final ArrayList<HashMap<String, String>> DisplayInfo = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTitle("Weather for " + getIntent().getStringExtra("zipcode"));
		WeatherInfo.clear();
		DisplayInfo.clear();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_weather);

		new JSONParserTask(GetWeather.this).execute();
	}

	protected void fill_ListView()
	{
		try
		{
			Set<Map.Entry<String, String>> entrySet = WeatherInfo.entrySet();
			for (Entry<String, String> entry : entrySet)
			{
				HashMap<String, String> temp = new HashMap<String, String>();
				temp.put("Parameter", entry.getKey());
				temp.put("Data", entry.getValue());
				DisplayInfo.add(temp);
			}

			setContentView(R.layout.activity_get_weather);
			SimpleAdapter adapter = new SimpleAdapter(this, DisplayInfo,
					R.layout.custom_row_view, new String[] { "Parameter",
							"Data" }, new int[] { R.id.text1, R.id.text2 });

			setListAdapter(adapter);
		}
		catch (Exception e)
		{
			Log.e("A1", "ERROR in onCreate(): " + e.toString());
			e.printStackTrace();
		}
	}

	protected void JSONParser()
	{
		String next;
		String json = "";
		String zipcode = getIntent().getStringExtra("zipcode");
		String API_Key = "";
		String BASE_PATH = "http://api.wunderground.com/api/" + API_Key
				+ "/conditions/q/" + zipcode + ".json";

		try
		{
			Log.i("GPS", "Getting Weather Underground information...");
			URL url = new URL(BASE_PATH);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()));

			while ((next = bufferedReader.readLine()) != null)
			{
				json += next;
			}

			json = '[' + json + ']'; // Correcting JSON formatting from
										// WeatherUnderground

			JsonNode root = readJson(json);

			for (JsonNode node : root)
			{
				if (node.path("response").has("error"))
				{
					WeatherInfo.put("Error", node.path("response")
							.path("error").path("description").toString()
							.replace("\"", ""));
				}
				else
				{
					WeatherInfo.put(
							"Location",
							node.path("current_observation")
									.path("display_location").path("full")
									.toString().replace("\"", "")
									+ " ("
									+ node.path("current_observation")
											.path("display_location")
											.path("country").toString()
											.replace("\"", "") + ")");

					WeatherInfo.put(
							"Temperature",
							node.path("current_observation")
									.path("temperature_string").toString()
									.replace("\"", ""));

					WeatherInfo.put(
							"Feels Like",
							node.path("current_observation")
									.path("feelslike_string").toString()
									.replace("\"", ""));

					WeatherInfo.put("Summary", node.path("current_observation")
							.path("weather").toString().replace("\"", ""));

					WeatherInfo.put("Wind", node.path("current_observation")
							.path("wind_string").toString().replace("\"", ""));

					WeatherInfo.put(
							"Relative Humidity",
							node.path("current_observation")
									.path("relative_humidity").toString()
									.replace("\"", ""));

					WeatherInfo.put("Zipcode", node.path("current_observation")
							.path("display_location").path("zip").toString()
							.replace("\"", ""));

					WeatherInfo.put("Area", node.path("current_observation")
							.path("observation_location").path("city")
							.toString().replace("\"", ""));

					String[] day = node.path("current_observation")
							.path("local_time_rfc822").toString()
							.replace("\"", "").replace("\"", "").split(" ");
					String date = day[0] + " " + day[1] + " " + day[2] + " "
							+ day[3];
					String time = day[4];
					SimpleDateFormat f1 = new SimpleDateFormat("hh:mm:ss",
							Locale.getDefault());
					Date d = f1.parse(time);
					SimpleDateFormat f2 = new SimpleDateFormat("hh:mma",
							Locale.getDefault());
					time = f2.format(d).toLowerCase(Locale.getDefault());
					WeatherInfo.put("Date", date);
					WeatherInfo.put("Time", time);
				}
			}
		}
		catch (Exception e)
		{
			Log.e("GPS", "ERROR in JSONParser(): " + e.toString());
			e.printStackTrace();
		}
	}

	protected static JsonNode readJson(String json) throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(json, JsonNode.class);
		JsonParser jp = mapper.getFactory().createJsonParser(json);
		rootNode = mapper.readTree(jp);
		return rootNode;
	}

	private class JSONParserTask extends AsyncTask<String, Void, String>
	{
		private ProgressDialog mDialog;
		private Context context;

		public JSONParserTask(ListActivity activity)
		{
			context = activity;
			mDialog = new ProgressDialog(context);
		}

		@Override
		protected String doInBackground(String... params)
		{
			Log.i("GPS", "AsyncTask launched");
			try
			{
				JSONParser();
				Log.i("GPS", "AsyncTask done");
			}
			catch (Exception e)
			{
				Log.e("GPS", "ERROR in AsyncTask: " + e.toString());
				e.printStackTrace();
			}
			return "";
		}

		protected void onPreExecute()
		{
			mDialog.setMessage("Loading...");
			mDialog.setCancelable(false);
			mDialog.show();
		}

		protected void onPostExecute(String str)
		{
			Log.i("GPS", "onPostExecute launched");
			fill_ListView();
			if (mDialog.isShowing())
				mDialog.dismiss();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_get_weather, menu);
		return true;
	}
}
