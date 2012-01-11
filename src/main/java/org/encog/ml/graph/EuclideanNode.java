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
package org.encog.ml.graph;

import org.encog.util.EngineArray;

public class EuclideanNode extends BasicNode {

	private final double[] data;
	
	public EuclideanNode(String label, double[] d) {
		super(label);
		this.data = EngineArray.arrayCopy(d);
	}
	
	public EuclideanNode(String label, double x, double y) {
		super(label);
		this.data = new double[2];
		this.data[0] = x;
		this.data[1] = y;
	}
	
	public double[] getData() {
		return data;
	}
	
	public static double distance(EuclideanNode p1, EuclideanNode p2) {
		return EngineArray.euclideanDistance(p1.getData(),p2.getData());
	}

}
