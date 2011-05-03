/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.neural.rbf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.rbf.RadialBasisFunction;
import org.encog.neural.flat.FlatNetworkRBF;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.persist.PersistError;
import org.encog.util.csv.CSVFormat;

public class PersistRBFNetwork implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return "RBFNetwork";
	}

	@Override
	public Object read(InputStream is) {
		RBFNetwork result = new RBFNetwork();
		FlatNetworkRBF flat =(FlatNetworkRBF)result.getFlat();
		
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		
		while( (section = in.readNextSection()) != null ) {
			if( section.getSectionName().equals("RBF-NETWORK") && section.getSubSectionName().equals("PARAMS") ) {
				Map<String,String> params = section.parseParams();
				result.getProperties().putAll(params);
			} if( section.getSectionName().equals("RBF-NETWORK") && section.getSubSectionName().equals("NETWORK") ) {
				Map<String,String> params = section.parseParams();
				
				flat.setBeginTraining(EncogFileSection.parseInt(params, BasicNetwork.TAG_BEGIN_TRAINING));					
				flat.setConnectionLimit( EncogFileSection.parseDouble(params, BasicNetwork.TAG_CONNECTION_LIMIT));		
				flat.setContextTargetOffset( EncogFileSection.parseIntArray(params, BasicNetwork.TAG_CONTEXT_TARGET_OFFSET));
				flat.setContextTargetSize(  EncogFileSection.parseIntArray(params, BasicNetwork.TAG_CONTEXT_TARGET_SIZE));
				flat.setEndTraining( EncogFileSection.parseInt(params, BasicNetwork.TAG_END_TRAINING));
				flat.setHasContext( EncogFileSection.parseBoolean(params, BasicNetwork.TAG_HAS_CONTEXT));
				flat.setInputCount( EncogFileSection.parseInt(params, PersistConst.INPUT_COUNT));
				flat.setLayerCounts( EncogFileSection.parseIntArray(params, BasicNetwork.TAG_LAYER_COUNTS));
				flat.setLayerFeedCounts( EncogFileSection.parseIntArray(params, BasicNetwork.TAG_LAYER_FEED_COUNTS));
				flat.setLayerContextCount( EncogFileSection.parseIntArray(params, BasicNetwork.TAG_LAYER_CONTEXT_COUNT));
				flat.setLayerIndex( EncogFileSection.parseIntArray(params, BasicNetwork.TAG_LAYER_INDEX));
				flat.setLayerOutput( EncogFileSection.parseDoubleArray(params, PersistConst.OUTPUT));
				flat.setOutputCount( EncogFileSection.parseInt(params, PersistConst.OUTPUT_COUNT));
				flat.setWeightIndex( EncogFileSection.parseIntArray(params, BasicNetwork.TAG_WEIGHT_INDEX));
				flat.setWeights( EncogFileSection.parseDoubleArray(params,  PersistConst.WEIGHTS));
				flat.setBiasActivation( EncogFileSection.parseDoubleArray(params,  BasicNetwork.TAG_BIAS_ACTIVATION));
			} 
			else if( section.getSectionName().equals("RBF-NETWORK") && section.getSubSectionName().equals("ACTIVATION") ) {
				int index = 0;
				
				flat.setActivationFunctions(new ActivationFunction[flat.getLayerCounts().length]);
				 
				for(String line : section.getLines()) {
					ActivationFunction af =  null;
					List<String> cols = EncogFileSection.splitColumns(line);
					String name = "org.encog.engine.network.activation." + cols.get(0);
					try {
						Class<?> clazz = Class.forName(name);
						af = (ActivationFunction) clazz.newInstance();
					} catch (ClassNotFoundException e) {
						throw new PersistError(e);
					} catch (InstantiationException e) {
						throw new PersistError(e);
					} catch (IllegalAccessException e) {
						throw new PersistError(e);
					}
					
					for(int i=0;i<af.getParamNames().length;i++) {
						af.setParam(i, CSVFormat.EG_FORMAT.parse(cols.get(i+1)));
					}
					
					flat.getActivationFunctions()[index++] = af;
				}
				
			}
			else if( section.getSectionName().equals("RBF-NETWORK") && section.getSubSectionName().equals("RBF") ) {
				int index = 0;
				
				int hiddenCount = flat.getLayerCounts()[1];
				int inputCount = flat.getLayerCounts()[2];
				
				flat.setRBF(new RadialBasisFunction[hiddenCount]);
				 
				for(String line : section.getLines()) {
					RadialBasisFunction rbf =  null;
					List<String> cols = EncogFileSection.splitColumns(line);
					String name = "org.encog.mathutil.rbf." + cols.get(0);
					try {
						Class<?> clazz = Class.forName(name);
						rbf = (RadialBasisFunction) clazz.newInstance();
					} catch (ClassNotFoundException e) {
						throw new PersistError(e);
					} catch (InstantiationException e) {
						throw new PersistError(e);
					} catch (IllegalAccessException e) {
						throw new PersistError(e);
					}
					
					rbf.setWidth(CSVFormat.EG_FORMAT.parse(cols.get(1)));
					rbf.setPeak(CSVFormat.EG_FORMAT.parse(cols.get(2)));
					rbf.setCenters(new double[inputCount]);
					
					for(int i=0;i<inputCount;i++) {
						rbf.getCenters()[i] = CSVFormat.EG_FORMAT.parse(cols.get(i+3));
					}
					
					flat.getRBF()[index++] = rbf;
				}
				
				
			}
		} 
				 
		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		RBFNetwork net = (RBFNetwork)obj;
		FlatNetworkRBF flat = (FlatNetworkRBF)net.getFlat();
		out.addSection("RBF-NETWORK");
		out.addSubSection("PARAMS");
		out.addProperties(net.getProperties());
		out.addSubSection("NETWORK");
		out.writeProperty(BasicNetwork.TAG_BEGIN_TRAINING, flat.getBeginTraining());
		out.writeProperty(BasicNetwork.TAG_CONNECTION_LIMIT, flat.getConnectionLimit());		
		out.writeProperty(BasicNetwork.TAG_CONTEXT_TARGET_OFFSET, flat.getContextTargetOffset() );
		out.writeProperty(BasicNetwork.TAG_CONTEXT_TARGET_SIZE, flat.getContextTargetSize() );
		out.writeProperty(BasicNetwork.TAG_END_TRAINING, flat.getEndTraining() );
		out.writeProperty(BasicNetwork.TAG_HAS_CONTEXT, flat.getHasContext() );
		out.writeProperty(PersistConst.INPUT_COUNT, flat.getInputCount() );
		out.writeProperty(BasicNetwork.TAG_LAYER_COUNTS, flat.getLayerCounts() );
		out.writeProperty(BasicNetwork.TAG_LAYER_FEED_COUNTS, flat.getLayerFeedCounts() );
		out.writeProperty(BasicNetwork.TAG_LAYER_CONTEXT_COUNT, flat.getLayerContextCount() );
		out.writeProperty(BasicNetwork.TAG_LAYER_INDEX, flat.getLayerIndex() );
		out.writeProperty(PersistConst.OUTPUT, flat.getLayerOutput() );
		out.writeProperty(PersistConst.OUTPUT_COUNT, flat.getOutputCount() );
		out.writeProperty(BasicNetwork.TAG_WEIGHT_INDEX, flat.getWeightIndex() );
		out.writeProperty(PersistConst.WEIGHTS, flat.getWeights());
		out.writeProperty(BasicNetwork.TAG_BIAS_ACTIVATION, flat.getBiasActivation());
		out.addSubSection("ACTIVATION");
		for(ActivationFunction af : flat.getActivationFunctions()) {
			out.addColumn(af.getClass().getSimpleName());
			for(int i=0;i<af.getParams().length;i++) {
				out.addColumn(af.getParams()[i]);
			}
			out.writeLine();
		}
		out.addSubSection("RBF");
		for( RadialBasisFunction rbf : flat.getRBF() ) {
			out.addColumn(rbf.getClass().getSimpleName());
			out.addColumn(rbf.getWidth());
			out.addColumn(rbf.getPeak());
			for(int i=0;i<rbf.getCenters().length;i++) {
				out.addColumn(rbf.getCenters()[i]);
			}
			out.writeLine();
		}

		out.flush();		
	}


	@Override
	public int getFileVersion() {
		return 1;
	}

}
