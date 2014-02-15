package alpha.android;

import java.util.concurrent.ExecutionException;

import alpha.android.common.CommonUtilities;
import alpha.android.webservice.WebserviceManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends FragmentActivity
{
	// Managing objects
	private WebserviceManager webServiceManager;

	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

	
    // Method that handles the click-event of the Login button
    public void verifyLogin(View view)
    {
    	// Get the entered data EditText's and extract the data
    	EditText edUserName = (EditText) findViewById(R.id.edLogin);
    	EditText edPassword = (EditText) findViewById(R.id.edPassword);
    	String username = edUserName.getText().toString();
    	String password = edPassword.getText().toString();
    	
    	String[] loginParamKeys = {"username", "password"};
    	String[] loginParamValues = {username, password};
    	
    	try
    	{
        	// Instantiate AsyncTask successful WebserviceManager and execute the request with the login parameters
    		webServiceManager = new WebserviceManager(CommonUtilities.REST_LOGIN, loginParamKeys);
			String result = webServiceManager.execute(loginParamValues).get();
			
			// Check and handle result message
			if (result == null)
			{
				Toast.makeText(this, "The web service appears to be offline. Please try again later.", Toast.LENGTH_LONG).show();
			}
			else if (!result.contains("fail"))
			{
				// Remove quotes
				result = result.replaceAll("^\"|\"$", "");
				result = result.replaceAll("\\\\n", "");
				
	 			Toast.makeText(this, "Login successful, welcome back " + result, Toast.LENGTH_LONG).show();
	 			
        	    navigateHome(result);
			}
			else
			{
				Toast.makeText(this, "Login unsuccessful, please try again.", Toast.LENGTH_LONG).show();
			}
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
    }

    
    // Starts the HomeActivity when logged in successfully
    public void navigateHome(String username)
    {
    	// Create new Intent to start the HomeActivity
    	Intent intent = new Intent(this, HomeActivity.class);
    	intent.putExtra("username", username);
    	startActivity(intent);
    }

    
    // Method that handles the click-event of the Register button
    public void btnRegister_click(View view)
    {
    	// Create new Intent to start the HomeActivity
    	Intent intent = new Intent(this, RegisterActivity.class);
    	startActivityForResult(intent, 1);
    }

    
    // User came from registration form
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	  if (requestCode == 1)
    	  {
    	     if(resultCode == RESULT_OK)
    	     {      
    	         String username = data.getStringExtra("username");
    	         
	        	 EditText edUserName = (EditText) findViewById(R.id.edLogin);
	        	 edUserName.setText(username);
    	     }
    	  }
    }
    
	
	// TESTING WEBSERVICE FUNCTION FOR BRECHT
//	public void testGcmId(View view)
//	{
//    	// Get the entered data EditText's and extract the data
//    	EditText edUserName = (EditText) findViewById(R.id.edLogin);
//    	String username = edUserName.getText().toString();
//    	
//    	Log.i("TESTT", username + " was found ");
//    	
//    	String[] columnNames = {"username"};
//    	String[] columnValues = {username};
//    	
//		try
//		{
//			final String result = new WebserviceManager(CommonUtilities.REST_CHECK_GCM, columnNames).execute(columnValues).get();
//			
//			Toast.makeText(this, "Printing result for Brecht:   " + result, Toast.LENGTH_LONG).show();
//		}
//		catch (InterruptedException e)
//		{
//			e.printStackTrace();
//			Log.i(CommonUtilities.TAG, "InterruptedException thrown from MainActivity with cause " + e.getCause());
//		}
//    	catch (ExecutionException e)
//    	{
//			e.printStackTrace();
//			Log.i(CommonUtilities.TAG, "ExecutionException thrown from MainActivity with cause " + e.getCause());
//		}
//    	catch (Exception e)
//    	{
//    		e.printStackTrace();
//			Log.i(CommonUtilities.TAG, "Error in MainActivity of type:   " + e.getMessage().toString() +
//									   "with cause " + e.getCause());
//    	}
//	}
	
    public void bypassLogin(View v) {
    	
    	EditText edUserName = (EditText) findViewById(R.id.edLogin);
    	String username = edUserName.getText().toString();
    	
    	if (username.equals(""))
    		username = "Bypasser";
    	
    	navigateHome(username);
    }
	
}