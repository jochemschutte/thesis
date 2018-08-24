package io.message;

import prototype.main.RumEngine;

public abstract class RumExporter{
	
	private String exportAs;
	
	protected void setExportAs(String exportAs) {
		this.exportAs = exportAs;
	}
	
	public String getExportAs() {
		if(exportAs == null)
			throw new NullPointerException("'exportAs' was not set or was null");
		return this.exportAs;
	}
	
	public abstract String export(IOMessage m, RumEngine e);
	
}