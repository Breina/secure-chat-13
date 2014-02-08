package alpha.android.speechbubble;

import java.util.ArrayList;

import alpha.android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


public class ListviewAdapter extends BaseAdapter
{
	private Context appContext;
	private ArrayList<Message> mMessages;


	public ListviewAdapter(Context context, ArrayList<Message> messages)
	{
		super();
		this.appContext = context;
		this.mMessages = messages;
	}
	
	@Override
	public int getCount()
	{
		return mMessages.size();
	}
	
	@Override
	public Object getItem(int position)
	{		
		return mMessages.get(position);
	}
	
	@SuppressLint("ResourceAsColor") @Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Message message = (Message) this.getItem(position);

		ViewHolder holder; 
		if(convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(appContext).inflate(R.layout.list_chat_row, parent, false);
			holder.message = (TextView) convertView.findViewById(R.id.message_text);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		
		holder.message.setText(message.getMessage());
		
		LayoutParams lp = (LayoutParams) holder.message.getLayoutParams();

		//Check whether message is mine to show green background and align to right
		if(message.isMine())
		{
			holder.message.setBackgroundResource(R.drawable.speech_bubble_green);
			lp.gravity = Gravity.RIGHT;
		}
		//If not mine then it is from sender to show orange background and align to left
		else
		{
			holder.message.setBackgroundResource(R.drawable.speech_bubble_orange);
			lp.gravity = Gravity.LEFT;
		}
		holder.message.setLayoutParams(lp);
		holder.message.setTextColor(R.color.textColor);	
		
		return convertView;
	}
	
	
	private static class ViewHolder
	{
		TextView message;
	}

	@Override
	public long getItemId(int position)
	{
		//Unimplemented, because we aren't using Sqlite.
		return 0;
	}

}
