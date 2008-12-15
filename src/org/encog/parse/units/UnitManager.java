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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.encog.parse.Parse;
import org.encog.parse.recognize.Recognize;
import org.encog.parse.recognize.RecognizeElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class UnitManager {

  public final static String BASE_WEIGHT = "base-weight";

  private Collection<UnitConversion> conversions = new ArrayList<UnitConversion>();
  private Map<String,String> aliases = new HashMap<String,String>();

  private void loadConvert(Element inputNode)
  {
    //   <convert from="lb" to "base-weight" pre="0" post="0" ratio="4536"/>

    String from = inputNode.getAttribute("from");
    String to = inputNode.getAttribute("to");
    String pre = inputNode.getAttribute("pre");
    String post = inputNode.getAttribute("post");
    String ratio = inputNode.getAttribute("ratio");

    double numPre = Double.parseDouble(pre);
    double numPost = Double.parseDouble(post);
    double numRatio = Double.parseDouble(ratio);

    UnitConversion convert = new UnitConversion(from,to,numPre,numPost,numRatio);
    conversions.add(convert);
  }

  private void loadAlias(Element inputNode)
  {
//System.out.println("load alias");
    String from = inputNode.getAttribute("from");
    String to = inputNode.getAttribute("to");
    aliases.put(from.toLowerCase(),to);
  }


  private void loadConversions(Element list)
  {
    for ( Node child = list.getFirstChild(); child != null;
        child = child.getNextSibling() ) {
      if ( !(child instanceof Element ) )
        continue;
      Element node = (Element)child;

      if (node.getNodeName().equals("convert") )
        loadConvert(node);
    }
  }

  private void loadAliases(Element list)
  {
    for ( Node child = list.getFirstChild(); child != null;
        child = child.getNextSibling() ) {
      if ( !(child instanceof Element ) )
        continue;
      Element node = (Element)child;

      if (node.getNodeName().equals("alias") )
        loadAlias(node);
    }
  }




  public void load(InputStream in)
  {
    try {
      // setup the XML parser stuff
      DocumentBuilderFactory dbf =
      DocumentBuilderFactory.newInstance();

      DocumentBuilder db = null;
      db = dbf.newDocumentBuilder();

      Document doc = null;
      doc = db.parse(in);
      Element memory = doc.getDocumentElement();


      // read in the data

      // first count the number of training sets

      for ( Node child = memory.getFirstChild(); child != null;
          child = child.getNextSibling() ) {
        if ( !(child instanceof Element ) )
          continue;
        Element node = (Element)child;

        if (node.getNodeName().equals("conversions") )
          loadConversions(node);
        else if (node.getNodeName().equals("aliases") )
          loadAliases(node);
      }

    } catch ( javax.xml.parsers.ParserConfigurationException e ) {
      System.out.println(e);

    } catch ( org.xml.sax.SAXException e ) {
      System.out.println(e);

    } catch ( java.io.IOException e ) {
      System.out.println(e);

    }
  }

  public void load(String name)
  {
    try {
      InputStream is = new FileInputStream(new File(name));
      load(is);
      is.close();
    } catch ( java.io.FileNotFoundException e ) {

    } catch ( java.io.IOException e ) {

    }
  }

  public String resolveAlias(String in)
  {
    String base = aliases.get(in.toLowerCase());
    if(base==null)
      return in;
    else
      return base;
  }

  public double convert(String from,String to,double input)
  {
    from = resolveAlias(from);
    to = resolveAlias(to);

    for(UnitConversion convert: this.conversions)
    {
      if( convert.getFrom().equals(from) && convert.getTo().equals(to) )
        return convert.convert(input);
    }
    return 0;
  }

  public void createRecognizers(Parse parse)
  {
    Map<Object,Object> map = new HashMap<Object,Object>();// put everything in a map to eliminate duplicates

    // create the recognizers
    Recognize weightRecognize = parse.getTemplate().createRecognizer("weightUnit");
    RecognizeElement weightElement = weightRecognize.createElement(RecognizeElement.ALLOW_ONE);

    // get all of the units
    for(UnitConversion unit: this.conversions)
    {
      map.put(unit.getFrom(),null);
      map.put(unit.getTo(),null);          
    }

    // get all of the aliases
    for(Map.Entry<String,String> entry: this.aliases.entrySet())
    {
      map.put(entry.getKey(),null);
      map.put(entry.getValue(),null);  
    }

    // now add all of the units to the correct recognizers
    for(Map.Entry<String,String> entry: this.aliases.entrySet())
    {     
      weightElement.addAcceptedSignal("word",entry.getKey().toString());      
    }
  }
}
