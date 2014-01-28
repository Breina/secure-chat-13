package alpha.android;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity
{
	private GoogleMap googleMap;
	private Location lastLoc;
	private MarkerOptions marker;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmentcontainer_activity_home);
        
        initPos();
        initilizeMap();
	}
	
	
	private void initPos()
	{
		LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> provList = locMan.getProviders(true);
		Log.d("provider", "Provider listing: " + provList.size());
		
		for (String provName : provList)
		{
			Log.d("provider", provName);
		}

		// define some criteria for the location provider
		Criteria provProps = new Criteria();
		provProps.setCostAllowed(false);
		provProps.setAccuracy(Criteria.ACCURACY_COARSE);
		provProps.setPowerRequirement(Criteria.POWER_MEDIUM);

		// get a location provider with these criteria
		String providerName = locMan.getBestProvider(provProps, true);
		
		Toast popup = Toast.makeText(getApplicationContext(), "Using " + providerName, Toast.LENGTH_LONG);
		popup.show();
		
		if (providerName == null || !locMan.isProviderEnabled(providerName))
		{
			// if none is found use default
			providerName = LocationManager.NETWORK_PROVIDER;
		}

		if (!locMan.isProviderEnabled(providerName))
		{
			String text = "No working location provider found!";
			popup = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
			popup.show();
		}

		// register for location updates
		LocationUpdateHandler luha = new LocationUpdateHandler();
		locMan.requestLocationUpdates(providerName, 5000, 10, luha);

		// set initial text
		lastLoc = locMan.getLastKnownLocation(providerName);
		//this.et_log.setText(getLocationString(lastLoc) + "\n");
	}
	
	/**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
 
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        
        marker = new MarkerOptions();
        marker.title("Hier");
        
        updatePos(lastLoc);
        
        // adding marker
        googleMap.addMarker(marker);
    }
    
    private void updatePos(Location location) {
    	if (location != null)
    		marker.position(new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude()));
    	else
    		marker.position(new LatLng(0, 0));
    }

	/**
	 * Converts a location into a readable version with latitude and longitude
	 * @param loc
	 * @return
	 */ 
	private String getLocationString(Location loc) {
		if (loc == null) {
			return null;
		} else {
			return String.format(Locale.US, "(%.2f,%.2f)", loc.getLatitude(), loc.getLongitude());
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initPos();
		initilizeMap();
	}

	private class LocationUpdateHandler implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			updatePos(location);
			
			Toast.makeText(getApplicationContext(),
                    "Location changed: " + getLocationString(location), Toast.LENGTH_SHORT)
                    .show();
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

}
