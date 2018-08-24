package io.message;

import prototype.main.RumEngine;

public class MessageExporter extends RumExporter{
	
	String fieldName;
	
	public MessageExporter(String fieldName) {
		this(fieldName, fieldName);
	}
	
	public MessageExporter(String fieldName, String exportAs) {
		this.setExportAs(exportAs);
		this.fieldName = fieldName;
	}
	
	@Override
	public String export(IOMessage m, RumEngine e){
		return m.getVars().get(fieldName);
	}
}