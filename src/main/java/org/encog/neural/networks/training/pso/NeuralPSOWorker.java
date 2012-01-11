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

import org.encog.util.concurrency.EngineTask;

/**
 * PSO multi-treaded worker.
 * It allows PSO to offload all of the individual 
 * particle calculations to a separate thread.
 * 
 * Contributed by:
 * Geoffroy Noel
 * https://github.com/goffer-looney 
 * 
 * @author Geoffroy Noel
 */
public class NeuralPSOWorker implements EngineTask {

    private NeuralPSO m_neuralPSO;
    private int m_particleIndex;
    private boolean m_init = false;

    /**
     * Constructor.
     * 
     * @param neuralPSO     the training algorithm
     * @param particleIndex the index of the particle in the swarm
     * @param init          true for an initialisation iteration 
     * 
     */
    public NeuralPSOWorker(NeuralPSO neuralPSO, int particleIndex, boolean init) {
        m_neuralPSO = neuralPSO;
        m_particleIndex = particleIndex;
        m_init = init;
    }

    /**
     * Update the particle velocity, position and personal best.
     */
    public final void run() {
        m_neuralPSO.updateParticle(m_particleIndex, m_init);
    }

}
