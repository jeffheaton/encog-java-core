/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.neural.networks.training;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

/**
 * Test class to evaluate NguyenWidrowRandomizer against RangeRandomizer
 * 
 * 
 * @author Stephan Corriveau
 *
 */
public class EvaluateNuguyenWidrow {

   public static void main( String[] args ) {
   
       NeuralDataSet trainingData1 = new BasicNeuralDataSet( XOR.XOR_INPUT, XOR.XOR_IDEAL );
       NeuralDataSet trainingData2 = new BasicNeuralDataSet( XOR.XOR_INPUT, XOR.XOR_IDEAL );
       NeuralDataSet trainingData3 = new BasicNeuralDataSet( XOR.XOR_INPUT, XOR.XOR_IDEAL );
       
       for ( int i = 0; i < 1; i++ ) {
           

           
           BasicNetwork network3 = NetworkUtil.createXORNetworknNguyenWidrowUntrained();
           
           Train bpropNguyen = new Backpropagation( network3, trainingData3, 0.9, 0.8 );     
           train(i, bpropNguyen, "NguyenWidrowRandomizer" );
           
           BasicNetwork network2 = NetworkUtil.createXORNetworkUntrained();
           
           Train bpropRange = new Backpropagation( network2, trainingData2, 0.9, 0.8 );     
           train(i, bpropRange,  "RangeRandomizer       ");
 
       }
   } 
 
   private final static void train( long it, Train train, String randomizerUsed ){
      
           train.iteration();
           double error1 = train.getError();
           int epoch = 1;
           
           do {
               train.iteration();
               epoch++;
               
           } while ((epoch < 5000) && (train.getError() > 0.009 ));
           double error2 = train.getError();
           double improve = (error1-error2)/error1;
           
           System.out.println( randomizerUsed + "\t" + it  + "\t" + train.getError()  + "\t" + epoch  + "\t" + improve);
          
           
          
      
   }
   

}
