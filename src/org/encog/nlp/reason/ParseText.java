/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
package org.encog.nlp.reason;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.encog.nlp.EncogNLP;
import org.encog.nlp.NLPError;
import org.encog.parse.signal.Signal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * 
 * Note: This class is part of the Encog Natural Language Processing(NLP)
 * package.  This package is still under heavy construction, and will not 
 * be considered stable until Encog 3.0.
 * 
 * @author jheaton
 *
 */
abstract public class ParseText {   
    private EncogNLP context;
    
    /**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /** Creates a new instance of ParseText */
    public ParseText (EncogNLP context) {
    	this.context = context;
    }
    
    abstract void parseWord(String word);
    abstract public String parse(String input);
    
    public void loadFile(String filename)
    {
    	try
    	{
        String line;
        FileReader f;
        BufferedReader r;
        
        f = new FileReader( new File(filename) );
        r = new BufferedReader(f);
        do
        {
            line = r.readLine();
            if( line!=null )
            {
                parse(line);
            }
        } while( line!=null );
        r.close();
        f.close();
    	}
    	catch(IOException e)
    	{
    		if( logger.isErrorEnabled())
    		{
    			logger.error("Exception",e);
    		}
    		throw new NLPError(e);
    	}
    }     
    
    void splitWords(String input,List<String> words)
    {
    	words.clear();
    	Signal signal = context.getParse().parse(input);   
    	//System.out.println( signal.dump() );
    	Collection<Signal> list = signal.findByType("word");
    	for(Signal s:list)
    	{
    		words.add(s.toString());
    	}    	
    }    
}
