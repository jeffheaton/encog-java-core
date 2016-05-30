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
