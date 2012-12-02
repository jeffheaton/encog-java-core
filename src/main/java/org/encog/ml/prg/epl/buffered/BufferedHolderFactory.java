package org.encog.ml.prg.epl.buffered;

import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.EPLHolderFactory;

public class BufferedHolderFactory implements EPLHolderFactory {

	@Override
	public EPLHolder factor(int thePopulationSize, int theMaxFrameSize) {
		return new BufferedHolder(thePopulationSize,theMaxFrameSize);
	}

}
