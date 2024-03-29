package alpha.android.fragments;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import alpha.android.R;
import alpha.android.common.CommonUtilities;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {
	private static final String GPSMARKER = "GPS";
	private static final double SPACESINGLE = 0.5d;
	private static final int MAPPADDING = 100;
	private static final boolean ZOOMTOSELECTION = false;

	public static final String SIZEKEY = "marker_size";
	public static final String MARKERPREFIX = "marker_";

	private SupportMapFragment fragment;
	private GoogleMap googleMap;
	private Location lastLoc;
	private String providerName;
	private LocationManager locMan;
	private Marker selectedMarker;
	private Button btnZoom, btnDelete, btnOk;
	
	private MarkerOptions gpsMarker;
	private ArrayList<MarkerOptions> markers;
	private MarkerOptions lastAddedMarker;
	private MarkerOptions draggingMarker;
	
	private boolean prefAnimate, prefStore;

	public MapFragment() {
		markers = new ArrayList<MarkerOptions>();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.map, fragment).commit();
		}

		initilizeMap();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate view
		View mapView = inflater.inflate(R.layout.fragment_content_map,
				container, false);

		// ZOOM BUTTON
		btnZoom = (Button) mapView.findViewById(R.id.btnZoom);
		btnZoom.setOnClickListener(new OnBtnZoomHandler());

		// DELETE BUTTON
		btnDelete = (Button) mapView.findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(new OnBtnDeleteHandler());

		// OK BUTTON
		btnOk = (Button) mapView.findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnBtnOkHandler());

		return mapView;
	}

	private void initPos() {
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

		if (providerName == null || !locMan.isProviderEnabled(providerName)) {
			// if none is found use default
			providerName = LocationManager.NETWORK_PROVIDER;
		}

		if (!locMan.isProviderEnabled(providerName)) {
			String text = "No working location provider found!";
			Toast.makeText(getActivity().getApplicationContext(), text,
					Toast.LENGTH_LONG).show();

			return;
		} else {
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
	 * Function to load map. If map is not created it will create it for you
	 **/
	private void initilizeMap() {
		try {
			MapsInitializer.initialize(getActivity());
		} catch (GooglePlayServicesNotAvailableException e) {
			Toast.makeText(getActivity().getApplicationContext(),
					"Google play services not available", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		SharedPreferences prefs = getPrefs();
		prefAnimate = prefs.getBoolean("pref_key_animate_camera", true);
		prefStore = prefs.getBoolean("pref_key_store_location", false);
				
		if (googleMap == null) {
			googleMap = fragment.getMap();
		}

		googleMap.setOnMarkerClickListener(new MarkerClickHandler());
		googleMap.setOnMapLongClickListener(new MapLongClickHandler());
		googleMap.setOnMarkerDragListener(new MarkerDragHandler());

		initPos();
		loadMarkers();

		zoomToFitMarkers();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		saveMarkers();
	}

	private SharedPreferences getPrefs() {
		SharedPreferences sharedPrefs = getActivity().getSharedPreferences(
				CommonUtilities.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		return sharedPrefs;
	}

	private void saveMarkers() {

		if (!prefStore) {
			Log.i(CommonUtilities.TAG,
					"Not saving because the preference is either unexisting or off.");
			return;
		}

		Log.i(CommonUtilities.TAG, "Saving markers");

		// SharedPreferences sharedPrefs = getActivity()
		// .getSharedPreferences("alpha.android", Context.MODE_PRIVATE);

		SharedPreferences prefs = getPrefs();
		Editor editor = prefs.edit();

		int size = markers.size();
		MarkerOptions marker;

		int prefIndex = 0;

		for (int listIndex = 0; listIndex < size; listIndex++) {
			marker = markers.get(listIndex);

			if (isMarkerGPS(marker))
				continue;

			LatLng pos = marker.getPosition();

			Log.i(CommonUtilities.TAG, "Adding " + marker.getTitle()
					+ " at pos " + pos.latitude + "," + pos.longitude);

			editor.putLong(MARKERPREFIX + "lat_" + prefIndex,
					Double.doubleToLongBits(pos.latitude));
			editor.putLong(MARKERPREFIX + "lng_" + prefIndex,
					Double.doubleToLongBits(pos.longitude));
			editor.putString(MARKERPREFIX + "title_" + prefIndex,
					marker.getTitle());

			prefIndex++;
		}

		editor.putInt(SIZEKEY, prefIndex);
		editor.commit();

		Log.i(CommonUtilities.TAG, "All done!");
	}

	private void loadMarkers() {

		Log.i(CommonUtilities.TAG, "Loading markers");

		SharedPreferences prefs = getPrefs();

		int size = prefs.getInt(SIZEKEY, 0);

		if (size == 0)
			return;

		for (int i = 0; i < size; i++) {

			double lat = Double.longBitsToDouble(prefs.getLong(MARKERPREFIX
					+ "lat_" + i, 0l));
			double lng = Double.longBitsToDouble(prefs.getLong(MARKERPREFIX
					+ "lng_" + i, 0l));
			String title = prefs.getString(MARKERPREFIX + "title_" + i, null);

			if (lat == 0l || lng == 0l) {
				Log.w(CommonUtilities.TAG, "Bad location data found!");
				continue;
			}

			LatLng pos = new LatLng(lat, lng);
			addMarkerFromLoad(pos, title);
		}

		Log.i(CommonUtilities.TAG, "All done!");
	}

	private void addMarkerFromLoad(LatLng pos, String title) {

		Log.i(CommonUtilities.TAG, "Adding " + title + " at pos "
				+ pos.latitude + "," + pos.longitude);

		MarkerOptions marker = new MarkerOptions();
		marker.draggable(true);
		marker.position(pos);
		marker.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_RED));

		if (title != null)
			marker.title(title);

		markers.add(marker);

		googleMap.addMarker(marker);

		btnZoom.setEnabled(true);
	}

	private void addNewMarker(LatLng pos) {
		Log.i(CommonUtilities.TAG, "Adding marker at pos " + pos.latitude + ","
				+ pos.longitude);

		MarkerOptions marker = new MarkerOptions();
		marker.draggable(true);
		marker.position(pos);
		marker.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_RED));

		lastAddedMarker = marker;

		promptForName();
	}

	private void continueToAddNewMarker(String name) {

		if (name != null)
			lastAddedMarker.title(name);

		markers.add(lastAddedMarker);

		googleMap.addMarker(lastAddedMarker);

		btnZoom.setEnabled(true);

	}

	private void promptForName() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Marker");
		alert.setMessage("Input name");

		final EditText input = new EditText(getActivity());
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = input.getText().toString();
				
				if (text.isEmpty())
					text = new Date().toString();

				continueToAddNewMarker(text);
				// Log.i(CommonUtilities.TAG, "setting text " +
				// input.getText().toString());
			}
		});
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// nothing
					}
				});

		alert.show();
	}

	private void zoomToFitMarkers() {

		Log.i(CommonUtilities.TAG, "Attempting to zoom map...");

		if (markers.size() == 0) {
			Log.e(CommonUtilities.TAG,
					"No markers found, this should not happen!");
			return;
		}

		try {

			LatLngBounds bounds;

			if (markers.size() > 1) {
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for (MarkerOptions marker : markers) {
					builder.include(marker.getPosition());
				}
				bounds = builder.build();
			} else {
				LatLng singleMarkerPos = markers.get(0).getPosition();
				double lat = singleMarkerPos.latitude;
				double lon = singleMarkerPos.longitude;

				bounds = new LatLngBounds(new LatLng(lat - SPACESINGLE, lon
						- SPACESINGLE), new LatLng(lat + SPACESINGLE, lon
						+ SPACESINGLE));
			}

			// Google map's camera that is
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
					MAPPADDING);
			
			if (prefAnimate)
				googleMap.animateCamera(cu);
			else
				googleMap.moveCamera(cu);

			Log.i(CommonUtilities.TAG, "Zoomed successfully");

		} catch (Exception e) {

			Log.w(CommonUtilities.TAG,
					"Zooming didn't work, the map size was probably O: "
							+ e.getMessage());
			Log.i(CommonUtilities.TAG, "Waiting for map to be ready...");

			googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {

				@Override
				public void onCameraChange(CameraPosition arg0) {

					zoomToFitMarkers();

					googleMap.setOnCameraChangeListener(null);
				}
			});
		}
	}

	private void updatePos() {
		lastLoc = locMan.getLastKnownLocation(providerName);

		if (lastLoc != null) {
			gpsMarker.position(new LatLng(lastLoc.getLatitude(), lastLoc
					.getLongitude()));
			Log.i(CommonUtilities.TAG, "Updating gps position...");
		} else {
			gpsMarker.position(new LatLng(0, 0));
			Log.w(CommonUtilities.TAG, "Setting gps position to (0,0)");
		}
	}

	private boolean isMarkerGPS(Marker marker) {
		String potentialTitle = marker.getTitle();
		return (potentialTitle != null && potentialTitle.equals(GPSMARKER));
	}

	private boolean isMarkerGPS(MarkerOptions marker) {
		String potentialTitle = marker.getTitle();
		return (potentialTitle != null && potentialTitle.equals(GPSMARKER));
	}

	private void hilightMarker(Marker marker) {
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

		if (isMarkerGPS(marker))
			btnDelete.setEnabled(false);
		else
			btnDelete.setEnabled(true);

		btnOk.setEnabled(true);

																												if (marker.getTitle().length() < 20)
		marker.showInfoWindow();
	}

	/**
	 * Converts a location into a readable version with latitude and longitude
	 * 
	 * @param loc
	 * @return
	 */
	public String getLocationString(Location loc) {
		if (loc == null) {
			return "Undefined";
		} else {
			return String.format(Locale.US, "(%.2f,%.2f)", loc.getLatitude(),
					loc.getLongitude());
		}
	}

	private void removeSelectedMarker() {
		if (selectedMarker == null) {
			Log.e(CommonUtilities.TAG,
					"No selection found, this should not happen!");
			return;
		}

		String supposedTitle = selectedMarker.getTitle();
		if (supposedTitle != null && supposedTitle.equals(GPSMARKER)) {
			Toast.makeText(getActivity(), "Can't remove GPS position",
					Toast.LENGTH_SHORT).show();
			return;
		}

		markers.remove(selectedMarker);

		int size = markers.size();
		MarkerOptions markerOptions;
		for (int i = 0; i < size; i++) {
			markerOptions = markers.get(i);
			if (markerOptions.getPosition()
					.equals(selectedMarker.getPosition())) {
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

		btnOk.setEnabled(false);
	}

	private void sendToChat() {
		if (selectedMarker == null) {
			Toast.makeText(getActivity(), "No marker selected.",
					Toast.LENGTH_SHORT).show();
			Log.w(CommonUtilities.TAG,
					"Button was enabled but no marker was selected.");
			return;
		}

		Bundle bundle = savePosition(selectedMarker.getPosition(),
				selectedMarker.getTitle());

		Fragment messageFragment = new MessageFragment();
		messageFragment.setArguments(bundle);

		// Check if there was previous content -> replace , else -> add
		if (messageFragment != null)
			if (getActivity().getSupportFragmentManager().findFragmentById(
					R.id.contentFragment_container_main) != null)
				getActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.contentFragment_container_main,
								messageFragment).commit();
			else
				getActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.contentFragment_container_main,
								messageFragment).commit();
	}

	private Bundle savePosition(LatLng pos, String title) {
		Bundle bundle = new Bundle();
		String fileName = (title == null || title.length() > 20 ? "somewhere" : title);
		FileOutputStream fos = null;

		bundle.putString("location", fileName);

		try {
			fos = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);

			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeDouble(pos.latitude);
			oos.writeDouble(pos.longitude);

			oos.flush();

			oos.close();
			fos.close();

			Log.i(CommonUtilities.TAG, "Position was succesfully saved: "
					+ fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bundle;
	}

	private class MapLongClickHandler implements OnMapLongClickListener {

		@Override
		public void onMapLongClick(LatLng pos) {
			addNewMarker(pos);
		}

	}

	private class MarkerClickHandler implements OnMarkerClickListener {

		@Override
		public boolean onMarkerClick(Marker m) {
			hilightMarker(m);

			return !ZOOMTOSELECTION;
		}

	}

	private class MarkerDragHandler implements OnMarkerDragListener {

		@Override
		public void onMarkerDrag(Marker arg0) {

		}

		@Override
		public void onMarkerDragEnd(Marker marker) {

			if (draggingMarker == null)
				return;
			
			draggingMarker.position(marker.getPosition());
			draggingMarker = null;
		}

		@Override
		public void onMarkerDragStart(Marker marker) {

			Log.i(CommonUtilities.TAG,
					"Dragging started, attempting to find it.");

			boolean found = false;
			
			for (MarkerOptions m : markers) {
				
				if (m.getTitle().equals(marker.getTitle())) {
					draggingMarker = m;
					found = true;

					break;
				}
			}
			
//			
//
//			for (MarkerOptions m : markers) {
//
//				LatLng pos = m.getPosition();
//				double lat0 = pos.latitude;
//				double lng0 = pos.longitude;
//
//				pos = arg0.getPosition();
//				double lat1 = pos.latitude;
//				double lng1 = pos.longitude;
//
//				Log.i(CommonUtilities.TAG, "Comparing " + lat0 + "=" + lat1
//						+ " and " + lng0 + "=" + lng1);
//
//				if (lat0 == lat1 && lng0 == lng1) {
//
//					
//				}
//			}

			if (found)
				Log.i(CommonUtilities.TAG, "Found marker");
			else
				Log.i(CommonUtilities.TAG, "Not found marker");

		}

	}

	private class OnBtnZoomHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			zoomToFitMarkers();
		}
	}

	private class OnBtnDeleteHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			removeSelectedMarker();
		}

	}

	private class OnBtnOkHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			sendToChat();
		}

	}

	private class LocationUpdateHandler implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {

			updatePos();

			// Crashes when running in background
			// Toast.makeText(getActivity().getApplicationContext(),
			// "Location changed: " + getLocationString(location),
			// Toast.LENGTH_SHORT).show();
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
