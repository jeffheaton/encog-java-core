package org.encog.ml.graph.search;

import org.encog.ml.graph.BasicPath;

public interface Prioritizer {
	boolean isHigherPriority(BasicPath first, BasicPath second);
}
