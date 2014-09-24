/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.ml;

/**
 * This interface is the base for all Encog Machine Learning methods.  It 
 * defines very little, other than the fact that a subclass is a Machine 
 * Learning Method.  A MLMethod is an algorithm that accepts data and 
 * provides some sort of insight into it.  This could be a neural network, 
 * support vector machine, clustering algorithm, or something else entirely.
 * 
 * Many MLMethods must be trained by a MLTrain object before they are useful.
 */
public interface MLMethod {
}
