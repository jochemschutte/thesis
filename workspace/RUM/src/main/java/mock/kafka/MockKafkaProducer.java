package mock.kafka;

import java.util.Map;

import io.message.IOMessage;

public class MockKafkaProducer implements io.pubsub.Producer{
	
	public void publish(String topic, IOMessage m) {
		publish(topic, m.getVars());
	}
	
	public void publish(String topic, Map<String,String> m) {
		MockKafkaServer.SERVER.publish(topic, m);
	}
} 