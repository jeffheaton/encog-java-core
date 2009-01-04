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
  
  public String toString()
  {
	  StringBuilder result = new StringBuilder();
	  result.append("[RecognizeElement:");
	  result.append(this.name);
	  result.append(']');
	  return result.toString();
  }
}
