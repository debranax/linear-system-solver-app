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
import com.debranax.linearsystem.utils.Constants;
import com.debranax.linearsystem.utils.Utils;

import java.io.*;
import java.math.BigDecimal;

public class AugmentedMatrix extends AppCompatActivity {

    private TableLayout tableLayout;
    private ActivityAugmentedMatrixBinding binding;
    private int unknowns;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.augmented_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.solve_item) {
            solve();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        String[][] savedMatrix = new String[unknowns][unknowns + 1];
        Utils.fillAugmentedMatrix(savedMatrix, tableLayout);
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.SAVE_STATE_AUGMENTED_MATRIX, savedMatrix);
    }

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

    private EditText getEditTextSystem(final String var, final int row, final int column,
                                       final int totalColumns,
                                       final String[][] savedMatrix) {
        EditText editText = new EditText(this);
        int columnHint = totalColumns == column ? row + 1 : column + 1;
        editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setWidth(Constants.WIDTH_EDIT_TEXT_AUGMENTED);
        editText.setMinWidth(Constants.WIDTH_EDIT_TEXT_AUGMENTED);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        editText.setHint(var + columnHint);
        editText.setFilters(new InputFilter.LengthFilter[]{
                new InputFilter.LengthFilter(Constants.MAX_TEXT_LENGTH)});
        if (savedMatrix != null) {
            if (savedMatrix[row][column] != null) {
                editText.setText(savedMatrix[row][column]);
            }
        }
        return editText;
    }

    private void solve() {
        BigDecimal[][] matrix = new BigDecimal[unknowns][unknowns + 1];
        LinearSystemInfo linearSystemInfo;
        Intent intent;
        boolean validation = Utils.fillAugmentedMatrix(matrix, tableLayout);
        if (!validation) {
            Toast.makeText(AugmentedMatrix.this,
                    getString(R.string.empty_field_augmented), Toast.LENGTH_LONG).show();
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
}