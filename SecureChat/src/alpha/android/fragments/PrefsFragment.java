package alpha.android.fragments;

import alpha.android.R;
import alpha.android.common.CommonUtilities;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class PrefsFragment extends PreferenceListFragment implements
		SharedPreferences.OnSharedPreferenceChangeListener
{

	@Override
	public void onCreate(Bundle icicle)
	{

		super.onCreate(icicle);
		PreferenceManager preferenceManager = getPreferenceManager();
		preferenceManager.setSharedPreferencesName(CommonUtilities.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.xml.preferences);
		preferenceManager.getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key)
	{
	}
}