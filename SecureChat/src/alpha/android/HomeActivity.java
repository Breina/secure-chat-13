package alpha.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import alpha.android.common.CommonUtilities;
import alpha.android.fragments.CameraFragment;
import alpha.android.fragments.ContactsFragment;
import alpha.android.fragments.HomeFragment;
import alpha.android.fragments.MapFragment;
import alpha.android.fragments.MessageFragment;
import alpha.android.fragments.OptionsFragment;
import alpha.android.fragments.PreferenceListFragment;
import alpha.android.fragments.PrefsFragment;
import alpha.android.gcm.GcmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class HomeActivity extends FragmentActivity implements
		PreferenceListFragment.OnPreferenceAttachedListener
		
{
	// Managing objects
	private CameraManager camManager;
	
	// Menu Objects
    private DrawerLayout menuDrawerLayout;
    private ListView menuDrawerListView;
    private ActionBarDrawerToggle menuDrawerToggle;

    
    /**
     * LIFECYCLE HOME ACTIVITY
     */
    // On Create Home Activity
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container_home);

		// Get Drawer's Layout and List
        menuDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuDrawerListView = (ListView) findViewById(R.id.left_drawer);

        // Set up the drawer's list view with items and click listener
        menuDrawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.list_navigation_drawer_row, CommonUtilities.MENU_ITEMS_HOME));
        menuDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
        
        // Set up the ActionBarDrawerToggle
        menuDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* HomeActivity */
                menuDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "Open drawer" description */
                R.string.drawer_close  /* "Close drawer" description */
                )
        {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view)
            {
                ImageView slider = (ImageView) findViewById(R.id.iv_left_drawer);
                slider.setVisibility(View.VISIBLE);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView)
            {
                ImageView slider = (ImageView) findViewById(R.id.iv_left_drawer);
                slider.setVisibility(View.INVISIBLE);
            }
        };

        // Set the Drawer Toggle as the DrawerListener
        menuDrawerLayout.setDrawerListener(menuDrawerToggle);

 		// Set the Home Icon as menu button
 	    getActionBar().setHomeButtonEnabled(true);

 	    // Inflates HomeFragment
 		menuItemClicked(CommonUtilities.MENU_POS_HOME);
    }
	
	
    // Syncs the Drawer Toggle state after onRestoreInstanceState has occurred.
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        
        menuDrawerToggle.syncState();
    }
    

    // Configuration changed
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        
        menuDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    
    
    /**
     * CLICK EVENTS
     */
	// Click event of Drawer Action Bar
    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            menuItemClicked(position);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (menuDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        int itemId = item.getItemId();
		if (itemId == R.id.action_settings)
		{
			Toast.makeText(this, "Settings selected", Toast.LENGTH_LONG).show();
		}
		else
		{
		}
        return super.onOptionsItemSelected(item);
    }
    
    // Occurs when a menu-item was clicked -> handles the fragments
    private void menuItemClicked(int position)
    {
		Fragment content = null;

		switch (position)
		{
			case CommonUtilities.MENU_POS_HOME:
				content = new HomeFragment();
				break;
			
			case CommonUtilities.MENU_POS_CONTACTS:
				content = new ContactsFragment();
				break;
				
			case CommonUtilities.MENU_POS_CHAT:
				content = new MessageFragment();
				break;
			
			case CommonUtilities.MENU_POS_CAMERA:
				content = new CameraFragment();
				break;
			
			case CommonUtilities.MENU_POS_LOCATION:
				content = new MapFragment();
				break;
				
			case CommonUtilities.MENU_POS_PREFS:
				content = new PrefsFragment();
				break;
	
			case CommonUtilities.MENU_POS_OPTIONS:
				content = new OptionsFragment();
				break;
	
			case CommonUtilities.MENU_POS_LOGOUT:
				Toast.makeText(getApplicationContext(), "Successfully logged out", Toast.LENGTH_LONG).show();
				finish();
				break;
	
			default:
				break;
		}

		// Check if there was previous content -> replace , else -> add
		if (content != null)
			if (getSupportFragmentManager().findFragmentById(
					R.id.contentFragment_container_main) != null)
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.contentFragment_container_main, content)
						.commit();
			else
				getSupportFragmentManager().beginTransaction()
						.add(R.id.contentFragment_container_main, content)
						.commit();
		
        // Highlight the selected item, update the title, and close the drawer
        menuDrawerListView.setItemChecked(position, true);
        getActionBar().setTitle((CommonUtilities.MENU_ITEMS_HOME[position]));
        menuDrawerLayout.closeDrawer(menuDrawerListView);
    }
    
    
	// Handle Activity Result (Camera, )
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			// CAMERA
			if (requestCode == CommonUtilities.REQUEST_IMAGE_CAPTURE)
			{
				Bitmap imageBitmap = null;

				if (data.getExtras().get("data") != null)
				{
					imageBitmap = (Bitmap) data.getExtras().get("data");

					Log.i(CommonUtilities.TAG, "Successfully received image: " + imageBitmap.toString());
				}
				else
				{
					try
					{
						imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}

				if (imageBitmap != null)
				{
					// Put bitmap image in image view
					ImageView imgView = (ImageView) findViewById(R.id.ivPicture);
					imgView.setImageBitmap(imageBitmap);
				}
			}
		}
		else
		{

			Log.i(CommonUtilities.TAG, "Failed receiving image onActivityResult");
		}
	}

	
	
	/**
	 * GOOGLE CLOUD MESSAGING
	 */
	// Checks if Google Play Services .apk is installed on the device
	private boolean checkPlayServices()
	{
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (resultCode != ConnectionResult.SUCCESS)
		{
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
			{
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						CommonUtilities.PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			}
			else
			{
				Log.i(CommonUtilities.TAG, "This device is not supported.");

			}

			return false;
		}

		return true;
	}
	
	
	// TODO: Fixes the Google Play Services .apk (re-install)
	public void fixPlayServices(View v)
	{
		String text;
		
		if (checkPlayServices())
			text = "Success";
		else
			text = "Failure";
		
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}

	
	// TODO: Fixes the GCM instance (re-initiate)
	public void fixGcmServices(View v)
	{
		GoogleCloudMessaging.getInstance(getApplicationContext());
	}

	
	// TODO: Re-registers device registration (first check if needed)
	public void fixDeviceRegistration(View v)
	{
		GcmManager gcmManager = new GcmManager(getApplicationContext());
		String result = gcmManager.registerInBackground();
		
		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
	}

	

	/**
	 * CAMERA INTENT
	 */
	// Handles click-event of the 'Take a picture'-button in the fragment_content_camera
	public void initiateCamera(View view)
	{
		// Check for camera device
		if (!checkForValidCameraDevice())
		{
			Toast.makeText(
					this,
					"No camera device was found. Please enable or install your camera.",
					Toast.LENGTH_LONG).show();
			return;
		}
		else
		{
			Intent takePictureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);

			// Create new instance of CameraManager
			camManager = new CameraManager(this);

			// Create the File where the photo should go
			File photoFile = null;

			try
			{
				photoFile = camManager.createImageFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Log.i(CommonUtilities.TAG,
						"IOException while getting the photo back to HomeActivity with cause "
								+ e.getCause());
			}

			// Continue only if the File was successfully created
			if (photoFile != null)
			{
				// takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				// Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent,
						CommonUtilities.REQUEST_IMAGE_CAPTURE);
			}
		}

	}

	// Checks whether the device has a valid camera
	private boolean checkForValidCameraDevice()
	{
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
			return true;
		else
			return false;
	}


	
	
	// TODO: ???
	@Override
	public void onPreferenceAttached(PreferenceScreen root, int xmlId)
	{
		// TODO Auto-generated method stub

	}

}
