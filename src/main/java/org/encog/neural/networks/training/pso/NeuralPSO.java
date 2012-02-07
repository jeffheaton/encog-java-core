/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.neural.networks.training.pso;

import org.encog.mathutil.VectorAlgebra;
import org.encog.mathutil.randomize.NguyenWidrowRandomizer;
import org.encog.mathutil.randomize.Randomizer;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.concurrency.EngineConcurrency;
import org.encog.util.concurrency.TaskGroup;

/**
 * Iteratively trains a population of neural networks by applying   
 * particle swarm optimisation (PSO).
 * 
 * Contributed by:
 * Geoffroy Noel
 * https://github.com/goffer-looney 
 * 
 * References: 
 *  James Kennedy and Russell C. Eberhart, Particle swarm optimization, 
 * Proceedings of the IEEE International Conference on Neural Networks, 
 * 1995, pp. 1942-1948
 * 
 * @author Geoffroy Noel
 */
public class NeuralPSO extends BasicTraining {

    protected boolean m_multiThreaded = true;
    protected VectorAlgebra m_va;
    protected CalculateScore m_calculateScore;
    protected Randomizer m_randomizer;

    // Swarm state and memories.
    protected BasicNetwork[] m_networks;
    protected double[][] m_velocities;
    protected double[][] m_bestVectors;
    protected double[] m_bestErrors;
    protected int m_bestVectorIndex;

    // Although this is redundant with m_bestVectors[m_bestVectorIndex],
    // m_bestVectors[m_bestVectorIndex] is not thread safe.
    private double[] m_bestVector;
    BasicNetwork m_bestNetwork = null;

    // Typical range is 20 - 40 for many problems. 
    // More difficult problems may need much higher value. 
    // Must be low enough to keep the training process 
    // computationally efficient.
    protected int m_populationSize = 30;

    // Determines the size of the search space. 
    // The position components of particle will be bounded to 
    // [-maxPos, maxPos]
    // A well chosen range can improve the performance. 
    // -1 is a special value that represents boundless search space. 
    protected double m_maxPosition = -1;

    // Maximum change one particle can take during one iteration.
    // Imposes a limit on the maximum absolute value of the velocity 
    // components of a particle. 
    // Affects the granularity of the search.
    // If too high, particle can fly past optimum solution.
    // If too low, particle can get stuck in local minima.
    // Usually set to a fraction of the dynamic range of the search
    // space (10% was shown to be good for high dimensional problems).
    // -1 is a special value that represents boundless velocities. 
    protected double m_maxVelocity = 2;

    // c1, cognitive learning rate >= 0
    // tendency to return to personal best position
    protected double m_c1 = 2.0;
    // c2, social learning rate >= 0
    // tendency to move towards the swarm best position
    protected double m_c2 = 2.0;

    // w, inertia weight.
    // Controls global (higher value) vs local exploration 
    // of the search space. 
    // Analogous to temperature in simulated annealing.
    // Must be chosen carefully or gradually decreased over time.
    // Value usually between 0 and 1.
    protected double m_inertiaWeight = 0.4;

    // If true, the position of the previous global best position 
    // can be updated *before* the other particles have been modified.
    private boolean m_pseudoAsynchronousUpdate = false;

    /**
     * Constructor.
     * 
     * @param network           an initialised Encog network. 
     *                          The networks in the swarm will be created with 
     *                          the same topology as this network.
     * @param randomizer        any type of Encog network weight initialisation
     *                          object.
     * @param calculateScore    any type of Encog network scoring/fitness object. 
     * @param populationSize    the swarm size.
     */
    public NeuralPSO(final BasicNetwork network,
            final Randomizer randomizer, final CalculateScore calculateScore,
            final int populationSize) {
        super(TrainingImplementationType.Iterative);
        // initialisation of the member variables
        m_populationSize = populationSize;
        m_randomizer = randomizer;
        m_calculateScore = calculateScore;
        m_bestNetwork = network;

        m_networks = new BasicNetwork[m_populationSize];
        m_velocities = null; 
        m_bestVectors = new double[m_populationSize][];
        m_bestErrors = new double[m_populationSize];
        m_bestVectorIndex = -1;

        // get a vector from the network.
        m_bestVector = NetworkCODEC.networkToArray(m_bestNetwork);

        m_va = new VectorAlgebra();
    }
    
    /**
     * Construct a PSO using a training set score function, 20 particles and the
     * NguyenWidrowRandomizer randomizer.
     * @param network The network to train. an initialised Encog network. 
     * The networks in the swarm will be created with 
     * the same topology as this network.
     * @param trainingSet The training set.
     */
    public NeuralPSO(BasicNetwork network, MLDataSet trainingSet)        
    {   
    	this(network, new NguyenWidrowRandomizer(), new TrainingSetScore(trainingSet), 20);
    }

