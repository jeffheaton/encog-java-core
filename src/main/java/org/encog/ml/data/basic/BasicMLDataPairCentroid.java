package org.encog.ml.data.basic;

import org.encog.ml.data.MLData;
import org.encog.util.kmeans.Centroid;

public class BasicMLDataPairCentroid
implements Centroid<BasicMLDataPair>, Cloneable
{	
	private BasicMLData value;
	
	public BasicMLDataPairCentroid(BasicMLDataPair o)
	{
		this.value = (BasicMLData) o.getInput().clone();
	} 
	

	public void remove(BasicMLDataPair d, 
			int s)
	{
		double[] a = d.getInputArray();
		
		for (int i = 0; i < value.size(); i++)
			value.setData(i,  
				((value.getData(i) * s) - a[i]) / (s-1));
	}
	
	public double distance(BasicMLDataPair d)
	{
		MLData diff = value.minus(d.getInput());
		double sum = 0.;
		
		for (int i = 0; i < diff.size(); i++)
			sum += diff.getData(i) * diff.getData(i);
	
		return Math.sqrt(sum);
	}


	@Override
	public void add(BasicMLDataPair d, int s) 	{
		double[] a = d.getInputArray();
		
		for (int i = 0; i < value.size(); i++)
			value.setData(i,  
				((value.getData(i) * s) + a[i]) / (s+1));
	}
}
