package prototype.model;

import java.util.Map;

import bsh.EvalError;
import bsh.Interpreter;

public class ResourceFunction{
	String expression;
	
	public ResourceFunction(String expression) {
		this.expression = expression;
	}
	
	public Double execute(RumMessage m, Map<String, Double> values) throws EvalError{
		Interpreter interpreter = new Interpreter();
		for(Map.Entry<String, Double> entry : m.getVars().entrySet()) {
			interpreter.set(entry.getKey(), entry.getValue());
		}
		for(Map.Entry<String, Double> entry : values.entrySet()) {
			interpreter.set(entry.getKey(), entry.getValue());
		}
		// '0+' to enforce execution as arthmatic expression
		Object o = interpreter.eval("0+"+expression);
		if(o instanceof Double) {
			return (double)o;
		}else {
			return (int)o*1.0;
		}
			
	}
	
	@Override
	public String toString() {
		return expression;
	}
	
}