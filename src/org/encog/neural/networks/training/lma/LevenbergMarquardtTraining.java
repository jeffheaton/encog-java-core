package org.encog.neural.networks.training.lma;

import org.encog.math.matrices.Matrix;
import org.encog.math.matrices.decomposition.LUDecomposition;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.TrainingError;

public class LevenbergMarquardtTraining extends BasicTraining {
	
	private BasicNetwork network; 
	private Indexable indexableTraining;
	private int inputLength;
	private int parametersLength;
	private double[] weights;
	private Matrix hessianMatrix;
	private double[][] hessian;
	private double alpha;
	private double beta;
	private double lambda;
	private double[] gradient;
	private double[] diagonal;
	private double v = 10.0;
	private double[] deltas;
	private final static double lambdaMax = 1e25;
	private NeuralDataPair pair;
	
	public LevenbergMarquardtTraining(BasicNetwork network, NeuralDataSet training)
	{
		if( !(training instanceof Indexable) ) 
		{
			throw new TrainingError("Levenberg Marquardt requires an indexable training set.");
		}
		
		Layer outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);
		
		if( outputLayer == null ) 
		{
			throw new TrainingError("Levenberg Marquardt requires an output layer.");
		}
		
		if( outputLayer.getNeuronCount()!=1 )
		{
			throw new TrainingError("Levenberg Marquardt requires an output layer with a single neuron.");
		}

		this.setTraining(training);
		this.indexableTraining = (Indexable)getTraining();
		this.network = network;
		this.inputLength = (int)this.indexableTraining.getRecordCount();
		this.parametersLength = this.network.getStructure().calculateSize();
		this.hessianMatrix = new Matrix(this.parametersLength,this.parametersLength);
		this.hessian = this.hessianMatrix.getData();
		this.alpha = 0.0;
		this.beta = 1.0;
		this.lambda = 0.1;
		this.deltas = new double[this.parametersLength];
		this.gradient = new double[this.parametersLength];
		this.diagonal = new double[this.parametersLength];
		
		BasicNeuralData input = new BasicNeuralData(this.indexableTraining.getInputSize());
		BasicNeuralData ideal = new BasicNeuralData(this.indexableTraining.getIdealSize());
		this.pair = new BasicNeuralDataPair(input,ideal);	
	}
	
	
	public BasicNetwork getNetwork() {
		return this.network;
	}

	public void iteration() {
		
		this.weights = NetworkCODEC.networkToArray(network);
		
		ComputeJacobian j = new ComputeJacobian(this.network,this.indexableTraining);
		
		double sumOfSquaredErrors = j.calculate(this.weights);		
		double sumOfSquaredWeights = calculateSumOfSquaredWeights();
		
		//this.setError(j.getError());
		this.calculateHessian(j.getJacobian(), j.getRowErrors());
		
		// Define the objective function
        // bayesian regularization objective function
        double objective = beta * sumOfSquaredErrors + alpha * sumOfSquaredWeights;
        double current = objective + 1.0;



        // Begin of the main Levenberg-Macquardt method
        lambda /= v;

        // We'll try to find a direction with less error
        //  (or where the objective function is smaller)
        while (current >= objective && lambda < lambdaMax)
        {
            lambda *= v;
            
            //System.out.println("Current:" + current);
            //System.out.println("Objective:" + objective);

            // Update diagonal (Levenberg-Marquardt formula)
            for (int i = 0; i < this.parametersLength; i++)
                hessian[i][i] = diagonal[i] + (lambda + alpha);

            // Decompose to solve the linear system
            LUDecomposition decomposition = new LUDecomposition(this.hessianMatrix);

            // Check if the Jacobian has become non-invertible
            if (!decomposition.isNonsingular()) continue;

            // Solve using LU (or SVD) decomposition
            deltas = decomposition.Solve(gradient);

            // Update weights using the calculated deltas
            sumOfSquaredWeights = updateWeights();

            // Calculate the new error
            sumOfSquaredErrors = 0.0;
            for (int i = 0; i < this.inputLength; i++)
            {
            	this.indexableTraining.getRecord(i, this.pair);
                NeuralData actual = network.compute(pair.getInput());
                double e = this.pair.getIdeal().getData(0) - actual.getData(0); 
                sumOfSquaredErrors += e*e;
            }
            sumOfSquaredErrors /=2.0;

            // Update the objective function
            current = beta * sumOfSquaredErrors + alpha * sumOfSquaredWeights;

            // If the object function is bigger than before, the method
            //  is tried again using a greater dumping factor.
        }

        // If this iteration caused a error drop, then next iteration
        //  will use a smaller damping factor.
        lambda /= v;	
        
        this.setError(sumOfSquaredErrors);
	}
	
	public double updateWeights() {
		double result = 0;
		double[] w = this.weights.clone();

		for (int i = 0; i < w.length; i++) {
			w[i] += this.deltas[i];
			result+=w[i]*w[i];
		}

		NetworkCODEC.arrayToNetwork(w, this.network);

		return result/2.0;
	}
	
	public void calculateHessian(double[][] jacobian, double[] errors)
	{
		for (int i = 0; i < this.parametersLength; i++)
        {
            // Compute Jacobian Matrix Errors
            double s = 0.0;
            for (int j = 0; j < this.inputLength; j++)
            {
                s += jacobian[j][i] * errors[j];
            }
            gradient[i] = s;

            // Compute Quasi-Hessian Matrix using Jacobian (H = J'J)
            for (int j = 0; j < this.parametersLength; j++)
            {
                double c = 0.0;
                for (int k = 0; k < this.inputLength; k++)
                {
                    c += jacobian[k][i] * jacobian[k][j];
                }
                hessian[i][j] = beta * c;
            }
        }
		
		for (int i = 0; i < this.parametersLength; i++)
            diagonal[i] = hessian[i][i];
	}


	private double calculateSumOfSquaredWeights() {
		double result = 0;
		
		for(int i=0;i<this.weights.length;i++) {
			result+=this.weights[i]*this.weights[i];
		}
		
		return result/2.0;
	}

}
