package org.encog.solve.genetic.innovation;

import java.util.ArrayList;
import java.util.List;

public class BasicInnovationList implements InnovationList {
	
	private final List<Innovation> list = new ArrayList<Innovation>();

	public List<Innovation> getInnovations() {
		return list;
	}
	
	public void add(Innovation innovation) {
		this.list.add(innovation);
	}
	
	public Innovation get(int id) {
		return this.list.get(id);
	}
	
	
}
