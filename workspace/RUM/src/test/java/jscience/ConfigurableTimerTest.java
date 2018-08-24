package jscience;

import org.junit.Test;

import architecture.demo.global.ConfigurableTimer;

public class ConfigurableTimerTest{
	
	@Test
	public void run() {
		ConfigurableTimer t = new ConfigurableTimer(1000);
		long start = t.getTimeElapsedMillis();
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e) {}
		System.out.println(t.getTimeElapsedMillis()-start);
	}
	
}