package io.message;

import java.io.Serializable;

import prototype.main.RumEngine;

public abstract class RumExporter implements Serializable{
	
	private static final long serialVersionUID = -8895481434366685850L;
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