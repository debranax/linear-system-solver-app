package com.debranax.linearsystem.utils;

import android.text.TextUtils;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.ActionBar;

import com.debranax.linearsystem.math.LinearSystemUtils;

import java.math.BigDecimal;

public class Utils {

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

    public static boolean fillAugmentedMatrix(BigDecimal[][] matrix, final TableLayout tableLayout) {
        for (int row = 0; row < tableLayout.getChildCount(); row++) {
            View child = tableLayout.getChildAt(row);
            if (child instanceof TableRow) {
                TableRow tableRow = (TableRow) child;
                for (int column = 0; column < tableRow.getChildCount(); column++) {
                    TextView textView = (TextView) tableRow.getChildAt(column);
                    if (TextUtils.isEmpty(textView.getText().toString())) {
                        return false;
                    }
                    matrix[row][column] = new BigDecimal(textView.getText().toString());
                }
            }
        }
        return true;
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
}
