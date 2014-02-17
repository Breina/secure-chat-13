package alpha.android.fragments;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import alpha.android.CameraManager;
import alpha.android.R;
import alpha.android.common.CommonUtilities;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class CameraFragment extends Fragment implements OnClickListener
{
	private CameraManager camManager;
	private Bitmap takenPicture;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// CamManager -> will instantiate Camera
		camManager = new CameraManager(getActivity());
		Intent cameraIntent = camManager.initiateCameraIntent();
		startActivityForResult(cameraIntent, CommonUtilities.REQUEST_IMAGE_CAPTURE);
		
		// takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
		// Uri.fromFile(photoFile));
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{		
		View view = inflater.inflate(R.layout.fragment_content_camera, container, false);
		
		Button b = (Button) view.findViewById(R.id.btnPicture);
		b.setText("Send as attachment");
	    b.setOnClickListener(this);
	        
		return view;
	}
	
	
	// Handle Activity Result (taken picture)
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == Activity.RESULT_OK)
		{
			// CAMERA
			if (requestCode == CommonUtilities.REQUEST_IMAGE_CAPTURE)
			{
				if (data.getExtras().get("data") != null)
				{
					takenPicture = (Bitmap) data.getExtras().get("data");
					
					Log.i(CommonUtilities.TAG, "Successfully received image: " + takenPicture.toString());
				}
				else
				{
					try
					{
						takenPicture = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
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

				if (takenPicture != null)
				{
					optimizePictureDimensions(takenPicture);
					
					// Put bitmap image in image view
					ImageView imgView = (ImageView) getActivity().findViewById(R.id.ivPicture);
					imgView.setImageBitmap(takenPicture);
					
					savePictureInternally(takenPicture);
				}
			}
		}
		else
		{

			Log.i(CommonUtilities.TAG, "Failed receiving image onActivityResult");
		}
	}


	// Button click-events
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnPicture:
				
				// Save image internally
				Bundle bundle = saveThumbnailInternally(takenPicture);
				
				// Create MessageFragment with fileName of taken picture
				Fragment messageFragment = new MessageFragment();
				messageFragment.setArguments(bundle);
				
				// Check if there was previous content -> replace , else -> add
				if (messageFragment != null)
					if (getActivity().getSupportFragmentManager().findFragmentById(
							R.id.contentFragment_container_main) != null)
						getActivity().getSupportFragmentManager().beginTransaction()
								.replace(R.id.contentFragment_container_main, messageFragment)
								.commit();
					else
						getActivity().getSupportFragmentManager().beginTransaction()
								.add(R.id.contentFragment_container_main, messageFragment)
								.commit();
        }
	}


	// Saves a .png thumbnail of the taken picture into the app's internal storage (private)
	private Bundle saveThumbnailInternally(Bitmap outputImage)
	{
		Bundle fileNameBundle = new Bundle();
        String fileName = outputImage.toString() + ".png";
        FileOutputStream fos = null;
        
        fileNameBundle.putString("fileName", fileName);
        
        try
        {
        	fos = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
        	
    	    outputImage.compress(Bitmap.CompressFormat.PNG, 90, fos);
    	    
    	    fos.close();
    	    
        	Log.i(CommonUtilities.TAG, "Thumbnail successfully internally saved: " + fileName);
	    }
        catch (FileNotFoundException e)
        {
        	e.printStackTrace();
        }
        catch (IOException e)
        {
			e.printStackTrace();
		}
	    
	    return fileNameBundle;
	}

	
	// Saves the taken image into the app's internal storage (private)	
	private void savePictureInternally(Bitmap outputImage)
	{
        String fileName = outputImage.toString() + ".png";
        fileName = fileName.substring(25);
        FileOutputStream fos = null;
        
        try
        {
        	fos = getActivity().openFileOutput("LAST_SAVED_IMAGE.png", Context.MODE_PRIVATE);
        	Log.i(CommonUtilities.TAG, "Image successfully internally saved: " + fileName);
        	
        	// 100 means no compression
    	    outputImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
    	    
    	    fos.close();
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
	
	
	// Optimizes pictures' dimensions for optimal memory usage
	private Bitmap optimizePictureDimensions(Bitmap image)
	{
		ImageView imgView = (ImageView) getActivity().findViewById(R.id.ivPicture);
		
	    // Get the dimensions of the View
	    int targetW = imgView.getWidth();
	    int targetH = imgView.getHeight();

	    // Get the dimensions of the bitmap
	    android.graphics.BitmapFactory.Options bmOptions = new android.graphics.BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    
	    int photoW = image.getWidth();
	    int photoH = image.getHeight();

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    return Bitmap.createScaledBitmap(image, targetW, targetH, false);
	}
}