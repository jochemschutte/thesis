package architecture.demo.processes;

import java.util.Arrays;

import architecture.components.SingleMessageProcessor;
import io.message.IOMessage;

public class Reporter extends SingleMessageProcessor{

	private static final long serialVersionUID = -7348313219531277546L;
	String format;
	String[] args;
	
	public Reporter(String format, String... args) {
		super();
		this.format = format;
		this.args = args;
	}
	
	public void print(String s) {
		System.out.println(s);
	}
	
	public void print(IOMessage m) {
		Object[] vars = Arrays.stream(args).map(v->m.getVars().get(v)).toArray();
		System.out.println(String.format(format, vars));
	}
	
	@Override
	public void runForMessage(IOMessage m) {
		print(m);			
	}
	
}