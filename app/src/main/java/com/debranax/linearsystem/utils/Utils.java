package com.debranax.linearsystem.utils;

import android.text.TextUtils;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.ActionBar;

import com.debranax.linearsystem.math.LinearSystemUtils;

import java.math.*;

public class Utils {
    private static final String VALID_NUMBER = "^[+-]?\\d+([.]\\d+|(\\s\\d+)?[/][1-9]\\d*)?";

    public enum ValidationResult {Empty, InvalidNumber, OK}

    /**
     * Fill the augmented matrix based on the tableLayout where data is captured by the user
     * @param matrix Array where is stored the augmented matrix
     * @param tableLayout TableLayout where data is captured by the user
     */
    public static void fillAugmentedMatrix(final String[][] matrix, final TableLayout tableLayout) {
        for (int row = 0; row < tableLayout.getChildCount(); row++) {
            View child = tableLayout.getChildAt(row);
            if (child instanceof TableRow) {
                TableRow tableRow = (TableRow) child;
                Utils.fillAugmentedMatrixRow(matrix, row, tableRow);
            }
        }
    }

    /**
     * Fill the row of the augmented matrix based on the tableLayout where data is captured by the user
     * @param matrix Array where is stored the augmented matrix
     * @param row Row to fill
     * @param tableRow TableRow where values are aceptured bu the user
     */
    private static void fillAugmentedMatrixRow(final String[][] matrix , int row, final TableRow tableRow){
        for (int column = 0; column < tableRow.getChildCount(); column++) {
            TextView textView = (TextView) tableRow.getChildAt(column);
            if (!TextUtils.isEmpty(textView.getText().toString())) {
                matrix[row][column] = textView.getText().toString();
            }
        }
    }
    /**
     * Validate and fill the augmented matrix based on the tableLayout where data is captured by the user
     * @param matrix Array where is stored the augmented matrix
     * @param tableLayout TableLayout where data is captured by the user
     * @return Return the result of the validation
     */
    public static ValidationResult fillAugmentedMatrix(BigDecimal[][] matrix, final TableLayout tableLayout) {
        for (int row = 0; row < tableLayout.getChildCount(); row++) {
            View child = tableLayout.getChildAt(row);
            if (child instanceof TableRow) {
                TableRow tableRow = (TableRow) child;
                for (int column = 0; column < tableRow.getChildCount(); column++) {
                    TextView textView = (TextView) tableRow.getChildAt(column);
                    String value = textView.getText().toString();
                    ValidationResult validationResult = Utils.validateAugmentedMatrix(value);
                    if (validationResult!=null){
                        textView.requestFocus();
                        return validationResult;
                    }
                    Utils.fillAugmentedMatrixValue(matrix, row, column, value);
                }
            }
        }
        return ValidationResult.OK;
    }

    /**
     * Validate the values in the textView where data is captured by the user
     * @param value Value of the TextView
     * @return If an error is found return the enum {@link ValidationResult} value otherwise return null
     */
    private static ValidationResult validateAugmentedMatrix(final String value){
        if (TextUtils.isEmpty(value)) {
            return ValidationResult.Empty;
        }
        if (!Utils.isValidNumber(value)) {
            return ValidationResult.InvalidNumber;
        }
        return  null;
    }

    /**
     * Set a value in the augmented matrix based on row and column
     * @param matrix Augmented matrix
     * @param row Row where is store the value
     * @param column Column where is store the value
     * @param value Value to store
     */
    private static void fillAugmentedMatrixValue(BigDecimal[][] matrix, int row, int column,
                                                 String value){
        if (value.contains("/")) {
            matrix[row][column] = Utils.getBigDecimalFromFraction(value);
        } else {
            matrix[row][column] = new BigDecimal(value);
        }
    }
    /**
     * Validate if the augmented matrix is a zero matrix
     * @param matrix Augmented matrix
     * @return true if it is a zero matrix
     */
    public static boolean isZeroMatrix(BigDecimal[][] matrix) {
        int zeroCounter = 0;
        int totalRows = LinearSystemUtils.getTotalRows(matrix);
        int totalCols = LinearSystemUtils.getLastColumnIndex(matrix) + 1;
        for (int row = 0; row < totalRows; row++) {
            for (int column = 0; column < totalCols; column++) {
                if (matrix[row][column].doubleValue() == 0)
                    zeroCounter++;
            }
        }
        return zeroCounter == (totalRows * totalCols);
    }

