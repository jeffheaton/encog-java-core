package org.encog.parse;

import java.util.*;
import javax.xml.parsers.*;

import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.parse.recognize.Recognize;
import org.encog.parse.recognize.RecognizeElement;
import org.encog.parse.signal.Signal;
import org.encog.parse.units.UnitManager;
import org.w3c.dom.*;

import java.io.*;
import java.net.URL;


public class Parse {  
  private static UnitManager unitManager;
  private ParseTemplate template;

  public Signal parseFile(String name)
  throws IOException,FileNotFoundException
  {
    FileReader fileReader = new FileReader(name);
    BufferedReader bufferedReader = new BufferedReader(fileReader);

    String contents="";
    String line="";

    while ( (line=bufferedReader.readLine())!=null )
      contents+=line+"\r\n";

    bufferedReader.close();
    fileReader.close();
    return parse(contents);
  }

  public static void setUnitMananger(UnitManager unitManager)
  {
    Parse.unitManager = unitManager;
  }

  public static UnitManager getUnitManager()
  {
    return Parse.unitManager;
  }

  private boolean parseIteration(Signal input)
  {
    boolean changed = false;

    for(Recognize recognize:template.getRecognizers()) {
      if(recognize.recognize(input))
        changed = true;
    }
    return changed;
  }

  public Signal parse(String input)
  {
    Signal result = new Signal(input+" ");
    result.resetDelta();

    do
    {
      result.resetDelta();
      parseIteration(result);
    }
    while( result.getDelta() );
    return result;
  }

/**
 * @return the template
 */
public ParseTemplate getTemplate() {
	return template;
}

public void load() throws IOException {
	EncogPersistedCollection encog = new EncogPersistedCollection();
	encog.loadResource("org/encog/data/template.eg");
	this.template = (ParseTemplate) encog.find("parse-native");	
}




}
