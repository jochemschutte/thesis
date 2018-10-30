package architecture.components;

import static javax.measure.unit.SI.MILLI;
import static javax.measure.unit.SI.SECOND;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.measure.quantity.Duration;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.jscience.physics.amount.Amount;

import io.message.IOMessage;

public abstract class AccumulatorProcessor extends SingleMessageProcessor implements Runnable{
		
	long millis;
	List<IOMessage> buffer = new LinkedList<>();
	AtomicBoolean running = new AtomicBoolean(false);
		
	public AccumulatorProcessor(long millis) {
		super();
		this.millis = millis;
	}
	
	public AccumulatorProcessor(Amount<Duration> time) {
		this(time.to(MILLI(SECOND)).getExactValue());
	}
	
	@Override
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
		super.prepare(conf, context, collector);
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		while(true) {
			List<IOMessage> cut = buffer;
			buffer = new LinkedList<>();
			sortRange(cut);
			runForRange(cut);
			try {
				Thread.sleep(millis);
			}catch(InterruptedException e) {}
		}
	}
			
	@Override
	public void runForMessage(IOMessage m) {
		buffer.add(m);
	}
	
	public abstract void runForRange(List<IOMessage> range);
	public abstract void sortRange(List<IOMessage> range);	
}