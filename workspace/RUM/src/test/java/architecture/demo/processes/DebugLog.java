package architecture.demo.processes;

import static architecture.demo.global.Topics.MESSAGE;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import architecture.components.SingleMessageProcessor;
import io.message.IOMessage;

public class DebugLog extends SingleMessageProcessor{

	public static BufferedWriter out;
	
	public DebugLog() {
		super();
		try {
			out = new BufferedWriter(new FileWriter("log.txt"));
			Timer t = new Timer();
			t.schedule(new Flusher(), 1000, 1000);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void runForMessage(IOMessage m) {
		try{
			out.write(m.getVars().get(MESSAGE)+'\n');
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private class Flusher extends TimerTask{
		
		@Override
		public void run() {
			try{
				out.flush();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}