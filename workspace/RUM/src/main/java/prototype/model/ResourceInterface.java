package prototype.model;

public class ResourceInterface{
	
	public enum InterfaceType {CONSUMES, OFFERS}
	
	int value;
	InterfaceType type;
	
	public ResourceInterface(InterfaceType type) {
		this.type = type;
	}
	
	public InterfaceType getType() {
		return this.type;
	}
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
}