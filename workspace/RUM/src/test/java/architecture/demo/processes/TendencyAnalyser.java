package architecture.demo.processes;

import static architecture.demo.global.Fields.TOTAL_USED_BANDWIDTH;
import static architecture.demo.global.Topics.FIELD_LABEL;
import static architecture.demo.global.Topics.LABEL_DECREASING_THROUGHPUT;
import static architecture.demo.global.Topics.LOG;
import static architecture.demo.global.Topics.MESSAGE;
import static architecture.demo.global.Topics.SEVERITY;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import architecture.components.HistoricProcessor;
import io.message.IOMessage;

public class TendencyAnalyser extends HistoricProcessor{
	
	private static final int BUFFER_SIZE = 5;
	
	public TendencyAnalyser() {
		super();
	}

	@Override
	public void runForMessageHistoric(List<IOMessage> history) {
		List<Double> avgs = history.stream().map(m -> Double.parseDouble(m.getVars().get(TOTAL_USED_BANDWIDTH))).collect(Collectors.toList());
		Iterator<Double> iter = avgs.iterator();
		if(iter.hasNext()) {
			double prev;
			boolean decr = false;
			decr = history.size() >= BUFFER_SIZE;
			prev = iter.next();
			while(iter.hasNext() && decr) {
				double tmp = iter.next();
				decr = tmp > prev;
				prev =  tmp;
			}
			if(decr) {
				IOMessage m = new IOMessage();
				m.getVars().put(SEVERITY, "ERROR");
				m.getVars().put(FIELD_LABEL, LABEL_DECREASING_THROUGHPUT);
				m.getVars().put(MESSAGE, String.format("[%s]", String.join(",", avgs.stream().map(d->d.toString()).collect(Collectors.toList()))));
				publish(LOG, m);
			}
		}
	}

	@Override
	public void cleanHistory(LinkedList<IOMessage> history) {
		while(history.size() > BUFFER_SIZE) {
			history.removeFirst();
		}
	}
	
}