package com.aotobang.app.entity;

import java.io.File;

public class VoiceMsgBody extends FileMsgBody {
	private boolean listen;
	public boolean isListen() {
		return listen;
	}
	public void setListen(boolean listen) {
		this.listen = listen;
	}
	private File voiceFile;
	private int length;
	public File getVoiceFile() {
		return voiceFile;
	}
	public void setVoiceFile(File voiceFile) {
		this.voiceFile = voiceFile;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
	
	
}
