package com.debranax.linearsystem.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.debranax.linearsystem.R;
import com.debranax.linearsystem.databinding.*;
import com.debranax.linearsystem.math.LinearSystemInfo;
import com.debranax.linearsystem.math.LinearSystemsSolver;
import com.debranax.linearsystem.utils.*;

import java.io.*;
import java.math.BigDecimal;

public class AugmentedMatrix extends AppCompatActivity implements TextWatcher, View.OnFocusChangeListener {

    private TableLayout tableLayout;
    private ActivityAugmentedMatrixBinding binding;
    private int unknowns;
    private String beforeNumber = "";

    /**
     * Actions when activity is created
     * @param savedInstanceState Saved Instance State Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[][] savedMatrix = null;
        super.onCreate(savedInstanceState);
        binding = ActivityAugmentedMatrixBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar.getRoot();
        setSupportActionBar(toolbar);
        Utils.setActionBar(getSupportActionBar(), null);
        Bundle bundle = getIntent().getExtras();
        unknowns = bundle != null ? bundle.getInt(Constants.UNKNOWNS) : 0;
        if (unknowns == 0) {
            Toast.makeText(this, getString(R.string.missing_argument_results), Toast.LENGTH_LONG).show();
            return;
        }
        tableLayout = binding.tableLayoutAugmentedMatrix;
        if (savedInstanceState != null) {
            Serializable serializable = savedInstanceState.getSerializable(Constants.SAVE_STATE_AUGMENTED_MATRIX);
            if (serializable != null) {
                savedMatrix = (String[][]) serializable;
            }
        }
        processTableLayout(savedMatrix);
    }

    /**
     * Inflate the menu
     * @param menu Menu to inflate
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.augmented_menu, menu);
        return true;
    }

    /**
     *  Action when an option menu is selected
     * @param item Item selected
     * @return True if item is selected otherwise default parent value is return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.solve_item) {
            solve();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save the current values in the view (augmented matrix)
     * @param outState Bundle where state is saved
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        String[][] savedMatrix = new String[unknowns][unknowns + 1];
        Utils.fillAugmentedMatrix(savedMatrix, tableLayout);
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.SAVE_STATE_AUGMENTED_MATRIX, savedMatrix);
    }

    /**
     * Set the information in the TableLayout based on the matrix pass as an argument
     * e.g when the device is rotated the view is recreated but the data is saved and
     * "restore" this information in the view
     * @param savedMatrix Augmented matrix where values are read
     */
    private void processTableLayout(String[][] savedMatrix) {
        final String VAR_VALUE = "X";
        final String A_VALUE = "a";
        for (int row = 0; row < unknowns; row++) {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            tableRow.setLayoutParams(layoutParams);
            for (int column = 0; column < unknowns; column++) {
                EditText editText = this.getEditTextSystem(VAR_VALUE, row, column,
                        unknowns, savedMatrix);
                tableRow.addView(editText);
            }
            EditText editText = this.getEditTextSystem(A_VALUE, row, unknowns,
                    unknowns, savedMatrix);
            tableRow.addView(editText);
            tableLayout.addView(tableRow);
        }
        tableLayout.setBackgroundResource(R.drawable.table_border);
    }

    /**
     * Create an EditText instance to put it in the TableLayout
     * @param var Hint value show in the EditText
     * @param row Row of the augmented matrix
     * @param column Column of the augmented matrix
     * @param totalColumns Total of columns of the augmented matrix
     * @param savedMatrix Augmented matrix
     * @return EditText instance
     */
    private EditText getEditTextSystem(final String var, final int row, final int column,
                                       final int totalColumns,
                                       final String[][] savedMatrix) {
        EditText editText = new EditText(this);
        int columnHint = totalColumns == column ? row + 1 : column + 1;
        editText.setKeyListener(FractionalNumberKeyListener.getInstance());
        editText.setWidth(Constants.WIDTH_EDIT_TEXT_AUGMENTED);
        editText.setMinWidth(Constants.WIDTH_EDIT_TEXT_AUGMENTED);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        editText.setHint(var + columnHint);
        editText.setFilters(new InputFilter.LengthFilter[]{
                new InputFilter.LengthFilter(Constants.MAX_TEXT_LENGTH)});
        if (savedMatrix != null && savedMatrix[row][column] != null) {
            editText.setText(savedMatrix[row][column]);
        }
        editText.addTextChangedListener(this);
        editText.setOnFocusChangeListener(this);
        return editText;
    }

    /**
     * Validate the values of the augmented matrix and if it is OK solve the linear system
     */
    private void solve() {
        BigDecimal[][] matrix = new BigDecimal[unknowns][unknowns + 1];
        LinearSystemInfo linearSystemInfo;
        Intent intent;
        boolean validation;
        Utils.ValidationResult validationResult = Utils.fillAugmentedMatrix(matrix, tableLayout);
        if (validationResult == Utils.ValidationResult.Empty) {
            Toast.makeText(AugmentedMatrix.this,
                    getString(R.string.empty_field_augmented), Toast.LENGTH_LONG).show();
            return;
        }
        if (validationResult == Utils.ValidationResult.InvalidNumber) {
            Toast.makeText(AugmentedMatrix.this,
                    getString(R.string.invalid_field_augmented), Toast.LENGTH_LONG).show();
            return;
        }
        validation = Utils.isZeroMatrix(matrix);
        if (validation) {
            Toast.makeText(AugmentedMatrix.this,
                    getString(R.string.zero_matrix_validation_augmented), Toast.LENGTH_LONG).show();
            return;
        }
        linearSystemInfo = LinearSystemsSolver.solve(matrix);
        intent = new Intent(AugmentedMatrix.this, Results.class);
        intent.putExtra(Constants.LINEAR_SYSTEM_INFO, linearSystemInfo);
        startActivity(intent);
    }

    /**
     * Method required as part of the interface OnFocusChangeListener. NOT USED
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * Method required as part of the interface OnFocusChangeListener. NOT USED
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    /**
     * Action after Text changed of the Editable used to captured the augmented matrix
     * @param s Editable used to captured the augmented matrix
     */
    @Override
    public void afterTextChanged(Editable s) {
        boolean isValidNumber = Utils.isWritingValidNumber(s.toString());
        if (!isValidNumber) {
            s.replace(0, s.length(), beforeNumber);
        }
        beforeNumber = s.toString();
    }

    /**
     * Action after focus changed , is applied to EditText where the user capture the augmented matrix
     * @param v View (EditText where the user capture the augmented matrix)
     * @param hasFocus True if has focus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText editText = (EditText) v;
        if (hasFocus) {
            beforeNumber = editText.getText().toString().trim();
            return;
        }
        String number = editText.getText().toString().trim();
        if (number.length() >= 2 && number.startsWith(".")) {
            number = "0" + number;
        } else if (number.length() >= 2 && number.endsWith(".")) {
            number = number.substring(0, 1);
        }
        editText.setText(number);
    }
}