    /**
     * Initialise the particle positions and velocities, 
     * personal and global bests.
     * Only does this if they have not yet been initialised.
     */
    void initPopulation() {
        if (m_velocities == null) {
            int dimensionality = m_bestVector.length;
            m_velocities = new double[m_populationSize][dimensionality];
            // run an initialisation iteration
            iterationPSO(true);
        }
    }

    /**
     * Runs one PSO iteration over the whole population of networks.
     */
    @Override
    public void iteration() {
        initPopulation();

        preIteration();
        iterationPSO(false);
        postIteration();
    }

    /**
     * Internal method for the iteration of the swarm.
     * 
     * @param init  true if this is an initialisation iteration.
     */
    protected void iterationPSO(boolean init) {
        final TaskGroup group = EngineConcurrency.getInstance().createTaskGroup();

        for (int i = 0; i < m_populationSize; i++) {
            NeuralPSOWorker worker = new NeuralPSOWorker(this, i, init);
            if (!init && this.isMultiThreaded()) {
                EngineConcurrency.getInstance().processTask(worker, group);
            } else {
                worker.run();
            }
        }
        if (this.isMultiThreaded()) {
            group.waitForComplete();
        }
        updateGlobalBestPosition();
    }

    /**
     * Update the velocity, position and personal 
     * best position of a particle 
     * 
     * @param particleIndex     index of the particle in the swarm
     * @param init              if true, the position and velocity
     *                          will be initialised. 
     */
    protected void updateParticle(int particleIndex, boolean init) {
        int i = particleIndex;
        double[] particlePosition = null;
        if (init) {
            // Create a new particle with random values.
            // Except the first particle which has the same values 
            // as the network passed to the algorithm.
            if (m_networks[i] == null) {
                m_networks[i] = (BasicNetwork) m_bestNetwork.clone();
                if (i > 0) m_randomizer.randomize(m_networks[i]);
            }
            particlePosition = getNetworkState(i);
            m_bestVectors[i] = particlePosition;

            // randomise the velocity
            m_va.randomise(m_velocities[i], m_maxVelocity);
        } else {
            particlePosition = getNetworkState(i);
            updateVelocity(i, particlePosition);

            // velocity clamping
            m_va.clampComponents(m_velocities[i], m_maxVelocity);

            // new position (Xt = Xt-1 + Vt)
            m_va.add(particlePosition, m_velocities[i]);

            // pin the particle against the boundary of the search space.
            // (only for the components exceeding maxPosition)
            m_va.clampComponents(particlePosition, m_maxPosition);

            setNetworkState(i, particlePosition);
        }
        updatePersonalBestPosition(i, particlePosition);
    }

    /**
     * Update the velocity of a particle 
     * 
     * @param particleIndex     index of the particle in the swarm
     * @param particlePosition  the particle current position vector
     */
    protected void updateVelocity(int particleIndex, double[] particlePosition) {
        int i = particleIndex;
        double[] vtmp = new double[particlePosition.length];

        // Standard PSO formula

        // inertia weight
        m_va.mul(m_velocities[i], m_inertiaWeight);

        // cognitive term
        m_va.copy(vtmp, m_bestVectors[i]);
        m_va.sub(vtmp, particlePosition);
        m_va.mulRand(vtmp, m_c1);
        m_va.add(m_velocities[i], vtmp);

        // social term
        if (i != m_bestVectorIndex) {
            m_va.copy(vtmp, m_pseudoAsynchronousUpdate ? m_bestVectors[m_bestVectorIndex] : m_bestVector);
            m_va.sub(vtmp, particlePosition);
            m_va.mulRand(vtmp, m_c2);
            m_va.add(m_velocities[i], vtmp);
        }
    }

    /**
     * Update the personal best position of a particle.
     * 
     * @param particleIndex     index of the particle in the swarm
     * @param particlePosition  the particle current position vector
     */
    protected void updatePersonalBestPosition(int particleIndex, double[] particlePosition) {
        // set the network weights and biases from the vector
        double score = m_calculateScore.calculateScore(m_networks[particleIndex]);

        // update the best vectors (g and i)
        if ((m_bestErrors[particleIndex] == 0) || isScoreBetter(score, m_bestErrors[particleIndex])) {
            m_bestErrors[particleIndex] = score;
            m_va.copy(m_bestVectors[particleIndex], particlePosition);
        }
    }

    /**
     * Update the swarm's best position
     */
    protected void updateGlobalBestPosition() {
        boolean bestUpdated = false;
        for (int i = 0; i < m_populationSize; i++) {
            if ((m_bestVectorIndex == -1) || isScoreBetter(m_bestErrors[i], m_bestErrors[m_bestVectorIndex])) {
                m_bestVectorIndex = i;
                bestUpdated = true;
            }
        }
        if (bestUpdated) {
            m_va.copy(m_bestVector, m_bestVectors[m_bestVectorIndex]);
            m_bestNetwork.decodeFromArray(m_bestVector);
            setError(m_bestErrors[m_bestVectorIndex]);
        }
    }

