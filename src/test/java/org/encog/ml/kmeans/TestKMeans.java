package org.encog.ml.kmeans;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.MLCluster;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;

public class TestKMeans extends TestCase {
	public static final double[][] DATA = {
        {28,15,22},
        {16,15,32},
        {32,20,44},
        {1,2,3},
        {3,2,1} };
	
    public void testCluster (){
                
        BasicMLDataSet set = new BasicMLDataSet();
        
        for(int i=0;i<DATA.length;i++)
        {
        	set.add(new BasicMLData(DATA[i]));
        }

        KMeansClustering kmeans = new KMeansClustering(2,set);
        
        kmeans.iteration(100);
        Assert.assertEquals(37, (int)kmeans.getWCSS());
                              
        int i = 1;
        for(MLCluster cluster: kmeans.getClusters())
        {
        	MLDataSet ds = cluster.createDataSet();
            MLDataPair pair = BasicMLDataPair.createPair(ds.getInputSize(), ds.getIdealSize());
            for(int j=0;j<ds.getRecordCount();j++)
            {
            	ds.getRecord(j, pair);
            	for(j=0;j<pair.getInputArray().length;j++) {
            		if(i==1) {
            			Assert.assertTrue(pair.getInputArray()[j]>10);
            		} else {
            			Assert.assertTrue(pair.getInputArray()[j]<10);
            		}
            	}
            	
            }
            
            i++;
        }       
    }

}
