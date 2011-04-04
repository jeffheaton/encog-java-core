package org.encog.app.analyst.commands;

import org.encog.app.analyst.EncogAnalyst;

public class CmdReset extends Cmd {

	public final static String COMMAND_NAME = "RESET";
	
	public CmdReset(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand(String args) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
