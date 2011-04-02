package org.encog.persist;

import java.util.HashMap;
import java.util.Map;

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

public class PersistorRegistry {
	
	private static PersistorRegistry instance;
	private Map<String,EncogPersistor> map = new HashMap<String,EncogPersistor>();
	
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
	}

	public void add(EncogPersistor persistor) {
		map.put(persistor.getPersistClassString(), persistor);		
	}
	
	public static PersistorRegistry getInstance() {
		if( instance==null ) {
			instance = new PersistorRegistry();
		}
		
		return instance;
	}
	
	public EncogPersistor getPersistor(String name) {
		return map.get(name);
	}
	
	public EncogPersistor getPersistor(Class<?> clazz) {
		return getPersistor(clazz.getSimpleName());
	}
}
