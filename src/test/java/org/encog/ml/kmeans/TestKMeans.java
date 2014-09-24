/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
        kmeans.iteration();
        //Assert.assertEquals(37, (int)kmeans.getWCSS());
                              
        int i = 1;
        for(MLCluster cluster: kmeans.getClusters())
        {
        	MLDataSet ds = cluster.createDataSet();
            MLDataPair pair = BasicMLDataPair.createPair(ds.getInputSize(), ds.getIdealSize());
            ds.getRecord(0, pair);
        	double t = pair.getInputArray()[0];
        	
            for(int j=0;j<ds.getRecordCount();j++)
            {
            	ds.getRecord(j, pair);
            	
            	for(j=0;j<pair.getInputArray().length;j++) {
            		if(t>10) {
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
