package architecture.components;

import java.util.Observable;
import java.util.Observer;

import io.message.IOMessage;
import io.pubsub.Consumer;
import mock.kafka.MockKafkaConsumer;

public abstract class SingleMessageProcessor extends Processor implements Observer{

	Consumer consumer = new MockKafkaConsumer();
	
	public SingleMessageProcessor() {
		super();
		consumer.addObserver(this);
	}
	
	@Override
	public void update(Observable obs, Object o) {
		runForMessage((IOMessage)o);
	}
	
	@Override
	public void subscribe(String topic) {
		consumer.subscribe(topic);
	}
	
	@Override
	public void remove(String topic) {
		consumer.remove(topic);
	}
	
	public abstract void runForMessage(IOMessage m);
	
	
}