/*
 * UnitTest.java
 *
 * Created on May 26, 2004, 8:27 PM
 */

package org.encog.nlp.unit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.encog.nlp.Context;
import org.encog.nlp.memory.RomMemory;


/**
 *
 * @author  jheaton
 */
public class UnitTest {
	private Context context;
	private final static String basePath = "c:\\shared\\workspace\\NLP\\";
    
    /** Creates a new instance of UnitTest */
    public UnitTest() { 
    	context = new Context();
    	context.init(basePath);
        RomMemory.load(context.getMemory());       
    }
    
    public void test(String filename)
    throws IOException,FileNotFoundException
    {
        String line = "";
        String expect = null;
        
        FileReader f;// the actual file stream
        BufferedReader r;// used to read the file line by line

        f = new FileReader( new File(filename) );
        r = new BufferedReader(f);
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
                        if( !s.equalsIgnoreCase(expect) )
                            System.out.println("Unit test failed:" + rest + "||" + s );
                        else 
                            System.out.println("Success:" + rest + "||" + s);
                    }
                    expect = null;
                }
                else if(command.equalsIgnoreCase("expect") )
                {
                    expect = rest;
                }
            }
        } while(line!=null);                             
        context.getMemory().save("c:\\memory.txt");
    }    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try
        {
        UnitTest unit = new UnitTest();
        unit.test("c:\\shared\\workspace\\NLP\\unit.txt");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
}
