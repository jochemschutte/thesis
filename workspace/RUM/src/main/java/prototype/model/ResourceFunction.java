package prototype.model;

import java.util.Map;

import bsh.EvalError;
import bsh.Interpreter;

public class ResourceFunction{
	String expression;
	
	public ResourceFunction(String expression) {
		this.expression = expression;
	}
	
	public int execute(Message m, Map<String, Integer> values) throws EvalError{
		Interpreter interpreter = new Interpreter();
		for(Map.Entry<String, Integer> entry : m.getVars().entrySet()) {
			interpreter.set(entry.getKey(), entry.getValue());
		}
		for(Map.Entry<String, Integer> entry : values.entrySet()) {
			interpreter.set(entry.getKey(), entry.getValue());
		}
		return (Integer)interpreter.eval(expression);
	}
	
}