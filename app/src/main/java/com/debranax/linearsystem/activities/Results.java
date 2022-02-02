package com.debranax.linearsystem.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.debranax.linearsystem.R;
import com.debranax.linearsystem.databinding.*;
import com.debranax.linearsystem.math.LinearSystemInfo;
import com.debranax.linearsystem.math.LinearSystemUtils;
import com.debranax.linearsystem.utils.Constants;
import com.debranax.linearsystem.utils.Utils;

import java.math.BigDecimal;


public class Results extends AppCompatActivity {

    private TableLayout tableLayout;
    private ActivityResultsBinding binding;

    /**
     * Actions when activity is created
     * @param savedInstanceState Saved Instance State Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar.getRoot();
        setSupportActionBar(toolbar);
        Utils.setActionBar(getSupportActionBar(), R.drawable.ic_home);
        Bundle bundle = getIntent().getExtras();
        tableLayout = binding.tableLayoutResults;
        LinearSystemInfo linearSystemInfo = bundle != null ? (LinearSystemInfo) bundle.get(Constants.LINEAR_SYSTEM_INFO) : null;
        this.processResponse(linearSystemInfo);
    }

    /**
     * Process the results of the linear system
     * @param linearSystemInfo
     */
    private void  processResponse(final LinearSystemInfo linearSystemInfo){
        if (linearSystemInfo == null){
            Toast.makeText(this, getString(R.string.missing_argument_results), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!linearSystemInfo.isSolved()) {
            processErrorSystem(linearSystemInfo);
            return;
        }
        processSolvedSystem(linearSystemInfo);
    }


    /**
     * Inflate the menu
     * @param menu Menu to inflate
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.results_menu, menu);
        return true;
    }

    /**
     *  Action when an option menu is selected
     * @param item Item selected
     * @return True if item is selected otherwise default parent value is return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.copy_results) {
            copyResults();
            return true;
        }
        if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Copy the solution to the clipboard
     */
    private void copyResults() {
        StringBuilder stringBuilder = new StringBuilder();
        String message = getString(R.string.copied_results);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip;
        for (int row = 0; row < tableLayout.getChildCount(); row++) {
            String rowResult = this.getResults(row);
            stringBuilder.append(rowResult);
            stringBuilder.append(System.getProperty("line.separator"));
        }
        clip = ClipData.newPlainText("Solution", stringBuilder.toString());
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        } else {
            message = getString(R.string.not_copied_results);
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Copy the solution to a string of the row pass as parameter
     * @param row Row whee the result is obtained
     * @return String with the result
     */
    private String getResults (final int row){
        StringBuilder stringBuilder = new StringBuilder();
        View child = tableLayout.getChildAt(row);
        if (child instanceof TableRow) {
            TableRow tableRow = (TableRow) child;
            for (int column = 0; column < tableRow.getChildCount(); column++) {
                TextView textView = (TextView) tableRow.getChildAt(column);
                stringBuilder.append(textView.getText().toString());
            }
        }
        return  stringBuilder.toString();
    }

    /**
     * Actions when the linear system is solved successfully
     * @param linearSystemInfo Instance of the results of the linear system
     */
    private void processSolvedSystem(final LinearSystemInfo linearSystemInfo) {
        for (int row = 1; row <= linearSystemInfo.getSolution().length; row++) {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            TextView textViewVar = new TextView(this);
            TextView textViewSol = new TextView(this);
            BigDecimal bigDecimalResult;
            tableRow.setLayoutParams(layoutParams);
            tableRow.setBackgroundResource(R.drawable.table_border);

            textViewVar.setText(String.format(" X%s ", row));
            bigDecimalResult = linearSystemInfo.getSolution()[row - 1];
            if (LinearSystemUtils.isLong(bigDecimalResult)) {
                textViewSol.setText(String.format("= %s", bigDecimalResult.longValue()));
            } else {
                textViewSol.setText(String.format("= %s", LinearSystemUtils.format(bigDecimalResult,
                        Constants.FORMAT_RESULTS)));
            }
            setCommonTextAttributes(textViewVar);
            textViewVar.setPadding(Constants.PADDING_35, Constants.PADDING_10,
                    Constants.PADDING_10, Constants.PADDING_10);
            setCommonTextAttributes(textViewSol);
            textViewSol.setPadding(Constants.PADDING_10, Constants.PADDING_10,
                    Constants.PADDING_45, Constants.PADDING_10);
            tableRow.addView(textViewVar);
            tableRow.addView(textViewSol);
            tableLayout.addView(tableRow);
        }
    }

    /**
     * Set some common properties of the TextView used to show the results
     * @param textView TextView where properties are applied
     */
    private void setCommonTextAttributes(TextView textView) {
        textView.setTextSize(Constants.SIZE_TEXT_RESULTS);
        textView.setMinHeight(Constants.HEIGHT_TEXT_RESULTS);
    }

    /**
     * If there is an error trying to solve the linear systems,has trivial solution or is not possible
     * to solve the linear system a message is displayed in the activity
     * @param linearSystemInfo Instance of the results of the linear system
     */
    private void processErrorSystem(LinearSystemInfo linearSystemInfo) {
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);

        TextView textViewMessage = new TextView(this);
        tableRow.setLayoutParams(layoutParams);
        textViewMessage.setTextSize(Constants.SIZE_TEXT_RESULTS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textViewMessage.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        if (linearSystemInfo.getStatusCode()
                == LinearSystemUtils.StatusCode.HOMOGENEOUS.getStatusCodeVal()) {
            textViewMessage.setText(getString(R.string.homogeneous_matrix_results));
        } else if (linearSystemInfo.getStatusCode()
                == LinearSystemUtils.StatusCode.ZERO_COLUMN.getStatusCodeVal()) {
            textViewMessage.setText(getString(R.string.zero_matrix_results));
        } else {
            textViewMessage.setText(getString(R.string.unexpected_error_augmented));
        }
        tableRow.addView(textViewMessage);
        tableLayout.addView(tableRow);
    }

}