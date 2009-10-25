package org.encog.neural.networks.training.propagation.multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.encog.neural.networks.training.propagation.PropagationLevel;
import org.encog.neural.networks.training.propagation.PropagationSynapse;
import org.encog.neural.networks.training.propagation.PropagationUtil;

public class GradientMap {
	
	private final Map<PropagationLevel,List<PropagationLevel>> levelMap = new HashMap<PropagationLevel,List<PropagationLevel>>();
	private final Map<PropagationSynapse,List<PropagationSynapse>> synapseMap = new HashMap<PropagationSynapse,List<PropagationSynapse>>();
	private final List<PropagationLevel> levels = new ArrayList<PropagationLevel>();
	private final List<PropagationSynapse> synapses = new ArrayList<PropagationSynapse>();

	public GradientMap(PropagationUtil master, MultiPropagation mprop)
	{
		linkLevels(master,mprop);
	}
	
	private void linkLevels(PropagationUtil master, MultiPropagation mprop)
	{
		// build a list of iterators to access the worker levels one at a time 
		List<Iterator<PropagationLevel>> workerLevelIterator = new ArrayList<Iterator<PropagationLevel>>();
		
		for(MPROPWorker worker: mprop.getWorkers())
		{
			workerLevelIterator.add(worker.getPropagationUtil().getLevels().iterator());
		}
		
		for(PropagationLevel masterLevel: master.getLevels() )
		{
			// add to master level list
			this.levels.add(masterLevel);
			
			// build a list of worker levels
			List<PropagationLevel> workerLevels = new ArrayList<PropagationLevel>();
			for(Iterator<PropagationLevel> iterator: workerLevelIterator )
			{
				PropagationLevel workerLevel = iterator.next();
				workerLevels.add(workerLevel);
			}
			this.levelMap.put(masterLevel, workerLevels);
			linkSynapses(masterLevel,workerLevels);
		}
	}

	private void linkSynapses(PropagationLevel masterLevel,
			List<PropagationLevel> workerLevels) {
		
		List<Iterator<PropagationSynapse>> workerSynapseIteratorList = new ArrayList<Iterator<PropagationSynapse>>();
		
		for(PropagationLevel workerLevel: workerLevels)
		{
			workerSynapseIteratorList.add(workerLevel.getOutgoing().iterator());
		}
		
		for(PropagationSynapse masterSynapse: masterLevel.getOutgoing() )
		{
			// add to master synapse list
			this.synapses.add(masterSynapse);
			
			// build a list of worker synapses
			List<PropagationSynapse> workerSynapses = new ArrayList<PropagationSynapse>();
			
			for(Iterator<PropagationSynapse> iterator: workerSynapseIteratorList )
			{
				PropagationSynapse workerSynapse = iterator.next();
				workerSynapses.add(workerSynapse);
			}
			
			this.synapseMap.put(masterSynapse, workerSynapses);
		}
	}
	
	public void collect()
	{
		// handle levels
		for(PropagationLevel masterLevel: this.levels)
		{
			collectLevel(masterLevel);
		}
		
		// handle synapses
		for(PropagationSynapse masterSynapse: this.synapses)
		{
			collectSynapse(masterSynapse);
		}
	}

	private void collectSynapse(PropagationSynapse masterSynapse) {
		List<PropagationSynapse> workerSynapses = this.synapseMap.get(masterSynapse);
		
		double[][] masterMatrixGradients = masterSynapse.getAccMatrixGradients().getData();
		
		for( PropagationSynapse workerSynapse: workerSynapses )
		{
			double[][] workerMatrixGradients = workerSynapse.getAccMatrixGradients().getData();
			
			for(int r=0;r<masterMatrixGradients.length;r++)
			{
				for(int c=0;c<masterMatrixGradients[r].length;c++)
				{
					masterMatrixGradients[r][c]+=workerMatrixGradients[r][c];
					workerMatrixGradients[r][c]=0;
				}
			}
		}
		
	}

	private void collectLevel(PropagationLevel masterLevel) {
		List<PropagationLevel> workerLevels = this.levelMap.get(masterLevel);
		
		double[] masterThresholdGradients = masterLevel.getThresholdGradients();
		
		for(PropagationLevel workerLevel: workerLevels )
		{
			double[] workerThresholdGradiends = workerLevel.getThresholdGradients();
			
			for(int i=0;i<workerThresholdGradiends.length;i++)
			{
				masterThresholdGradients[i]+=workerThresholdGradiends[i];
				workerThresholdGradiends[i]=0;
			}
		}
		
	}

	public Map<PropagationLevel, List<PropagationLevel>> getLevelMap() {
		return levelMap;
	}

	public Map<PropagationSynapse, List<PropagationSynapse>> getSynapseMap() {
		return synapseMap;
	}

	public List<PropagationLevel> getLevels() {
		return levels;
	}

	public List<PropagationSynapse> getSynapses() {
		return synapses;
	}
}
