package org.encog.parse.signal;

import java.util.*;

public class Signal {
  private char value = 0;
  private List<Signal> data = new ArrayList<Signal>();
  private List<String> types = new ArrayList<String>();
  private String name;
  private boolean ignore;
  private boolean delta;

  public Signal()
  {
  }

  public Signal(char value)
  {
    this.value = value; 
  }

  public Signal(String value)
  {
    for (int i=0;i<value.length();i++) {
      Signal signal = new Signal(value.charAt(i));
      data.add(signal);
    }
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return this.name;
  }

  public List getData()
  {
    return data;
  }

  public int size()
  {
    return data.size();
  }

  public void clear()
  {
    data.clear();
  }

  public char getValue()
  {
    return value;
  }

  public boolean isChar()
  {
    return(value!=0);
  }

  public Signal pack(int begin,int end,String type)
  throws InstantiationException,IllegalAccessException
  {
    return pack(begin,end,type,Signal.class);
  }

  public Signal pack(int begin,int end,String type,Class signalClass)
  throws InstantiationException,IllegalAccessException
  {
    delta = true;
    Object array[] = data.toArray();

    // create the new recognized signal of the correct type, 
    // defaults to Signal if none specified
    Signal temp = (Signal)signalClass.newInstance(); 
    for (int i=0;i<array.length;i++) {
      Signal signal = (Signal)array[i];
      if ( (i>=begin) && (i<end) ) {
        data.remove(signal);
        temp.add(signal);  
      }
    }
    temp.addType(type);

    // now allow the new type to parse internally.
    // if this new type is just a Signal, nothing will
    // happen, as Signal.parse does nothing.

    temp.parse();

    // now insert the newly created subsignal into
    // this signal.

    if (begin>data.size())
      data.add(temp);
    else
      data.add(begin,temp);

    return temp;
  }

  public void insert(int begin,Signal signal)
  {
    delta = true;
    data.add(begin,signal);
  }

  public void cut(int begin,int end)
  {
    Object array[] = data.toArray();
    delta = true;

    for (int i=0;i<array.length;i++) {
      Signal signal = (Signal)array[i];
      if ( (i>=begin) && (i<end) )
        data.remove(signal);
    }
  }


  public String dump()
  {
    String result = "";

    result+="[";

    if (types.size()>0) {
      Iterator typesIterator = types.iterator();
      while (typesIterator.hasNext()) {
        String type = (String)typesIterator.next();
        result+=type;
        if (typesIterator.hasNext())
          result+=",";
      }
      result+=":";
    }

    if (!isChar()) {
    	      
      if (data.size()>1)
        result+="{";
      
      for(Signal signal:data ) {
        result+=signal.dump();
      }
      if (data.size()>1)
        result+="}";
    } else
      result+=value;
    result+="]";
    return result;
  }

  public void add(Signal signal)
  {
    data.add(signal);
    delta = true;
  }

  public void addType(String type)
  {
    types.add(type);
    delta = true;
  }

  public boolean hasType(String str)
  {
	  for(String type:types) {
      if ( type.equalsIgnoreCase(str) )
        return true;
    }
    return false;
  }

  public Collection<Signal> findByType(String type)
  {
    Collection<Signal> result = new ArrayList<Signal>();
    
    for(Signal signal:data) {
      if (signal.hasType(type))
        result.add(signal);
    }
    return result;
  }

  public Signal findByType(String type,int count)
  {
	  for(Signal signal:data) {

      if (signal.hasType(type)) {
        if (count==0)
          return signal;
        count--;
      }

      Signal signal2 = signal.findByType(name,count);
      if (signal2!=null)
        return signal2;
    }
    return null;
  }

  public String toString()
  {
    String result = "";

    if (isChar()) {
      return ""+getValue();
    } else {
    	for(Signal signal:data) {    	
        result+=signal.toString();
      }

    }
    return result;
  }

  public boolean getIgnore()
  {
    return ignore;
  }

  public void setIgnore(boolean ignore)
  {
    this.ignore = ignore;
  }

  public void parse()
  {
  }

  public void resetDelta()
  {
    delta = false;
  }

  public boolean getDelta()
  {
    return delta;
  }

}
