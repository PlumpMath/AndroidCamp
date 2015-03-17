package com.cmr.andevcongeodemo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements
	GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
	LocationListener{
	
	private final static String TAG = "MainActivity";
	private final static String SENDER_ID = "748790230476";
	private GoogleCloudMessaging gcm;
	private String registrationId;
	private NotificationHub hub;
	private Activity mActivity;
	

	private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mActivity = this;

		gcm = GoogleCloudMessaging.getInstance(this);

		String connectionString = "ListenConnectionString";
		hub = new NotificationHub("HubName", connectionString, this);
		NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);

		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.setMyLocationEnabled(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        registerWithGcm();
	}
	
	@SuppressWarnings("unchecked")
	private void registerWithGcm() {
	   new AsyncTask() {
	      @Override
	      protected Object doInBackground(Object... params) {
	         try {
	            registrationId = gcm.register(SENDER_ID);
	            Log.i(TAG, "Registered with id: " + registrationId);
	            
	         } catch (Exception e) {
	            return e;
	         }
	         return null;
	     }
	      
	      protected void onPostExecute(Object result) {
	    	  	Toast.makeText(mActivity, "Registered for GCM", Toast.LENGTH_SHORT).show();
	      };
	   }.execute(null, null, null);
	}
	
	@Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.w(TAG, "Location: " + msg);
        
        mGoogleApiClient.disconnect();
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude()); 
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, 10.0f);
        mMap.animateCamera(update);
        (new GetAddressTask(this)).execute(location);
        
    }
	
	public void registerWithCity(final String city) {
		Log.i(TAG, "Registering with city");
		
		new AsyncTask() {
		      @Override
		      protected Object doInBackground(Object... params) {
		         try {
		        	 	hub.register(registrationId, "AndroidUser", "AllUsers", city.replace(" ", ""));
		         } catch (Exception e) {
		        	 	Log.e(TAG, "Issue registering with hub with tag: " + e.getMessage());
		            return e;
		         }
		         return null;
		     }
		      protected void onPostExecute(Object result) {
		    	  	Toast.makeText(mActivity, "Registered with hubs", Toast.LENGTH_SHORT).show();
		    	  		    	  	
		      };
		   }.execute(null, null, null);
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }
	
	@Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }
	
	@Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        Log.i(TAG, "GoogleApiClient connection has failed");
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this, 9000);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (Exception e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        		Log.e(TAG, "Error: " + connectionResult.getErrorCode());
        }
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_find_location) {
            mGoogleApiClient.connect();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	
	private class GetAddressTask extends
    		AsyncTask<Location, Void, String> {
			Context mContext;
			
			public GetAddressTask(Context context) {
				super();
				mContext = context;
			}

		/**
		 * Get a Geocoder instance, get the latitude and longitude
		 * look up the address, and return it
		 *
		 * @params params One or more Location objects
		 * @return A string containing the address of the current
		 * location, or an empty string if no address can be found,
		 * or an error message
		 */
		@Override
		protected String doInBackground(Location... params) {
			
			List<Address> addresses = getFromLocation(params[0].getLatitude(), params[0].getLongitude(), 1);
			if (addresses == null || addresses.size() == 0)
				return "Redmond, WA";
			else
				return addresses.get(0).getLocality();
	}
		
		@Override
        protected void onPostExecute(String address) {
            Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
            //Register for Notification Hubs
            registerWithCity(address);
        }
	}
	
public static List<Address> getFromLocation(double lat, double lng, int maxResult){
		
		String address = String.format(Locale.ENGLISH,"http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="+Locale.getDefault().getCountry(), lat, lng);
		HttpGet httpGet = new HttpGet(address);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		List<Address> retList = null;
		
		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
			
			
			retList = new ArrayList<Address>();
			
			
			if("OK".equalsIgnoreCase(jsonObject.getString("status"))){
				JSONArray results = jsonObject.getJSONArray("results");
				for (int i=0;i<results.length();i++ ) {
					JSONObject result = results.getJSONObject(i);
					JSONArray components = result.getJSONArray("address_components");
					for (int j=0; j< components.length(); j++) {
						String type = components.getJSONObject(j).getString("types");
						if (type.contains("\"locality\"")) {
							Address addr = new Address(Locale.US);
							addr.setLocality(components.getJSONObject(j).getString("long_name"));							
							retList.add(addr);
						}
					}
				}
			}
		} catch (ClientProtocolException e) {
			Log.e(TAG, "Error calling Google geocode webservice.", e);
		} catch (IOException e) {
			Log.e(TAG, "Error calling Google geocode webservice.", e);
		} catch (JSONException e) {
			Log.e(TAG, "Error parsing Google geocode webservice response.", e);
		}
		return retList;
	}
}
