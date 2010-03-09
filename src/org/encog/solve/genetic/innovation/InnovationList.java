package org.encog.solve.genetic.innovation;

import java.util.List;

public interface InnovationList {
	
	List<Innovation> getInnovations();
	void add(Innovation innovation);
	Innovation get(int id);
}
