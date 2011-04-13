package org.encog.ml.kmeans;

import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.engine.data.BasicEngineData;
import org.encog.engine.data.EngineData;
import org.encog.engine.data.EngineDataSet;
import org.encog.ml.MLCluster;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public class TestKMeans extends TestCase {
	public static final double[][] DATA = {
        {28,15,22},
        {16,15,32},
        {32,20,44},
        {1,2,3},
        {3,2,1} };
	
    public void testCluster (){
                
        BasicNeuralDataSet set = new BasicNeuralDataSet();
        
        for(int i=0;i<DATA.length;i++)
        {
        	set.add(new BasicNeuralData(DATA[i]));
        }

        KMeansClustering kmeans = new KMeansClustering(2,set);
        
        kmeans.iteration(100);
        Assert.assertEquals(37, (int)kmeans.getWCSS());
                              
        int i = 1;
        for(MLCluster cluster: kmeans.getClusters())
        {
        	EngineDataSet ds = cluster.createDataSet();
            EngineData pair = BasicEngineData.createPair(ds.getInputSize(), ds.getIdealSize());
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
