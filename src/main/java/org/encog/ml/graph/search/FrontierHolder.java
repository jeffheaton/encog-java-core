package org.encog.ml.graph.search;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.graph.BasicNode;
import org.encog.ml.graph.BasicPath;

public class FrontierHolder {
	
	private final List<BasicPath> contents = new ArrayList<BasicPath>();
	private final Prioritizer prioritizer;
	
	public FrontierHolder(Prioritizer thePrioritizer) {
		this.prioritizer = thePrioritizer;
	}

	public List<BasicPath> getContents() {
		return contents;
	}
	
	public void add(BasicPath path) {
		for(int i=0;i<this.contents.size();i++) {
			if( this.prioritizer.isHigherPriority(path, this.contents.get(i)) ) {
				this.contents.add(i, path);
				return;
			}
		}
		// must be lowest priority, or the list is empty
		this.contents.add(path);
	}
	
	public BasicPath pop() {
		if( contents.size()==0 )
			return null;
		
		BasicPath result = contents.get(0);
		contents.remove(0);
		return result;
	}

	public int size() {
		return this.contents.size();
	}
	
	public boolean containsDestination(BasicNode node) {
		for(BasicPath path: this.contents) {
			if( path.getDestinationNode().equals(node)) {
				return true;
			}
		}
		return false;
	}
	
	
}
