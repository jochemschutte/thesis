package io.message;

import prototype.main.RumEngine;

public class ResourceInterfaceExporter extends RumExporter{

	String resourceName;
	String componentName;
	
	public ResourceInterfaceExporter(String exportAs, String resourceName, String componentName) {
		this.setExportAs(exportAs);
		this.resourceName = resourceName;
		this.componentName = componentName;
	}
	
	@Override
	public String export(IOMessage m, RumEngine e) {
		return e.getResources().get(resourceName).getInterfaces().stream().filter(i -> i.getComponent().getIdentifier().equals(componentName)).findAny().get().getValue().toString();
	}
	
} 