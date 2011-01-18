/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationStep;
import org.encog.neural.art.ART1;
import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATNeuron;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.util.obj.SerializeObject;

public class TestPersistNEAT extends TestCase {
	
	public final String EG_FILENAME = "encogtest.eg";
	public final String EG_RESOURCE = "test";
	public final String SERIAL_FILENAME = "encogtest.ser";
	
	private NEATNetwork create()
	{
		List<NEATNeuron> neurons = new ArrayList<NEATNeuron>();
		ActivationFunction af = new ActivationStep();
		
		// create the neurons
		NEATNeuron input1 = new NEATNeuron(
				NEATNeuronType.Input, 
				1,
				0.1, 
				0.2,
				0.3);
		
		NEATNeuron input2 = new NEATNeuron(
				NEATNeuronType.Input, 
				2,
				0.1, 
				0.2,
				0.3);
		
		NEATNeuron bias = new NEATNeuron(
				NEATNeuronType.Bias, 
				3,
				0.1, 
				0.2,
				0.3);
		
		NEATNeuron hidden1 = new NEATNeuron(
				NEATNeuronType.Hidden, 
				4,
				0.1, 
				0.2,
				0.3);
		
		NEATNeuron output = new NEATNeuron(
				NEATNeuronType.Output, 
				5,
				0.1, 
				0.2,
				0.3);

		// add the neurons
		neurons.add(input1);
		neurons.add(input2);
		neurons.add(hidden1);
		neurons.add(bias);
		neurons.add(output);
		
		// connect everything
		link(0.01, input1, hidden1, false);
		link(0.01, input2, hidden1, false);
		link(0.01, bias, hidden1, false);
		link(0.01, hidden1, output, false);
		
		// create the network
		NEATNetwork result = new NEATNetwork(2, 
				1,
				neurons,
				af, 
				3);
				
		return result;
	}
	
	private void link(
			double weight, NEATNeuron from, NEATNeuron to, boolean recurrent)
	{
		NEATLink l = new NEATLink(weight, from, to, recurrent);
		from.getOutputboundLinks().add(l);
		to.getInboundLinks().add(l);
	}
	
	public void testPersistEG()
	{
		NEATNetwork network = create();

		EncogMemoryCollection encog = new EncogMemoryCollection();
		encog.add(EG_RESOURCE, network);
		encog.save(EG_FILENAME);
		
		EncogMemoryCollection encog2 = new EncogMemoryCollection();
		encog2.load(EG_FILENAME);
		NEATNetwork network2 = (NEATNetwork)encog2.find(EG_RESOURCE);
		validate(network2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		NEATNetwork network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		NEATNetwork network2 = (NEATNetwork)SerializeObject.load(SERIAL_FILENAME);
				
		validate(network2);
	}
	
	public void testPersistSerialEG() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		NEATNetwork network = create();
		
		SerializeObject.saveEG(SERIAL_FILENAME, network);
		NEATNetwork network2 = (NEATNetwork)SerializeObject.loadEG(SERIAL_FILENAME);
				
		validate(network2);
	}
	
	private void validate(NEATNetwork network)
	{
		Assert.assertEquals(1, network.getOutputCount());
		Assert.assertEquals(2, network.getInputCount());
		Assert.assertEquals(3, network.getNetworkDepth());
		Assert.assertTrue(network.getActivationFunction() instanceof ActivationStep);
		Assert.assertEquals(0.0, ((ActivationStep)network.getActivationFunction()).getCenter() );
		Assert.assertEquals(1.0, ((ActivationStep)network.getActivationFunction()).getHigh() );
		Assert.assertEquals(0.0, ((ActivationStep)network.getActivationFunction()).getLow() );
		Assert.assertEquals(5,network.getNeurons().size());
		
		Map<NEATNeuronType,NEATNeuron> neurons = new HashMap<NEATNeuronType,NEATNeuron>();
		
		for(NEATNeuron neuron: network.getNeurons())
		{
			neurons.put(neuron.getNeuronType(), neuron);
		}
		
		Assert.assertEquals(4, neurons.size());
	
		NEATNeuron output = neurons.get(NEATNeuronType.Output);
		NEATNeuron input = neurons.get(NEATNeuronType.Input);
		Assert.assertEquals(1, input.getOutputboundLinks().size());
		Assert.assertEquals(0, input.getInboundLinks().size());
		Assert.assertEquals(0, output.getOutputboundLinks().size());
		Assert.assertEquals(1, output.getInboundLinks().size());
	}
}
