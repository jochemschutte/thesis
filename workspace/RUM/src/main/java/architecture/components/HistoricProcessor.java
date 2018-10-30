package architecture.components;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.message.IOMessage;

@SuppressWarnings("serial")
public abstract class HistoricProcessor extends SingleMessageProcessor{

	private LinkedList<IOMessage> buffer = new LinkedList<>();
	
	public HistoricProcessor() {
		super();
	}
	
	@Override
	public void runForMessage(IOMessage m) {
		buffer.addFirst(m);		
		cleanHistory(buffer);
		runForMessageHistoric(ImmutableList.copyOf(buffer));
	}
	
	/**
	 * Cannot add message to history, cannot edit history!
	 */
	public abstract void runForMessageHistoric(List<IOMessage> history);
	public abstract void cleanHistory(LinkedList<IOMessage> history);
	
}