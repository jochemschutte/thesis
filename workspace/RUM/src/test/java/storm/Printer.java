package storm;

import java.io.IOException;

import architecture.components.SingleMessageProcessor;
import io.message.IOMessage;

public class Printer extends SingleMessageProcessor{

	public int id;
	
	public Printer(int id) {
		this.id = id;
	}
	
	@Override
	public void runForMessage(IOMessage m) {
		String s = String.format("sender: %s,\treciever: %s,\tindex: %s", m.getVars().get("id"), id, m.getVars().get("v"));
		System.out.println(s);
		try{
			StormTest.out.write(s+'\n');
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
}