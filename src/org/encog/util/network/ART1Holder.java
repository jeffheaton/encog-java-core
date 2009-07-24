package org.encog.util.network;

import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.WeightedSynapse;

public class ART1Holder {
	
	private BasicNetwork network;
	private Layer layerF1;
	private Layer layerF2;
	private Synapse synapseF1toF2;
	private Synapse synapseF2toF1;
	
	/**
	 * last winner in F2 layer.
	 */
	int winner;

	/**
	 * A parameter for F1 layer.
	 */
	double a1;
	
	/**
	 * B parameter for F1 layer.
	 */
    double b1;
    
    /**
     * C parameter for F1 layer.
     */
    double c1;
    
    /**
     * D parameter for F1 layer.
     */
    double d1;
    
    /**
     * L parameter for net.
     */
    double l;
    
    /**
     * The vigilance parameter.
     */
    double vigilance;
    
    boolean[] inhibitF1; 
    boolean[] inhibitF2;
    
    private int noWinner;
    
    private BiPolarNeuralData outputF1;
    private BiPolarNeuralData outputF2;
    


	public ART1Holder(final int input,final int output)
	{
		this.network = new BasicNetwork();
		this.layerF1 = new BasicLayer(new ActivationLinear(),false,input);
		this.layerF2 = new BasicLayer(new ActivationLinear(),false,output);
		this.inhibitF1 = new boolean[input];
		this.inhibitF2 = new boolean[output];
		this.synapseF1toF2 = new WeightedSynapse(layerF1,layerF2);
		this.synapseF2toF1 = new WeightedSynapse(layerF2,layerF1);
		this.layerF1.getNext().add(this.synapseF1toF2);
		this.layerF2.getNext().add(this.synapseF2toF1);
		this.network.setInputLayer(this.layerF1);
		this.network.setOutputLayer(this.layerF2);
		
		this.outputF1 = new BiPolarNeuralData(input);
		this.outputF2 = new BiPolarNeuralData(output);
		
		  this.a1  = 1;
		  this.b1  = 1.5;
		  this.c1  = 5;
		  this.d1  = 0.9;
		  this.l   = 3;
		  this.vigilance = 0.9;
		  
		  this.noWinner = output;

	}
	
	public double magnitude(NeuralData input)
	{
	  double result;

	  result = 0;
	  for (int i=0; i<this.layerF1.getNeuronCount(); i++) {
	    result += input.getData(i);
	  }
	  return result;
	}


	public void adjustWeights()
	{
	  double MagnitudeInput_;

	  for (int i=0; i<this.layerF1.getNeuronCount(); i++) {
	    if ( this.outputF1.getBoolean(i) ) {
	      MagnitudeInput_ = magnitude(this.outputF1);
	      this.synapseF1toF2.getMatrix().set(i,winner,1);
	      this.synapseF2toF1.getMatrix().set(winner,i, l / (l - 1 + MagnitudeInput_));
	    }
	    else {
	      this.synapseF1toF2.getMatrix().set(i,winner,0);
	      this.synapseF2toF1.getMatrix().set(winner,i,0);
	    }
	  }
	}
	
	private void computeF1(BiPolarNeuralData input)
	{
	  double Sum, Activation;
	   
	  for (int i=0; i<this.layerF1.getNeuronCount(); i++) {
	    Sum = this.synapseF1toF2.getMatrix().get(i,winner) * this.outputF2.getData(winner);
	    Activation = (input.getData(i) + this.d1 * Sum - this.b1) /
	                 (1 + this.a1 * (input.getData(i) + this.d1 * Sum) + this.c1);
	    this.outputF1.setData(i, Activation > 0);
	  }
	}

	private void computeF2()
	{
	  int  i,j;
	  double Sum, MaxOut;
	   
	  MaxOut = Double.NEGATIVE_INFINITY;
	  this.winner = this.noWinner;
	  for (i=0; i<this.layerF2.getNeuronCount(); i++) {
	    if (! this.inhibitF2[i] ) {
	      Sum = 0;
	      for (j=0; j<this.layerF1.getNeuronCount(); j++) {
	        Sum += this.synapseF2toF1.getMatrix().get(i,j) * this.outputF1.getData(j);
	      }
	      if (Sum > MaxOut) {
	        MaxOut = Sum;
	        this.winner = i;
	      }
	    }
	    this.outputF2.setData(i, false);
	  }
	  if (this.winner != this.noWinner)
	    this.outputF2.setData(winner, true);
	}

	public double getA1() {
		return a1;
	}

	public void setA1(double a1) {
		this.a1 = a1;
	}

	public double getB1() {
		return b1;
	}

	public void setB1(double b1) {
		this.b1 = b1;
	}

	public double getC1() {
		return c1;
	}

	public void setC1(double c1) {
		this.c1 = c1;
	}

	public double getD1() {
		return d1;
	}

	public void setD1(double d1) {
		this.d1 = d1;
	}

	public double getL() {
		return l;
	}

	public void setL(double l) {
		this.l = l;
	}

	public double getVigilance() {
		return vigilance;
	}

	public void setVigilance(double vigilance) {
		this.vigilance = vigilance;
	}

	private void getOutput(BiPolarNeuralData output)
	{	   
	  for (int i=0; i<this.layerF2.getNeuronCount(); i++) {
	    output.setData(i, this.outputF2.getBoolean(i));
	  }
	}


	public void compute(BiPolarNeuralData input, BiPolarNeuralData output)
	{
	  int  i;
	  boolean Resonance, Exhausted;
	  double MagnitudeInput, MagnitudeInput_;
      
	  for (i=0; i<this.layerF2.getNeuronCount(); i++) {
	    this.inhibitF2[i] = false;
	  }
	  Resonance = false;
	  Exhausted = false;
	  do {
	    setInput(input);
	    computeF2();
	    getOutput(output);
	    if ( winner != this.noWinner) {
	      computeF1(input);
	      MagnitudeInput = magnitude(input);
	      MagnitudeInput_ = magnitude(this.outputF1);
	      if ((MagnitudeInput_ / MagnitudeInput) < this.vigilance)
	        this.inhibitF2[this.winner] = true;
	      else
	        Resonance = true;
	    }
	    else Exhausted = true;
	  } while (! (Resonance || Exhausted));
	  if (Resonance)
	    adjustWeights();      
	}
	
	public int getWinner() {
		return winner;
	}

	public int getNoWinner() {
		return noWinner;
	}

	private void setInput(NeuralData input)
	{
	  double Activation;
	   
	  for (int i=0; i<this.layerF1.getNeuronCount(); i++) {
	    Activation = input.getData(i) / (1 + this.a1 * (input.getData(i) + this.b1) + this.c1);
	    this.outputF1.setData(i, (Activation > 0));
	  }
	}


	
}
