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
package org.encog.util;

import java.util.Date;

import junit.framework.TestCase;

import org.encog.util.time.DateUtil;
import org.encog.util.time.TimeSpan;
import org.encog.util.time.TimeUnit;

public class TestTimeSpan extends TestCase {
	public void testDiffMonths()
	{
		Date date1 = DateUtil.createDate(1, 1, 2008);
		Date date2 = DateUtil.createDate(6, 1, 2008);
		TestCase.assertEquals(5, new TimeSpan(date1,date2).getSpan(TimeUnit.MONTHS));
		date1 = DateUtil.createDate(12, 1, 2008);
		date2 = DateUtil.createDate(1, 1, 2009);
		TestCase.assertEquals(1, new TimeSpan(date1,date2).getSpan(TimeUnit.MONTHS));
		date1 = DateUtil.createDate(12, 31, 2008);
		date2 = DateUtil.createDate(1, 1, 2009);
		TestCase.assertEquals(1, new TimeSpan(date1,date2).getSpan(TimeUnit.MONTHS));
	}
}
