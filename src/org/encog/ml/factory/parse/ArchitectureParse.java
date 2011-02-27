package org.encog.ml.factory.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.util.SimpleParser;


public class ArchitectureParse {
	
	public static ArchitectureLayer parseLayer(String line, int defaultValue) {
		ArchitectureLayer layer = new ArchitectureLayer();
		
		String check = line.trim().toUpperCase();
		
		// first check for bias
		if( check.endsWith(":B")) {
			check = check.substring(0,check.length()-3);
			layer.setBias(true);
		}
		
		// see if simple number
		try {
			layer.setCount( Integer.parseInt(check) );
			if( layer.getCount()<0) {
				throw new EncogError("Count cannot be less than zero.");
			}
		} catch(NumberFormatException f) {
			// ignore, its just a different format
		}
		
		// see if it is a default
		if( "?".equals(check) ) {
			if( defaultValue<0 ) {
				throw new EncogError("Default (?) in an invalid location.");
			}
			else {
				layer.setCount(defaultValue);
				return layer;
			}
		}
		
		// single item, no function
		int startIndex = check.indexOf('(');
		int endIndex = check.lastIndexOf(')');
		if( startIndex==-1 ) {
			layer.setName(check);
			return layer;
		}
		
		// function
		if( endIndex==-1) {
			throw new EncogError("Illegal parentheses.");			
		}
		
		layer.setName(check.substring(0,startIndex).trim());
		
		String paramStr = check.substring(startIndex+1,endIndex-1);
		Map<String,String> params = parseParams(paramStr);
		layer.getParams().putAll(params);
		return layer;
	}
	
	public static Map<String,String> parseParams(String line)
	{
		Map<String,String> result = new HashMap<String,String>();
		
		SimpleParser parser = new SimpleParser(line);
		
		while (!parser.eol())
        {
            String name = parseName(parser).toUpperCase();

            parser.eatWhiteSpace();
            if (!parser.lookAhead("=", false))
                throw new EncogError("Missing equals(=) operator.");
            else
                parser.advance();

            String value = parseValue(parser);

            result.put(name, value);

            if (!parser.parseThroughComma())
                break;
        }
		
		return result;
	}
	
	private static String parseName(SimpleParser parser)
	{
		StringBuilder result = new StringBuilder();
        parser.eatWhiteSpace();
        while (parser.isIdentifier())
        {
            result.append(parser.readChar());
        }
        return result.toString();
	}
	
	public static List<String> parseLayers(String line)
	{
		List<String> result = new ArrayList<String>();
		
		int base = 0;
		boolean done = false;
		
		do {
			String part;
			int index = line.indexOf("->",base );
			if( index!=-1 ) {
				part = line.substring(base,index).trim();
				base = index+2;
			} else {
				part = line.substring(base).trim();
				done = true;
			}
			
			boolean bias = part.endsWith("b");
			if( bias ) {
				part = part.substring(0, part.length()-1);
			}
			
			result.add(part);

		} while(!done);
		
		return result;
	}
	
	private static String parseValue(SimpleParser parser)
    {
        boolean quoted = false;
        StringBuilder str = new StringBuilder();

        parser.eatWhiteSpace();

        if (parser.peek() == '\"')
        {
            quoted = true;
            parser.advance();
        }

        while (!parser.eol())
        {
            if (parser.peek() == '\"')
            {
                if (quoted)
                {
                    parser.advance();
                    if (parser.peek() == '\"')
                    {
                        str.append(parser.readChar());
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    str.append(parser.readChar());
                }
            }
            else if (!quoted && (parser.isWhiteSpace() || parser.peek() == ','))
            {
                break;
            }
            else
            {
                str.append(parser.readChar());
            }
        }
        return str.toString();

    }
}
