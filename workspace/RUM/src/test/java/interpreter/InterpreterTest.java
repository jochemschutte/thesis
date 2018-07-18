package interpreter;

import org.junit.Test;

import bsh.EvalError;
import bsh.Interpreter;

public class InterpreterTest{
	
	@Test
	public void run() {
		Interpreter i = new Interpreter();
		try {
			i.set("henk", 5);
			System.out.println(i.eval("henk*3"));
		} catch (EvalError e) {
			e.printStackTrace();
		}
	}
}