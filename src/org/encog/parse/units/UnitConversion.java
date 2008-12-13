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
package org.encog.parse.units;

public class UnitConversion
{
  private String from;
  private String to;
  private double addPreRatio;
  private double addPostRatio;
  private double ratio;

  public UnitConversion(String from,String to,double addPreRatio,double addPostRatio,double ratio)
  {
    this.from = from;
    this.to = to;
    this.addPreRatio = addPreRatio;
    this.addPostRatio = addPostRatio;
    this.ratio = ratio;
  }

  public String getFrom()
  {
    return this.from;
  }

  public void setFrom(String from)
  {
    this.from = from;
  }

  public String getTo()
  {
    return this.to;
  }

  public void setTo(String to)
  {
    this.to = to;
  }

  public void setAddPreRatio(double addPreRatio)
  {
    this.addPreRatio = addPreRatio;
  }

  public double getAddPreRatio()
  {
    return this.addPreRatio;
  }


  public void setAddPostRatio(double addPostRatio)
  {
    this.addPostRatio = addPostRatio;
  }

  public double getAddPostRatio()
  {
    return this.addPostRatio;
  }

  public void setRatio(double ratio)
  {
    this.ratio = ratio;
  }

  public double getRatio()
  {
    return this.ratio;
  }

  public double convert(double input)
  {
    return(((input+addPreRatio)*ratio)+addPostRatio);
  }



}
