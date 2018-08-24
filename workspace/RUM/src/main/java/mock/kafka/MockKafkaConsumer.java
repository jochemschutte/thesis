package mock.kafka;

import java.util.Map;

import io.message.IOMessage;

public class MockKafkaConsumer extends io.pubsub.Consumer{
	
	public void consume(Map<String,String> keyvalue){
		consume(new IOMessage(keyvalue));
	}
	
	public void subscribe(String topic) {
		MockKafkaServer.SERVER.consumers.put(topic, this);
	}
	
	public void remove(String topic) {
		MockKafkaServer.SERVER.consumers.remove(topic, this);
	}
	
	public void consume(IOMessage m) {
		this.setChanged();
		this.notifyObservers(m);
	}
}