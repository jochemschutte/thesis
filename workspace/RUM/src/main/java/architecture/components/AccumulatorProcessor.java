package architecture.components;

import static javax.measure.unit.SI.MILLI;
import static javax.measure.unit.SI.SECOND;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.measure.quantity.Duration;

import org.jscience.physics.amount.Amount;

import io.message.IOMessage;
import io.pubsub.Consumer;
import mock.kafka.MockKafkaConsumer;

public abstract class AccumulatorProcessor extends Processor implements Observer{
	
	//<temp>
	Consumer consumer = new MockKafkaConsumer();
	@Override
	public void subscribe(String topic) {consumer.subscribe(topic);}
	@Override
	public void remove(String topic) {consumer.remove(topic);}
	//</temp>
	
	
	long millis;
	LinkedList<IOMessage> buffer = new LinkedList<>();
	
	public AccumulatorProcessor(long millis) {
		super();
		this.millis = millis;
		consumer.addObserver(this);
		Timer t = new Timer();
		t.schedule(new Cutter(), 0, millis);
	}
	
	public AccumulatorProcessor(Amount<Duration> time) {
		this(time.to(MILLI(SECOND)).getExactValue());
	}
			
	@Override
	public void update(Observable obs, Object o) {
		buffer.add((IOMessage)o);
	}
	
	public abstract void runForRange(List<IOMessage> range);
	public abstract void sortRange(List<IOMessage> range);	
	private class Cutter extends TimerTask{
		
		@Override
		public void run() {
			List<IOMessage> cut = buffer;
			buffer = new LinkedList<>();
			sortRange(cut);
			runForRange(cut);
		}
	}
}