package com.debranax.linearsystem;

import android.util.*;

import com.debranax.linearsystem.math.*;

import org.junit.*;

import java.math.*;

/**
 *Local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LinearSystemsSolverTest {

    @Test
    public void homogeneousSystem() {
        double[][] matrix = {{1, 2, 0}, {4, 6, 0}};
        BigDecimal[][] bgMatrix = this.getBDDecimalMatrix(matrix);
        LinearSystemInfo linearSystemInfo = LinearSystemsSolver.solve(bgMatrix);
        Assert.assertEquals(LinearSystemUtils.StatusCode.HOMOGENEOUS.getStatusCodeVal(), linearSystemInfo.getStatusCode());
    }

    @Test
    public void zeroColumnSystem() {
        double[][] matrix = {{1, 1, 2}, {2, 2, 2}};
        BigDecimal[][] bgMatrix = this.getBDDecimalMatrix(matrix);
        LinearSystemInfo linearSystemInfo = LinearSystemsSolver.solve(bgMatrix);
        Assert.assertEquals(LinearSystemUtils.StatusCode.ZERO_COLUMN.getStatusCodeVal(), linearSystemInfo.getStatusCode());
    }

    @Test
    public void successSystem() {
        double[][] matrix = {{1, 1, 1, 4}, {1, 2, 4, 12}, {2, -3, -1, 4}};
        BigDecimal[][] bgMatrix = this.getBDDecimalMatrix(matrix);
        double[] solutionExpected = {2, -1, 3};
        double delta = 0;
        double[] actualSolution;
        LinearSystemInfo linearSystemInfo = LinearSystemsSolver.solve(bgMatrix);
        Assert.assertTrue(linearSystemInfo.isSolved());
        actualSolution = this.getSolutionAsDouble(linearSystemInfo.getSolution());
        //Math.abs( expected – actual ) <= delta
        Assert.assertArrayEquals(solutionExpected, actualSolution, delta);

    }

    @Test
    public void successSystem2() {
        double[][] matrix = {{6, 5, 2}, {5, 5, 2}};
        BigDecimal[][] bgMatrix = this.getBDDecimalMatrix(matrix);
        double[] solutionExpected = {0, 0.4};
        //The format takes only 5 decimals so more than this is acceptable delta
        double delta = 0.000001d;
        double[] actualSolution;
        LinearSystemInfo linearSystemInfo = LinearSystemsSolver.solve(bgMatrix);
        Assert.assertTrue(linearSystemInfo.isSolved());
        actualSolution = this.getSolutionAsDouble(linearSystemInfo.getSolution());
        //Math.abs( expected – actual ) <= delta
        Assert.assertArrayEquals(solutionExpected, actualSolution, delta);

    }

    private double[] getSolutionAsDouble(BigDecimal[] solution) {
        double[] retVal = new double[solution.length];
        for (int row = 0; row < solution.length; row++) {
            retVal[row] = solution[row].doubleValue();
        }
        return retVal;
    }

    /**
     * Return array as Bigdecimal based on an array on double (it is easier to instance array of double for testing purpose)
     *
     * @param matrix Array of double
     * @return Array of BigDecimal
     */
    private BigDecimal[][] getBDDecimalMatrix(double[][] matrix) {
        BigDecimal[][] retVal = new BigDecimal[matrix.length][matrix[0].length];
        for (int row = 0; row < matrix.length; row++) {
            for (int column = 0; (column < matrix[0].length); column++) {
                retVal[row][column] = BigDecimal.valueOf(matrix[row][column]);
            }
        }
        return retVal;
    }
}