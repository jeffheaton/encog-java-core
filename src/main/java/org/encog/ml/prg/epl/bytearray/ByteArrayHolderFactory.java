package org.encog.ml.prg.epl.bytearray;

import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.EPLHolderFactory;

public class ByteArrayHolderFactory implements EPLHolderFactory {

	@Override
	public EPLHolder factor(int thePopulationSize, int theMaxFrameSize) {
		return new ByteArrayHolder(thePopulationSize, theMaxFrameSize);
	}

}
