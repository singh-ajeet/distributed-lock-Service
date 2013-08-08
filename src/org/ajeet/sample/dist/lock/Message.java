package org.ajeet.sample.dist.lock;

import java.io.Serializable;

public final class Message implements Serializable{
	private final String content;
	private final String source;
	

	public Message(String content, String source){
		this.content = content;
		this.source = source;
	}

	public String getContent(){
		return this.content;
	}

	public String getSource() {
		return this.source;
	}
}
