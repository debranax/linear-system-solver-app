package com.debranax.linearsystem.activities;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.widget.Toast;

import com.debranax.linearsystem.*;
import com.debranax.linearsystem.databinding.*;
import com.debranax.linearsystem.utils.Constants;
import com.debranax.linearsystem.utils.Utils;

public class AboutActivity extends AppCompatActivity {
    private ActivityAboutBinding binding;

    /**
     * Actions when activity is created
     * @param savedInstanceState Saved Instance State Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar.getRoot();
        setSupportActionBar(toolbar);
        Utils.setActionBar(getSupportActionBar(), null);
        binding.textViewVersion.setText("Ver. " + BuildConfig.VERSION_NAME);
        binding.textViewVersion.setTextSize(Constants.SIZE_TEXT_RESULTS);
        binding.textViewVersion.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.textViewContact.setTextSize(Constants.SIZE_TEXT_RESULTS);
        binding.textViewSourceCode.setTextSize(Constants.SIZE_TEXT_RESULTS);
        binding.textViewAbout.setTextSize(Constants.SIZE_TEXT_RESULTS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.textViewAbout.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

    }

    /**
     * Actions when activity is resumed
     */
    @Override
    protected void onResume() {
        @ColorInt int color = getDefaultBackgroundColor();
        binding.contactRow.setBackgroundColor(color);
        binding.sourceCodeRow.setBackgroundColor(color);
        super.onResume();
    }

    /**
     * Get default background color of the current theme
     * @return  int value of the color
     */
    private @ColorInt
    int getDefaultBackgroundColor() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.backgroundColor, typedValue, true);
        return typedValue.data;
    }

    /**
     * Process activity to go to the source code (github link)
     * @param v View that call the action
     */
    public void goToSourceCode(View v) {
        String url = Constants.URL_REPO;
        changeBackgroundColor(v);
        openWebPage(url);
    }

    /**
     * Process activity to show  the email client
     * @param v View that call the action
     */
    public void openEmail(View v) {
        String[] addresses = {Constants.CONTACT_EMAIL};
        changeBackgroundColor(v);
        composeEmail(addresses, Constants.SUBJECT_EMAIL);
    }

    /**
     * Change the color background
     * @param v View where change is applied
     */
    private void changeBackgroundColor(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            v.setBackgroundColor(this.getColor(R.color.A_700));
        } else {
            //getResources().getColor(bgColor)) produces a warning in project build (deprecated)
            v.setBackgroundColor(getResources().getColor(R.color.A_700));
        }
    }

    /**
     * Launch an Intent, if not possible to launch it display a Toast message
     * @param intent Intent to launch
     */
    private void launchActivity(Intent intent) {
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.no_intent_available), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Open Intent related to a web page (link)
     * @param url Url to open
     */
    public void openWebPage(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        launchActivity(intent);
    }

    /**
     * Compose tha basic email body
     * @param addresses  Recipient
     * @param subject Subject of the email
     */
    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        launchActivity(intent);
    }
}