package architecture.components;

import io.message.IOMessage;
import io.pubsub.Producer;
import mock.kafka.MockKafkaProducer;

public abstract class Processor extends Thread{
	
	Producer producer = new MockKafkaProducer();
	
	public abstract void subscribe(String topic);
	public abstract void remove(String topic);
	
	protected void publish(String topic, IOMessage m) {
		producer.publish(topic,  m);
	}
}