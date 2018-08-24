package architecture.demo.processes;

import static architecture.demo.global.Fields.AVG_THROUGHPUT;
import static architecture.demo.global.Fields.NETWORK_BANDWIDTH;
import static architecture.demo.global.Fields.NR_LESS_YEAR;
import static architecture.demo.global.Fields.NR_MESSAGES;
import static architecture.demo.global.Fields.SERVICE_TIME;
import static architecture.demo.global.Fields.SYSTEM_RUNTIME;
import static architecture.demo.global.Fields.THROUGHPUT;
import static architecture.demo.global.Fields.TOTAL_USED_BANDWIDTH;
import static architecture.demo.global.Topics.ANALYSER;
import static architecture.demo.global.Topics.APPLICATION;

import java.util.List;

import javax.measure.quantity.Duration;

import org.jscience.physics.amount.Amount;

import com.google.common.collect.ImmutableMap;

import architecture.components.AccumulatorProcessor;
import architecture.demo.global.ConfigurableTimer;
import io.message.IOMessage;

public class ApplicationCollector extends AccumulatorProcessor{
	
	long startMillis;
	
	public ApplicationCollector(Amount<Duration> time) {
		super(time);
		startMillis = ConfigurableTimer.getInstance().getTimeElapsedMillis();
	}
	
	@Override
	public void sortRange(List<IOMessage> range) {
		// no sorting required;
	}

	@Override
	public void runForRange(List<IOMessage> range) {
		if(startMillis > 0)
			throw new IllegalStateException("ApplicationCollector should not be used. Use AcucmulatorImplementation instead");
		float totalThroughput = range.stream().map(m->Float.parseFloat(m.getVars().get(THROUGHPUT))).reduce((float)0.0, (a,b)->a+b);
		float avgThroughput = totalThroughput == 0 ? 0 : totalThroughput/range.size();
		float totalBandwidthUsed = range.stream().map(m->Float.parseFloat(m.getVars().get(NETWORK_BANDWIDTH))).reduce((float)0.0, (a,b)->a+b);
		long nrLessThenYear = range.stream().map(m->Double.parseDouble(m.getVars().get(SERVICE_TIME))).filter(y->y<1.0).count();
		long daysRunning = (ConfigurableTimer.getInstance().getTimeElapsedMillis()-startMillis)/1000;
		long yearsRunning = daysRunning/365;
		daysRunning %= 365;
		long monthsRunning = daysRunning/30;
		daysRunning %= 30;
		String timeRunning = String.format("y:%d m:%d d:%d", yearsRunning, monthsRunning, daysRunning);
		IOMessage message = new IOMessage(ImmutableMap.of(SYSTEM_RUNTIME, timeRunning, AVG_THROUGHPUT, avgThroughput+"", NR_LESS_YEAR, nrLessThenYear+"", NR_MESSAGES, range.size()+""));
		publish(APPLICATION, message);
		publish(ANALYSER, new IOMessage(ImmutableMap.of(TOTAL_USED_BANDWIDTH, totalBandwidthUsed+"")));
	}
	
	
}