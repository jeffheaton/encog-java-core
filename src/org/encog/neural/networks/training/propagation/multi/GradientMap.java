/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.neural.networks.training.propagation.multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.encog.neural.networks.training.propagation.PropagationLevel;
import org.encog.neural.networks.training.propagation.PropagationSynapse;
import org.encog.neural.networks.training.propagation.PropagationUtil;

/**
 * The gradient map is used to track all of the workers and collect their
 * gradient descents into the master neural network for training. This allows
 * the threads to work somewhat independently and then aggregate their results
 * at the end of each iteration.
 * 
 * A map is built up ahead of time to allow quick access when the training is
 * actually running.
 */
public class GradientMap {

	/**
	 * A mapping between master levels and a list of each corresponding worker
	 * level.
	 */
	private final Map<PropagationLevel, List<PropagationLevel>> levelMap = new HashMap<PropagationLevel, List<PropagationLevel>>();

	/**
	 * A mapping between master levels and a list of each corresponding worker
	 * level.
	 */
	private final Map<PropagationSynapse, List<PropagationSynapse>> synapseMap = new HashMap<PropagationSynapse, List<PropagationSynapse>>();

	/**
	 * A list of all of the master levels.
	 */
	private final List<PropagationLevel> levels = new ArrayList<PropagationLevel>();

	/**
	 * A list of all of the master synapses.
	 */
	private final List<PropagationSynapse> synapses = new ArrayList<PropagationSynapse>();

	/**
	 * Construct a mapping between the master network training levels and all of
	 * the workers. This builds up the internal map that will be used to quickly
	 * collect results at the end of each training iteration.
	 * 
	 * @param master
	 *            The training util for the master network that all results are
	 *            aggregated to.
	 * @param mprop
	 *            The MPROP object that contains all of the workers.
	 */
	public GradientMap(final PropagationUtil master,
			final MultiPropagation mprop) {
		linkLevels(master, mprop);
	}

	/**
	 * Collect the gradient descents from all levels and synapses and place them
	 * in the master training utility.
	 */
	public void collect() {
		// handle levels
		for (final PropagationLevel masterLevel : this.levels) {
			collectLevel(masterLevel);
		}

		// handle synapses
		for (final PropagationSynapse masterSynapse : this.synapses) {
			collectSynapse(masterSynapse);
		}
	}

	/**
	 * Collect the gradient descents for the specific master level. This will
	 * sum the gradient descents for all worker threads and place them in the
	 * master level.
	 * 
	 * @param masterLevel
	 *            The level to collect from.
	 */
	private void collectLevel(final PropagationLevel masterLevel) {
		final List<PropagationLevel> workerLevels = this.levelMap
				.get(masterLevel);

		final double[] masterThresholdGradients = masterLevel
				.getThresholdGradients();

		for (final PropagationLevel workerLevel : workerLevels) {
			final double[] workerThresholdGradiends = workerLevel
					.getThresholdGradients();

			for (int i = 0; i < workerThresholdGradiends.length; i++) {
				masterThresholdGradients[i] += workerThresholdGradiends[i];
				workerThresholdGradiends[i] = 0;
			}
		}

	}

	/**
	 * Collect the gradient descents from all of the worker synapses and place
	 * them in the master synapses.
	 * 
	 * @param masterSynapse
	 *            The master synapse to recieve the results from the workers.
	 */
	private void collectSynapse(final PropagationSynapse masterSynapse) {
		final List<PropagationSynapse> workerSynapses = this.synapseMap
				.get(masterSynapse);

		final double[][] masterMatrixGradients = masterSynapse
				.getAccMatrixGradients().getData();

		for (final PropagationSynapse workerSynapse : workerSynapses) {
			final double[][] workerMatrixGradients = workerSynapse
					.getAccMatrixGradients().getData();

			for (int r = 0; r < masterMatrixGradients.length; r++) {
				for (int c = 0; c < masterMatrixGradients[r].length; c++) {
					masterMatrixGradients[r][c] += workerMatrixGradients[r][c];
					workerMatrixGradients[r][c] = 0;
				}
			}
		}

	}

	/**
	 * @return A map between master levels and lists of corresponding worker
	 *         levels.
	 */
	public Map<PropagationLevel, List<PropagationLevel>> getLevelMap() {
		return this.levelMap;
	}

	/**
	 * @return All of the master levels.
	 */
	public List<PropagationLevel> getLevels() {
		return this.levels;
	}

	/**
	 * @return A map between worker synapses and lists of corresponding worker
	 *         synapses.
	 */
	public Map<PropagationSynapse, List<PropagationSynapse>> getSynapseMap() {
		return this.synapseMap;
	}

	/**
	 * @return All of the master synapses.
	 */
	public List<PropagationSynapse> getSynapses() {
		return this.synapses;
	}

	/**
	 * Actually begin building the linked map between master network training
	 * levels and all of the workers.
	 * 
	 * @param master
	 *            The training util for the master network that all results are
	 *            aggregated to.
	 * @param mprop
	 *            The MPROP object that contains all of the workers.
	 */
	private void linkLevels(final PropagationUtil master,
			final MultiPropagation mprop) {
		// build a list of iterators to access the worker levels one at a time
		final List<Iterator<PropagationLevel>> workerLevelIterator = new ArrayList<Iterator<PropagationLevel>>();

		for (final MPROPWorker worker : mprop.getWorkers()) {
			workerLevelIterator.add(worker.getPropagationUtil().getLevels()
					.iterator());
		}

		for (final PropagationLevel masterLevel : master.getLevels()) {
			// add to master level list
			this.levels.add(masterLevel);

			// build a list of worker levels
			final List<PropagationLevel> workerLevels = new ArrayList<PropagationLevel>();
			for (final Iterator<PropagationLevel> iterator : workerLevelIterator) {
				final PropagationLevel workerLevel = iterator.next();
				workerLevels.add(workerLevel);
			}
			this.levelMap.put(masterLevel, workerLevels);
			linkSynapses(masterLevel, workerLevels);
		}
	}

	/**
	 * Link the specified worker synapses to the specified master synapses.
	 * 
	 * @param masterLevel
	 *            The master level that contains the synapses to link.
	 * @param workerLevels
	 *            The worker levels that correspond to the master level.
	 */
	private void linkSynapses(final PropagationLevel masterLevel,
			final List<PropagationLevel> workerLevels) {

		final List<Iterator<PropagationSynapse>> workerSynapseIteratorList = new ArrayList<Iterator<PropagationSynapse>>();

		for (final PropagationLevel workerLevel : workerLevels) {
			workerSynapseIteratorList.add(workerLevel.getOutgoing().iterator());
		}

		for (final PropagationSynapse masterSynapse : masterLevel.getOutgoing()) {
			// add to master synapse list
			this.synapses.add(masterSynapse);

			// build a list of worker synapses
			final List<PropagationSynapse> workerSynapses = new ArrayList<PropagationSynapse>();

			for (final Iterator<PropagationSynapse> iterator : workerSynapseIteratorList) {
				final PropagationSynapse workerSynapse = iterator.next();
				workerSynapses.add(workerSynapse);
			}

			this.synapseMap.put(masterSynapse, workerSynapses);
		}
	}
}
