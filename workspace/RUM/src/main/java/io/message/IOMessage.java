package io.message;

import java.util.Map;
import java.util.TreeMap;

public class IOMessage{
	
	Map<String, String> vars;
	
	public IOMessage() {
		vars = new TreeMap<>();
	}
	
	public IOMessage(Map<String, String> vars) {
		this.vars = vars;
	}
	
	public Map<String, String> getVars() {
		return vars;
	}
	
	
}