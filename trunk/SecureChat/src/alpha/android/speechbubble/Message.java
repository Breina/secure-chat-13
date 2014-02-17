package alpha.android.speechbubble;

import android.text.SpannableString;

public class Message {
	private String textMessage;
	private SpannableString imageMessage;
	private boolean isMine;
	private boolean isLocation;

	// Constructor for text message
	public Message(String message, boolean isMine) {
		this.textMessage = message;
		this.isMine = isMine;
		this.isLocation = false;
	}

	// Constructor for image message
	public Message(SpannableString image, boolean isMine) {
		this.imageMessage = image;
		this.isMine = isMine;
		this.isLocation = false;
	}

	public Message(String title, boolean isMine, boolean isLocation) {

		this.textMessage = title;
		this.isMine = isMine;
		this.isLocation = isLocation;
	}

	public Object getMessage() {
		if (this.textMessage != null)
			return textMessage;
		else
			return imageMessage;
	}

	public void setTextMessage(String message) {
		this.textMessage = message;
	}

	public boolean isMine() {
		return isMine;
	}

	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}

	public boolean isLocation() {
		return isLocation;

	}

}
