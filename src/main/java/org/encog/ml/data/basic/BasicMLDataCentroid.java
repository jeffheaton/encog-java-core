package org.encog.ml.data.basic;

import org.encog.ml.data.MLData;
import org.encog.util.kmeans.Centroid;

public class BasicMLDataCentroid
implements Centroid<MLData>, Cloneable
{	
	private BasicMLData value;
	
	public BasicMLDataCentroid(MLData o)
	{
		this.value = (BasicMLData) o.clone();
	} 
	
	
	public void add(MLData d,
			int s)
	{
		double[] a = d.getData();
		
		for (int i = 0; i < value.size(); i++)
			value.setData(i,  
				((value.getData(i) * s) + a[i]) / (s+1));
	}
	
	
	public void remove(MLData d, 
			int s)
	{
		double[] a = d.getData();
		
		for (int i = 0; i < value.size(); i++)
			value.setData(i,  
				((value.getData(i) * s) - a[i]) / (s-1));
	}
	
	public double distance(MLData d)
	{
		MLData diff = value.minus(d);
		double sum = 0.;
		
		for (int i = 0; i < diff.size(); i++)
			sum += diff.getData(i) * diff.getData(i);
	
		return Math.sqrt(sum);
	}
}
