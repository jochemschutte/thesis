package architecture.demo.helpers;

import static architecture.demo.global.Fields.TIMESTAMP;

import java.util.Comparator;

import io.message.IOMessage;

public class TimestampComparator implements Comparator<IOMessage>{

	@Override
	public int compare(IOMessage arg0, IOMessage arg1) {
		String m0 = arg0.getVars().get(TIMESTAMP);
		String m1 = arg1.getVars().get(TIMESTAMP);
		if(m0 == null || m1 == null) {			
			throw new IllegalArgumentException("a message did not have a timestamp");
		}
		Long t0 = Long.parseLong(m0);
		Long t1 = Long.parseLong(m1);
		return t0.compareTo(t1);
	}
	
}