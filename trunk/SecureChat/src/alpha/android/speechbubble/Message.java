package alpha.android.speechbubble;

import android.text.SpannableString;


public class Message
{
	private String textMessage;
	private SpannableString imageMessage;
	private boolean isMine;
	

	// Constructor for text message
	public Message(String message, boolean isMine)
	{
		this.textMessage = message;
		this.isMine = isMine;
	}
	
	
	// Constructor for image message
	public Message(SpannableString image, boolean isMine)
	{
		this.imageMessage = image;
		this.isMine = isMine;
	}

	
	public Object getMessage()
	{
		if (this.textMessage != null)
			return textMessage;
		else
			return imageMessage;
	}
	
	public void setTextMessage(String message)
	{
		this.textMessage = message;
	}
	
	public boolean isMine()
	{
		return isMine;
	}
	
	public void setMine(boolean isMine)
	{
		this.isMine = isMine;
	}
	
}
