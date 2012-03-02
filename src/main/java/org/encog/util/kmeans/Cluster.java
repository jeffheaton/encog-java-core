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

/**
 * A cluster.
 * @param <T> The type of data to cluster.
 */
public class Cluster<T extends CentroidFactory<? super T>>
{
	/**
	 * The contents of the cluster.
	 */
	final private List<T> contents = new ArrayList<T>();
	
	/**
	 * The centroid of this cluster.
	 */
	private Centroid<? super T> centroid;
	
	/**
	 * Create an empty cluster.
	 */
	public Cluster()
	{
	}
	
	/**
	 * Create a cluster with one initial data point.
	 * @param d The initial data point.
	 */
	public Cluster(T d)
	{
		contents.add(d);
		centroid = d.createCentroid();
	}
	
	/**
	 * @return The contents of this cluster.
	 */
	public List<T> getContents()
	{
		return this.contents;
	}
	
	/**
	 * Add a element to the cluster.
	 * @param e The element to add.
	 */
	public void add(T e)
	{
		if (centroid == null)
			centroid = e.createCentroid();
		else
			centroid.add(e);
		
		this.contents.add(e);
	}
	
	/**
	 * Remove the specified index from the cluster.
	 * @param i The index to remove.
	 */
	public void remove(int i)
	{
		centroid.remove(this.contents.get(i) );
		this.contents.remove(i);
	}
	
	/**
	 * @return The centroid of this cluster.
	 */
	public Centroid<? super T> centroid()
	{
		return centroid;
	}
}
