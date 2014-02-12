package alpha.android.fragments;

import java.util.List;
import java.util.Locale;

import alpha.android.R;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment
{
	private GoogleMap googleMap;
	private Location lastLoc;
	private MarkerOptions marker;
	private String providerName;
	private LocationManager locMan;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_content_map, container, false);
		
		initPos();
		initilizeMap();

		return view;
	}

	
	private void initPos()
	{
		locMan = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		List<String> provList = locMan.getProviders(true);
		
		Log.d("provider", "Provider listing: " + provList.size());
		
		for (String provName : provList)
			Log.d("provider", provName);

		// define some criteria for the location provider
		Criteria provProps = new Criteria();
		provProps.setCostAllowed(false);
		provProps.setAccuracy(Criteria.ACCURACY_FINE);
		provProps.setPowerRequirement(Criteria.POWER_HIGH);

		// get a location provider with these criteria
		providerName = locMan.getBestProvider(provProps, true);
		
		Log.d("provider", "Using " + providerName);
		
		if (providerName == null || !locMan.isProviderEnabled(providerName))
		{
			// if none is found use default
			providerName = LocationManager.NETWORK_PROVIDER;
		}

		if (!locMan.isProviderEnabled(providerName))
		{
			String text = "No working location provider found!";
			Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_LONG).show();

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
            googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
 
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        
        marker = new MarkerOptions();
        marker.title("Hier");
        
        updatePos();
        
        // adding marker
        googleMap.addMarker(marker);
    }
    
    private void updatePos() {
    	
    	lastLoc = locMan.getLastKnownLocation(providerName);
    	
    	if (lastLoc != null)
    		marker.position(new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude()));
    	else
    		marker.position(new LatLng(0, 0));
    }

	/**
	 * Converts a location into a readable version with latitude and longitude
	 * @param loc
	 * @return
	 */ 
	public String getLocationString(Location loc) {
		if (loc == null) {
			return "Undefined";
		} else {
			return String.format(Locale.US, "(%.2f,%.2f)", loc.getLatitude(), loc.getLongitude());
		}
	}

	private class LocationUpdateHandler implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			updatePos();
			
			Toast.makeText(getActivity().getApplicationContext(),
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
