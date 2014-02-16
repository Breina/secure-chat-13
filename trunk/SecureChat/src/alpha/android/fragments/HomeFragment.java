package alpha.android.fragments;

import alpha.android.R;
import alpha.android.common.CommonUtilities;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_content_home, container,
				false);
		
		Button btnUpdate = (Button) view.findViewById(
				R.id.btn_update_inbox);
		btnUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateMessages(v);
			}
		});

		return view;
	}

	public void updateMessages(View view) {
		Log.i(CommonUtilities.TAG, "Updating messages...");
	}

}
