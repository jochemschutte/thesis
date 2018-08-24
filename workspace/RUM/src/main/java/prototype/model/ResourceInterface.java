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
		return value;
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
	
	public static ResourceInterface[] connect(Component offering, Component consuming, Resource r, InterfaceType type) {
		ResourceInterface[] result = new ResourceInterface[2];
		result[0] = new ResourceInterface(r, offering, InterfaceType.OFFERS);
		result[1] = new ResourceInterface(r, consuming, type);
		return result;
	}
	
	public static ResourceInterface[] connect(Component offering, Component consuming, Resource r) {
		return connect(offering, consuming, r, InterfaceType.CONSUMES);
	}
	
	public static ResourceInterface[] connect(Component offering, Component consuming, Resource r, String functionOffer, String functionConsumer, InterfaceType type) {
		if(functionOffer != null)
			offering.getResourceFunctions().put(r,  new ResourceFunction(functionOffer));
		if(functionConsumer != null)
			consuming.getResourceFunctions().put(r, new ResourceFunction(functionConsumer));
		return connect(offering, consuming, r, type);
	}
	
	public static ResourceInterface[] connect(Component offering, Component consuming, Resource r, String functionOffer, String functionConsumer) {
		return connect(offering, consuming, r, functionOffer, functionConsumer, InterfaceType.CONSUMES);
	}
	
}