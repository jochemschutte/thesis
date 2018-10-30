package prototype.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

public class ResourceFunction implements Serializable{
	
	private static final long serialVersionUID = 3923695473818970648L;
	SerializableFunction<Double[], Double> function;
	String[] argNames;
	
	public ResourceFunction(double constant) {
		function = x->constant;
		argNames = new String[0];
	}
	
	public ResourceFunction(String variable) {
		try {
			double val = Double.parseDouble(variable);
			function = x->val;
			argNames = new String[0];
		}catch(NumberFormatException e) {
			if(!variable.matches("[A-Za-z][A-Za-z0-9_]*"))
				throw new IllegalArgumentException(String.format("\"new ResourceException(\"%s\")\": identifier string expected, but identifier was invalid (possibly arithmetic expression)", variable));
			function = x->x[0];
			argNames = new String[] {variable};
		}
	}
	
	public ResourceFunction(SerializableFunction<Double[], Double> function, String... argNames) {
		this.function = function;
		this.argNames = argNames;
	}
	
	public Double execute(RumMessage m, Map<String, Double> values) throws NoSuchElementException{
		Double[] vars = Arrays.stream(argNames).map(arg->getVar(arg, m, values)).toArray(Double[]::new); 
		if(Arrays.stream(vars).filter(v->v==null).count() > 0)
			return null;
		return function.apply(vars);	
	}
	
	private Double getVar(String varName, RumMessage m, Map<String, Double> values) {
		if(m.getVars().containsKey(varName))
			return m.getVars().get(varName);
		if(values.containsKey(varName))
			return values.get(varName);
		throw new NoSuchElementException(String.format("'%s' not found in RumMessage or available resources", varName));
	}
	
	@Override
	public String toString() {
		return function.toString() + ": " + String.join(", ", argNames);
	}
	
	public static class IncalculableException extends RuntimeException{

		private static final long serialVersionUID = 1L;
		
	}
	
}