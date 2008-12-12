package org.encog.parse;

public class AcceptedSignal {
  private String type;
  private String value;

  public AcceptedSignal(String type,String value)
  {
    this.type = type;
    this.value = value;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public void setValue(String value)
  {
    this.type = value;
  }

  public String getType()
  {
    return this.type;
  }

  public String getValue()
  {
    return this.value;
  }

}
