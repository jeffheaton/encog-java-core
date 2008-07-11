/*
  * Encog Neural Network and Bot Library for Java
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * Copyright 2008, Heaton Research Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
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
package org.encog.bot.spider;

import java.io.*;

/**
 * The Heaton Research Spider 
 * Copyright 2007 by Heaton Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * SpiderInputStream: This class is used by the spider to
 * both parse and save an InputStream.
 * 
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 * 
 * @author Jeff Heaton
 * @version 1.1
 */
public class SpiderInputStream extends InputStream {
  /**
   * The InputStream to read from.
   */
  private InputStream is;

  /**
   * The OutputStream to write to.
   */
  private OutputStream os;

  /**
   * Construct the SpiderInputStream. Whatever is read from
   * the InputStream will also be written to the
   * OutputStream.
   * 
   * @param is
   *          The InputStream.
   * @param os
   *          The OutputStream.
   */
  public SpiderInputStream(InputStream is, OutputStream os) {
    this.is = is;
    this.os = os;
  }

  /*
   * Read a single byte from the stream. @throws IOException
   * If an I/O exception occurs. @return The character that
   * was read from the stream.
   */
  @Override
  public int read() throws IOException {
    int ch = this.is.read();
    if (this.os != null) {
      this.os.write(ch);
    }
    return ch;
  }

  /**
   * Set the OutputStream.
   * 
   * @param os
   *          The OutputStream.
   */
  public void setOutputStream(OutputStream os) {
    this.os = os;
  }

}
