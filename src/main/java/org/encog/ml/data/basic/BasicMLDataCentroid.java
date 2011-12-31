package org.encog.ml.data.basic;

import org.encog.ml.data.MLData;
import org.encog.util.kmeans.Centroid;

public class BasicMLDataCentroid
implements Centroid<BasicMLData>, Cloneable
{	
	private BasicMLData value;
	
	public BasicMLDataCentroid(BasicMLData o)
	{
		this.value = (BasicMLData) o.clone();
	} 
	
	
	public void add(BasicMLData d,
			int s)
	{
		double[] a = d.getData();
		
		for (int i = 0; i < value.size(); i++)
			value.setData(i,  
				((value.getData(i) * s) + a[i]) / (s+1));
	}
	
	
	public void remove(BasicMLData d, 
			int s)
	{
		double[] a = d.getData();
		
		for (int i = 0; i < value.size(); i++)
			value.setData(i,  
				((value.getData(i) * s) - a[i]) / (s-1));
	}
	
	public double distance(BasicMLData d)
	{
		MLData diff = value.minus(d);
		double sum = 0.;
		
		for (int i = 0; i < diff.size(); i++)
			sum += diff.getData(i) * diff.getData(i);
	
		return Math.sqrt(sum);
	}
}
