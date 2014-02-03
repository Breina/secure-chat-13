package alpha.android.fragments;

import java.util.ArrayList;

import alpha.android.R;
import alpha.android.common.CommonUtilities;
import alpha.android.contacts.Contact;
import alpha.android.contacts.ContactsAdapter;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsFragment extends ListFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		contacts.add(new Contact("Tjeu", "is number 1"));
		contacts.add(new Contact("Brecht", "is number 2"));
		contacts.add(new Contact("And we have also", "number 3"));
		
		Toast.makeText(getActivity(), String.valueOf(contacts.get(0).obtainGcmId()), Toast.LENGTH_LONG).show();
		Log.i(CommonUtilities.TAG, "HOEREN   !!!!!!! " + String.valueOf(contacts.get(0).obtainGcmId()));
		
		setListAdapter(new ContactsAdapter(getActivity(), contacts));
		//setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.contact_row, values));		
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onStart() {
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
