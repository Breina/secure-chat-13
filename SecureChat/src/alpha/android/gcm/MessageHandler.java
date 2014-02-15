package alpha.android.gcm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.android.common.CommonUtilities;
import alpha.android.speechbubble.Message;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MessageHandler extends AsyncTask<Void, String, String>
{
    private GoogleCloudMessaging gcm;
	private ArrayList<Message> messages;
	private AtomicInteger msgId;
	
	
	public MessageHandler(GoogleCloudMessaging gcmInstance)
	{
		gcm = gcmInstance;
		msgId = new AtomicInteger();
		messages = new ArrayList<Message>();
		//adapter = lvAdapter;
	}
	
	
	@Override
	protected String doInBackground(Void... params)
	{
		// this.publishProgress(String.format("%s started writing", sender));
		String msg = "";
            
        try
        {
            Bundle data = new Bundle();
            	data.putString("my_message", "Hello World");
              	
            String id = Integer.toString(msgId.incrementAndGet());
            gcm.send(CommonUtilities.SENDER_ID + "@gcm.googleapis.com", id, data);
            
            msg = "success";
            Log.i(CommonUtilities.TAG, "Message was sent");
        }
        catch (IOException ex)
        {
            msg = "error";
        }
        
	    return msg;
	}
	
	
	@Override
	protected void onPostExecute(String text)
	{
		addNewMessage(new Message(text, false)); // add the orignal message from server.
	}
	
	
	public void addNewMessage(Message m)
	{
		messages.add(m);
		//adapter.notifyDataSetChanged();
		//getListView().setSelection(messages.size()-1);

        Log.i(CommonUtilities.TAG, "Message was added:   " + m.toString());
	}
}