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
package org.encog.neural.networks.logic;

import org.encog.neural.networks.BasicNetwork;

public abstract class ARTLogic implements NeuralLogic {

	/**
	 * Neural network property, the A1 parameter.
	 */
	public static final String PROPERTY_A1 = "A1";
	
	/**
	 * Neural network property, the B1 parameter.
	 */
	public static final String PROPERTY_B1 = "B1";
	
	/**
	 * Neural network property, the C1 parameter.
	 */
	public static final String PROPERTY_C1 = "C1";
	
	/**
	 * Neural network property, the D1 parameter.
	 */
	public static final String PROPERTY_D1 = "D1";
	
	/**
	 * Neural network property, the L parameter.
	 */
	public static final String PROPERTY_L = "L";
	
	/**
	 * Neural network property, the vigilance parameter.
	 */
	public static final String PROPERTY_VIGILANCE = "VIGILANCE";

	/**
	 * The network.
	 */
	private BasicNetwork network;

	/**
	 * Setup the network logic, read parameters from the network.
	 * @param network The network that this logic class belongs to.
	 */
	public void init(BasicNetwork network) {
		this.network = network;		
	}
	
	/**
	 * @return The network in use.
	 */
	public BasicNetwork getNetwork()
	{
		return this.network;
	}

}
