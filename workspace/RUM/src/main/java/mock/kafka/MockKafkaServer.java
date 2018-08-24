package mock.kafka;

import java.util.Map;
import java.util.Observable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class MockKafkaServer extends Observable{
	
	public static final MockKafkaServer SERVER = new MockKafkaServer();
	
	SetMultimap<String, MockKafkaConsumer> consumers = HashMultimap.create();
	
	public void publish(String topic, Map<String,String> m) {
		if(consumers.containsKey(topic))
			consumers.get(topic).forEach(c -> new ConsumerRunner(c, m).start());
	}
	
	private class ConsumerRunner extends Thread{
	
		MockKafkaConsumer c;
		Map<String, String> m;
		
		public ConsumerRunner(MockKafkaConsumer c, Map<String, String> m) {
			this.c = c;
			this.m = m;
		}
		
		@Override
		public void run() {
			c.consume(m);
		}
	}
	
}