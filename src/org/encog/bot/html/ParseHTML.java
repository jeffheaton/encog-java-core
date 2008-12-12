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
package org.encog.bot.html;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ParseHTML
{

  /*
   * A mapping of certain HTML encoded values(i.e. &nbsp;)
   * to their actual character values.
   */
  private static Map<String, Character> charMap;

  /**
   * The stream that we are parsing from.
   */
  private PeekableInputStream source;

  /**
   * The current HTML tag. Access this property if the read
   * function returns 0.
   */
  private HTMLTag tag = new HTMLTag();

  private String lockedEndTag;

  /**
   * The constructor should be passed an InputStream that we
   * will parse from.
   * 
   * @param is
   *          An InputStream to parse from.
   */
  public ParseHTML(InputStream is)
  {
    this.source = new PeekableInputStream(is);

    if (charMap == null)
    {
      charMap = new HashMap<String, Character>();
      charMap.put("nbsp", ' ');
      charMap.put("lt", '<');
      charMap.put("gt", '>');
      charMap.put("amp", '&');
      charMap.put("quot", '\"');
      charMap.put("bull", (char) 149);
      charMap.put("trade", (char) 129);
    }
  }

  /**
   * Return the last tag found, this is normally called just
   * after the read function returns a zero.
   * 
   * @return The last HTML tag found.
   */
  public HTMLTag getTag()
  {
    return this.tag;
  }

  /**
   * Read a single character from the HTML source, if this
   * function returns zero(0) then you should call getTag to
   * see what tag was found. Otherwise the value returned is
   * simply the next character found.
   * 
   * @return The character read, or zero if there is an HTML
   *         tag. If zero is returned, then call getTag to
   *         get the next tag.
   * 
   * @throws IOException
   *           If an error occurs while reading.
   */
  public int read() throws IOException
  {
    // handle locked end tag
    if (this.lockedEndTag != null)
    {
      if (peekEndTag(this.lockedEndTag))
      {
        this.lockedEndTag = null;
      } else
      {
        return this.source.read();
      }
    }

    // look for next tag
    if (this.source.peek() == '<')
    {
      parseTag();
      if (this.tag.getType()==HTMLTag.Type.BEGIN
          && (this.tag.getName().equalsIgnoreCase("script") || this.tag
              .getName().equalsIgnoreCase("style")))
      {
        this.lockedEndTag = this.tag.getName().toLowerCase();
      }
      return 0;
    } else if (this.source.peek() == '&')
    {
      return parseSpecialCharacter();
    } else
    {
      return (this.source.read());
    }
  }

  /**
   * Convert the HTML document back to a string.
   */
  @Override
  public String toString()
  {
    try
    {
      StringBuilder result = new StringBuilder();

      int ch = 0;
      StringBuilder text = new StringBuilder();
      do
      {
        ch = read();
        if (ch == 0)
        {
          if (text.length() > 0)
          {
            result.append("Text:" + text.toString() + "\n");
            text.setLength(0);
          }
          result.append("Tag:" + getTag() + "\n");
        } else if (ch != -1)
        {
          text.append((char) ch);
        }
      } while (ch != -1);
      if (text.length() > 0)
      {
        result.append("Text:" + text.toString().trim()+"\n");
      }
      return result.toString();
    } catch (IOException e)
    {
      return "[IO Error]";
    }
  }

  /**
   * Parse any special characters(i.e. &nbsp);
   * 
   * @return The character that was parsed.
   * @throws IOException
   *           If a read error occurs
   */
  private char parseSpecialCharacter() throws IOException
  {
    char result = (char) this.source.read();
    int advanceBy = 0;

    // is there a special character?
    if (result == '&')
    {
      int ch = 0;
      StringBuilder buffer = new StringBuilder();

      // loop through and read special character
      do
      {
        ch = this.source.peek(advanceBy++);
        if ((ch != '&') && (ch != ';') && !Character.isWhitespace(ch))
        {
          buffer.append((char) ch);
        }

      } while ((ch != ';') && (ch != -1) && !Character.isWhitespace(ch));

      String b = buffer.toString().trim().toLowerCase();

      // did we find a special character?
      if (b.length() > 0)
      {
        if (b.charAt(0) == '#')
        {
          try
          {
            result = (char) Integer.parseInt(b.substring(1));
          } catch (NumberFormatException e)
          {
            advanceBy = 0;
          }
        } else
        {
          if (charMap.containsKey(b))
          {
            result = charMap.get(b);
          } else
          {
            advanceBy = 0;
          }
        }
      } else
      {
        advanceBy = 0;
      }
    }

    while (advanceBy > 0)
    {
      read();
      advanceBy--;
    }

    return result;
  }

  /**
   * Check to see if the ending tag is present.
   * @param name The type of end tag being sought.
   * @return True if the ending tag was found.
   * @throws IOException Thrown if an IO error occurs.
   */
  private boolean peekEndTag(String name) throws IOException
  {
    int i = 0;

    // pass any whitespace
    while ((this.source.peek(i) != -1)
        && Character.isWhitespace(this.source.peek(i)))
    {
      i++;
    }

    // is a tag beginning
    if (this.source.peek(i) != '<')
    {
      return false;
    } else
    {
      i++;
    }

    // pass any whitespace
    while ((this.source.peek(i) != -1)
        && Character.isWhitespace(this.source.peek(i)))
    {
      i++;
    }

    // is it an end tag
    if (this.source.peek(i) != '/')
    {
      return false;
    } else
    {
      i++;
    }

    // pass any whitespace
    while ((this.source.peek(i) != -1)
        && Character.isWhitespace(this.source.peek(i)))
    {
      i++;
    }

    // does the name match
    for (int j = 0; j < name.length(); j++)
    {
      if (Character.toLowerCase(this.source.peek(i)) != Character
          .toLowerCase(name.charAt(j)))
      {
        return false;
      }
      i++;
    }

    return true;
  }

  /**
   * Remove any whitespace characters that are next in the
   * InputStream.
   * 
   * @throws IOException
   *           If an I/O exception occurs.
   */
  protected void eatWhitespace() throws IOException
  {
    while (Character.isWhitespace((char) this.source.peek()))
    {
      this.source.read();
    }
  }

  /**
   * Parse an attribute name, if one is present.
   * 
   * @throws IOException
   *           If an I/O exception occurs.
   */
  protected String parseAttributeName() throws IOException
  {
    eatWhitespace();

    if ("\"\'".indexOf(this.source.peek()) == -1)
    {
      StringBuilder buffer = new StringBuilder();
      while (!Character.isWhitespace(this.source.peek())
          && (this.source.peek() != '=') && (this.source.peek() != '>')
          && (this.source.peek() != -1))
      {
        int ch = parseSpecialCharacter();
        buffer.append((char) ch);
      }
      return buffer.toString();
    } else
    {
      return (parseString());
    }
  }

  /**
   * Called to parse a double or single quote string.
   * 
   * @return The string parsed.
   * @throws IOException
   *           If an I/O exception occurs.
   */
  protected String parseString() throws IOException
  {
    StringBuilder result = new StringBuilder();
    eatWhitespace();
    if ("\"\'".indexOf(this.source.peek()) != -1)
    {
      int delim = this.source.read();
      while ((this.source.peek() != delim) && (this.source.peek() != -1))
      {
        if (result.length() > 1000)
        {
          break;
        }
        int ch = parseSpecialCharacter();
        if ((ch == 13) || (ch == 10))
        {
          continue;
        }
        result.append((char) ch);
      }
      if ("\"\'".indexOf(this.source.peek()) != -1)
      {
        this.source.read();
      }
    } else
    {
      while (!Character.isWhitespace(this.source.peek())
          && (this.source.peek() != -1) && (this.source.peek() != '>'))
      {
        result.append(parseSpecialCharacter());
      }
    }

    return result.toString();
  }

  /**
   * Called when a tag is detected. This method will parse
   * the tag.
   * 
   * @throws IOException
   *           If an I/O exception occurs.
   */
  protected void parseTag() throws IOException
  {
	this.tag = new HTMLTag();
    StringBuilder tagName = new StringBuilder();

    this.source.read();

    // Is it a comment?
    if ((this.source.peek(0) == '!') && (this.source.peek(1) == '-')
        && (this.source.peek(2) == '-'))
    {
      while (this.source.peek() != -1)
      {
        if ((this.source.peek(0) == '-') && (this.source.peek(1) == '-')
            && (this.source.peek(2) == '>'))
        {
          break;
        }
        if (this.source.peek() != '\r')
        {
          tagName.append((char) this.source.peek());
        }
        this.source.read();
      }
      tagName.append("--");
      this.source.read();
      this.source.read();
      this.source.read();
      return;
    }

    // Find the tag name
    while (this.source.peek() != -1)
    {
      // if this is the end of the tag, then stop
      if (Character.isWhitespace((char) this.source.peek())
          || (this.source.peek() == '>') )
      {
        break;
      }
      
      // if this is both a begin and end tag then stop
      if( tagName.length()>0 && (this.source.peek()=='/') )
      {
    	  break;
      }
      
      tagName.append((char) this.source.read());
    }

    eatWhitespace();
    
    if( tagName.charAt(0)=='/' )
    {
    	this.tag.setName(tagName.substring(1).toString());
    	this.tag.setType(HTMLTag.Type.END);
    }
    else
    {
    	this.tag.setName(tagName.toString());
    	this.tag.setType(HTMLTag.Type.BEGIN);
    }
    // get the attributes

    while ((this.source.peek() != '>') && (this.source.peek() != -1))
    {
      String attributeName = parseAttributeName();
      String attributeValue = null;

      if (attributeName.equals("/"))
      {
        eatWhitespace();
        if (this.source.peek() == '>')
        {
          this.tag.setType(HTMLTag.Type.BOTH);
          break;
        }
      }

      // is there a value?
      eatWhitespace();
      if (this.source.peek() == '=')
      {
        this.source.read();
        attributeValue = parseString();
      }

      this.tag.setAttribute(attributeName, attributeValue);
    }
    this.source.read();
  }

}
