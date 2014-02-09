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
	private View footerView;
	
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
	        // FOOTER
			footerView = inflater.inflate(R.layout.list_chat_footer, null);
			
			// ADAPTER
			setListAdapter(new ListviewAdapter(getActivity(), messages));

	        return super.onCreateView(inflater, container, savedInstanceState);
	 }
	 
	 
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		

		getListView().addFooterView(footerView);
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


}