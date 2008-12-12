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
