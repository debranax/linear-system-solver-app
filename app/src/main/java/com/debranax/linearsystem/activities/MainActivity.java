package com.debranax.linearsystem.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.*;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;

import com.debranax.linearsystem.R;
import com.debranax.linearsystem.databinding.*;
import com.debranax.linearsystem.utils.Constants;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private EditText editTextUnknowns;

    /**
     * Actions when activity is created
     * @param savedInstanceState Saved Instance State Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        editTextUnknowns = binding.editTextNumberUnknowns;
        showSoftKeyboard(editTextUnknowns);
        binding.imageButtonStart.setOnClickListener(v -> processValidations());
        Toolbar toolbar = binding.toolbar.getRoot();
        setSupportActionBar(toolbar);
    }

    /**
     * Show soft keyboard
     * @param view When request focus the View (EditText) show the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Inflate the menu
     * @param menu Menu to inflate
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     *  Action when an option menu is selected
     * @param item Item selected
     * @return True if item is selected otherwise default parent value is return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Validate unknowns captured by the user in the editTextUnknowns
     */
    private void processValidations() {
        String unknownsVal = editTextUnknowns.getText().toString();
        int unknowns;
        if (TextUtils.isEmpty(unknownsVal)) {
            Toast.makeText(MainActivity.this, getString(R.string.empty_unknowns_main), Toast.LENGTH_LONG).show();
            return;
        }
        unknowns = Integer.parseInt(unknownsVal);
        if (unknowns < Constants.MIN_UNKNOWNS || unknowns > Constants.MAX_UNKNOWNS_DEFAULT) {
            Toast.makeText(MainActivity.this, getString(R.string.invalid_unknowns_main), Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, AugmentedMatrix.class);
        intent.putExtra(Constants.UNKNOWNS, unknowns);
        startActivity(intent);
    }

}