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
package org.encog.neural.neat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.util.csv.CSVFormat;

public class PersistNEATNetwork implements EncogPersistor {

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
		NEATNetwork result = new NEATNetwork();	
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		Map<Integer,NEATNeuron> neuronMap = new HashMap<Integer,NEATNeuron>();
		
		while( (section = in.readNextSection()) != null ) {
			if( section.getSectionName().equals("NEAT") && section.getSubSectionName().equals("PARAMS") ) {
				Map<String,String> params = section.parseParams();
				result.getProperties().putAll(params);
			} if( section.getSectionName().equals("NEAT") && section.getSubSectionName().equals("NETWORK") ) {
				Map<String,String> params = section.parseParams();
				
				result.setInputCount( EncogFileSection.parseInt(params,PersistConst.INPUT_COUNT));
				result.setOutputCount( EncogFileSection.parseInt(params,PersistConst.OUTPUT_COUNT));
				result.setActivationFunction( EncogFileSection.parseActivationFunction(params,PersistConst.ACTIVATION_FUNCTION));
				result.setNetworkDepth( EncogFileSection.parseInt(params,PersistConst.DEPTH));
				result.setActivationCycles( EncogFileSection.parseInt(params, PersistConst.ACTIVATION_CYCLES));
			} else if( section.getSectionName().equals("NEAT") && section.getSubSectionName().equals("NEURONS") ) {
				for (String line : section.getLines()) {
					List<String> cols = EncogFileSection.splitColumns(line);

					final long neuronID = Integer.parseInt(cols.get(0));
					final NEATNeuronType neuronType = PersistNEATPopulation.stringToNeuronType(cols.get(1)); 
					final double splitY = CSVFormat.EG_FORMAT.parse(cols.get(2));
					final double splitX = CSVFormat.EG_FORMAT.parse(cols.get(3));
					
					NEATNeuron neatNeuron = new NEATNeuron(neuronType, neuronID,
						splitY,splitX);
					result.getNeurons().add(neatNeuron);
					neuronMap.put((int)neuronID, neatNeuron);
				}				
			} else if( section.getSectionName().equals("NEAT") && section.getSubSectionName().equals("LINKS") ) {
				for (String line : section.getLines()) {
					List<String> cols = EncogFileSection.splitColumns(line);
					int fromID = Integer.parseInt(cols.get(0));
					int toID = Integer.parseInt(cols.get(1));
					boolean recurrent = Integer.parseInt(cols.get(2))>0;
					double weight = CSVFormat.EG_FORMAT.parse(cols.get(3));
					NEATNeuron fromNeuron = neuronMap.get(fromID);
					NEATNeuron toNeuron = neuronMap.get(toID);
					NEATLink neatLink = new NEATLink(weight,fromNeuron,toNeuron,recurrent);
					fromNeuron.getOutputboundLinks().add(neatLink);
					toNeuron.getInboundLinks().add(neatLink);
				}
			}
		}
		 
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
		out.writeProperty(PersistConst.ACTIVATION_FUNCTION, neat.getActivationFunction());
		out.writeProperty(PersistConst.DEPTH, neat.getNetworkDepth());
		out.writeProperty(PersistConst.ACTIVATION_CYCLES, neat.getActivationCycles());
		
		out.addSubSection("NEURONS");
		for (NEATNeuron neatNeuron : neat.getNeurons() ) {
			out.addColumn(neatNeuron.getNeuronID());
			out.addColumn(PersistNEATPopulation.neuronTypeToString(neatNeuron.getNeuronType()));
			out.addColumn(neatNeuron.getSplitX());
			out.addColumn(neatNeuron.getSplitY());
			out.writeLine();
		}
		
		out.addSubSection("LINKS");
		for (NEATNeuron neatNeuron : neat.getNeurons() ) {
						
			for(NEATLink link: neatNeuron.getOutputboundLinks() ) {
				writeLink(out,link);
			}			
		}
		
		out.flush();
	}
	
	private void writeLink(EncogWriteHelper out, NEATLink link) {
		out.addColumn(link.getFromNeuron().getNeuronID());
		out.addColumn(link.getToNeuron().getNeuronID());
		out.addColumn(link.isRecurrent());
		out.addColumn(link.getWeight());
		out.writeLine();		
	}

}
