package architecture.demo.processes;

import static architecture.demo.global.Topics.SENSOR;

import architecture.components.Spout;
import io.message.IOMessage;

public class SensorSpout extends Spout{

	@Override
	public void nextTuple() {
		int milis = 1;
		while(Sensor.sensorQueue.isEmpty()) {
			milis = Math.max(milis, milis*2);
			try{
				Thread.sleep(milis);
			}catch(InterruptedException e) {}
		}
		IOMessage m = Sensor.sensorQueue.poll();
		if(m != null) {
			publish(SENSOR, m);
		}
	}
	
}