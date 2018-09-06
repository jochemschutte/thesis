package mock.kafka;

import static architecture.demo.global.Topics.FIELD_LABEL;
import static architecture.demo.global.Topics.MESSAGE;
import static architecture.demo.global.Topics.SEVERITY;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import architecture.components.SingleMessageProcessor;
import io.message.IOMessage;
import io.pubsub.Producer;

public class MockKafkaConsumerGroup extends MockKafkaConsumer{

	ConcurrentLinkedQueue<IOMessage> queue = new ConcurrentLinkedQueue<>();
	Collection<? extends SingleMessageProcessor> consumers;
	Collection<ConsumerRunner> runners = null;
	AtomicLong backoff = new AtomicLong(1);
	AtomicInteger processed = new AtomicInteger(0);
	
	public MockKafkaConsumerGroup(Collection<? extends SingleMessageProcessor> cons) {
		consumers = cons;
		new Timer().schedule(new ConsumerGroupLogger(), 10000,5000);
	}
	
	public void start() {
		runners = consumers.stream().map(c -> new ConsumerRunner(c)).collect(Collectors.toSet());
		runners.forEach(r-> r.start());
	}
	
	@Override
	public void consume(IOMessage m) {
		queue.offer(m);
		runners.forEach(r -> r.interrupt());
	}
	
//	@Override
//	public void run() {
//		while(true) {
//			boolean found = false;
//			System.out.println(backoff+ "-" + queue.size());
//			try {
//				Thread.sleep(backoff.get());
//			}catch(InterruptedException e) {}
//			if(!queue.isEmpty()) {
//				for(Map.Entry<SingleMessageProcessor, AtomicBoolean> entry : consumers.entrySet()) {
//					if(entry.getValue().getAndSet(true) == false) {
//						found = true;
//						new ConsumerRunner(entry.getKey(), queue.poll().getVars()).start();
//						break;
//					}
//				}
//			}
//			if(!found)
//				backoff.getAndUpdate(x->x*2);
//			else
//				backoff.getAndUpdate(x->Math.max(x/2,1));
//		}
//	}
	
	private class ConsumerGroupLogger extends TimerTask{
		
		Producer p = new MockKafkaProducer();
		
		@Override
		public void run() {
			String message = String.format("Processed: %d, queued: %d", processed.getAndSet(0), queue.size());
			p.publish("DEBUG", new IOMessage(ImmutableMap.of(SEVERITY, "INFO", FIELD_LABEL, "CONSUMER_GROUP", MESSAGE, message)));
		}
	}
	
	private class ConsumerRunner extends Thread{
		
		SingleMessageProcessor c;
		
		public ConsumerRunner(SingleMessageProcessor c) {
			this.c = c;
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(backoff.get()-1);
				}catch(InterruptedException e) {}
				IOMessage next = queue.poll();
				if(next != null) {
					c.runForMessage(next);
//					backoff.getAndUpdate(x->Math.max(x/2,1));
					backoff.set(1);
					processed.incrementAndGet();
				}else {
					backoff.getAndUpdate(x->Math.max(x,x*2));
				}
			}
		}
	}
	
}