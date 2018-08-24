package io.pubsub;

import java.util.Observable;

import io.message.IOMessage;

public abstract class Consumer extends Observable{
	
	public abstract void consume(IOMessage m);
	public abstract void subscribe(String topic);
	public abstract void remove(String topic);
	
}