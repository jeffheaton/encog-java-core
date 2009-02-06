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
import org.encog.nlp.EncogNLP;
import org.encog.nlp.lexicon.EncogLexicon;
import org.encog.nlp.memory.RomMemory;
import org.encog.util.orm.ORMSession;
import org.encog.util.orm.SessionManager;


/**
 *
 * @author  jheaton
 */
public class TestNLPScript extends TestCase {
	private EncogNLP context;
	
	
	static final String DRIVER = "com.mysql.jdbc.Driver";
    static final String URL = "jdbc:mysql://localhost/lex2";
    static final String UID = "root";
    static final String PWD = "";
    static final String DIALECT = "org.hibernate.dialect.MySQLDialect";
	
    
    /** Creates a new instance of UnitTest 
     * @throws IOException */
    public TestNLPScript() throws IOException { 
    	
    	SessionManager manager = new SessionManager(DRIVER, URL, UID, PWD, DIALECT);
    	
    	//SessionManager.getInstance().initHSQL("/Users/jeff/Data/encog");
		ORMSession session = manager.openSession();
    	
    	context = new EncogNLP(session);
    	
    	
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
            System.out.println(line);
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
                    	System.out.println("O:"+s);
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
