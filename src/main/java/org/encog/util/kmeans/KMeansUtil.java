/*
 * Encog(tm) Core v3.4 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2017 Heaton Research, Inc.
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
import java.util.Collection;
import java.util.List;

/**
 * Generic KMeans clustering object.
 *
 * @param <K> The type to cluster.
 */
public class KMeansUtil<K extends CentroidFactory<? super K>> {
	
	/**
	 * The clusters.
	 */
	private final ArrayList<Cluster<K>> clusters;
	
	/**
	 * The number of clusters.
	 */
	private final int k;

	/**
	 * Construct the clusters.  Call process to perform the cluster.
	 * @param theK The number of clusters.
	 * @param theElements The elements to cluster.
	 */
	public KMeansUtil(int theK, List<? extends K> theElements) {
		this.k = theK;
		clusters = new ArrayList<Cluster<K>>(theK);
		initRandomClusters(theElements);
	}

	/**
	 * Create random clusters.
	 * @param elements The elements to cluster.
	 */
	  private void initRandomClusters( List<? extends K> elements )
  	  {
	    for (int i=0; i<k; i++) clusters.add(new Cluster<K>());

	    // straight random assignment sometimes leaves a cluster empty
	    // which may cause problems later, hence the more complicated approach
	  int amountLeft = elements.size(), place = -1;

	    for ( K e : elements )
    	    {
	      	if (amountLeft-- == k)  // we have just enough elements left for one per cluster
        	place = 0;  // place elements in all empty clusters

	      if (place >= 0)
	      {
	        for (; place<clusters.size(); place++)
	        {
	        Cluster<K> c = clusters.get( place );

	          if (c.getContents().isEmpty())
	          {
	            c.add( e );
	            break;
	          }
	        }
	        if (place == clusters.size()) // e was not placed, place it randomly
	          place = -1;
	        else
	          continue;  // only continue if e was placed
	      }

	      clusters.get( (int) Math.floor( Math.random()*k ) ).add( e );
	    }
	  }

	/**
	 * Perform the cluster.
	 */
	public void process() {
	  ArrayList<Cluster<K>> newclusters = new ArrayList<Cluster<K>>();

	  for (int i=0; i<k; i++) newclusters.add( new Cluster<K>());

	  for (int i = 0; i < k; i++)
	  {
	  Cluster<K> thisCluster = clusters.get( i );
	  List<K> thisElements = thisCluster.getContents();

	     for (int j = 0; j < thisElements.size(); j++)
	     {
	     K thisElement = thisElements.get( j );
	     int nearestCluster = nearestClusterIndex( thisElement );

	       newclusters.get( nearestCluster ).add( thisElement );
	     }
	  }

	  clusters.clear();
	  for ( Cluster<K> c : newclusters )
	     clusters.add( c );
	}

	/**
	 * Find the nearest cluster to the element.
	 * @param element The element.
	 * @return The nearest cluster.
	 */
	private Cluster<K> nearestCluster(K element) {
		    return clusters.get( nearestClusterIndex( element ));
  	}
  
	 private int nearestClusterIndex( K element )
	 {
	 double distance = Double.MAX_VALUE;
	 int result = -1;

	  for (int i = 0; i < clusters.size(); i++)
	  {
	  Centroid<? super K> c = clusters.get( i ).centroid();
    
	    if (null == c) continue;
    
	  double thisDistance = c.distance( element );

	   if (distance > thisDistance)
	   {
	      distance = thisDistance;
	      result = i;
	    }
	  }

	  return result;
	}

	/**
	 * Get a cluster by index.
	 * @param index The index to get.
	 * @return The cluster.
	 */
	public Collection<K> get(int index) {
		return clusters.get(index).getContents();
	}

	/**
	 * @return The number of clusters.
	 */
	public int size() {
		return clusters.size();
	}

	/**
	 * Get a cluster by index.
	 * @param i The index to get.
	 * @return The cluster.
	 */
	public Cluster<K> getCluster(int i) {
		return this.clusters.get(i);
	}
}
