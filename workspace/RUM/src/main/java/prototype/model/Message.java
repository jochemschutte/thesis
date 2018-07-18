package prototype.model;

import java.util.Map;

public class Message{
	
	String currentModel;
	Map<String, Integer> vars;
	
	public Message(String currentModel, Map<String, Integer> vars) {
		this.currentModel = currentModel;
		this.vars = vars;
	}
	
	public String getCurrentModel() {
		return currentModel;
	}
	
	public Map<String, Integer> getVars() {
		return vars;
	}
	
	
}