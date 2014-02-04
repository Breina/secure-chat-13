package alpha.android.fragments;

import java.util.ArrayList;

import alpha.android.R;
import alpha.android.contacts.Contact;
import alpha.android.contacts.ContactsAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsFragment extends ListFragment
{

	private boolean contactSelected;
	private Contact lastSelectedContact;
	private MenuItem menuStartChat, menuDelete;

	public ContactsFragment()
	{
		contactSelected = false;
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

		menuStartChat = menu.findItem(R.id.start_chat);
		menuDelete = menu.findItem(R.id.delete);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		return super.onOptionsItemSelected(item);
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
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		contacts.add(new Contact("Tjeu", "beheerder"));
		contacts.add(new Contact("Brecht", "cxgamer"));
		contacts.add(new Contact("Jan", "chickensl4y3r"));

		setListAdapter(new ContactsAdapter(getActivity(), contacts));

		return super.onCreateView(inflater, container, savedInstanceState);
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
