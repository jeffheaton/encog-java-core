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
