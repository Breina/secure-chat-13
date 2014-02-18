package alpha.android.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class MessageFragment extends ListFragment implements OnClickListener {
	private ArrayList<Message> messages;

	private HashMap<String, LatLng> locations;
	private TextView tv_input;
	private EditText ed_recipient;
	private String pretext;
	private String preRecipient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		messages = new ArrayList<Message>();
		locations = new HashMap<String, LatLng>();

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

					addLocation(title, pos);

					// messages.add(new Message(title, true, pos));

				} catch (StreamCorruptedException e) {
					Log.e(CommonUtilities.TAG, e.getMessage() + ", " + e.getCause());
				} catch (FileNotFoundException e) {
					Log.e(CommonUtilities.TAG, e.getMessage() + ", " + e.getCause());
				} catch (IOException e) {
					Log.e(CommonUtilities.TAG, e.getMessage() + ", " + e.getCause());
				}

			}
			if (getArguments().getString("contact") != null) {

				try {

					String name = getArguments().getString("contact");
					String path = getActivity().getFilesDir() + "/" + name;

					File file = new File(path);
					FileInputStream fis = new FileInputStream(file);
					
					int length = (int) file.length();
					
					byte[] buffer = new byte[length];
					
					fis.read(buffer);
					preRecipient = new String(buffer);
					
					Log.d(CommonUtilities.TAG, "Adding recipient: " + preRecipient);
					
				} catch (StreamCorruptedException e) {
					Log.e(CommonUtilities.TAG, e.getMessage() + ", " + e.getCause());
				} catch (FileNotFoundException e) {
					Log.e(CommonUtilities.TAG, e.getMessage() + ", " + e.getCause());
				} catch (IOException e) {
					Log.e(CommonUtilities.TAG, e.getMessage() + ", " + e.getCause());
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

		tv_input = (TextView) view.findViewById(R.id.text);
		if (pretext != null)
			tv_input.setText(pretext);
		
		ed_recipient = (EditText) view.findViewById(R.id.ed_recipient);
		if (preRecipient != null)
			ed_recipient.setText(preRecipient);

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

	private String encodeLocations() {

		Log.i(CommonUtilities.TAG, "Encoding locations");

		StringBuilder sb = new StringBuilder();

		Iterator<String> i = locations.keySet().iterator();
		String title;
		LatLng pos;
		String inputText = tv_input.getText().toString();

		while (i.hasNext()) {
			title = i.next();

			if (!inputText.contains("#" + title + "#")) {
				Log.w(CommonUtilities.TAG, "Couldn't find location: " + title);
				locations.remove(title);

				continue;
			}

			pos = locations.get(title);

			sb.append("#");
			sb.append(title);
			sb.append(",");
			sb.append(String.valueOf(pos.latitude));
			sb.append(",");
			sb.append(String.valueOf(pos.longitude));
			sb.append("#");
		}

		locations.clear();

		return sb.toString();
	}

	private void parseLocations(String str) {

		Log.i(CommonUtilities.TAG, "Parsing locations");

		char c;
		boolean inside = false;
		int lastIndex = 0;

		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);

			if (c == '#') {

				if (!inside)

					lastIndex = i + 1;

				else {

					String subStr = str.substring(lastIndex, i - 1);
					parseLocation(subStr);
				}

				inside = !inside;

			}
		}
	}

	private void parseLocation(String str) {

		String[] parts = str.split(",");

		LatLng pos = new LatLng(Double.parseDouble(parts[1]),
				Double.parseDouble(parts[2]));

		messages.add(new Message(parts[0], false, pos));

	}

	private void addLocation(String title, LatLng pos) {

		Log.i(CommonUtilities.TAG, "Adding location");

		locations.put(title, pos);

		if (tv_input != null) {
			String inputText = tv_input.getText().toString();

			if (inputText.charAt(inputText.length() - 1) != ' ')
				inputText += " ";

			inputText += "#" + title + "# ";

			tv_input.setText(inputText);
		} else {
			pretext = "#" + title + "#";
		}
	}

}
