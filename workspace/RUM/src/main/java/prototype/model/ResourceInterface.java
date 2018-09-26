package prototype.model;

public class ResourceInterface{
	
	public enum InterfaceType {CONSUMES, OFFERS, CALC}
	
	Double value = null;
	InterfaceType type;
	Resource resource;
	Component component;
	
	public ResourceInterface(Resource resource, Component component, InterfaceType type) {
		this.resource = resource;
		this.component = component;
		this.type = type;
		this.resource.getInterfaces().add(this);
		this.component.getInterfaces().add(this);
	}
	
	public ResourceInterface(Resource resource, Component component, InterfaceType type, ResourceFunction function) {
		this(resource,component,type);
		component.getResourceFunctions().put(resource, function);
	}
	
	public InterfaceType getType() {
		return this.type;
	}
	
	public Double getValue() {
		return this.value;
	}
	public void setValue(Double value) {
		this.value = value;
	}

	public Resource getResource() {
		return resource;
	}
	
	public Component getComponent() {
		return this.component;
	}
	
	@Override
	public String toString() {
		return String.format("%s %f %s", type.toString().toLowerCase(), value, resource.getIdentifier());
	}
	
}