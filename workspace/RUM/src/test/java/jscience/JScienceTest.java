package jscience;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class JScienceTest{
	
	@Test
	public void run() {
		Amount<Duration> t = Amount.valueOf(1, SI.SECOND);
		System.out.println(t);
		System.out.println(t.to(SI.MILLI(SI.SECOND)));
		System.out.println(t.to(SI.MILLI(SI.SECOND)).getExactValue());
	}
	
	@Test
	public void testMultimap() {
		SetMultimap<String, String> map = HashMultimap.create();
		System.out.println(map.get(""));
	}
	
	public static long millis;
	@Test
	public void timerTest() {
		Timer t = new Timer();
		millis = System.currentTimeMillis();
		t.schedule(new TestTimer(),  0, 1000);
		try {
			Thread.sleep(20000);
		}catch(InterruptedException e) {}
		System.out.println(t.purge());
		t.cancel();
	}
	
	private class TestTimer extends TimerTask{
		
		int i = 0;
		
		@Override
		public void run() {
			i++;
			try {
				Thread.sleep(5000);
			}catch(InterruptedException e) {}
			System.out.println(i+": " +(System.currentTimeMillis()-millis));
		}
	}
	
	@Test
	public void testHashMultimap() {
		HashMultimap<String, String> map = HashMultimap.create();
		map.put("henk", null);
		System.out.println(map);
		map.putAll("ingrid", new HashSet<String>());
		System.out.println(map);
	}
	
}