package alpha.android;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class CameraManager 
{
	//private static String currentPhotoPath;
	private Context appContext;
	
	
	public CameraManager(Context context)
	{
		appContext = context;
	}

	
	// Create a new imageFile in context
	public File createImageFile() throws IOException
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

	
	// Optimizes pictures' dimensions for optimal memory usage
//	private Bitmap optimizePictureDimensions()
//	{
//		ImageView imgView = (ImageView) findViewById(R.id.ivPicture);
//		
//	    // Get the dimensions of the View
//	    int targetW = imgView.getWidth();
//	    int targetH = imgView.getHeight();
//
//	    // Get the dimensions of the bitmap
//	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//	    bmOptions.inJustDecodeBounds = true;
//	    BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
//	    int photoW = bmOptions.outWidth;
//	    int photoH = bmOptions.outHeight;
//
//	    // Determine how much to scale down the image
//	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//	    // Decode the image file into a Bitmap sized to fill the View
//	    bmOptions.inJustDecodeBounds = false;
//	    bmOptions.inSampleSize = scaleFactor;
//	    bmOptions.inPurgeable = true;
//
//	    return BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
//	}
}
