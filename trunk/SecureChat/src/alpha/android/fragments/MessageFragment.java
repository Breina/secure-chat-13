package alpha.android.fragments;

import java.util.ArrayList;

import alpha.android.R;
import alpha.android.speechbubble.ListviewAdapter;
import alpha.android.speechbubble.Message;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MessageFragment extends ListFragment
{
	private ArrayList<Message> messages;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		messages = new ArrayList<Message>();

		messages.add(new Message("Hello", false));
		messages.add(new Message("Hi!", true));
		messages.add(new Message("Wassup??", false));
		messages.add(new Message("Nothing much, working on speech bubbles.", true));
		messages.add(new Message("It's going goooood.", true));
		messages.add(new Message("Allright!", false));


	}
	
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState)
	 {
		 View view = inflater.inflate(R.layout.fragment_content_gcm, container, false);
		 
		 // ADAPTER
		 ListviewAdapter adapter = new ListviewAdapter(getActivity(), messages);
		 setListAdapter(adapter);

         return view;
	 }

	@Override
	public void onStart()
	{
		super.onStart();
		
		 if (getFragmentManager().findFragmentById(R.id.content_fragment) != null)
	        	getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}



	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		setListAdapter(null);
	}


}
