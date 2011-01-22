/*
 * Encog(tm) Core v3.0 Unit Test - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.persist.location;

import java.io.InputStream;

import org.encog.util.file.Directory;
import org.junit.Assert;
import org.junit.Test;

public class TestResourcePersistence {
	
	@Test
	public void testResource() throws Exception
	{
		ResourcePersistence location = new ResourcePersistence("org/encog/data/testresource");
		InputStream is = location.createInputStream();
		String str = Directory.readStream(is);
		Assert.assertEquals("This is a test resource",str);
	}
}
