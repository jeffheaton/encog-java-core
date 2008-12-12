package org.encog.parse.recognize;

import java.util.ArrayList;
import java.util.Collection;

import org.encog.parse.AcceptedSignal;
import org.encog.parse.signal.Signal;

public class RecognizeElement {
  public static final int ALLOW_ONE = 1;
  public static final int ALLOW_MULTIPLE = 2;

  private String charsKnown = "";
  private int allow = ALLOW_ONE;
  private String name = null;
  private Collection<AcceptedSignal> recognizedSignals = new ArrayList<AcceptedSignal>();

  RecognizeElement()
  {
  }

  public void addAcceptedSignal(String type,String value)
  {
    AcceptedSignal accepted = new AcceptedSignal(type,value);
    recognizedSignals.add(accepted);
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return this.name;
  }

  RecognizeElement(int allow)
  {
    this.allow = allow;
  }

  public int getAllow()
  {
    return allow;
  }


  public void addRange(char low,char high)
  {
    for (char ch=low;ch<=high;ch++)
      charsKnown+=ch;
  }

  public void add(char ch)
  {
    charsKnown+=ch;
  }


  public void setType(String type)
  {
  }

  public boolean recognize(Signal signal)
  {
    if (!signal.isChar()) {
    	for(AcceptedSignal accepted:recognizedSignals) {
        if ( signal.hasType(accepted.getType()) ) {
          if (accepted.getValue()==null)
            return true;
          if (accepted.getValue().equals(signal.toString()))
            return true;
        }
      }
      return false;
    } else
      return( charsKnown.indexOf(signal.getValue())!=-1 );       
  }
}
