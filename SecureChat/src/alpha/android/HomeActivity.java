package alpha.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import alpha.android.common.CommonUtilities;
import alpha.android.fragments.CameraFragment;
import alpha.android.fragments.ContactsFragment;
import alpha.android.fragments.HomeFragment;
import alpha.android.fragments.MenuFragment;
import alpha.android.fragments.MessageFragment;
import alpha.android.fragments.OptionsFragment;
import alpha.android.fragments.PreferenceListFragment;
import alpha.android.fragments.PrefsFragment;
import alpha.android.gcm.GcmManager;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
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
		MenuFragment.OnMenuSelectedListener,
		PreferenceListFragment.OnPreferenceAttachedListener,
		GcmManager.GcmDataConnection
{
	// Managing objects
	private CameraManager camManager;
	private GcmManager gcmManager;
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container_home);

		// Get Drawer's Layout and List
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.list_navigation_drawer_row, CommonUtilities.MENU_ITEMS_HOME));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* HomeActivity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(R.string.drawer_open);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(R.string.drawer_close);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Instantiate new GCM Manager (Google Cloud Messaging)
 		gcmManager = new GcmManager(this);
 		
 		// Set the Swipe Icon
 	    getActionBar().setHomeButtonEnabled(true);
 	    getActionBar().setTitle(R.string.drawer_open);

 	    // Inflates HomeFragment
 		onMenuItemSelected(CommonUtilities.MENU_POS_HOME);
 		
 	    // Add name of logged in user to welcome message
 		updateWelcomeMessage(getIntent().getStringExtra("username"));
    }
	
    
	// Click event of Drawer Action Bar
    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
            selectItem(position);
        }
    }
    
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


	// Appends the username to the welcome message
	private void updateWelcomeMessage(String username)
	{
		// TextView tvWelcomeMessage = (TextView) findViewById(R.id.tv_welcome);
		// tvWelcomeMessage.append(username);
	}

	// MenuFragment's MenuItem clicked
	@Override
	public void onMenuItemSelected(int position)
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
			
//			case CommonUtilities.MENU_POS_LOCATION:
//				content = new LocationFragment();
//				break;
				
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
	}
	
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...
        switch (item.getItemId()) {
    case R.id.action_settings:
      Toast.makeText(this, "Settings selected", Toast.LENGTH_LONG).show();
      break;

    default:
      break;
    }
        return super.onOptionsItemSelected(item);
    }
    
    
    /** Swaps fragments in the main content view */
    private void selectItem(int position)
    {
    	onMenuItemSelected(position);

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        getActionBar().setTitle((CommonUtilities.MENU_ITEMS_HOME[position]));
        mDrawerLayout.closeDrawer(mDrawerList);
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

				if (data.getExtras() != null)
				{
					imageBitmap = (Bitmap) data.getExtras().get("data");

					Log.i(CommonUtilities.TAG, "Successfully received image: "
							+ imageBitmap.toString());
				}
				else
				{
					try
					{
						imageBitmap = MediaStore.Images.Media.getBitmap(
								this.getContentResolver(), data.getData());
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

				// Put bitmap image in image view
				ImageView imgView = (ImageView) findViewById(R.id.ivPicture);
				imgView.setImageBitmap(imageBitmap);
			}
		}
		else
		{

			Log.i(CommonUtilities.TAG,
					"Failed receiving image onActivityResult");
		}
	}

	public void fixPlayServices(View v)
	{
		String text;
		
		if (checkPlayServices())
			text = "Success";
		else
			text = "Failure";
		
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}

	public void fixGcmServices(View v)
	{
		GoogleCloudMessaging.getInstance(getApplicationContext());
	}

	public void fixDeviceRegistration(View v)
	{
		String result = gcmManager.registerInBackground();
		
		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
	}

	private boolean checkPlayServices()
	{
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

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

	// Handles click-event of the 'Take a picture'-button in the
	// fragment_content_camera
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

	@Override
	public void onPreferenceAttached(PreferenceScreen root, int xmlId)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void registrationResponse(String result)
	{
		Toast.makeText(getApplicationContext(), "Printing result: " + result, Toast.LENGTH_LONG).show();
	}

	/**
	 * TODO: NOG HERPLAATSEN
	 */
	// public void sendMessage(View v)
	// {
	// EditText text = (EditText) this.findViewById(R.id.text);
	// String newMessage = text.getText().toString().trim();
	// if(newMessage.length() > 0)
	// {
	// text.setText("");
	// addNewMessage(new Message(newMessage, true));
	// new SendMessage().execute();
	// }
	// }
	//
	//
	// private class SendMessage extends AsyncTask<Void, String, String>
	// {
	// ArrayList<Message> messages;
	// AwesomeAdapter adapter;
	// static Random rand = new Random();
	// static String sender;
	//
	// @Override
	// protected String doInBackground(Void... params) {
	// try {
	// Thread.sleep(2000); //simulate a network call
	// }catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	// this.publishProgress(String.format("%s started writing", sender));
	// try {
	// Thread.sleep(2000); //simulate a network call
	// }catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// this.publishProgress(String.format("%s has entered text", sender));
	// try {
	// Thread.sleep(3000);//simulate a network call
	// }catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	//
	// return Utility.messages[rand.nextInt(Utility.messages.length-1)];
	//
	//
	// }
	// @Override
	// public void onProgressUpdate(String... v) {
	//
	// if(messages.get(messages.size()-1).isStatusMessage())//check wether we
	// have already added a status message
	// {
	// messages.get(messages.size()-1).setMessage(v[0]); //update the status for
	// that
	// adapter.notifyDataSetChanged();
	// getListView().setSelection(messages.size()-1);
	// }
	// else{
	// addNewMessage(new Message(true,v[0])); //add new message, if there is no
	// existing status message
	// }
	// }
	// @Override
	// protected void onPostExecute(String text) {
	// if(messages.get(messages.size()-1).isStatusMessage)//check if there is
	// any status message, now remove it.
	// {
	// messages.remove(messages.size()-1);
	// }
	//
	// addNewMessage(new Message(text, false)); // add the orignal message from
	// server.
	// }
	//
	//
	// }
	//
	// void addNewMessage(Message m)
	// {
	// messages.add(m);
	// adapter.notifyDataSetChanged();
	// getListView().setSelection(messages.size()-1);
	// }
}
