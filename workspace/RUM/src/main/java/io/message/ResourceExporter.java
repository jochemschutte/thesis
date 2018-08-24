package io.message;

import prototype.main.RumEngine;
import prototype.model.Resource;
import prototype.model.ResourceInterface.InterfaceType;

public class ResourceExporter extends RumExporter{ 
	
	String resourceName;
	InterfaceType t;
	
	public ResourceExporter(String resourceName, InterfaceType t) {
		this(resourceName, resourceName, t);
	}
	
	public ResourceExporter(String exportAs, String resourceName, InterfaceType t) {
		this.setExportAs(exportAs);
		this.resourceName = resourceName;
		this.t = t;
	}
	
	@Override
	public String export(IOMessage m, RumEngine e) {
		switch(t) {
		case OFFERS:
			return Resource.collect(e.getResources().get(resourceName).offered()).toString();
		case CONSUMES: 
			return Resource.collect(e.getResources().get(resourceName).consumed()).toString();
		default:
			throw new IllegalStateException("Other interface types than 'OFFERES' and 'CONSUMES' are not supported in ResourceExporter.");
		}
	}
}