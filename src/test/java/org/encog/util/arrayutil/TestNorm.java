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

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;

public class TestNorm extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
	
	public void testRoundTrip1() {
		NormalizedField field = new NormalizedField(NormalizationAction.Normalize, null, 10, 0, -1, 1);
		double d = 5;
		double d2= field.normalize(d);
		double d3 = field.deNormalize(d2);
		
		Assert.assertTrue( ((int)d) == ((int)d3) );

	}
	
	public void testRoundTrip2() {
		NormalizedField field = new NormalizedField(NormalizationAction.Normalize, null, 10, -10, -1, 1);
		double d = 5;
		double d2= field.normalize(d);
		double d3 = field.deNormalize(d2);

		Assert.assertTrue( ((int)d) == ((int)d3) );
	}
	
	public void testSerialize() throws IOException, ClassNotFoundException {
		NormalizedField field = new NormalizedField(NormalizationAction.Normalize, null, 10, -10, -1, 1);
		SerializeObject.save(SERIAL_FILENAME, field);
		NormalizedField field2 = (NormalizedField)SerializeObject.load(SERIAL_FILENAME);
		Assert.assertEquals(field.getAction(), field2.getAction());
		
	}
}
