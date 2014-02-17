package alpha.android.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import alpha.android.R;
import alpha.android.common.CommonUtilities;
import alpha.android.speechbubble.ListviewAdapter;
import alpha.android.speechbubble.Message;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class MessageFragment extends ListFragment implements OnClickListener {
	private ArrayList<Message> messages;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		messages = new ArrayList<Message>();

		// Check if extra's (Camera / Location)
		if (getArguments() != null) {
			if (getArguments().getString("fileName") != null) {
				Bitmap attachedPicture = BitmapFactory.decodeFile(getActivity()
						.getFilesDir()
						+ "/"
						+ getArguments().getString("fileName"));

				ImageSpan is = new ImageSpan(getActivity(), attachedPicture);
				SpannableString text = new SpannableString(
						attachedPicture.toString());
				text.setSpan(is, 0, 0 + text.length(),
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

				messages.add(new Message(text, false));

			} 
			if (getArguments().getString("location") != null) {

				try {

					String title = getArguments().getString("location");
					String path = getActivity().getFilesDir() + "/" + title;

					ObjectInputStream ois = new ObjectInputStream(
							new FileInputStream(new File(path)));

					double lat = ois.readDouble();
					double lng = ois.readDouble();
					
					LatLng pos = new LatLng(lat, lng);
					
					messages.add(new Message(title, true, true));
					
				} catch (StreamCorruptedException e) {
					Log.e(CommonUtilities.TAG, e.getMessage());
				} catch (FileNotFoundException e) {
					Log.e(CommonUtilities.TAG, e.getMessage());
				} catch (IOException e) {
					Log.e(CommonUtilities.TAG, e.getMessage());
				}

			}
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_content_gcm, container,
				false);

		// ADAPTER
		ListviewAdapter adapter = new ListviewAdapter(getActivity(), messages);
		setListAdapter(adapter);

		// Set button onClick-listeners
		Button b1 = (Button) view.findViewById(R.id.btn_Send);
		b1.setOnClickListener(this);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		if (getFragmentManager().findFragmentById(R.id.content_fragment) != null)
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		setListAdapter(null);
	}

	// Handles the click-events of the buttons
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_Send:
			// TODO: SEND MESSAGE
			break;

		default:
			break;
		}
	}

	public void sendMessage(View view) {

	}
}
