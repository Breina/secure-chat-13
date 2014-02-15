package alpha.android;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

import alpha.android.common.CommonUtilities;
import alpha.android.gcm.GcmManager;
import alpha.android.webservice.WebserviceManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class RegisterActivity extends Activity implements GcmManager.GcmDataConnection
{
	// Managing objects
	private WebserviceManager webServiceManager;
	private GcmManager gcmManager;
	private String registrationID;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
        // Create new instance of GcmManager (alpha.android.gcm)
        gcmManager = new GcmManager(this);
	}


    // Method that handles the click-event of the Registration button
    public void verifyAccountRegistration(View view)
    {
    	// Get the entered data EditText's and extract the data
    	EditText edUserName = (EditText) findViewById(R.id.edUsername);
    	EditText edPassword = (EditText) findViewById(R.id.edPassword);
    	
    	final String username = edUserName.getText().toString();
    	String password = edPassword.getText().toString();
    	
    	// Validate entered data and show error message if one returned
    	String validationResult = validateData(username, password);
    	if (validationResult != null)
    	{
    		Toast.makeText(this, validationResult, Toast.LENGTH_LONG).show();
    		return;
    	}

		// Set the data for the WebserviceManager to use
    	String[] registrationParamKeys = {"username", "password", "gcm_registration_id"};
    	String[] registrationParamValues = {username, password, registrationID};
    	
    	try
    	{
        	// Instantiate AsyncTask WebserviceManager and execute the request with the registration parameters
    		webServiceManager = new WebserviceManager(CommonUtilities.REST_REGISTER, registrationParamKeys);
			String result = webServiceManager.execute(registrationParamValues).get();
			
			// Cut off leading and trailing quote
			result = result.substring(1);
			result = result.substring(0, result.length() - 1);
			
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();

			// If registered successfully
			if (result.toLowerCase(Locale.getDefault()).contains("congratulations"))
			{
                new Handler().postDelayed(new Runnable()
                {
                      @Override
                      public void run()
                      {
                    	  // Create new Intent to start the HomeActivity
                      	Intent returnIntent = new Intent();
                      	returnIntent.putExtra("username", username);
                      	setResult(RESULT_OK, returnIntent);     
                      	finish();
                      }
                 }, 1500);
			}
			else
			{
				return;
			}

		}
    	catch (IllegalStateException e)
    	{
			e.printStackTrace();
			Log.i(CommonUtilities.TAG, "IllegalStateException thrown from MainActivity");
    	}
    	catch (InterruptedException e)
    	{
			e.printStackTrace();
			Log.i(CommonUtilities.TAG, "InterruptedException thrown from MainActivity");
		}
    	catch (ExecutionException e)
    	{
			e.printStackTrace();
			Log.i(CommonUtilities.TAG, "ExecutionException thrown from MainActivity");
		}
    	catch (Exception e)
    	{
    		e.printStackTrace();
			Log.i(CommonUtilities.TAG, "Error in MainActivity of type:   " + e.getMessage().toString());
    	}
    }
    

	// Validate entered data in edit text fields
    private String validateData(String username, String password)
    {
    	if (username.length() <= 3 || username.length() > 15)
    		return "Username has to contain between 4 and 15 characters.";
    		
		if (password.length() <= 5)
			return "Password has to be longer than 5 characters.";
		
		return null;
	}


	// Handles the click-event of the register gcm button
	public void verifyGcmRegistration(View v)
	{
    	TextView tvGcmRegId = (TextView) findViewById(R.id.tvGcmRegistration);
    	String gcm_registration_id = tvGcmRegId.getText().toString();

    	// Reg ID already entered
    	if (!gcm_registration_id.toLowerCase(Locale.getDefault()).startsWith("please"))
    	{
    		Toast.makeText(getApplicationContext(), "You already registered your device.", Toast.LENGTH_LONG).show();
    	}
    	else
    	{
    		if (checkPlayServices())
    			gcmManager.registerInBackground();
    		else
    			Toast.makeText(getApplicationContext(), "Please install the Google Play Services APK in order to proceed.",
    						   Toast.LENGTH_LONG).show();
    		
        	// will finally enter response()-method through GcmManager's onPostExecute();
    	}
    	
	}
	
	
	// Check the device to make sure it has the Google Play Services APK
    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
            	try
            	{
            		GooglePlayServicesUtil.getErrorDialog(resultCode, this, CommonUtilities.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            	}
            	catch (IllegalStateException e)
            	{
        			e.printStackTrace();
        			Log.i(CommonUtilities.TAG, "IllegalStateException thrown from MainActivity");
            	}
            	catch (NoClassDefFoundError e)
            	{
        			e.printStackTrace();
        			Log.i(CommonUtilities.TAG, "NoClassDefFoundError thrown from MainActivity");
            	}
            	catch (Exception e)
            	{
        			e.printStackTrace();
        			Log.i(CommonUtilities.TAG, "Exception thrown from MainActivity");
            	}
            }
            else
            {
                Log.i(CommonUtilities.TAG, "This device is not supported.");
                
                finish();
            }
            
            return false;
        }
        
        return false;
    }
    
    
	// Overridden method of GcmManager's interface GcmDataConnection (to get registration id when registered in background)
	@Override
	public void registrationResponse(String registration_id)
	{
    	TextView tvGcmRegId = (TextView) findViewById(R.id.tvGcmRegistration);
    	tvGcmRegId.setText("Device successfully registered.");
    	
    	this.registrationID = registration_id;
    	
    	// Get references to the Buttons, so we can enable the RegisterAccount button and disable the RegisterDevice
    	// and disable the GcmRegisterButton
    	Button btnRegisterAccount = (Button) findViewById(R.id.btnAccountRegister);
    	Button btnRegisterGcm = (Button) findViewById(R.id.btnGcmRegister);
    	btnRegisterAccount.setEnabled(true);
    	btnRegisterGcm.setEnabled(false);
	}

}
