package alpha.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import alpha.android.common.CommonUtilities;
import alpha.android.fragments.HomeContentFragment;
import alpha.android.fragments.MenuFragment;
import alpha.android.gcm.GcmManager;
import alpha.android.webservice.WebserviceManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends FragmentActivity implements MenuFragment.OnMenuSelectedListener
{
	// Managing objects
	private CameraManager camManager;
	
	// Fragment objects
	private MenuFragment menuFragment;
	private HomeContentFragment contentFragment;
	private Bundle menuItemsBundle;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragmentcontainer_activity_home);
		
        // Restore from previous state if possible
//        if (savedInstanceState != null)
//            return;
        
        // Create a new MenuFragment instance with a bundle containing the menu-items that need to be inflated
    	menuItemsBundle = new Bundle();
    	menuItemsBundle.putStringArray("menu_items", CommonUtilities.MENU_ITEMS_HOME);
        menuFragment = new MenuFragment();
    	menuFragment.setArguments(menuItemsBundle);
        
    	// Create a new ContentFragment with index 0: HOME
    	contentFragment = new HomeContentFragment();
    	menuItemsBundle.putInt("menuIndex", 0);
    	contentFragment.setArguments(menuItemsBundle);
    	
        // Add the menuFragment and contentFragment to its FrameLayouts
        getSupportFragmentManager().beginTransaction().add(R.id.menuFragment_container_main, menuFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.contentFragment_container_main, contentFragment).commit();
        
    	// Add name of logged in user to welcome message
        updateWelcomeMessage(getIntent().getStringExtra("username"));
	}
	
	
	// Appends the username to the welcome message
	private void updateWelcomeMessage(String username)
	{
//		TextView tvWelcomeMessage = (TextView) findViewById(R.id.tv_welcome);
//		tvWelcomeMessage.append(username);
	}
	
	
	// MenuFragment's MenuItem clicked
    @Override
    public void onMenuItemSelected(int position)
    {
    	// Put the clicked menu index in the Bundle object
    	menuItemsBundle.putInt("menuIndex", position);
    	
    	// Set and replace the contentFragment that was opened
    	contentFragment = new HomeContentFragment();
    	contentFragment.setArguments(menuItemsBundle);

    	// Check if there was previous content -> replace , else -> add
    	if (getSupportFragmentManager().findFragmentById(R.id.contentFragment_container_main) != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.contentFragment_container_main, contentFragment).commit();
    	else
    		getSupportFragmentManager().beginTransaction().add(R.id.contentFragment_container_main, contentFragment).commit();
    }
    
    
	// Handle Activity Result (Camera, )
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode == RESULT_OK)
		{
			// CAMERA
		    if (requestCode == CommonUtilities.REQUEST_IMAGE_CAPTURE)
		    {
		    	Bitmap imageBitmap = null;
		    	
		    	if(data.getExtras() != null)
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
		    	
				
	            // Put bitmap image in image view
	            ImageView imgView = (ImageView) contentFragment.getView().findViewById(R.id.ivPicture);
	            imgView.setImageBitmap(imageBitmap);
	    	}
	    }
	    else
	    {

	        Log.i(CommonUtilities.TAG, "Failed receiving image onActivityResult");
	    }
	}

	
	// Handles click-event of the 'Take a picture'-button in the fragment_content_camera
	public void initiateCamera(View view)
	{
		// Check for camera device
		if(!checkForValidCameraDevice())
		{
			Toast.makeText(this, "No camera device was found. Please enable or install your camera.", Toast.LENGTH_LONG).show();
			return;
		}
		else
		{
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

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
	            Log.i(CommonUtilities.TAG, "IOException while getting the photo back to HomeActivity with cause " + e.getCause());
	        }
	        
	        // Continue only if the File was successfully created
	        if (photoFile != null)
	        {
	            //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,  Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, CommonUtilities.REQUEST_IMAGE_CAPTURE);
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
}
