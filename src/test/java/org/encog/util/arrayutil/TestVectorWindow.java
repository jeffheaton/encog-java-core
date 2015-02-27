/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.util.arrayutil;

import org.encog.Encog;
import org.encog.EncogError;
import org.junit.Assert;
import org.junit.Test;

public class TestVectorWindow {
	@Test(expected=EncogError.class)
	public void testIncomplete() {
		double[] o = new double[1];
		VectorWindow window = new VectorWindow(3);
		window.add(new double[] {1});
		window.copyWindow(o, 0);
	}
	
	@Test
	public void testComplete() {
		final double[] expect = {1.0, 2.0, 3.0};
		double[] o = new double[3];
		VectorWindow window = new VectorWindow(3);
		window.add(new double[] {1});
		window.add(new double[] {2});
		window.add(new double[] {3});
		window.copyWindow(o, 0);
		Assert.assertArrayEquals(expect,o,Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	@Test
	public void testCompleteScroll() {
		final double[] expect = {2.0, 3.0, 4.0};
		double[] o = new double[3];
		VectorWindow window = new VectorWindow(3);
		window.add(new double[] {1});
		window.add(new double[] {2});
		window.add(new double[] {3});
		window.add(new double[] {4});
		window.copyWindow(o, 0);
		Assert.assertArrayEquals(expect,o,Encog.DEFAULT_DOUBLE_EQUAL);
	}

}
