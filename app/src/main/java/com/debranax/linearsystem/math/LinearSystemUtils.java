package com.debranax.linearsystem.math;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class LinearSystemUtils {
    public static final int SCALE = 10;
    public static final String HOMOGENEOUS_MESSAGE = "The system has a trivial or non-trivial solution";
    public static final String ZERO_COLUMN_MESSAGE = "All elements below the diagonal  " +
            "are zero, this program can not solve this linear system";
    public static final String SYSTEM_WAS_SOLVED_MESSAGE = "The system was solved!";

    public enum StatusCode {
        UNEXPECTED_ERROR(-1),
        NO_INFO(0),
        HOMOGENEOUS(1),
        ZERO_COLUMN(2),
        SOLVED(3);

        private final int code;

        StatusCode(int code) {
            this.code = code;
        }

        public int getStatusCodeVal() {
            return this.code;
        }
    }

    /**
     * Return the stack trace of the  Throwable pass as an argument
     * @param throwable Throwable where the stack trace is extracted
     * @return Return a string containing the stack trace
     */
    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    /**
     * Validate if the parameter is Long
     * @param bigDecimal Parameter to evaluate
     * @return True if parameter is considered Long
     */
    public static boolean isLong(BigDecimal bigDecimal) {
        //If double equal to long value that means does not have decimal values but watch out for precision of double
        return bigDecimal.doubleValue() == bigDecimal.longValue();
    }

    /**
     * Format the BigDecimal with the pattern passed in the method
     * @param bg BigDecimal to format
     * @param pattern Pattern to use in the formatting
     * @return String formatted
     */
    public static String format(BigDecimal bg, String pattern) {
        //When is applied the format to very small numbers could be return -0.0
        String formattedNumber  = new DecimalFormat(pattern).format(bg);
        double parseDouble =  Double.parseDouble(new DecimalFormat(pattern).format(bg));
        if (Math.abs(parseDouble) == 0){
            return "0";
        }
        return formattedNumber;
    }

    /**
     * Get the last column index of the matrix
     * @param matrix Array where the last index column is obtained
     * @return  Last Index
     */
    public static int getLastColumnIndex(final BigDecimal[][] matrix) {
        return matrix[0].length - 1;
    }

    /**
     * Get total rows of the matrix
     * @param matrix Array where the total rows is obtained
     * @return Total rows
     */
    public static int getTotalRows(final BigDecimal[][] matrix) {
        return matrix.length;
    }
}
