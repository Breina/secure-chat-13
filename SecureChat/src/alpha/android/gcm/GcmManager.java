package alpha.android.gcm;

import java.io.IOException;

import alpha.android.common.CommonUtilities;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmManager
{
    private GoogleCloudMessaging gcm;
	private String registrationID;
	private GcmDataConnection gcmDataConn;
	
	
	public GcmManager(Context appContext)
	{
		gcmDataConn = (GcmDataConnection) appContext;
        gcm = GoogleCloudMessaging.getInstance(appContext);
	}

 
    /**
     * Registers the application with GCM servers in ASyncTask
     */
	public String registerInBackground()
	{
		new AsyncTask<Void, Void, String>()
        {
	        @Override
	        protected String doInBackground(Void... params)
	        {
	            Log.i(CommonUtilities.TAG, "Starting registering");
	    	
	            try
	            {
	                registrationID = gcm.register(CommonUtilities.SENDER_ID);
	            }
	            catch (IOException ex)
	            {
	            	ex.printStackTrace();
	               return "fail";
	            }
	            
	            return registrationID;
	        }
	          
	        @Override
	        protected void onPostExecute(String reg_id)
	        {
	            Log.i(CommonUtilities.TAG, "Entered onPostExecute of Backgroundtask GCM Registration");
	            
	            if (gcmDataConn != null) 
	            {
	            	gcmDataConn.registrationResponse(reg_id);
	            }
	        }
        }.execute(null, null, null);
	      
	    return registrationID;
	  
	}
	  
	
	// Interface that provides a data connection with other activities
	public interface GcmDataConnection
	{
		// Returns response to RegisterActivity
		public void registrationResponse(String result);
	}
	
}