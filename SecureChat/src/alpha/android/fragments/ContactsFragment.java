package alpha.android.fragments;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.google.android.gms.maps.model.LatLng;

import alpha.android.R;
import alpha.android.common.CommonUtilities;
import alpha.android.contacts.Contact;
import alpha.android.contacts.ContactsAdapter;
import alpha.android.webservice.WebserviceManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsFragment extends ListFragment
{

	private final static int ADD = 0;
	private final static int CHAT = 1;
	private final static int DEL = 2;
	
	private final static String KEY_CONTACT_NAME_PREFIX = "key_contact_name_";
	private final static String KEY_CONTACT_USERNAME_PREFIX = "key_contact_username_";
	private final static String KEY_SIZE = "key_contacts_size";

	private boolean contactSelected;
	private Contact lastSelectedContact;
	private MenuItem menuStartChat, menuDelete;
	private ContactsAdapter contactsAdapter;
	
	private String lastEnteredUsername;

	public ContactsFragment()
	{
		contactSelected = false;
	}	

	private void promptForName()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("New contact");
		alert.setMessage("Input name");

		final EditText input = new EditText(getActivity());
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String name = input.getText().toString();
				Contact c = new Contact(name, lastEnteredUsername);
				
				contactsAdapter.addContact(c);
				contactsAdapter.notifyDataSetChanged();
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// nothing
			}
		});

		alert.show();
	}
	
	private void promptForUserName()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("New contact");
		alert.setMessage("Input username");

		final EditText input = new EditText(getActivity());
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				lastEnteredUsername = input.getText().toString();
				promptForName();
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// nothing
			}
		});

		alert.show();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.contacts, menu);

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		
//		menuAdd = menu.findItem(R.id.add);
		menuStartChat = menu.findItem(R.id.start_chat);
		menuDelete = menu.findItem(R.id.delete);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		int id = item.getOrder();

		switch (id)
		{
		case ADD:
			promptForUserName();
			return true;

		case CHAT:
			navigateToChat();
			return false;

		case DEL:
			contactsAdapter.removeContact(lastSelectedContact);
			contactsAdapter.notifyDataSetChanged();
			return true;
		}
		
		return false;
	}

	private void navigateToChat() {
		
		Bundle bundle = saveContact();
		
		Fragment messageFragment = new MessageFragment();
		messageFragment.setArguments(bundle);

		// Check if there was previous content -> replace , else -> add
		if (messageFragment != null)
			if (getActivity().getSupportFragmentManager().findFragmentById(
					R.id.contentFragment_container_main) != null)
				getActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.contentFragment_container_main,
								messageFragment).commit();
			else
				getActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.contentFragment_container_main,
								messageFragment).commit();
		
	}
	
	private Bundle saveContact() {
		Bundle bundle = new Bundle();
		String fileName = lastSelectedContact.getName();
		FileOutputStream fos = null;

		bundle.putString("contact", fileName);

		try {
			fos = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
			
			fos.write(lastSelectedContact.getUsername().getBytes());

			fos.flush();

			fos.close();

			Log.i(CommonUtilities.TAG, "Position was succesfully saved: "
					+ fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bundle;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);

		lastSelectedContact = (Contact) l.getItemAtPosition(position);

		menuStartChat.setTitle("Chat with " + lastSelectedContact.getName());
		menuDelete.setTitle("Delete " + lastSelectedContact.getName());

		if (!contactSelected)
		{
			menuStartChat.setVisible(true);
			menuDelete.setVisible(true);

			contactSelected = true;
		}

		getActivity().openOptionsMenu();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{

		contactsAdapter = new ContactsAdapter(getActivity());
		
		loadContacts();
		
		setListAdapter(contactsAdapter);

		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	private SharedPreferences getPrefs()
	{
		return PreferenceManager.getDefaultSharedPreferences(getActivity());
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		saveContacts();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
//		saveContacts();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		if (contactsAdapter.getCount() != 0)
			return;
		
		loadContacts();
		contactsAdapter.notifyDataSetChanged();
	}
	
	private void saveContacts() {
		
		Log.i(CommonUtilities.TAG, "Saving contacts...");
		
		SharedPreferences prefs = getPrefs();
		Editor editor = prefs.edit();
		
		int size = contactsAdapter.getCount();
		editor.putInt(KEY_SIZE, size);
		
		Contact c;
		
		for (int i = 0; i < size; i++) {
			
			c = (Contact) contactsAdapter.getItem(i);
			
			Log.i(CommonUtilities.TAG, c.toString());
			
			editor.putString(KEY_CONTACT_NAME_PREFIX + i, c.getName());
			editor.putString(KEY_CONTACT_USERNAME_PREFIX + i, c.getUsername());
		}
		
		editor.commit();
		
		Log.i(CommonUtilities.TAG, "Done!");
	}
	
	private void loadContacts() {
		
		Log.i(CommonUtilities.TAG, "Loading contacts...");
		
		SharedPreferences prefs = getPrefs();
		
		int size = prefs.getInt(KEY_SIZE, 0);
		
		String name, username;
		
		for (int i = 0; i < size; i++) {
			
			name = prefs.getString(KEY_CONTACT_NAME_PREFIX + i, null);
			username = prefs.getString(KEY_CONTACT_USERNAME_PREFIX + i, null);
			
			if (name == null || username == null) {
				Log.w(CommonUtilities.TAG, "Contact name and/or username is null! Username=" + username + " name=" + name);
				continue;
			}
			
			Contact c = new Contact(name, username);
			
			Log.i(CommonUtilities.TAG, c.toString());
			contactsAdapter.addContact(c);
		}
		
		Log.i(CommonUtilities.TAG, "Done!");
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if (getFragmentManager().findFragmentById(R.id.content_fragment) != null)
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		setListAdapter(null);
	}
	


}
