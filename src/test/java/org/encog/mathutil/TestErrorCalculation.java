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
package org.encog.mathutil;

import org.encog.Encog;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.mathutil.error.ErrorCalculationMode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jeff on 5/30/16.
 */
public class TestErrorCalculation {

    @Test
    public void testPerfectHotLogLoss() {
        ErrorCalculation.setMode(ErrorCalculationMode.HOT_LOGLOSS);
        ErrorCalculation calc = new ErrorCalculation();
        calc.updateError(new double[]{0.0,0.0,1.0},new double[]{0.0,0.0,1.0},1.0);
        calc.updateError(new double[]{0.0,1.0,0.0},new double[]{0.0,1.0,0.0},1.0);
        calc.updateError(new double[]{1.0,0.0,0.0},new double[]{1.0,0.0,0.0},1.0);
        Assert.assertEquals(0.0,calc.calculateLogLoss(), Encog.DEFAULT_DOUBLE_EQUAL);
    }

    @Test
    public void testHotLogLoss() {
        ErrorCalculation.setMode(ErrorCalculationMode.HOT_LOGLOSS);
        ErrorCalculation calc = new ErrorCalculation();
        calc.updateError(new double[]{0.0,0.0,1.0},new double[]{0.0,0.0,1.0},1.0);
        calc.updateError(new double[]{0.0,0.95,0.5},new double[]{0.0,1.0,0.0},1.0);
        calc.updateError(new double[]{0.85,0.1,0.5},new double[]{1.0,0.0,0.0},1.0);
        double e = calc.calculateLogLoss();
        Assert.assertEquals(0.0712707412951,calc.calculateLogLoss(), Encog.DEFAULT_DOUBLE_EQUAL);
    }

    @Test
    public void testPerfectLogLoss() {
        ErrorCalculation.setMode(ErrorCalculationMode.LOGLOSS);
        ErrorCalculation calc = new ErrorCalculation();
        calc.updateError(new double[]{0.0,0.0,1.0},new double[]{2.0},1.0);
        calc.updateError(new double[]{0.0,1.0,0.0},new double[]{1.0},1.0);
        calc.updateError(new double[]{1.0,0.0,0.0},new double[]{0.0},1.0);
        Assert.assertEquals(0.0,calc.calculateLogLoss(), Encog.DEFAULT_DOUBLE_EQUAL);
    }

    @Test
    public void testLogLoss() {
        ErrorCalculation.setMode(ErrorCalculationMode.LOGLOSS);
        ErrorCalculation calc = new ErrorCalculation();
        calc.updateError(new double[]{0.0,0.0,1.0},new double[]{2.0},1.0);
        calc.updateError(new double[]{0.0,0.95,0.5},new double[]{1.0},1.0);
        calc.updateError(new double[]{0.85,0.1,0.5},new double[]{0.0},1.0);
        double e = calc.calculateLogLoss();
        Assert.assertEquals(0.0712707412951,calc.calculateLogLoss(), Encog.DEFAULT_DOUBLE_EQUAL);
    }

    @AfterClass
    public static void teardown() {
        ErrorCalculation.setMode(ErrorCalculationMode.MSE);
    }
}
