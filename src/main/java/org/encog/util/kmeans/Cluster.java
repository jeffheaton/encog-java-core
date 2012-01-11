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
package org.encog.util.kmeans;

import java.util.ArrayList;
import java.util.List;

public class Cluster<T extends CentroidFactory<? super T>>
{
	final private List<T> contents = new ArrayList<T>();
	private Centroid<? super T> centroid;
	
	public Cluster()
	{
	}
	
	public Cluster(T d)
	{
		contents.add(d);
		centroid = d.createCentroid();
	}
	
	public List<T> getContents()
	{
		return this.contents;
	}
	
	
	public void add(T e)
	{
		if (centroid == null)
			centroid = e.createCentroid();
		else
			centroid.add(e, this.contents.size());
		
		this.contents.add(e);
	}
	
	
	public void remove(int i)
	{
		centroid.remove(this.contents.get(i), this.contents.size());
		this.contents.remove(i);
	}
	
	
	public Centroid<? super T> centroid()
	{
		return centroid;
	}
}
