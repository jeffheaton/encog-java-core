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
