/*
 * Encog Artificial Intelligence Framework v1.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
package org.encog.parse.signal.units;

import java.text.NumberFormat;

import org.encog.parse.Parse;
import org.encog.parse.signal.Signal;
import org.encog.parse.units.UnitManager;


public class Weight extends Signal
{
  final static int 
    UNIT_MASS = 1,
    UNIT_LENGTH = 2,
    UNIT_TEMPERATURE= 3;

  private double number;
  public void parse()
  {
    Signal num = findByType("number",0);
    try
    {
    number = Double.parseDouble(num.toString());
    }
    catch(NumberFormatException e)
    {
      number = 0;
    }


    Signal unit = findByType("weightUnit",0);

    number = Parse.getUnitManager().convert(unit.toString(),UnitManager.BASE_WEIGHT,number);
    
  }

  public String toString()
  {
    double oz = Parse.getUnitManager().convert(UnitManager.BASE_WEIGHT,"oz",number);
    double g = Parse.getUnitManager().convert(UnitManager.BASE_WEIGHT,"g",number);

    NumberFormat nf = NumberFormat.getNumberInstance();
    


    return "" + nf.format(oz) + "oz(" + nf.format(g) + "gram" + ((g>=2)?"s":"") + ")";
  }
}
