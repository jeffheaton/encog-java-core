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
 * A fallback stream that will attempt to read from the resource first, failing that, read from a file.
 */
public class ObtainFallbackStream implements ObtainInputStream {
    /**
     * The name of the file to read.
     */
    private String datasetName;

    /**
     * The path.
     */
    private String path;

    /**
     * The resources.
     */
    private Class resources;

    /**
     * Construct the source.
     * @param thePath The path.
     * @param theDatasetName The name of the dataset.
     * @param theResources Where to pull the resources from.
     */
    public ObtainFallbackStream(String thePath, String theDatasetName, Class theResources) {
        this.datasetName = theDatasetName;
        this.path = thePath;
        this.resources = theResources;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream obtain() {
        File target = null;
        final InputStream istream = this.resources.getResourceAsStream("/"+this.datasetName);
        if (istream == null) {
            try {
                target = new File(this.path,this.datasetName);
                return new FileInputStream(target);
            } catch (FileNotFoundException e) {
                throw new EncogError("Cannot access data set, make sure the resources are available: " + target);
            }

        }
        return istream;
    }
}
