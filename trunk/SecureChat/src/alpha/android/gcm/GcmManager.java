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
	            String result = "";
	            
	            Log.i(CommonUtilities.TAG, "Starting registering");
	    	
	            try
	            {
	                registrationID = gcm.register(CommonUtilities.SENDER_ID);
	                result = "Device registered successfully, registration ID = " + registrationID;
	                
	                // You should send the registration ID to your server over HTTP, so it
	                // can use GCM/HTTP or CCS to send messages to your application
	                sendRegistrationIdToBackend();
	                
	                // For this demo: we don't need to send it because the device will send
	                // upstream messages to a server that echo back the message using the
	                // 'from' address in the message.
	                // Persist the regID - no need to register again.
	                //storeRegistrationId(applicationContext, registrationID);
	            }
	            catch (IOException ex)
	            {
	               result = "Error :" + ex.getMessage();
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
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
	  
	  
	public interface GcmDataConnection
	{
		public void registrationResponse(String result);
	}
	
	  
	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP to send messages
	 */
	private void sendRegistrationIdToBackend()
	{
	    // Implementation
	}
	
	
	/**
	 * Stores the registration ID
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId)
	{
	    // Implementation
	}
  
	

	
}