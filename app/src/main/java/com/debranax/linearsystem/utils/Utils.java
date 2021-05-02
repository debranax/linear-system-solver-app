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
    private static final String VALID_NUMBER = "^[+-]?\\d+([.]\\d?|(\\s\\d+)?[/][1-9]\\d*)?";

    public enum ValidationResult {Empty, InvalidNumber, OK}

    public static void fillAugmentedMatrix(final String[][] matrix, final TableLayout tableLayout) {
        for (int row = 0; row < tableLayout.getChildCount(); row++) {
            View child = tableLayout.getChildAt(row);
            if (child instanceof TableRow) {
                TableRow tableRow = (TableRow) child;
                for (int column = 0; column < tableRow.getChildCount(); column++) {
                    TextView textView = (TextView) tableRow.getChildAt(column);
                    if (!TextUtils.isEmpty(textView.getText().toString())) {
                        matrix[row][column] = textView.getText().toString();
                    }
                }
            }
        }
    }

    public static ValidationResult fillAugmentedMatrix(BigDecimal[][] matrix, final TableLayout tableLayout) {
        for (int row = 0; row < tableLayout.getChildCount(); row++) {
            View child = tableLayout.getChildAt(row);
            if (child instanceof TableRow) {
                TableRow tableRow = (TableRow) child;
                for (int column = 0; column < tableRow.getChildCount(); column++) {
                    TextView textView = (TextView) tableRow.getChildAt(column);
                    String value = textView.getText().toString();
                    if (TextUtils.isEmpty(value)) {
                        textView.requestFocus();
                        return ValidationResult.Empty;
                    }
                    if (!Utils.isValidNumber(value)) {
                        textView.requestFocus();
                        return ValidationResult.InvalidNumber;
                    }
                    if (value.contains("/")) {
                        matrix[row][column] = Utils.getBigDecimalFromFraction(value);
                    } else {
                        matrix[row][column] = new BigDecimal(value);
                    }
                }
            }
        }
        return ValidationResult.OK;
    }

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

    public static void setActionBar(ActionBar actionBar, @DrawableRes Integer resId) {
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (resId != null) {
                actionBar.setHomeAsUpIndicator(resId);
            }
        }
    }

    public static boolean isWritingValidNumber(String number) {
        return number.trim().matches(VALID_NUMBER) && !number.endsWith("  ")
                || Utils.isWritingNumber(number);
    }

    public static boolean isValidNumber(String number) {
        return number.trim().matches(VALID_NUMBER);
    }

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

    private static int countChar(String s, String stringToCount) {
        int countAfterReplace = s.replaceAll(stringToCount, "").length();
        return s.length() - countAfterReplace;
    }

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
