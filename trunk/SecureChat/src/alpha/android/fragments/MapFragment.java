package alpha.android.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import alpha.android.R;
import alpha.android.common.CommonUtilities;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment
{
	private static final String GPSMARKER = "GPS";
	private static final double SPACESINGLE = 0.5d;
	private static final int MAPPADDING = 50;
	private static final boolean ANIMATION = true;
	private static final boolean ZOOMTOSELECTION = false;
	
	private static final String SIZEKEY = "marker_size";
	private static final String MARKERPREFIX = "marker_";
	
	public static RelativeLayout view;

	private GoogleMap googleMap;
	private Location lastLoc;
	private MarkerOptions gpsMarker;
	private ArrayList<MarkerOptions> markers;
	private String providerName;
	private LocationManager locMan;
	
	private Marker selectedMarker;
	private Button btnZoom, btnDelete, btnAccept;

	public MapFragment()
	{		
		markers = new ArrayList<MarkerOptions>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// Clear view if needed
	    if (view != null)
	    {
	        ViewGroup parent = (ViewGroup) view.getParent();
	        
	        if (parent != null)
	            parent.removeView(view);
	    }
	    
	    // Inflate view
	    try
	    {
	    	view = new RelativeLayout(getActivity());
			View mapView = inflater.inflate(R.layout.fragment_content_map, container,
					false);
			
			view.addView(mapView, new RelativeLayout.LayoutParams(-1, -1));
			
			LinearLayout linearLayout = new LinearLayout(getActivity());
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			view.addView(linearLayout);
			
			btnZoom = new Button(getActivity());
			btnZoom.setText("Zoom");
			btnZoom.setWidth(80);
			btnZoom.setHeight(40);
			btnZoom.setEnabled(false);
			btnZoom.setOnClickListener(new OnBtnZoomHandler());
			linearLayout.addView(btnZoom);
			
			btnDelete = new Button(getActivity());
			btnDelete.setText("Del");
			btnDelete.setWidth(80);
			btnDelete.setHeight(40);
			btnDelete.setEnabled(false);
			btnDelete.setOnClickListener(new OnBtnDeleteHandler());
			linearLayout.addView(btnDelete);
			
			btnAccept = new Button(getActivity());
			btnAccept.setText("OK");
			btnAccept.setWidth(80);
			btnAccept.setHeight(40);
			linearLayout.addView(btnAccept);
			

			initilizeMap();
			initPos();
	    }
	    catch (InflateException e)
	    {
	        // map is already there
	    	return view;
	    }
	    
	    return view;
	    
	}

	private void initPos()
	{
		locMan = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
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
			Toast.makeText(getActivity().getApplicationContext(), text,
					Toast.LENGTH_LONG).show();

			// TODO: add this return when you no longer want the 0,0 gps
//			 return;

		}
		else
		{
			// register for location updates
			LocationUpdateHandler luha = new LocationUpdateHandler();
			locMan.requestLocationUpdates(providerName, 5000, 10, luha);
			
			lastLoc = locMan.getLastKnownLocation(providerName);
		}

		// set initial text
		
		// this.et_log.setText(getLocationString(lastLoc) + "\n");

		gpsMarker = new MarkerOptions();
		gpsMarker.title(GPSMARKER);
		gpsMarker.draggable(true);
		gpsMarker.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		updatePos();

		markers.add(gpsMarker);
		googleMap.addMarker(gpsMarker);
		
		btnZoom.setEnabled(true);

	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap()
	{
		if (googleMap == null)
		{
			googleMap = ((SupportMapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null)
			{
				Toast.makeText(getActivity().getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();

				return;
			}
		}
		
		try
		{
			MapsInitializer.initialize(getActivity());
		}
		catch (GooglePlayServicesNotAvailableException e)
		{
			Toast.makeText(getActivity().getApplicationContext(), "Google play services not available", Toast.LENGTH_LONG).show();
		}

//		googleMap.setOnMarkerDragListener(new MarkerDraggedHandler());
		googleMap.setOnMarkerClickListener(new MarkerClickHandler());
		googleMap.setOnMapLongClickListener(new MapLongClickHandler());

		// adding marker

	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		loadMarkers();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		saveMarkers();
	}
	
	private void saveMarkers() {
		
		Log.i(CommonUtilities.TAG, "Saving markers");
		
		SharedPreferences prefs = getActivity()
				.getSharedPreferences("alpha.android", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		
		int size = markers.size();
		MarkerOptions marker;
		
		int prefIndex = 0;
		
		for (int listIndex = 0; listIndex < size; listIndex++) {
			
			Log.i(CommonUtilities.TAG, Integer.toString(listIndex));
			
			marker = markers.get(listIndex);
			
			if (isMarkerGPS(marker))
				continue;
			
			LatLng pos = marker.getPosition();
			editor.putLong(MARKERPREFIX + "lat_" + prefIndex,
					Double.doubleToLongBits(pos.latitude));
			editor.putLong(MARKERPREFIX + "lng_" + prefIndex,
					Double.doubleToLongBits(pos.longitude));
			
			prefIndex++;
		}
		
		editor.putInt(SIZEKEY, prefIndex);
		editor.commit();
		
		Log.i(CommonUtilities.TAG, "All done!");
	}
	
	private void loadMarkers() {
		
		Log.i(CommonUtilities.TAG, "Loading markers");
		
		SharedPreferences prefs = getActivity()
				.getSharedPreferences("alpha.android", Context.MODE_PRIVATE);
		
		int size = prefs.getInt(SIZEKEY, 0);
		
		if (size == 0)
			return;
		
		for (int i = 0; i < size; i++) {
			
			Log.i(CommonUtilities.TAG, Integer.toString(i));
			
			double lat = Double.longBitsToDouble(
					prefs.getLong(MARKERPREFIX + "lat_" + i, 0l));
			double lng = Double.longBitsToDouble(
					prefs.getLong(MARKERPREFIX + "lng_" + i, 0l));
			
			if (lat == 0l || lng == 0l) {
				Log.w(CommonUtilities.TAG, "Bad location data found!");
				continue;
			}
			
			LatLng pos = new LatLng(lat, lng);
			addNewMarker(pos);					
		}
		
		Log.i(CommonUtilities.TAG, "All done!");
	}

	private void addNewMarker(LatLng pos)
	{
		MarkerOptions marker = new MarkerOptions();
		marker.draggable(true);
		marker.position(pos);
		marker.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_RED));

		markers.add(marker);

		googleMap.addMarker(marker);
		
		btnZoom.setEnabled(true);
	}

	private void zoomToFitMarkers()
	{
		if (markers.size() == 0) {
			Log.e(CommonUtilities.TAG, "No markers found, this should not happen!");
			return;
		}
		
		LatLngBounds bounds;
		
		if (markers.size() > 1) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			for (MarkerOptions marker : markers)
			{
				builder.include(marker.getPosition());
			}
			bounds = builder.build();
		} else {
			LatLng singleMarkerPos = markers.get(0).getPosition();
			double lat = singleMarkerPos.latitude;
			double lon = singleMarkerPos.longitude;
			
			bounds = new LatLngBounds(new LatLng(lat - SPACESINGLE, lon - SPACESINGLE),
					new LatLng(lat + SPACESINGLE, lon + SPACESINGLE));
		}
		

		// Google map's camera that is
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
				MAPPADDING);

		if (ANIMATION)
			googleMap.animateCamera(cu);
		else
			googleMap.moveCamera(cu);

	}

	private void updatePos()
	{
		lastLoc = locMan.getLastKnownLocation(providerName);

		if (lastLoc != null)
		{
			gpsMarker.position(new LatLng(lastLoc.getLatitude(), lastLoc
					.getLongitude()));
			Log.i(CommonUtilities.TAG, "Updating gps position...");
		}
		else
		{
			gpsMarker.position(new LatLng(0, 0));
			Log.w(CommonUtilities.TAG, "Setting gps position to (0,0)");
		}
	}
	
	private boolean isMarkerGPS(Marker marker) {
		String potentialTitle = marker.getTitle();
		return (potentialTitle != null && potentialTitle.equals(GPSMARKER));
	}
	
	private boolean isMarkerGPS(MarkerOptions marker)
	{
		String potentialTitle = marker.getTitle();
		return (potentialTitle != null && potentialTitle.equals(GPSMARKER));
	}

	private void hilightMarker(Marker marker)
	{
		if (marker == null)
			return;
		
		if (selectedMarker != null) {
			if (isMarkerGPS(selectedMarker))
				selectedMarker.setIcon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
			else
				selectedMarker.setIcon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		}
		
		selectedMarker = marker;
		
		marker.setIcon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		
		if (isMarkerGPS(marker)) {
			btnDelete.setEnabled(false);
			marker.showInfoWindow();
		}
		else
			btnDelete.setEnabled(true);
	}

	/**
	 * Converts a location into a readable version with latitude and longitude
	 * 
	 * @param loc
	 * @return
	 */
	public String getLocationString(Location loc)
	{
		if (loc == null)
		{
			return "Undefined";
		}
		else
		{
			return String.format(Locale.US, "(%.2f,%.2f)", loc.getLatitude(),
					loc.getLongitude());
		}
	}

	private void removeSelectedMarker()
	{
		if (selectedMarker == null) {
			Log.e(CommonUtilities.TAG, "No selection found, this should not happen!");
			return;
		}
		
		String supposedTitle = selectedMarker.getTitle();
		if (supposedTitle != null && supposedTitle.equals(GPSMARKER)) {
			Toast.makeText(getActivity(), "Can't remove GPS position", Toast.LENGTH_SHORT).show();
			return;
		}

		markers.remove(selectedMarker);
		
		int size = markers.size();
		MarkerOptions markerOptions;
		for (int i = 0; i < size; i++) {
			markerOptions = markers.get(i);
			if (markerOptions.getPosition().equals(selectedMarker.getPosition())) {
				markers.remove(markerOptions);
				Log.i(CommonUtilities.TAG, "Removing some marker");
				break;
			}
		}
		
		selectedMarker.remove();
		
		selectedMarker = null;
		
		if (markers.size() == 0)
			btnZoom.setEnabled(false);
		else
			btnDelete.setEnabled(true);
	}

	private class MapLongClickHandler implements OnMapLongClickListener
	{

		@Override
		public void onMapLongClick(LatLng pos)
		{
			addNewMarker(pos);
		}

	}

	private class MarkerClickHandler implements OnMarkerClickListener
	{

		@Override
		public boolean onMarkerClick(Marker m)
		{
			hilightMarker(m);

			return !ZOOMTOSELECTION;
		}

	}

	private class OnBtnZoomHandler implements OnClickListener {

		@Override
		public void onClick(View v)
		{
			zoomToFitMarkers();
		}
	}

	private class OnBtnDeleteHandler implements OnClickListener {
		@Override
		public void onClick(View v)
		{
			removeSelectedMarker();
		}
		
	}
	
	private class LocationUpdateHandler implements LocationListener
	{
		@Override
		public void onLocationChanged(Location location)
		{

			updatePos();

			// Crashes when running in background
//			Toast.makeText(getActivity().getApplicationContext(),
//					"Location changed: " + getLocationString(location),
//					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			// TODO Auto-generated method stub

		}
	}
}
