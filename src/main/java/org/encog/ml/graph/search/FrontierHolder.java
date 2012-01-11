/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
