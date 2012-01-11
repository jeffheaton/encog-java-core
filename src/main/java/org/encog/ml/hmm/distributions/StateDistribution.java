package org.encog.ml.hmm.distributions;

import java.io.Serializable;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

public interface StateDistribution 
extends Cloneable, Serializable
{
    public double probability(MLDataPair o);

    public MLDataPair generate();
    
    public void fit(MLDataSet oa);
    void fit(MLDataSet o, double[] weights);
        
    public StateDistribution clone();
}
