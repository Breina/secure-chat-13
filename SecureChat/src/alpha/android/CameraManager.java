package alpha.android;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import alpha.android.common.CommonUtilities;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class CameraManager 
{
	private Context appContext;
	
	
	public CameraManager(Context context)
	{
		appContext = context;
	}

	
	// Create a new imageFile in context
	private File createImageFile() throws IOException
	{	
		File image = null;
		
	    // Create image file properties
	    String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(new Date());
	    String imageFileName = "JPEG-" + timeStamp;
	    
	    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	    {
	        File storageDir = Environment.getExternalStorageDirectory();
	        
		    // Create image
		    image = File.createTempFile(imageFileName, ".jpg", storageDir);
		    
		    Log.i("TESTING", "SD: " + Uri.fromFile(image).toString());
	    }
	    else
	    {
		    final File storageDir = new File(appContext.getFilesDir().toString());
		    storageDir.mkdirs(); //create folders where write files
		    image = new File(storageDir, imageFileName);
		    
		    Log.i("TESTING", "INTERNAL: " + Uri.fromFile(image).toString());
		    // Save a file: path for use with ACTION_VIEW intents
		    //currentPhotoPath = "file:" + image.getAbsolutePath();
	    }
	    
	    return image;
	}

	
	// Initiates the camera
	public Intent initiateCameraIntent()
	{
		Intent takePictureIntent = null;
		File photoFile = null;
		
		// Check for camera device
		if (!checkForValidCameraDevice())
			Toast.makeText(appContext, "No camera device was found. Please enable or install your camera.", Toast.LENGTH_LONG).show();
		else
		{
			try
			{
				takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				photoFile = this.createImageFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Log.i(CommonUtilities.TAG,
						"IOException while getting the photo back to HomeActivity with cause "
								+ e.getCause());
			}
		}
		
		return takePictureIntent;
	}

	
	// Checks whether the device has a valid camera
	private boolean checkForValidCameraDevice()
	{
		if (appContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
			return true;
		else
			return false;
	}


}
