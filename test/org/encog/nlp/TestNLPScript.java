/*
 * UnitTest.java
 *
 * Created on May 26, 2004, 8:27 PM
 */

package org.encog.nlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.EncogError;
import org.encog.nlp.Context;
import org.encog.nlp.memory.RomMemory;


/**
 *
 * @author  jheaton
 */
public class TestNLPScript extends TestCase {
	private Context context;
    
    /** Creates a new instance of UnitTest 
     * @throws IOException */
    public TestNLPScript() throws IOException { 
    	context = new Context();
    	context.init();
        RomMemory.load(context.getMemory());       
    }
    
    public void testCase() throws Exception
    {
    	final String resource = "org/encog/nlp/script.txt";
    	ClassLoader loader = this.getClass().getClassLoader();
		InputStream is = loader.getResourceAsStream (resource);
		
		if( is==null )
			throw new EncogError("Can't read resource: " + resource );
    	
        String line = "";
        String expect = null;
        
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        do
        {
            line = r.readLine();
            if( line!=null )
            {
                int i = line.indexOf(':');
                String command = line.substring(0,i).trim();
                String rest = line.substring(i+1).trim();
                if( command.equalsIgnoreCase("submit") )
                {
                    String s = context.getEvaluate().parse(rest);
                    if( expect!=null ) 
                    {
                    	Assert.assertTrue(s.equalsIgnoreCase(expect));
                    }
                    expect = null;
                }
                else if(command.equalsIgnoreCase("expect") )
                {
                    expect = rest;
                }
            }
        } while(line!=null);   
        
        is.close();
    }    
        
}
