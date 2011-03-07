package org.encog.app.analyst.commands;

import org.encog.app.analyst.EncogAnalyst;

public class CmdSeries extends Cmd {

	public final static String COMMAND_NAME = "SERIES";
	
	public CmdSeries(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
