package architecture.demo.processes;

import java.util.Timer;
import java.util.TimerTask;

import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import architecture.components.SingleMessageProcessor;
import io.message.IOMessage;

public class TimedReporter extends SingleMessageProcessor{

	IOMessage current;
	String format;
	String[] vars;
	
	public TimedReporter(Amount<Duration> time, String format, String... vars) {
		this(time.to(SI.MILLI(SI.SECOND)).getExactValue(), format, vars);
	}
	
	public TimedReporter(long timeMillis, String format, String... vars) {
		this.format = format;
		this.vars = vars;
		Timer t = new Timer();
		t.schedule(new Printer(), 0, timeMillis);
	}
	
	@Override
	public void runForMessage(IOMessage m) {
		current = m;
	}
	
	private class Printer extends TimerTask{

		@Override
		public void run() {
			if(current != null)
				System.out.println(current);
			else{
				System.out.println("No message queued.\n");
			}
		}
		
	}
	
	
	
}