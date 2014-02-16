package alpha.android.gcm;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import alpha.android.common.CommonUtilities;
import alpha.android.webservice.WebserviceManager;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmManager
{
	private Context applicationContext;
	private GcmDataConnection dataConn;
    private GoogleCloudMessaging gcm;
	
	
	public GcmManager(Context appContext)
	{
		applicationContext = appContext;
        gcm = GoogleCloudMessaging.getInstance(appContext);
	}

 
	// Interface that will work as connection to get data from AsyncTask (RegisterInBackground)
	public static interface GcmDataConnection
	{
		public void registrationResponse(String reg_id);
	}
	
	
	// Listener for the DataConnection
	public void setGcmDataConnectionListener(GcmDataConnection gcmDataConn) {
        this.dataConn = gcmDataConn;
    }

 
    // Registers the application with GCM servers in ASyncTask
	public void registerInBackground()
	{
		new AsyncTask<Void, Void, String>()
        {
	        @Override
	        protected String doInBackground(Void... params)
	        {
	            Log.i(CommonUtilities.TAG, "Starting registering");
	    	
	            String registrationID = null;
	            
	            try
	            {
	            	registrationID = gcm.register(CommonUtilities.SENDER_ID);
	                Log.i(CommonUtilities.TAG, "Succesfully registered new ID:" + registrationID);
	            }
	            catch (IOException ex)
	            {
	            	ex.printStackTrace();
	            }
	            
	            return registrationID;
	        }
	          
	        @Override
	        protected void onPostExecute(String reg_id)
	        {
	            Log.i(CommonUtilities.TAG, "Entered onPostExecute of Backgroundtask GCM Registration");
	            
	            dataConn.registrationResponse(reg_id);
	        }
        }.execute(null, null, null);
	}
	  
	
	// Checks whether the user already had a Gcm Registration ID in the DB
	public String checkGcmRegistrationId(String username)
	{
    	String[] columnNames = {"username"};
    	String[] columnValues = {username};
    	
		try
		{
			return new WebserviceManager(CommonUtilities.REST_CHECK_GCM, columnNames).execute(columnValues).get();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			Log.i(CommonUtilities.TAG, "InterruptedException thrown from MainActivity with cause " + e.getCause());
		}
    	catch (ExecutionException e)
    	{
			e.printStackTrace();
			Log.i(CommonUtilities.TAG, "ExecutionException thrown from MainActivity with cause " + e.getCause());
		}
    	catch (Exception e)
    	{
    		e.printStackTrace();
			Log.i(CommonUtilities.TAG, "Error in MainActivity of type:   " + e.getMessage().toString() +
									   "with cause " + e.getCause());
    	}
		
		return null;
	}
	
	
	// Checks if Google Play Services .apk is installed on the device
	public boolean validateGooglePlayServices()
	{
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(applicationContext);

		if (resultCode != ConnectionResult.SUCCESS)
		{
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
			{
				GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) applicationContext,
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
	
	
	// Click-event of the Check Google Play Services - button
	public void checkPlayServices(View v)
	{
		String text;
		
		if (validateGooglePlayServices())
			text = "Google Play Services are functioning properly.";
		else
			text = "Google Play Services was not found. Please re-install the .apk";
		
		Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show();
	}


}