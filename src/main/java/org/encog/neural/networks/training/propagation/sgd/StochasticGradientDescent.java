/*
 * Encog(tm) Core v3.4 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2017 Heaton Research, Inc.
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
package org.encog.neural.networks.training.propagation.sgd;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.mathutil.randomize.generate.GenerateRandom;
import org.encog.mathutil.randomize.generate.MersenneTwisterGenerateRandom;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.error.CrossEntropyErrorFunction;
import org.encog.neural.error.ErrorFunction;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.Momentum;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.sgd.update.AdamUpdate;
import org.encog.neural.networks.training.propagation.sgd.update.UpdateRule;
import org.encog.util.EngineArray;

public class StochasticGradientDescent extends BasicTraining implements Momentum,
		LearningRate {

	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * The momentum.
	 */
	private double momentum;

    /**
     * The gradients.
     */
    private final double[] gradients;

    /**
     * The deltas for each layer.
     */
    private final double[] layerDelta;

    /**
     * L1 regularization.
     */
    private double l1;

    /**
     * L2 regularization.
     */
    private double l2;

    /**
     * The update rule to use.
     */
    private UpdateRule updateRule = new AdamUpdate();

    /**
	 * The last delta values.
	 */
	private double[] lastDelta;

    /**
     * A flat neural network.
     */
    private FlatNetwork flat;

    /**
     * The error function to use.
     */
    private ErrorFunction errorFunction = new CrossEntropyErrorFunction();

    /**
     * The error calculation.
     */
    private ErrorCalculation errorCalculation;

    private GenerateRandom rnd;

    private MLMethod method;

    public StochasticGradientDescent(final ContainsFlat network,
                                     final MLDataSet training) {
        this(network,training,new MersenneTwisterGenerateRandom());
    }

	public StochasticGradientDescent(final ContainsFlat network,
			final MLDataSet training, final GenerateRandom theRandom) {
        super(TrainingImplementationType.Iterative);

        setTraining(training);

        if( !(training instanceof BatchDataSet) ) {
            setBatchSize(25);
        }

        this.method = network;
        this.flat = network.getFlat();
        this.layerDelta = new double[this.flat.getLayerOutput().length];
        this.gradients = new double[this.flat.getWeights().length];
        this.errorCalculation = new ErrorCalculation();
        this.rnd = theRandom;
        this.learningRate = 0.001;
        this.momentum = 0.9;
    }

	public void process(final MLDataPair pair) {
        errorCalculation = new ErrorCalculation();

        double[] actual = new double[this.flat.getOutputCount()];

        flat.compute(pair.getInputArray(), actual);

		errorCalculation.updateError(actual, pair.getIdealArray(), pair.getSignificance());

		// Calculate error for the output layer.
		this.errorFunction.calculateError(
				flat.getActivationFunctions()[0], this.flat.getLayerSums(),this.flat.getLayerOutput(),
				pair.getIdeal().getData(), actual, this.layerDelta, 0,
				pair.getSignificance());

		// Apply regularization, if requested.
		if( this.l1> Encog.DEFAULT_DOUBLE_EQUAL
				|| this.l2>Encog.DEFAULT_DOUBLE_EQUAL  ) {
			double[] lp = new double[2];
			calculateRegularizationPenalty(lp);
			for(int i=0;i<actual.length;i++) {
				double p = (lp[0]*this.l1) + (lp[1]*this.l2);
				this.layerDelta[i]+=p;
			}
		}

		// Propagate backwards (chain rule from calculus).
		for (int i = this.flat.getBeginTraining(); i < this.flat
				.getEndTraining(); i++) {
			processLevel(i);
		}
	}

    public void update() {
        if( getIteration()==0 ) {
            this.updateRule.init(this);
        }

        preIteration();

        this.updateRule.update(this.gradients,this.flat.getWeights());
        setError(this.errorCalculation.calculate());

        postIteration();

        EngineArray.fill(this.gradients,0);
        this.errorCalculation.reset();

        if( getTraining() instanceof  BatchDataSet) {
            ((BatchDataSet)getTraining()).advance();
        }
    }

    public void resetError() {
        this.errorCalculation.reset();
    }

    private void processLevel(final int currentLevel) {
        final int fromLayerIndex = flat.getLayerIndex()[currentLevel + 1];
        final int toLayerIndex = flat.getLayerIndex()[currentLevel];
        final int fromLayerSize = flat.getLayerCounts()[currentLevel + 1];
        final int toLayerSize = flat.getLayerFeedCounts()[currentLevel];
        double dropoutRate = 0;

        final int index = this.flat.getWeightIndex()[currentLevel];
        final ActivationFunction activation = this.flat
                .getActivationFunctions()[currentLevel];

        // handle weights
        // array references are made method local to avoid one indirection
        final double[] layerDelta = this.layerDelta;
        final double[] weights = this.flat.getWeights();
        final double[] gradients = this.gradients;
        final double[] layerOutput = this.flat.getLayerOutput();
        final double[] layerSums = this.flat.getLayerSums();
        int yi = fromLayerIndex;
        for (int y = 0; y < fromLayerSize; y++) {
            final double output = layerOutput[yi];
            double sum = 0;

            int wi = index + y;
            final int loopEnd = toLayerIndex+toLayerSize;

            for (int xi = toLayerIndex; xi < loopEnd; xi++, wi += fromLayerSize) {
                gradients[wi] += output * layerDelta[xi];
                sum += weights[wi] * layerDelta[xi];
            }
            layerDelta[yi] = sum
                    * (activation.derivativeFunction(layerSums[yi], layerOutput[yi]));

            yi++;
        }
    }

    @Override
	public void iteration() {

        for(int i=0;i<getTraining().size();i++) {
            process(getTraining().get(i));
        }

        if( getIteration()==0 ) {
            this.updateRule.init(this);
        }

        preIteration();

        update();
        postIteration();

        if( getTraining() instanceof  BatchDataSet) {
            ((BatchDataSet)getTraining()).advance();
        }
	}

	@Override
	public boolean canContinue() {
		return false;
	}

	@Override
	public double getLearningRate() {
		return this.learningRate;
	}

	@Override
	public double getMomentum() {
		return this.momentum;
	}


	public boolean isValidResume(final TrainingContinuation state) {
		return false;
	}

	/**
	 * Pause the training.
	 * 
	 * @return A training continuation object to continue with.
	 */
	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void resume(final TrainingContinuation state) {
		throw new EncogError("Resume not currently supported.");
	}

	@Override
	public MLMethod getMethod() {
		return this.method;
	}

	@Override
	public void setLearningRate(final double rate) {
		this.learningRate = rate;
	}

	@Override
	public void setMomentum(final double m) {
		this.momentum = m;
	}
	
	public void preIteration() {
		super.preIteration();
	}

	public int getBatchSize() {
		if( getTraining() instanceof BatchDataSet ) {
            return ((BatchDataSet)getTraining()).getBatchSize();
        } else {
            return 0;
        }
	}

	public void setBatchSize(int theBatchSize) {
        if( getTraining() instanceof BatchDataSet ) {
            ((BatchDataSet)getTraining()).setBatchSize(theBatchSize);
        } else {
            BatchDataSet batchSet = new BatchDataSet(getTraining(),this.rnd);
            batchSet.setBatchSize(theBatchSize);
            setTraining(batchSet);
        }
	}

    public double getL1() {
        return l1;
    }

    public void setL1(double l1) {
        this.l1 = l1;
    }

    public double getL2() {
        return l2;
    }

    public void setL2(double l2) {
        this.l2 = l2;
    }

    public void calculateRegularizationPenalty(double[] l) {
        for (int i = 0; i < this.flat.getLayerCounts().length - 1; i++) {
            layerRegularizationPenalty(i, l);
        }
    }

    public void layerRegularizationPenalty(final int fromLayer, final double[] l) {
        final int fromCount = this.flat.getLayerTotalNeuronCount(fromLayer);
        final int toCount = this.flat.getLayerNeuronCount(fromLayer + 1);

        for (int fromNeuron = 0; fromNeuron < fromCount; fromNeuron++) {
            for (int toNeuron = 0; toNeuron < toCount; toNeuron++) {
                double w = this.flat.getWeight(fromLayer, fromNeuron, toNeuron);
                l[0]+=Math.abs(w);
                l[1]+=w*w;
            }
        }
    }

    public FlatNetwork getFlat() {
        return this.flat;
    }

    public UpdateRule getUpdateRule() {
        return updateRule;
    }

    public void setUpdateRule(UpdateRule updateRule) {
        this.updateRule = updateRule;
    }
}
