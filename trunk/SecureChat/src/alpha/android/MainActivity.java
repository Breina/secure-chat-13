package alpha.android;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

import alpha.android.common.CommonUtilities;
import alpha.android.gcm.GcmManager;
import alpha.android.webservice.WebserviceManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	// Managing objects
	private WebserviceManager webServiceManager;
	private boolean prefRememberUsername, prefRefreshId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences prefs = getPrefs();
		prefRememberUsername = prefs.getBoolean("pref_key_remember_username",
				false);
		prefRefreshId = prefs.getBoolean("pref_key_new_gcm_id", false);

	}

	@Override
	public View onCreateView(View parent, String name, Context context,
			AttributeSet attrs) {

		if (prefRememberUsername) {
			EditText edUserName = (EditText) findViewById(R.id.edLogin);

			String username = getPrefs().getString("pref_key_username",
					"not found");
			Log.i(CommonUtilities.TAG, "Loading " + username);
			edUserName.setText(username);

		}
		
		if (prefRefreshId)
			refreshGcmId();

		return super.onCreateView(parent, name, context, attrs);
	}
	
	
	private SharedPreferences getPrefs() {
		SharedPreferences sharedPrefs = getSharedPreferences(
				CommonUtilities.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		return sharedPrefs;
	}

	// Method that handles the click-event of the Login button
	public void verifyLogin(View view) {
		// Get the entered data EditText's and extract the data
		EditText edUserName = (EditText) findViewById(R.id.edLogin);
		EditText edPassword = (EditText) findViewById(R.id.edPassword);
		String username = edUserName.getText().toString();
		String password = edPassword.getText().toString();

		String[] loginParamKeys = { "username", "password" };
		String[] loginParamValues = { username, password };

		try {
			// Instantiate AsyncTask successful WebserviceManager and execute
			// the request with the login parameters
			webServiceManager = new WebserviceManager(
					CommonUtilities.REST_LOGIN, loginParamKeys);
			String result = webServiceManager.execute(loginParamValues).get();

			// Check and handle result message
			if (result == null) {
				Toast.makeText(
						this,
						"The web service appears to be offline. Please try again later.",
						Toast.LENGTH_LONG).show();
			} else if (!result.contains("fail")) {
				// Remove quotes
				result = result.replaceAll("^\"|\"$", "");
				result = result.replaceAll("\\\\n", "");

				Toast.makeText(this,
						"Login successful, welcome back " + result,
						Toast.LENGTH_LONG).show();

				navigateHome(result);
			} else {
				Toast.makeText(this, "Login unsuccessful, please try again.",
						Toast.LENGTH_LONG).show();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.i(CommonUtilities.TAG,
					"InterruptedException thrown from MainActivity with cause "
							+ e.getCause());
		} catch (ExecutionException e) {
			e.printStackTrace();
			Log.i(CommonUtilities.TAG,
					"ExecutionException thrown from MainActivity with cause "
							+ e.getCause());
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(CommonUtilities.TAG, "Error in MainActivity of type:   "
					+ e.getMessage().toString() + "with cause " + e.getCause());
		}

		if (prefRememberUsername) {

			Editor editor = getPrefs().edit();

			Log.i(CommonUtilities.TAG, "Saving " + username);
			editor.putString("pref_key_username", username).commit();
		}
	}

	// Starts the HomeActivity when logged in successfully
	public void navigateHome(String username) {
		// Create new Intent to start the HomeActivity
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("username", username);
		startActivity(intent);
	}

	// Method that handles the click-event of the Register button
	public void btnRegister_click(View view) {
		// Create new Intent to start the HomeActivity
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivityForResult(intent, 1);
	}

	// User came from registration form
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				String username = data.getStringExtra("username");

				EditText edUserName = (EditText) findViewById(R.id.edLogin);
				
				if (!prefRememberUsername)
					edUserName.setText(username);
			}
		}
	}

	public void refreshGcmId()
	{
		GcmManager gcmManager = new GcmManager(this);
		
    	String gcm_registration_id = "huh?";

    	// Reg ID already entered
    	if (!gcm_registration_id.toLowerCase(Locale.getDefault()).startsWith("please"))
    	{
    		Toast.makeText(getApplicationContext(), "You already registered your device.", Toast.LENGTH_LONG).show();
    	}
    	else
    	{
    		if (gcmManager.validateGooglePlayServices())
    			gcmManager.registerInBackground();
    		else
    			Toast.makeText(getApplicationContext(), "Please install the Google Play Services APK in order to proceed.",
    						   Toast.LENGTH_LONG).show();
    	}
	}

	public void bypassLogin(View v) {

		EditText edUserName = (EditText) findViewById(R.id.edLogin);
		String username = edUserName.getText().toString();

		if (username.equals(""))
			username = "Bypasser";

		navigateHome(username);
	}

}