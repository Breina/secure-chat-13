package alpha.android.fragments;

import java.io.File;
import java.io.IOException;

import alpha.android.CameraManager;
import alpha.android.R;
import alpha.android.common.CommonUtilities;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeContentFragment extends Fragment
{
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	if (getArguments() != null)
    	{
            // Check which menu item was clicked and open the corresponding content
        	Bundle bundleData = getArguments();
    		
        	switch (bundleData.getInt("menuIndex"))
    		{
    			// HOME
    			case 0:
    				return inflater.inflate(R.layout.fragment_content_home, container, false);
    				
        		// CONTACTS
    			case 1:
    				break;

    			// CHAT
    			case 2: 
    				return inflater.inflate(R.layout.fragment_content_gcm, container, false);
    						
    			// CAMERA	
    			case 3: 
    				return inflater.inflate(R.layout.fragment_content_camera, container, false);
    			
    			// LOCATION
    			case 5: 
    				break;
    				
    			// SETTINGS
    			case 6: 
    				break;
        				
    			// ANNIHILATE
    			case 7: 
    				break;
    				
    			// LOGOUT
    			case 4: 
    				break;
    				
    			default:
    				Log.i(CommonUtilities.TAG, "   * ContentFragment received an unknown clicked Menu Index: " + 
    										    bundleData.getInt("menuIndex"));
    				break;
    		}

    	}
    	
		return null;
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }
    
    
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }
    

}
