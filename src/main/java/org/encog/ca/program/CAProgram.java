package org.encog.ca.program;

import org.encog.ca.universe.Universe;

public interface CAProgram {

	void iteration();

	void randomize();
	
	/**
	 * @return the sourceUniverse
	 */
	Universe getSourceUniverse();
	void setSourceUniverse(Universe sourceUniverse);
	Universe getTargetUniverse();
	void setTargetUniverse(Universe targetUniverse);

}
