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
