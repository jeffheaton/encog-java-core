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
package org.encog.neural.pattern;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedForwardPattern implements NeuralNetworkPattern {

	private ActivationFunction activation;
	private int inputNeurons;
	private int outputNeurons;
	private List<Integer> hidden = new ArrayList<Integer>();
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void setActivationFunction(ActivationFunction activation) {
		this.activation = activation;
		
	}

	public void addHiddenLayer(int count) {
		this.hidden.add(count);		
	}

	public void setInputNeurons(int count) {
		this.inputNeurons = count;
		
	}

	public void setOutputNeurons(int count) {
		this.outputNeurons = count;
		
	}
	
	public BasicNetwork generate() {
		int y = 0;
		Layer layer;
		
		BasicNetwork result = new BasicNetwork();
		result.addLayer(layer = new BasicLayer(this.activation,true,this.inputNeurons));
		
		layer.setX(10);
		layer.setY(y);
		y+=120;
		
		for(Integer count: this.hidden)
		{
			result.addLayer(layer = new BasicLayer(this.activation,true,count));
			layer.setX(10);
			layer.setY(y);
			y+=120;
		}
		
		result.addLayer(layer = new BasicLayer(this.activation,true,this.outputNeurons));
		layer.setX(10);
		layer.setY(y);
		y+=120;
		
		result.getStructure().finalizeStructure();
		result.reset();
		
		return result;
	}

}
