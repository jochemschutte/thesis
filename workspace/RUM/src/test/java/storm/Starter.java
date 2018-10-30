package storm;

import com.google.common.collect.ImmutableMap;

import architecture.components.Spout;
import io.message.IOMessage;

@SuppressWarnings("serial")
public class Starter extends Spout{

	public int id;
	int mod;
	int index = 0;
	
	public Starter(int id, int mod) {
		this.id = id;
		this.mod = mod;
	}
	
	@Override
	public void nextTuple() {
		publish("c"+index%mod, new IOMessage(ImmutableMap.of("v", Integer.toString(index), "id", Integer.toString(id))));
		index++;
		try {
			Thread.sleep(1000);
		}catch(InterruptedException e) {}
	}
	
	
}