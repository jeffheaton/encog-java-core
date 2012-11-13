/*
 * Encog(tm) Core v3.2 - Java Version
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
package org.encog.neural.neat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.util.csv.CSVFormat;

public class PersistNEATNetwork implements EncogPersistor {

	public class TempNeuron {		
		private final ActivationFunction activationFunction;
		private final double preActivation;
		private final double postActivation;
		
		public TempNeuron(ActivationFunction activationFunction,
				double preActivation, double postActivation) {
			super();
			this.activationFunction = activationFunction;
			this.preActivation = preActivation;
			this.postActivation = postActivation;
		}

		public ActivationFunction getActivationFunction() {
			return activationFunction;
		}

		public double getPreActivation() {
			return preActivation;
		}

		public double getPostActivation() {
			return postActivation;
		}
		
		
	}
	
	@Override
	public int getFileVersion() {
		return 1;
	}

	@Override
	public String getPersistClassString() {
		return "NEATNetwork";
	}

	@Override
	public Object read(InputStream is) {
		Map<String, String> properties = new HashMap<String, String>();
		List<TempNeuron> neuronList = new ArrayList<TempNeuron>();
		List<NEATLink> linkList = new ArrayList<NEATLink>();
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		int inputCount = 0;
		int outputCount = 0;
		int activationCycles;
		
		// parse the lines
		while( (section = in.readNextSection()) != null ) {
			if( section.getSectionName().equals("NEAT") && section.getSubSectionName().equals("PARAMS") ) {
				Map<String,String> params = section.parseParams();
				properties.putAll(params);
			} if( section.getSectionName().equals("NEAT") && section.getSubSectionName().equals("NETWORK") ) {
				Map<String,String> params = section.parseParams();
				
				inputCount = EncogFileSection.parseInt(params,PersistConst.INPUT_COUNT);
				outputCount = EncogFileSection.parseInt(params,PersistConst.OUTPUT_COUNT);
				activationCycles = EncogFileSection.parseInt(params, PersistConst.ACTIVATION_CYCLES);
			} else if( section.getSectionName().equals("NEAT") && section.getSubSectionName().equals("NEURONS") ) {
				for (String line : section.getLines()) {
					List<String> cols = EncogFileSection.splitColumns(line);

					final double preActivation = CSVFormat.EG_FORMAT.parse(cols.get(0));
					final double postActivation = CSVFormat.EG_FORMAT.parse(cols.get(1));
					final ActivationFunction activation = EncogFileSection.parseActivationFunction(cols.get(2));
					neuronList.add(new TempNeuron(activation,preActivation,postActivation));
				}				
			} else if( section.getSectionName().equals("NEAT") && section.getSubSectionName().equals("LINKS") ) {
				for (String line : section.getLines()) {
					List<String> cols = EncogFileSection.splitColumns(line);
					final int fromNeuron = Integer.parseInt(cols.get(0));
					final int toNeuron = Integer.parseInt(cols.get(1));
					final double weight = CSVFormat.EG_FORMAT.parse(cols.get(2));
					linkList.add(new NEATLink(fromNeuron,toNeuron,weight));
				}	
			}
		}
		
		// create activation and links arrays
		ActivationFunction[] af = new ActivationFunction[neuronList.size()];
		NEATLink[]  links = new NEATLink[linkList.size()];
		
		// populate arrays
		for(int i=0;i<af.length;i++) {
			af[i] = neuronList.get(i).getActivationFunction();
		}
		
		for(int i=0;i<links.length;i++) {
			links[i] = linkList.get(i);
		}
		
		// create the network
		
		NEATNetwork result = new NEATNetwork(inputCount,
	            outputCount,
	            links,
	            af);
		
		// set the pre and post values
		
		for(int i=0;i<af.length;i++) {
			result.getPreActivation()[i] = neuronList.get(i).getPreActivation();
			result.getPreActivation()[i] = neuronList.get(i).getPostActivation();
		}
		 
		
		// return result
		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		NEATNetwork neat = (NEATNetwork)obj;
		out.addSection("NEAT");
		out.addSubSection("PARAMS");
		out.addProperties(neat.getProperties());
		out.addSubSection("NETWORK");
		
		out.writeProperty(PersistConst.INPUT_COUNT, neat.getInputCount());
		out.writeProperty(PersistConst.OUTPUT_COUNT, neat.getOutputCount());
		out.writeProperty(PersistConst.ACTIVATION_CYCLES, neat.getActivationCycles());
		
		out.addSubSection("NEURONS");
		int neuronCount = neat.getPreActivation().length;
				
		for(int i=0;i<neuronCount;i++) {
			out.addColumn(neat.getPreActivation()[i]);
			out.addColumn(neat.getPostActivation()[i]);
			out.addColumn(neat.getActivationFunctions()[i]);
			out.writeLine();
		}
				
		out.addSubSection("LINKS");
		for (NEATLink neatLink : neat.getLinks() ) {
			out.addColumn(neatLink.getFromNeuron());
			out.addColumn(neatLink.getToNeuron());
			out.addColumn(neatLink.getWeight());
			out.writeLine();	
		}
		
		out.flush();
	}

}
