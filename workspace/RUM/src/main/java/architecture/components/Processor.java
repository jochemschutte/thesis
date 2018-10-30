package architecture.components;

import io.message.IOMessage;

public interface Processor{
	
	public void publish(String topic, IOMessage message);
	public void setProduceTopics(Iterable<String> topics);
	
}