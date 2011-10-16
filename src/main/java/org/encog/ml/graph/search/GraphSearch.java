package org.encog.ml.graph.search;

import org.encog.ml.graph.BasicGraph;
import org.encog.ml.graph.BasicPath;

public interface GraphSearch extends Prioritizer {
	
	public BasicGraph getGraph();

	public SearchGoal getGoal();
	
	public void iteration();

	/**
	 * @return the solution
	 */
	public BasicPath getSolution();

}
