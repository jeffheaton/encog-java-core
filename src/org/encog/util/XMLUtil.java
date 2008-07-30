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
      return 0;
    try {
      return Long.parseLong(str);
    } catch ( java.lang.Exception ex ) {
      return 0;
    }
  }

  public static int findElementAsInt(Element e,String find,long def)
  {
    String str = findElementAsString(e,find);
    if ( str==null )
      return 0;
    try {
      return Integer.parseInt(str);
    } catch ( java.lang.Exception ex ) {
      return 0;
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
