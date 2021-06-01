package com.debranax.linearsystem.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LinearSystemsSolver {

    private final static Logger LOGGER = Logger.getLogger(LinearSystemsSolver.class.getName());

    /**
     * Try to solve the linear system provided in the matrix parameter
     *
     * @param augmentedMatrix Augmented matrix
     * @return An instance of LinearSystemInfo containing the result (solution if
     * found, status code, etc)
     */
    public static LinearSystemInfo solve(final BigDecimal[][] augmentedMatrix) {
        BigDecimal[][] matrix = augmentedMatrix.clone();
        LinearSystemInfo linearSystemInfo;
        int totalRows = LinearSystemUtils.getTotalRows(matrix);
        int columnIndexError;

        try {
            if (LinearSystemsSolver.isHomogeneous(matrix)) {
                return LinearSystemsSolver.getLinearSystemInfoResponse(LinearSystemUtils.HOMOGENEOUS_MESSAGE,
                        LinearSystemUtils.StatusCode.HOMOGENEOUS, null, Level.INFO);
            }
            columnIndexError = LinearSystemsSolver.processEntriesBelowDiagonal(matrix, totalRows);
            if (columnIndexError >= 0) {
                return LinearSystemsSolver.getLinearSystemInfoResponse(LinearSystemUtils.ZERO_COLUMN_MESSAGE
                                + "(column index " + columnIndexError + ")",
                        LinearSystemUtils.StatusCode.ZERO_COLUMN, matrix, Level.INFO);
            }
            LinearSystemsSolver.processEntriesAboveDiagonal(matrix, totalRows);
            linearSystemInfo = LinearSystemsSolver.processFinalResults(matrix);
        } catch (Exception e) {
            //TODO Implement a custom exception and throw it and use a better logger
            String exceptionTrace = LinearSystemUtils.getStackTrace(e);
            LinearSystemsSolver.LOGGER.severe(exceptionTrace);
            linearSystemInfo = LinearSystemsSolver.getLinearSystemInfoResponse(exceptionTrace,
                    LinearSystemUtils.StatusCode.UNEXPECTED_ERROR, null, Level.SEVERE);
        }

        return linearSystemInfo;
    }

    /**
     * Process all the elements below the diagonal
     *
     * @param matrix    Augmented matrix
     * @param totalRows Total rows of augmented matrix
     * @return Return -1 if the process finish OK otherwise return the column index where the process cannot continue
     */
    private static int processEntriesBelowDiagonal(BigDecimal[][] matrix, int totalRows) {
        int columnIndexError = -1;
        int columnIndex = 0;
        boolean ifRowSwapped;
        for (int rowIndex = 0; rowIndex < totalRows; rowIndex++) {
            ifRowSwapped = LinearSystemsSolver.swapRowsIfNeeded(matrix, rowIndex, columnIndex);
            if (ifRowSwapped) {
                LinearSystemsSolver.makeOneZeroBelowRow(matrix, totalRows, rowIndex, columnIndex);
                columnIndex++;
            } else {
                columnIndexError = columnIndex;
                break;
            }
        }
        return columnIndexError;
    }

    /**
     * If the element in the diagonal is zero tries to swap the rows
     *
     * @param matrix      Augmented matrix
     * @param rowIndex    Row index of the element in the diagonal
     * @param columnIndex Column index of the element in the diagonal
     * @return True if swapped succeed or there was not needed to swap rows
     */
    private static boolean swapRowsIfNeeded(BigDecimal[][] matrix, int rowIndex, int columnIndex) {
        BigDecimal diagonalEntry;
        boolean ifRowSwapped = true;
        diagonalEntry = matrix[rowIndex][columnIndex];
        if (diagonalEntry.doubleValue() == 0) {
            ifRowSwapped = LinearSystemsSolver.swapRows(matrix, rowIndex, rowIndex);
        }
        return ifRowSwapped;
    }

    /**
     * Process all the elements above the diagonal
     *
     * @param matrix    Augmented matrix
     * @param totalRows Total rows of augmented matrix
     */
    private static void processEntriesAboveDiagonal(BigDecimal[][] matrix, int totalRows) {
        int columnIndex = 1;
        for (int rowIndex = 0; rowIndex < totalRows - 1; rowIndex++) {
            LinearSystemsSolver.makeZeroAboveRow(matrix, rowIndex, columnIndex);
            columnIndex++;
        }
    }

    /**
     * Create a new instance if LinearSystemInfo containing the information provided in the parameters
     *
     * @param message    Message to log
     * @param statusCode Status Code
     * @param matrix     Augmented matrix (if not required send null)
     * @return Instance  of LinearSystemInfo
     */
    private static LinearSystemInfo getLinearSystemInfoResponse(
            final String message,
            final LinearSystemUtils.StatusCode statusCode, final BigDecimal[][] matrix,
            final Level level) {
        LinearSystemInfo linearSystemInfo = new LinearSystemInfo();
        LinearSystemsSolver.logMessage(message, level);
        linearSystemInfo.setStatusCode(statusCode.getStatusCodeVal());
        // To see partial row echelon form
        if (matrix != null) {
            linearSystemInfo.setMatrix(matrix);
        }
        return linearSystemInfo;
    }

    /**
     * Set the final result of the solve method
     *
     * @param matrix Augmented matrix
     */
    private static LinearSystemInfo processFinalResults(final BigDecimal[][] matrix) {
        LinearSystemInfo linearSystemInfo;
        int lastColumnIndex = LinearSystemUtils.getLastColumnIndex(matrix);
        int totalRows = LinearSystemUtils.getTotalRows(matrix);
        BigDecimal[] solution = new BigDecimal[totalRows];
        for (int rowIndex = 0; rowIndex < totalRows; rowIndex++) {
            solution[rowIndex] = matrix[rowIndex][lastColumnIndex];
        }
        linearSystemInfo = LinearSystemsSolver.getLinearSystemInfoResponse(LinearSystemUtils.SYSTEM_WAS_SOLVED_MESSAGE,
                LinearSystemUtils.StatusCode.SOLVED, null, Level.INFO);
        linearSystemInfo.setSolution(solution);
        return linearSystemInfo;
    }

    /**
     * Make "one" on the diagonal element and make "zeros" below the diagonal
     * element
     *
     * @param matrix      Augmented matrix
     * @param totalRows   Total rows in the matrix
     * @param rowIndex    Row index of the element in the diagonal
     * @param columnIndex Column index of the element in the diagonal
     */
    private static void makeOneZeroBelowRow(final BigDecimal[][] matrix, final int totalRows,
                                            final int rowIndex,
                                            final int columnIndex) {
        BigDecimal diagonalEntry = matrix[rowIndex][columnIndex];
        if (diagonalEntry.doubleValue() != 1) {
            LinearSystemsSolver.makeOneInRow(matrix, rowIndex, columnIndex);
        }
        if (rowIndex < totalRows) {
            LinearSystemsSolver.makeZeroBelowRow(matrix, rowIndex, columnIndex);
        }
    }

    /**
     * Verify it the matrix is homogeneous
     *
     * @param matrix Augmented matrix
     * @return true is it is homogeneous
     */
    public static boolean isHomogeneous(final BigDecimal[][] matrix) {
        int totalRows = LinearSystemUtils.getTotalRows(matrix);
        int lastColumnIndex = LinearSystemUtils.getLastColumnIndex(matrix);
        int zeroCounter = 0;

        for (int rowIndex = 0; rowIndex < totalRows; rowIndex++) {
            if (matrix[rowIndex][lastColumnIndex].doubleValue() == 0)
                zeroCounter++;
        }
        return zeroCounter == totalRows;
    }

    /**
     * Swap rows if the element in the diagonal is zero
     *
     * @param matrix       Augmented matrix
     * @param rowIndexFrom Row index where change should happen
     * @param colIndex     Column index where leading entry verification must be
     *                     done
     * @return true if the interchange was applied
     */
    private static boolean swapRows(BigDecimal[][] matrix, final int rowIndexFrom, final int colIndex) {
        int rowIndexTo = rowIndexFrom + 1;
        int totalRowsIndex = LinearSystemUtils.getTotalRows(matrix) - 1;
        BigDecimal diagonalEntry;

        while (rowIndexTo <= totalRowsIndex && (diagonalEntry = matrix[rowIndexTo][colIndex]).doubleValue() == 0) {
            if (diagonalEntry.doubleValue() > 0) {
                LinearSystemsSolver.swapRowsTo(matrix, rowIndexFrom, rowIndexTo);
                return true;
            }
            rowIndexTo++;
        }
        return false;
    }

    /**
     * Swap rows from @rowIndexFrom to @rowIndexTo
     *
     * @param matrix       Augmented matrix
     * @param rowIndexFrom Row index from
     * @param rowIndexTo   Row index to
     */
    private static void swapRowsTo(BigDecimal[][] matrix, final int rowIndexFrom, final int rowIndexTo) {
        int lastColumnIndexTo = LinearSystemUtils.getLastColumnIndex(matrix) + 1;
        BigDecimal[] rowToReplace = Arrays.copyOfRange(matrix[rowIndexFrom], 0, lastColumnIndexTo);
        BigDecimal[] newRow = Arrays.copyOfRange(matrix[rowIndexTo], 0, lastColumnIndexTo);

        matrix[rowIndexFrom] = newRow;
        matrix[rowIndexTo] = rowToReplace;
    }

    /**
     * Make "one" on the diagonal element
     *
     * @param matrix           Augmented matrix
     * @param rowIndex         Row index of the element in the diagonal
     * @param startIndexColumn Column index of the element in the diagonal
     */
    private static void makeOneInRow(BigDecimal[][] matrix, final int rowIndex, final int startIndexColumn) {
        int lastColumnIndex = LinearSystemUtils.getLastColumnIndex(matrix);
        BigDecimal pivot = matrix[rowIndex][startIndexColumn];

        for (int columnIndex = startIndexColumn; columnIndex <= lastColumnIndex; columnIndex++) {
            matrix[rowIndex][columnIndex] = matrix[rowIndex][columnIndex].divide(pivot, LinearSystemUtils.SCALE,
                    RoundingMode.HALF_UP);
        }
    }

    /**
     * Make "zeros" below the diagonal element
     *
     * @param matrix           Augmented matrix
     * @param rowIndex         Row index of the element in the diagonal
     * @param startIndexColumn Column index of the element in the diagonal
     */
    private static void makeZeroBelowRow(BigDecimal[][] matrix, final int rowIndex, final int startIndexColumn) {
        int totalRowsIndex = LinearSystemUtils.getTotalRows(matrix) - 1;
        int nextRowIndexStart = rowIndex + 1;

        LinearSystemsSolver.makeZeroAboveBelowRow(matrix, rowIndex, startIndexColumn, nextRowIndexStart,
                totalRowsIndex, false);
    }

    /**
     * Make "zeros" above the diagonal element
     *
     * @param matrix           Augmented matrix
     * @param rowIndex         Row index of the element in the diagonal
     * @param startIndexColumn Column index of the element in the diagonal
     */
    private static void makeZeroAboveRow(BigDecimal[][] matrix, final int rowIndex, final int startIndexColumn) {
        int nextRowIndexStart = 0;
        LinearSystemsSolver.makeZeroAboveBelowRow(matrix, rowIndex, startIndexColumn, nextRowIndexStart, rowIndex,
                true);
    }

    /**
     * Make "zeros" below/above the diagonal element
     *
     * @param matrix            Augmented matrix
     * @param rowIndex          Row index of the element in the diagonal
     * @param startIndexColumn  Column index of the element in the diagonal
     * @param nextRowIndexStart Row index where iteration start
     * @param maxRowIndex       Maximum index row to iterate
     * @param makeZeroAbove     true if comes from makeZeroAboveRow(...) method
     */
    private static void makeZeroAboveBelowRow(BigDecimal[][] matrix, final int rowIndex,
                                              final int startIndexColumn, final int nextRowIndexStart,
                                              final int maxRowIndex, final boolean makeZeroAbove) {
        int lastColumnIndex = LinearSystemUtils.getLastColumnIndex(matrix);
        int additionalIndexValue = makeZeroAbove ? 1 : 0;
        BigDecimal pivot;

        for (int nextRowIndex = nextRowIndexStart; nextRowIndex <= maxRowIndex; nextRowIndex++) {
            pivot = matrix[nextRowIndex][startIndexColumn];
            if (pivot.doubleValue() != 0) {
                for (int columnIndex = startIndexColumn; columnIndex <= lastColumnIndex; columnIndex++) {
                    matrix[nextRowIndex][columnIndex] = matrix[nextRowIndex][columnIndex]
                            .subtract(pivot.multiply(matrix[rowIndex + additionalIndexValue][columnIndex]));
                }
            }
        }
    }

    private static void logMessage(final String message, final Level level) {
        LinearSystemsSolver.LOGGER.log(level, message);
    }
}
