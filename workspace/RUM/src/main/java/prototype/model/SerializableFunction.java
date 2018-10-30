package prototype.model;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface SerializableFunction<I, O>extends Function<I, O>, Serializable {

//	private static final long serialVersionUID = 299760320671558648L;
	
}