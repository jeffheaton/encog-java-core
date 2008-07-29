package org.encog.neural.data.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler  extends DefaultHandler {

	private String tag;
	
	public void startDocument ()
    {
	tag = "";
    }

    public void endDocument ()
    {
	System.out.println("End document");
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes)
    {
    	if( tag.length()>0 )
    		tag+='.';
    	tag+=localName;
    	System.out.println(tag);
    }
    
    public void endElement(String uri, String localName, String qName)
    {
    	int index = tag.lastIndexOf('.');
    	if( index!=-1 )
    		tag = tag.substring(0,index);
    }
    
    public void characters (char ch[], int start, int length)
    {
    	String str = new String(ch,start,length).trim();
    	if( str.length()>0 )
    	{
//    		getCurrentLoader().text(str);
    	}
    }
	
}
