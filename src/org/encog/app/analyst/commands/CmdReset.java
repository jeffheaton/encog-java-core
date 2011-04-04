package org.encog.app.analyst.commands;

import java.util.Map;

import org.encog.app.analyst.EncogAnalyst;

public class CmdReset extends Cmd {

	public final static String COMMAND_NAME = "RESET";
	
	public CmdReset(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand(String args) {
		Map<String,String> revertedData = this.getAnalyst().getRevertData();
		this.getScript().getProperties().performRevert(revertedData);		
		return false;
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
