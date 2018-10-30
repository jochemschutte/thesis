package io.message;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class IOMessage implements Serializable{
	
	private static final long serialVersionUID = 8277862343028590547L;
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