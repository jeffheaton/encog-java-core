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
package org.encog.util.datastruct;

import org.junit.Assert;
import org.junit.Test;

public class TestWindowInt {
	
	@Test
	public void testSize() {
		WindowInt w = new WindowInt(5);
		Assert.assertEquals(5, w.size());
	}
	
	@Test
	public void testWindowAdd() {
		WindowInt w = new WindowInt(5);
		w.add(1);
		w.add(2);
		w.add(3);
		w.add(4);
		w.add(5);
		
		Assert.assertEquals(5,w.getData()[0]);
		Assert.assertEquals(4,w.getData()[1]);
		Assert.assertEquals(3,w.getData()[2]);
		Assert.assertEquals(2,w.getData()[3]);
		Assert.assertEquals(1,w.getData()[4]);
	}
	
	@Test
	public void testWindowShuft() {
		WindowInt w = new WindowInt(5);
		w.add(1);
		w.add(2);
		w.add(3);
		w.add(4);
		w.add(5);
		w.add(6);
		
		Assert.assertEquals(6,w.getData()[0]);
		Assert.assertEquals(5,w.getData()[1]);
		Assert.assertEquals(4,w.getData()[2]);
		Assert.assertEquals(3,w.getData()[3]);
		Assert.assertEquals(2,w.getData()[4]);
	}
}
