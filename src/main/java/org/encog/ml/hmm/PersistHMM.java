package org.encog.ml.hmm;

import java.io.InputStream;
import java.io.OutputStream;
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
		double pi[];
		Matrix transitionProbability;
		Map<String,String> properties = null;
		
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
				items = EncogFileSection.parseIntArray(params, HiddenMarkovModel.TAG_ITEMS);
				pi = section.parseDoubleArray(params,	HiddenMarkovModel.TAG_PI);
				transitionProbability = section.parseMatrix(params, HiddenMarkovModel.TAG_TRANSITION);
			} else if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().startsWith("DISTRIBUTION-")) {
				
			}
		}
		
		final HiddenMarkovModel result = new HiddenMarkovModel(states);
		result.getProperties().putAll(properties);

		

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
		out.writeProperty(HiddenMarkovModel.TAG_ITEMS,net.getItems());
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
