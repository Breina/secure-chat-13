package alpha.android.fragments;

import java.util.concurrent.ExecutionException;

import alpha.android.HomeActivity;
import alpha.android.R;
import alpha.android.common.CommonUtilities;
import alpha.android.gcm.GcmManager;
import alpha.android.gcm.GcmManager.GcmDataConnection;
import alpha.android.webservice.WebserviceManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class OptionsFragment extends Fragment implements OnClickListener
{
	// Managing objects
	private WebserviceManager webManager;
	private GcmManager gcmManager;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_content_options,
				container, false);

		CheckBox cbCameraAllowed = (CheckBox) view
				.findViewById(R.id.cb_allowCamera);
		CheckBox cbGpsAllowed = (CheckBox) view.findViewById(R.id.cb_allowGPS);

		if (checkCamera())
			cbCameraAllowed.setChecked(true);

		if (checkGPS())
			cbGpsAllowed.setChecked(true);

		
		// Instantiate the GCM managing object
		gcmManager = new GcmManager(getActivity());
		gcmManager.setGcmDataConnectionListener(new GcmDataConnection()
		{
			
			@Override
			public void registrationResponse(String reg_id)
			{
				Toast.makeText(getActivity(), "You re-registered succesfully. Congratulations." + reg_id, Toast.LENGTH_LONG).show();
				Button btn_reregister = (Button) getActivity().findViewById(R.id.btn_reregister);
				btn_reregister.setEnabled(false);
			}
		});
		
		
		Button b1 = (Button) view.findViewById(R.id.btn_reregister);
		Button b2 = (Button) view.findViewById(R.id.btn_checkPlayServices);
		Button b3 = (Button) view.findViewById(R.id.btn_checkDeviceRegistration);
		Button b4 = (Button) view.findViewById(R.id.btn_changePassword);

	    b1.setOnClickListener(this);
	    b2.setOnClickListener(this);
	    b3.setOnClickListener(this);
	    b4.setOnClickListener(this);
	    
		return view;
	}

	
	// Checks device for Camera allowance
	private boolean checkCamera()
	{
		if (!getActivity().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA))
			return false;

		return true;
	}

	
	// Checks device for GPS allowance
	private boolean checkGPS()
	{
		if (!getActivity().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_LOCATION))
			return false;

		if (getActivity().checkCallingOrSelfPermission(
				"android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED)
			return true;

		return false;

	}
	
	
	// Checks whether the user already has a Gcm registration ID in the DB
	public void checkDeviceRegistration(View v)
	{
		String result = gcmManager.checkGcmRegistrationId(HomeActivity.username);
		
		if (result == null)
		{
			Toast.makeText(getActivity(), "The webservice appears to be offline. Please try again later.", Toast.LENGTH_LONG).show();
			return;
		}
		else if (result.length() > 0 && !result.contains("fail"))
		{
			Toast.makeText(getActivity(), "You are already succesfully registered." + result, Toast.LENGTH_LONG).show();
		}
		else
		{
			Toast.makeText(getActivity(), "No registration was found, please use the button below to re-register.", Toast.LENGTH_LONG).show();
			
			// Enables re-register button
			Button btn_reregister = (Button) getActivity().findViewById(R.id.btn_reregister);
			btn_reregister.setEnabled(true);
		}
	}


	// Handles the click-events of the buttons
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_reregister:
				gcmManager.registerInBackground();
				break;
				
			case R.id.btn_checkPlayServices:
				gcmManager.checkPlayServices(v);
				break;	
				
			case R.id.btn_checkDeviceRegistration:
				checkDeviceRegistration(v);
				break;
				
			case R.id.btn_changePassword:
				changePassword(v);
				break;
				
			default:
				break;
        }
	}

	
	// Click-event handler of the change password; connects to Web Service
	public void changePassword(View view)
	{
		EditText ed_newPass = (EditText) getActivity().findViewById(R.id.ed_changePass);
		String newPass = ed_newPass.getText().toString();
		
		String result = validatePassword(newPass);
		
		if (result != null)
		{
			Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
			return;
		}
		else
		{
			Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
			
			String[] colNames = {"username", "password"};
			String[] colValues = {HomeActivity.username, newPass};
			
			try
			{
				webManager = new WebserviceManager(CommonUtilities.REST_CHANGE_PASS, colNames);
				String webResult = webManager.execute(colValues).get();
				
				if (webResult == null)
				{
					Toast.makeText(getActivity(), "The webservice appears to be offline. Please try again later.", Toast.LENGTH_LONG).show();
					return;
				}
				else
				{
					Toast.makeText(getActivity(), webResult, Toast.LENGTH_LONG).show();				
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
	}

	

	// Validate entered password
    private String validatePassword(String password)
    {
		if (password.length() <= 5)
			return "Password has to be longer than 5 characters.";
		else
			return null;
	}
}
