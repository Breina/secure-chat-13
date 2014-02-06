package alpha.android.speechbubble;


public class Message
{
	String message;
	boolean isMine;
	

	public Message(String message, boolean isMine)
	{
		this.message = message;
		this.isMine = isMine;
	}
	

	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
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
