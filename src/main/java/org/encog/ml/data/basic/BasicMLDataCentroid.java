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
package org.encog.ml.data.basic;

import org.encog.ml.data.MLData;
import org.encog.util.kmeans.Centroid;

/**
 * A basic implementation of a centroid.
 */
public class BasicMLDataCentroid
implements Centroid<MLData>, Cloneable
{	
	/**
	 * The value this centroid is based on.
	 */
	private BasicMLData value;
	
	/**
	 * Construct the centroid.
	 * @param o The object to base the centroid on.
	 */
	public BasicMLDataCentroid(MLData o)
	{
		this.value = (BasicMLData) o.clone();
	} 
	
	/**
	 * {@inheritDoc}
	 */
	public void add(MLData d)
	{
		double[] a = d.getData();
		
		for (int i = 0; i < value.size(); i++)
			value.setData(i,  
				((value.getData(i) * value.size()) + a[i]) / (value.size()+1));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void remove(MLData d)
	{
		double[] a = d.getData();
		
		for (int i = 0; i < value.size(); i++)
			value.setData(i,  
				((value.getData(i) * value.size()) - a[i]) / (value.size()-1));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public double distance(MLData d)
	{
		MLData diff = value.minus(d);
		double sum = 0.;
		
		for (int i = 0; i < diff.size(); i++)
			sum += diff.getData(i) * diff.getData(i);
	
		return Math.sqrt(sum);
	}
}
