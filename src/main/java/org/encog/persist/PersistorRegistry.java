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
package org.encog.persist;

import java.util.HashMap;
import java.util.Map;

import org.encog.ml.bayesian.PersistBayes;
import org.encog.ml.hmm.PersistHMM;
import org.encog.ml.svm.PersistSVM;
import org.encog.neural.art.PersistART1;
import org.encog.neural.bam.PersistBAM;
import org.encog.neural.cpn.PersistCPN;
import org.encog.neural.neat.PersistNEATNetwork;
import org.encog.neural.neat.PersistNEATPopulation;
import org.encog.neural.networks.PersistBasicNetwork;
import org.encog.neural.networks.training.propagation.PersistTrainingContinuation;
import org.encog.neural.pnn.PersistBasicPNN;
import org.encog.neural.rbf.PersistRBFNetwork;
import org.encog.neural.som.PersistSOM;
import org.encog.neural.thermal.PersistBoltzmann;
import org.encog.neural.thermal.PersistHopfield;

/**
 * Registry to hold persistors.  This is a singleton.
 */
public class PersistorRegistry {

	/**
	 * The instance.
	 */
	private static PersistorRegistry instance;

	/**
	 * @return The singleton instance.
	 */
	public static PersistorRegistry getInstance() {
		if (PersistorRegistry.instance == null) {
			PersistorRegistry.instance = new PersistorRegistry();
		}

		return PersistorRegistry.instance;
	}

	/**
	 * The mapping between name and persistor.
	 */
	private final Map<String, EncogPersistor> map = new HashMap<String, EncogPersistor>();

	/**
	 * Construct the object.
	 */
	private PersistorRegistry() {
		add(new PersistSVM());
		add(new PersistHopfield());
		add(new PersistBoltzmann());
		add(new PersistART1());
		add(new PersistBAM());
		add(new PersistBasicNetwork());
		add(new PersistRBFNetwork());
		add(new PersistSOM());
		add(new PersistNEATPopulation());
		add(new PersistNEATNetwork());
		add(new PersistBasicPNN());
		add(new PersistCPN());
		add(new PersistTrainingContinuation());
		add(new PersistBayes());
		add(new PersistHMM());
	}

	/**
	 * Add a persistor.
	 * @param persistor The persistor to add.
	 */
	public void add(final EncogPersistor persistor) {
		this.map.put(persistor.getPersistClassString(), persistor);
	}

	/**
	 * Get a persistor.
	 * @param clazz The class to get the persistor for.
	 * @return Return the persistor.
	 */
	public EncogPersistor getPersistor(final Class<?> clazz) {
		return getPersistor(clazz.getSimpleName());
	}

	/**
	 * Get the persistor by name.
	 * @param name The name of the persistor.
	 * @return The persistor.
	 */
	public EncogPersistor getPersistor(final String name) {
		return this.map.get(name);
	}
}
