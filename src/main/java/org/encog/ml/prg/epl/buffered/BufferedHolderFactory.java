package org.encog.ml.prg.epl.buffered;

import java.io.Serializable;

import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.EPLHolderFactory;

public class BufferedHolderFactory implements EPLHolderFactory, Serializable {

	@Override
	public EPLHolder factor(int thePopulationSize, int theMaxFrameSize) {
		return new BufferedHolder(thePopulationSize,theMaxFrameSize);
	}

}
