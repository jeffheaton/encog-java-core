package org.encog.ml.hmm;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.hmm.distributions.ContinousDistribution;
import org.encog.ml.hmm.distributions.DiscreteDistribution;
import org.encog.ml.hmm.distributions.StateDistribution;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;

/**
 * Persist a HMM.
 */
public class PersistHMM implements EncogPersistor {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getFileVersion() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPersistClassString() {
		return "HiddenMarkovModel";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object read(final InputStream is) {
		int states = 0;
		int items[];
		double pi[] = null;
		Matrix transitionProbability = null;
		Map<String,String> properties = null;
		List<StateDistribution> distributions = new ArrayList<StateDistribution>();
		
		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("HMM")
					&& section.getSubSectionName().equals("PARAMS")) {
				properties = section.parseParams();
				
			}
			if (section.getSectionName().equals("HMM")
					&& section.getSubSectionName().equals("CONFIG")) {
				final Map<String, String> params = section.parseParams();

				states = EncogFileSection.parseInt(params, HiddenMarkovModel.TAG_STATES);
				
				if( params.containsKey(HiddenMarkovModel.TAG_ITEMS) ) {
					items = EncogFileSection.parseIntArray(params, HiddenMarkovModel.TAG_ITEMS);
				}
				pi = section.parseDoubleArray(params,	HiddenMarkovModel.TAG_PI);
				transitionProbability = section.parseMatrix(params, HiddenMarkovModel.TAG_TRANSITION);
			} else if (section.getSectionName().equals("HMM")
					&& section.getSubSectionName().startsWith("DISTRIBUTION-")) {
				final Map<String, String> params = section.parseParams();
				String t = params.get(HiddenMarkovModel.TAG_DIST_TYPE);
				if( "ContinousDistribution".equals(t) ) {
					double[] mean = section.parseDoubleArray(params, HiddenMarkovModel.TAG_MEAN);
					Matrix cova = section.parseMatrix(params, HiddenMarkovModel.TAG_COVARIANCE);
					ContinousDistribution dist = new ContinousDistribution(mean,cova.getData());
					distributions.add(dist);
				} else if( "DiscreteDistribution".equals(t) ) {
					Matrix prob = section.parseMatrix(params, HiddenMarkovModel.TAG_PROBABILITIES);
					DiscreteDistribution dist = new DiscreteDistribution(prob.getData());
					distributions.add(dist);
				}
			}
		}
		
		final HiddenMarkovModel result = new HiddenMarkovModel(states);
		result.getProperties().putAll(properties);
		result.setTransitionProbability(transitionProbability.getData());
		result.setPi(pi);
		int index = 0;
		for(StateDistribution dist: distributions) {
			result.setStateDistribution(index++, dist);
		}
		

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void save(final OutputStream os, final Object obj) {
		final EncogWriteHelper out = new EncogWriteHelper(os);
		final HiddenMarkovModel net = (HiddenMarkovModel) obj;

		out.addSection("HMM");
		out.addSubSection("PARAMS");
		out.addProperties(net.getProperties());
		out.addSubSection("CONFIG");

		out.writeProperty(HiddenMarkovModel.TAG_STATES,net.getStateCount());
		if( net.getItems()!=null ) {
			out.writeProperty(HiddenMarkovModel.TAG_ITEMS,net.getItems());
		}
		out.writeProperty(HiddenMarkovModel.TAG_PI,net.getPi());
		out.writeProperty(HiddenMarkovModel.TAG_TRANSITION,new Matrix(net.getTransitionProbability()));
		
		for( int i=0; i<net.getStateCount();i++) {
			out.addSubSection("DISTRIBUTION-"+i);	
			StateDistribution sd = net.getStateDistribution(i);
			out.writeProperty(HiddenMarkovModel.TAG_DIST_TYPE, sd.getClass().getSimpleName());
			
			if( sd instanceof ContinousDistribution ) {
				ContinousDistribution cDist = (ContinousDistribution)sd;
				out.writeProperty(HiddenMarkovModel.TAG_MEAN, cDist.getMean());
				out.writeProperty(HiddenMarkovModel.TAG_COVARIANCE, cDist.getCovariance());
				
			} else if( sd instanceof DiscreteDistribution ) {
				DiscreteDistribution dDist = (DiscreteDistribution)sd;
				out.writeProperty(HiddenMarkovModel.TAG_PROBABILITIES, new Matrix(dDist.getProbabilities()));
			}
		}

		out.flush();
	}

}
