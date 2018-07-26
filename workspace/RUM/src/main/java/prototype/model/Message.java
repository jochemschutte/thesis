package prototype.model;

import java.util.Map;

public class Message{
	
	String currentModel;
	Map<String, Double> vars;
	
	public Message(String currentModel, Map<String, Double> vars) {
		this.currentModel = currentModel;
		this.vars = vars;
	}
	
	public String getCurrentModel() {
		return currentModel;
	}
	
	public Map<String, Double> getVars() {
		return vars;
	}
	
	
}