    /**
     * Compares two scores.
     * 
     * @param score1    a score
     * @param score2    a score
     * @return  true if score1 is better than score2
     */
    boolean isScoreBetter(double score1, double score2) {
        return ((m_calculateScore.shouldMinimize() && (score1 < score2)) || ((!m_calculateScore.shouldMinimize()) && (score1 > score2)));
    }

    @Override
    public TrainingContinuation pause() {
        return null;
    }

    @Override
    public boolean canContinue() {
        return false;
    }

    @Override
    public void resume(TrainingContinuation state) {
    }

    /**
     * Returns the state of a network in the swarm 
     * 
     * @param particleIndex     index of the network in the swarm
     * @return  an array of weights and biases for the given network
     */
    protected double[] getNetworkState(int particleIndex) { 
        return NetworkCODEC.networkToArray(m_networks[particleIndex]);
    }

    /**
     * Sets the state of the networks in the swarm
     * 
     * @param particleIndex     index of the network in the swarm
     * @param state             an array of weights and biases
     */
    protected void setNetworkState(int particleIndex, double[] state) { 
        NetworkCODEC.arrayToNetwork(state, m_networks[particleIndex]);
    }

    /**
     * Set the swarm size.
     * 
     * @param populationSize    the swarm size
     */
    public void setPopulationSize(int populationSize) {
        m_populationSize = populationSize;
    }

    /**
     * Returns the swarm size.
     * 
     * @return the swarm size.
     */
    public int getPopulationSize() {
        return m_populationSize;
    }

    /**
     * Sets the maximum velocity.
     * 
     * @param maxVelocity   Maximum velocity / Vmax
     */
    public void setMaxVelocity(double maxVelocity) {
        m_maxVelocity = maxVelocity;
    }

    /**
     * Get the maximum velocity (Vmax)
     * 
     * @return  maximum velocity (Vmax)
     */
    public double getMaxVelocity() {
        return m_maxVelocity;
    }

    /**
     * Set the boundary of the search space (Xmax)
     * 
     * @param maxPosition   maximum value for a component (Xmax)
     */
    public void setMaxPosition(double maxPosition) {
        m_maxPosition = maxPosition;
    }

    /**
     * Get the boundary of the search space (Xmax)
     * 
     * @return  the maximum value a component can take (Xmax)
     */
    public double getMaxPosition() {
        return m_maxPosition;
    }

    /**
     * Sets the cognition coefficient (c1).
     * 
     * @param c1    cognition coefficient (c1)
     */
    public void setC1(double c1) {
        m_c1 = c1;
    }

    /**
     * Get the cognition coefficient (c1).
     * 
     * @return  the cognition coefficient (c1)
     */
    public double getC1() {
        return m_c1;
    }

    /**
     * Set the social coefficient (c2).
     * 
     * @param c2    the social coefficient (c2)
     */
    public void setC2(double c2) {
        m_c2 = c2;
    }

    /**
     * Get the social coefficient (c2).
     * 
     * @return  the social coefficient (c2)
     */
    public double getC2() {
        return m_c2;
    }

    /**
     * Set the inertia weight (w)
     * 
     * @param inertiaWeight the inertia weight (w)
     */
    public void setInertiaWeight(double inertiaWeight) {
        m_inertiaWeight = inertiaWeight;
    }

    /**
     * Get the inertia weight (w)
     * 
     * @return the inertia weight (w)
     */
    public double getInertiaWeight() {
        return m_inertiaWeight;
    }

    /**
     * Get a description of all the current settings.
     * 
     * @return a String describing all the current setting in a single line. 
     */
    public String getDescription() { 
        return String.format("pop = %d, w = %.2f, c1 = %.2f, c2 = %.2f, Xmax = %.2f, Vmax = %.2f", 
                m_populationSize, m_inertiaWeight, m_c1, m_c2, m_maxPosition, m_maxVelocity);
    }

    @Override
    /**
     * Returns the most fit network in the swarm
     * 
     *  @return the most fit network in the swarm
     */
    public MLMethod getMethod() {
        return m_bestNetwork;
    }

    /**
     * Keep a reference to the passed population of networks.
     * This population is not copied, it will evolve during training.  
     * 
     * @param initialPopulation
     */
    public void setInitialPopulation(BasicNetwork[] initialPopulation) {
        m_networks = initialPopulation;
    }

    /**
     * Get the multi-threaded mode.
     * 
     * @return true if PSO works in multi-threaded mode
     */
    public boolean isMultiThreaded() {
        return m_multiThreaded;
    }

}
