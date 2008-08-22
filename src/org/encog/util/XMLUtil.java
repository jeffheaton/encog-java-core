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
package org.encog.util;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;



public class XMLUtil {
  private XMLUtil()
  {
  }

  public static Element findElement(Element e,String find)
  {
    for ( Node child = e.getFirstChild(); child != null;
        child = child.getNextSibling() ) {
      if ( ! (child instanceof Element) )
        continue;
      Element el = (Element)child;
      if ( el.getNodeName().equals(find) )
        return el;
    }
    return null;
  }

  public static String findElementAsString(Element e,String find)
  {
    Element el = findElement(e,find);
    
    if( el==null )
    	return null;

    for ( Node child = el.getFirstChild(); child != null;
        child = child.getNextSibling() ) {
      if ( !(child instanceof Text ) )
        continue;
      return child.getNodeValue();
    }
    return null;
  }

  public static long findElementAsLong(Element e,String find,long def)
  {
    String str = findElementAsString(e,find);
    if ( str==null )
      return def;
    try {
      return Long.parseLong(str);
    } catch ( NumberFormatException ex ) {
      return def;
    }
  }

  public static int findElementAsInt(Element e,String find,int def)
  {
    String str = findElementAsString(e,find);
    if ( str==null )
      return def;
    try {
      return Integer.parseInt(str);
    } catch ( NumberFormatException ex ) {
      return def;
    }
  }

  public static String getElementValue(Element e)
  {
    for ( Node child = e.getFirstChild(); child != null;
        child = child.getNextSibling() ) {
      if ( !(child instanceof Text ) )
        continue;
      return child.getNodeValue();
    }
    return null;
  }

  public static Node createProperty(Document doc,String name,String value)
  {
    Node n = doc.createElement(name);
    n.appendChild(doc.createTextNode(value));
    return n;
  }

  public static void addAttribute(Node e,String name,String value)
  {
    Attr attr = e.getOwnerDocument().createAttribute(name);
    attr.setValue(value);
    e.getAttributes().setNamedItem(attr);
  }



}
