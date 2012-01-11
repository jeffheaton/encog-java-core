package org.encog.ml.hmm.train.kmeans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.util.kmeans.KMeansUtil;

public class Clusters
{	
	private Hashtable<MLDataPair,Integer> clustersHash;
	private ArrayList<Collection<MLDataPair>> clusters;
	
	public Clusters(int k, MLDataSet observations)
	{
		
		clustersHash = new Hashtable<MLDataPair,Integer>();
		clusters = new ArrayList<Collection<MLDataPair>>();
		
		List<MLDataPair> list = new ArrayList<MLDataPair>();
		for(MLDataPair pair: observations) {
			list.add(pair);
		}
		KMeansUtil<MLDataPair> kmc = new KMeansUtil<MLDataPair>(k, list);
		kmc.process();
		
		for (int i = 0; i < k; i++) {
			Collection<MLDataPair> cluster = kmc.get(i);
			clusters.add(cluster);
			
			for (MLDataPair element : cluster) 
				clustersHash.put(element, i);
		}
	}
	
	
	public boolean isInCluster(MLDataPair o, int x)
	{
		return cluster(o) == x;
	}
	
	
	public int cluster(MLDataPair o)
	{
		return clustersHash.get(o);
	}
	
	
	public Collection<MLDataPair> cluster(int clusterNb)
	{
		return clusters.get(clusterNb);
	}
	
	
	public void remove(MLDataPair o, int clusterNb)
	{
		clustersHash.put(o,-1);
		clusters.get(clusterNb).remove(o); 
	}
	
	
	public void put(MLDataPair o, int clusterNb)
	{
		clustersHash.put(o,clusterNb);
		clusters.get(clusterNb).add(o);
	}
}
