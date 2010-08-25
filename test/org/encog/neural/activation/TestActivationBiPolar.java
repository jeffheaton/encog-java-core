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

import org.encog.EncogError;
import org.encog.persist.persistors.generic.GenericPersistor;
import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

public class TestActivationBiPolar extends TestCase {
	
	@Test
	public void testBiPolar() throws Throwable
	{
		ActivationBiPolar activation = new ActivationBiPolar();
		Assert.assertFalse(activation.hasDerivative());
		
		ActivationBiPolar clone = (ActivationBiPolar)activation.clone();
		Assert.assertNotNull(clone);
		
		double[] input = { 0.5, -0.5 };
		
		activation.activationFunction(input);
		
		Assert.assertEquals(1.0,input[0],0.1);
		Assert.assertEquals(-1.0,input[1],0.1);
		
		
		// test derivative, should throw an error
		try
		{
			activation.derivativeFunction(0);
			Assert.assertTrue(false);// mark an error
		}
		catch(EncogError e)
		{
			// good, this should happen
		}
		
		
	}
}
