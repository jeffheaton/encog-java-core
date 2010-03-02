/*
 * Encog(tm) Core v2.4
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

package org.encog.util;

import java.util.Date;

import org.encog.util.time.DateUtil;
import org.encog.util.time.TimeSpan;
import org.encog.util.time.TimeUnit;

import junit.framework.TestCase;

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