    /**
     * Set the setDisplayHomeAsUpEnabled as true and the setHomeAsUpIndicator  with resId (if not null)
     * @param actionBar Instance of the ActionBar
     * @param resId DrawableRes Resource Id
     */
    public static void setActionBar(ActionBar actionBar, @DrawableRes Integer resId) {
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (resId != null) {
                actionBar.setHomeAsUpIndicator(resId);
            }
        }
    }

    /**
     *
     * @param number Validate if the parameter it is considered a possible number
     * @return String that represent a possible number
     */
    public static boolean isWritingValidNumber(String number) {
        return number.trim().matches(VALID_NUMBER) && !number.endsWith("  ")
                || Utils.isWritingNumber(number);
    }

    /**
     * Validate if the parameter is a valid number (decimal)
     * @param number String that represent a possible number
     * @return true if it is valida number
     */
    public static boolean isValidNumber(String number) {
        return number.trim().matches(VALID_NUMBER);
    }

    /**
     * Validate if the parameter is a valid number (decimal or fractional)
     * @param number String that represent a possible number
     * @return true if it is valida number
     */
    private static boolean isWritingNumber(String number) {
        //TODO Change to regexp when possible
        if (number.equals("-")
                || number.equals(".")
                || number.equals("-.")
                || number.length() == 0) {
            return true;
        }
        if (Utils.countChar(number, "-") > 1
                || (number.contains("/") && number.contains("."))) {
            return false;
        }
        if (number.contains(".")) {
            return Utils.isWritingDecimal(number);
        } else {
            return Utils.isWritingFraction(number);
        }
    }

    /**
     * Validate if the parameter it is considered a possible decimal number
     * @param number String that represent a possible number
     * @return true if it is valida decimal number
     */
    private static boolean isWritingDecimal(String number) {
        if (number.trim().length() > 0 &&
                (number.indexOf("-") > 0
                        || number.endsWith(" ")
                        || number.contains(" "))) {
            return false;
        }
        //Could be easier with lambda but needs higher API number
        int totalPoints = Utils.countChar(number, "\\.");
        return totalPoints == 1;
    }

    /**
     *  Validate if the parameter it     is considered a possible fractional number
     * @param number String that represent a possible number
     * @return true if it is valida fractional number
     */
    private static boolean isWritingFraction(String number) {
        int totalSlash = countChar(number, "/");
        int totalSpaces = countChar(number, " ");
        int containSpace = number.indexOf(" ");
        char lastChar = number.charAt(number.length() - 1);
        boolean isValidSpace = containSpace < 0 || totalSpaces == 1;
        boolean isValidSlash = totalSlash < 2;
        boolean isValidLastChar =
                ((totalSlash == 0 && Character.isDigit(lastChar)) || Character.isSpaceChar(lastChar)
                        || (number.endsWith("/") && !number.endsWith(" /")));
        return isValidSpace && isValidSlash && isValidLastChar;
    }

    /**
     *  Count occurrences of a string in another string
     * @param s A String value, representing the string to search for
     * @param stringToCount String to count
     * @return Number of occurrences
     */
    private static int countChar(String s, String stringToCount) {
        int countAfterReplace = s.replaceAll(stringToCount, "").length();
        return s.length() - countAfterReplace;
    }

    /**
     * Convert fractional number to BigDecimal
     * @param fractionalNumber Fractional number to be converted
     * @return  BigDecimal value of fractional number
     */
    public static BigDecimal getBigDecimalFromFraction(String fractionalNumber) {
        String[] fractions = fractionalNumber.split(" ");
        BigDecimal numeratorBD;
        BigDecimal denominatorBD;
        BigDecimal integerBD;
        String numerator;
        String denominator;
        String integer = "0";
        if (fractions.length > 1) {
            integer = fractions[0];
            fractions = fractions[1].split("/");
        } else {
            fractions = fractionalNumber.split("/");
        }
        numerator = fractions[0];
        denominator = fractions[1];
        integerBD = new BigDecimal(integer);
        numeratorBD = new BigDecimal(numerator);
        denominatorBD = new BigDecimal(denominator);
        return integerBD.add(numeratorBD.divide(denominatorBD, LinearSystemUtils.SCALE,
                RoundingMode.HALF_UP));
    }
}
