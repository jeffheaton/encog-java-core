package org.encog.neural.networks.training.propagation.back;

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
           
           BasicNetwork network2 = NetworkUtil.createXORNetworkRangeRandomizedUntrained();
           
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
