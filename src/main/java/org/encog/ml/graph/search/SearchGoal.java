package org.encog.ml.graph.search;

import org.encog.ml.graph.BasicPath;

public interface SearchGoal {
	boolean isGoalMet(BasicPath path);
}
