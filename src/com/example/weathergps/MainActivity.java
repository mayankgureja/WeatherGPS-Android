package com.example.weathergps;

import java.util.List;
import java.util.Locale;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener
{
	private LocationManager locationManager;
	private String provider;
	String zipcode = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTitle(R.string.app_name);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.imageView1).setOnClickListener(
				new View.OnClickListener()
				{
					public void onClick(View v)
					{
						Intent browserIntent = new Intent(Intent.ACTION_VIEW,
								Uri.parse("http://www.mayankgureja.com"));
						startActivity(browserIntent);
					}
				});

		findViewById(R.id.button1).setOnClickListener(
				new View.OnClickListener()
				{

					@Override
					public void onClick(View arg0)
					{
						TextView tb = (TextView) findViewById(R.id.textView1);
						zipcode = tb.getText().toString();
						if (zipcode.isEmpty() || zipcode.length() != 5)
							Toast.makeText(MainActivity.this,
									"Please enter a valid zipcode",
									Toast.LENGTH_LONG).show();
						else
							goToWeather();
					}
				});

		findViewById(R.id.button2).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{
						enableGPS();
					}
				});
	}

	public void enableGPS()
	{
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Toast.makeText(MainActivity.this, "GPS is enabled",
					Toast.LENGTH_LONG).show();
			getLocation();
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"To automatically get weather data for your current location, turn on GPS.")
					.setTitle("Enable GPS")
					.setCancelable(false)
					.setPositiveButton("Settings",
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int id)
								{
									startActivityForResult(
											new Intent(
													Settings.ACTION_LOCATION_SOURCE_SETTINGS),
											1);
								}
							})
					.setNegativeButton("Skip",
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int id)
								{
									Toast.makeText(MainActivity.this,
											"GPS is NOT enabled",
											Toast.LENGTH_LONG).show();
									enableUserLocation();
								}
							}).show();
		}

	}

	public void getLocation()
	{
		locationManager = (LocationManager) getSystemService(MainActivity.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		if (location != null)
		{
			System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
		}
	}

	public void goToWeather()
	{
		Intent goToGetWeather = new Intent(MainActivity.this, GetWeather.class);
		goToGetWeather.putExtra("zipcode", zipcode);
		MainActivity.this.startActivity(goToGetWeather);

	}

	@Override
	public void onLocationChanged(Location location)
	{
		List<Address> addresses;

		float lat = (float) (location.getLatitude());
		float lng = (float) (location.getLongitude());

		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try
		{
			addresses = geocoder.getFromLocation(lat, lng, 1);
			for (Address item : addresses)
			{
				System.out.println("Zipcode: " + item.getPostalCode());
				zipcode = item.getPostalCode();

			}
			goToWeather();
		}
		catch (Exception e)
		{
			Log.d("Error", e.toString());
		}

	}

	public void enableUserLocation()
	{
		TextView tv = (TextView) findViewById(R.id.textView1);
		Button bt1 = (Button) findViewById(R.id.button1);
		Button bt2 = (Button) findViewById(R.id.button2);
		tv.setVisibility(0);
		bt1.setVisibility(0);
		bt2.setVisibility(0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Toast.makeText(MainActivity.this, "GPS is NOT enabled",
					Toast.LENGTH_LONG).show();
			enableUserLocation();
		}
		else
		{
			Toast.makeText(MainActivity.this, "GPS is enabled",
					Toast.LENGTH_LONG).show();
			getLocation();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onProviderDisabled(String arg0)
	{
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String arg0)
	{
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{
		// TODO Auto-generated method stub
	}

	@Override
	protected void onRestart()
	{
		setTitle(R.string.app_name);
		super.onResume();
		enableUserLocation();
	}

	@Override
	protected void onResume()
	{
		setTitle(R.string.app_name);
		super.onResume();
	}
}
