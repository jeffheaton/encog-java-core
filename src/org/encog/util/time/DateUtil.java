/*
  * Encog Neural Network and Bot Library for Java v1.x
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * 
  * Copyright 2008, Heaton Research Inc., and individual contributors.
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

package org.encog.util.time;

import java.util.Calendar;
import java.util.Date;

/**
 * Simple date utility class.  Used mainly to create Data objects.
 * @author jheaton
 *
 */
public class DateUtil {
	
	/**
	 * Private constructor.
	 */
	private DateUtil()
	{		
	}
	
	/**
	 * Construct a date. 
	 * @param month The month, January is 1.
	 * @param day The day.
	 * @param year The year.
	 * @return The newly created Date object.
	 */
	public static Date createDate(int month,int day,int year)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}
}
