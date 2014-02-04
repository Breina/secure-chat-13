package alpha.android.fragments;

import alpha.android.R;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

public class OptionsFragment extends Fragment
{

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

		return view;
	}

	private boolean checkCamera()
	{
		if (!getActivity().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA))
			return false;

		return true;
	}

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

}
