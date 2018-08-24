package architecture.demo.global;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

public class ConfigurableTimer extends Timer{
	
	long speedFactor;
	long startTimeMillis = System.currentTimeMillis();
	
	public ConfigurableTimer(long speedFactor) {
		super();
		this.speedFactor = speedFactor;
	}
	
	public ConfigurableTimer(long speedFactor, boolean isDaemon) {
		super(isDaemon);
		this.speedFactor = speedFactor;
	}
	
	public ConfigurableTimer(long speedFactor, String name) {
		super(name);
		this.speedFactor = speedFactor;
	}
	
	public ConfigurableTimer(long speedFactor, String name, boolean isDaemon) {
		super(name, isDaemon);
		this.speedFactor = speedFactor;
	}
	public void	schedule(TimerTask task, Date firstTime, long period) {
		super.schedule(task, firstTime, (long)(period/speedFactor));
	}
	
	public void schedule(TimerTask task, Amount<Duration> delay, Amount<Duration> period) {
		super.schedule(task,  delay.to(SI.MILLI(SI.SECOND)).getExactValue(), delay.to(SI.MILLI(SI.SECOND)).getExactValue());
	}
	
	public void	schedule(TimerTask task, long delay) {
		super.schedule(task, (long)(delay/speedFactor));
	}
	public void	schedule(TimerTask task, long delay, long period) {
		super.schedule(task,  (long)(delay/speedFactor),(long)(period/speedFactor));
	}
	public void	scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
		super.scheduleAtFixedRate(task,  firstTime, (long)(period/speedFactor));
	}
	public void	scheduleAtFixedRate(TimerTask task, long delay, long period) {
		super.scheduleAtFixedRate(task, (long)(delay/speedFactor),(long)(period/speedFactor));
	}
	
	public long getSpeedFactor(){
		return this.speedFactor;
	}
	
	public long getTimeElapsedMillis() {
		return (long)((System.currentTimeMillis()-startTimeMillis)*speedFactor);
	}
	
	private static ConfigurableTimer instance = null;;
	
	public static void setInstance(ConfigurableTimer instance) {
		if(ConfigurableTimer.instance != null)
			throw new IllegalStateException("ConfigurableTimer was already initialized");
		ConfigurableTimer.instance = instance;
	}
	
	public static ConfigurableTimer getInstance() {
		if(instance == null)
			throw new IllegalStateException("ConfigurableTimer was not initialized");
		return instance;
	}
	
}