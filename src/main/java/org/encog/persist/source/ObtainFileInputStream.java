/*
 * Encog(tm) Core v3.4 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2017 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.persist.source;

import org.encog.EncogError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Obtain an input stream from a file.
 */
public class ObtainFileInputStream implements ObtainInputStream {

    /**
     * The file to read.
     */
    private File file;

    /**
     * Construct the source.
     * @param theFile The file source for the input stream.
     */
    public ObtainFileInputStream(File theFile) {
        this.file = theFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream obtain() {
        try {
            return new FileInputStream(this.file);
        } catch (FileNotFoundException ex) {
            throw new EncogError(ex);
        }
    }
}
