package alpha.android.fragments;

import java.util.ArrayList;

import alpha.android.R;
import alpha.android.speechbubble.ListviewAdapter;
import alpha.android.speechbubble.Message;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MessageFragment extends ListFragment implements OnClickListener
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

		// Check if extra's (Camera / Location)
		if (getArguments() != null)
		{
			if (getArguments().getString("fileName") != null)
			{
				Bitmap attachedPicture = BitmapFactory.decodeFile(getActivity().getFilesDir() + "/" + getArguments().getString("fileName"));
				
				ImageSpan is = new ImageSpan(getActivity(), attachedPicture);
				SpannableString text = new SpannableString(attachedPicture.toString());
				text.setSpan(is, 0, 0 + text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				
				messages.add(new Message(text, false));
			}
			else if (getArguments().getString("location") != null)
			{
				// TODO: IMPLEMENT LOCATION OBJECT INTO CHAT BUBBLE
			}
		}
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState)
	 {
		 View view = inflater.inflate(R.layout.fragment_content_gcm, container, false);
		 
		 // ADAPTER
		 ListviewAdapter adapter = new ListviewAdapter(getActivity(), messages);
		 setListAdapter(adapter);

			
		 // Set button onClick-listeners
		 Button b1 = (Button) view.findViewById(R.id.btn_Send);
		 b1.setOnClickListener(this);
			
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
	public void onDestroyView()
	{
		super.onDestroyView();
		
		setListAdapter(null);
	}

	
	// Handles the click-events of the buttons
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_Send:
				//TODO: SEND MESSAGE
				break;
				
			default:
				break;
        }
	}
	
	public void sendMessage(View view)
	{
		
	}
}
