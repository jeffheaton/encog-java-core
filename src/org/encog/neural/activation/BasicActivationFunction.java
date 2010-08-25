/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.activation;

import org.encog.persist.EncogCollection;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.generic.GenericPersistor;

/**
 * Holds basic functionality that all activation functions will likely have use
 * of. Specifically it implements a name and description for the
 * EncogPersistedObject class.
 * 
 * @author jheaton
 * 
 */
public abstract class BasicActivationFunction implements ActivationFunction {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 672555213449163812L;
	
	
	protected double[] params;
	
	/**
	 * @return The object cloned.
	 */
	@Override
	public abstract Object clone();

	
	public double[] getParams()
	{
		return this.params;
	}
	
	public void setParam(int index, double value)
	{
		this.params[index] = value;
	}
	
	public abstract String[] getParamNames();

}
