package alpha.android.fragments;

import java.util.ArrayList;

import alpha.android.R;
import alpha.android.speechbubble.AwesomeAdapter;
import alpha.android.speechbubble.Message;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MessageFragment extends ListFragment {
	private ArrayList<Message> messages;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO Auto-generated method stub
		messages = new ArrayList<Message>();

		messages.add(new Message("Hello", false));
		messages.add(new Message("Hi!", true));
		messages.add(new Message("Wassup??", false));
		messages.add(new Message("nothing much, working on speech bubbles.", true));
		
		setListAdapter(new AwesomeAdapter(getActivity(), messages));
		
        View footerView = inflater.inflate(, null);
        addFooterView(footerView);
        add
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

        
	    return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		 if (getFragmentManager().findFragmentById(R.id.content_fragment) != null)
	        	getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}


}
