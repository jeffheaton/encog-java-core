package org.encog.ml.prg.epl.bytearray;

import java.io.Serializable;

import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.EPLHolderFactory;

public class ByteArrayHolderFactory implements EPLHolderFactory, Serializable {

	@Override
	public EPLHolder factor(int theMaxFrameSize) {
		return new ByteArrayHolder(theMaxFrameSize);
	}

}
