package io.pubsub;

import io.message.IOMessage;

public interface Producer{
	
	public void publish(String topic, IOMessage m);
}