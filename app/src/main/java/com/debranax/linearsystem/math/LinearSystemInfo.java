package com.debranax.linearsystem.math;

import java.io.Serializable;
import java.math.BigDecimal;

public class LinearSystemInfo implements Serializable {


    private BigDecimal[] solution;
    private BigDecimal[][] matrix;
    private int statusCode = LinearSystemUtils.StatusCode.NO_INFO.getStatusCodeVal();
    private final StringBuilder additionalInfo = new StringBuilder();

    public BigDecimal[] getSolution() {
        return solution;
    }

    public void setSolution(BigDecimal[] solution) {
        this.solution = solution;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    //TODO For future use
    public StringBuilder getAdditionalInfo() {
        return this.additionalInfo;
    }
    //TODO For future use
    public void appendAdditionalInfo(final String info) {
        this.additionalInfo.append(info);
    }

    public boolean isSolved() {
        return (this.statusCode == LinearSystemUtils.StatusCode.SOLVED.getStatusCodeVal());
    }

    public BigDecimal[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(BigDecimal[][] matrix) {
        this.matrix = matrix;
    }

}
