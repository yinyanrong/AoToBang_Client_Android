package com.aotobang.app.phone;

import org.linphone.core.LinphoneChatMessage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author Sylvain Berfini
 */
public class ChatMessage {
	private String message;
	private String timestamp;
	private String url;
	private boolean incoming;
	private int status;
	private int id;
	private Bitmap image;
	private boolean isRead;
	
	public ChatMessage(int id, String message, byte[] rawImage, String timestamp, boolean incoming, int status, boolean read) {
		super();
		this.id = id;
		this.message = message;
		this.timestamp = timestamp;
		this.incoming = incoming;
		this.status = status;
		this.image = rawImage != null ? BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length) : null;
		this.isRead = read;
	}
	
	public ChatMessage(int id, String message, Bitmap image, String timestamp, boolean incoming, int status, boolean read) {
		super();
		this.id = id;
		this.message = message;
		this.timestamp = timestamp;
		this.incoming = incoming;
		this.status = status;
		this.image = image;
		this.isRead = read;
	}
	
	public int getId() {
		return id;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public boolean isIncoming() {
		return incoming;
	}
	
	public void setIncoming(boolean incoming) {
		this.incoming = incoming;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public LinphoneChatMessage.State getStatus() {
		return LinphoneChatMessage.State.fromInt(status);
	}

	public Bitmap getImage() {
		return image;
	}
	
	public boolean isRead() {
		return isRead;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String toString() {
		return this.id + " : " + this.message + " (" + this.url + ") @ " + this.timestamp + ", read= " + this.isRead + ", incoming= " + this.incoming + ", status = " + this.status; 
	}
}
