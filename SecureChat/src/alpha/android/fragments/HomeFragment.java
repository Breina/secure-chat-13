package alpha.android.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import alpha.android.HomeActivity;
import alpha.android.R;
import alpha.android.common.CommonUtilities;
import alpha.android.webservice.WebserviceManager;
import android.content.Context;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment
{
	private WebserviceManager webManager;
	private String[] messages, senders;

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		readInternalData();

		// Re-initiate the chat-buttons
		createChatButtons();
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_content_home, container,
				false);

		Button btnUpdate = (Button) view.findViewById(R.id.btn_update_inbox);
		btnUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateMessages(v);
			}
		});

		return view;
	}

	
	// Click event-handler to pull messages from DB
	public void updateMessages(View view) {
		Log.i(CommonUtilities.TAG, "Pulling messages ..");

		String[] columnNames = { "recipient" };
		String[] values = { HomeActivity.username };

		try {
			webManager = new WebserviceManager(
					CommonUtilities.REST_PULL_MESSAGES, columnNames);
			String result = webManager.execute(values).get();

			if (result != null) {
				if (result.length() > 0) {
					// Cut off surrounding quotes
					result = result.substring(1);
					result = result.substring(0, (result.length() - 2));

					processMessages(result);
				} else {
					Toast.makeText(getActivity(),
							"There are no new messages. Try again later.",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(getActivity(),
						"RESULT WAS NULL. Try again later.", Toast.LENGTH_LONG)
						.show();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Log.i(CommonUtilities.TAG,
					"IllegalStateException thrown from MessageFragment");
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.i(CommonUtilities.TAG,
					"InterruptedException thrown from MessageFragment");
		} catch (ExecutionException e) {
			e.printStackTrace();
			Log.i(CommonUtilities.TAG,
					"ExecutionException thrown from MessageFragment");
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(CommonUtilities.TAG, "Error in MessageFragment of type:   "
					+ e.getMessage().toString());
		}
	}

	
	// Processes the pulled messages
	private void processMessages(String result)
	{
		String[] results = null;
		int senderCounter = 0;
		int messageCounter = 0;

		results = result.split(";;;;;");

		if ((results.length % 2) == 0) {
			senders = new String[results.length / 2];
			messages = new String[results.length / 2];

			for (int i = 0; i < results.length; i++) {
				if (i == 0 || (i % 2) == 0) {
					senders[senderCounter] = new String(results[i]);
					senderCounter++;
				} else {
					messages[messageCounter] = new String(results[i]);
					messageCounter++;
				}
			}

			// Save internally
			saveInternally("senders", senders);
			saveInternally("messages", messages);

			createChatButtons();
		}
	}

	
	// Saves the pulled senders and messages internally
	public void saveInternally(String filename, String[] arrayToSave)
	{
		FileOutputStream fos;

		try
		{
			fos = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);

			ObjectOutputStream outputStream = new ObjectOutputStream(fos);
			outputStream.writeObject(arrayToSave);
			outputStream.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	
	// Reads internal data to set senders and messages
	private void readInternalData()
	{
		try
		{
			if (getActivity().openFileInput("messages") != null)
			{
				FileInputStream fin = getActivity().openFileInput("messages");
				ObjectInputStream ois = new ObjectInputStream(fin);
				messages = (String[]) ois.readObject();
				ois.close();

				fin = getActivity().openFileInput("senders");
				ois = new ObjectInputStream(fin);
				senders = (String[]) ois.readObject();
				ois.close();
			}
		}
		catch (StreamCorruptedException e)
		{
			e.printStackTrace();
		}
		catch (OptionalDataException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			// continue here
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	
	// Collects all messages per user
	private String[] collectMessagesPerUser(String username)
	{
//		readInternalData();
		
		String[] returnMessages;
		ArrayList<String> messagesToOpen = new ArrayList<String>();

		for (int i = 0; i < messages.length; i++)
		{
			try
			{
				String sender = senders[i];

				if (sender == null)
				{
					Log.e(CommonUtilities.TAG, "JAWEL TJEU DAT MAG WEL.");
					continue;
				}

				if (sender.equals(username))
					messagesToOpen.add(messages[i]);
			}
			catch (Exception e)
			{
				Log.e(CommonUtilities.TAG, "Sender is null.");
			}
		}

		returnMessages = new String[messagesToOpen.size()];
		returnMessages = messagesToOpen.toArray(returnMessages);

		return returnMessages;
	}

	
	// Creates the needed GUI for every user that has sent messages to this device
	private void createChatButtons()
	{
		readInternalData();

		if (senders != null)
		{
			LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.ll_messages);

			Set<String> distinctSenders = new HashSet<String>(Arrays.asList(senders));

			if (distinctSenders.size() > 0)
			{
				for (final String sender : distinctSenders)
				{
					if (sender != null)
					{
						// SUB-LAYOUT
						LinearLayout subLayout = new LinearLayout(getActivity());
						subLayout.setOrientation(LinearLayout.HORIZONTAL);
						LinearLayout.LayoutParams subLayoutParams = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
						subLayout.setLayoutParams(subLayoutParams);

						// BUTTON OPEN MESSAGES
						LinearLayout.LayoutParams buttonLayout = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
						Button btnMessage = new Button(getActivity());
						btnMessage.setTextSize(17);
						btnMessage.setText("New messages from " + sender);
						btnMessage.setLayoutParams(buttonLayout);
						btnMessage.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v)
							{
								String[] messagesToPull = collectMessagesPerUser(sender);

								Bundle bundle = new Bundle();
								bundle.putString("sender", sender);
								bundle.putStringArray("messages", messagesToPull);

								// Create MessageFragment with sender and corresponding messages to pull
								Fragment messageFragment = new MessageFragment();
								messageFragment.setArguments(bundle);

								// Check if there was previous content ->
								// replace , else -> add
								if (messageFragment != null)
									if (getActivity()
											.getSupportFragmentManager()
											.findFragmentById(
													R.id.contentFragment_container_main) != null)
										getActivity()
												.getSupportFragmentManager()
												.beginTransaction()
												.replace(
														R.id.contentFragment_container_main,
														messageFragment)
												.commit();
									else
										getActivity()
												.getSupportFragmentManager()
												.beginTransaction()
												.add(R.id.contentFragment_container_main,
														messageFragment)
												.commit();
								
								// Delete the clicked Sender
								senders = removeElements(senders, sender);
								
								saveInternally("senders", senders);
								readInternalData();
							}
						});

						// ADD TO LAYOUT
						subLayout.addView(btnMessage);
						ll.addView(subLayout);

						// INVALIDATE
						ll.invalidate();
					}
				}
			} else {
				Toast.makeText(getActivity(), "No new messages were received.",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	
	// Removes an element from an array
	public String[] removeElements(String[] input, String deleteMe)
	{
		List<String> result = new LinkedList<String>();

		for (int i = 0; i < input.length; i++)
		{
			if (!deleteMe.equals(input[i]))
				result.add(input[i]);
			else
				messages[i] = null;
		}
		
		if (result.size() > 0)
			return result.toArray(input);
		else
		{
			Toast.makeText(getActivity(), "test", Toast.LENGTH_LONG).show();
			return null;
		}
	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		if (senders.length == 1)
		{
			Toast.makeText(getActivity(), "ON DESTROY EN SENDERS IS 1", Toast.LENGTH_LONG).show();
		}
		if (senders.length == 0)
		{
			Toast.makeText(getActivity(), "ON DESTROY EN SENDERS IS 0", Toast.LENGTH_LONG).show();
		}
		saveInternally("senders", senders);
		saveInternally("messages", messages);
	}

	
}
