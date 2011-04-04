package org.encog.app.analyst.commands;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.PropertyConstraints;
import org.encog.app.analyst.script.prop.PropertyEntry;
import org.encog.app.analyst.script.prop.PropertyType;

public class CmdSet extends Cmd {

	public final static String COMMAND_NAME = "SET";
	
	public CmdSet(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand(String args) {
		int index = args.indexOf('=');
		String dots = args.substring(0,index).trim();
		String value = args.substring(index+1).trim();
		
		PropertyEntry entry = PropertyConstraints.getInstance().findEntry(dots);
		
		if( entry==null ) {
			throw new AnalystError("Unknown property: " + args.toUpperCase());
		}
		
		// strip quotes
		if( value.charAt(0)=='\"') {
			value = value.substring(1);
		}
		if( value.endsWith("\"")) {
			value = value.substring(0,value.length()-1);
		}
		
		String[] cols = dots.split("\\.");
		String section = cols[0];
		String subSection = cols[1];
		String name = cols[2];
		
		entry.validate(section, subSection, name, value);		
		this.getProp().setProperty(entry.getKey(), value);

		return false;
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
