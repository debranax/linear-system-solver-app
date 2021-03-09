package com.debranax.linearsystem.activities;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.TypedValue;
import android.view.*;
import android.widget.Toast;

import com.debranax.linearsystem.*;
import com.debranax.linearsystem.databinding.*;
import com.debranax.linearsystem.utils.Constants;
import com.debranax.linearsystem.utils.Utils;

public class AboutActivity extends AppCompatActivity {
    private ActivityAboutBinding binding;

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
            binding.textViewAbout.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

    }

    @Override
    protected void onResume() {
        @ColorInt int color = getDefaultBackgroundColor();
        binding.contactRow.setBackgroundColor(color);
        binding.sourceCodeRow.setBackgroundColor(color);
        super.onResume();
    }

    private @ColorInt
    int getDefaultBackgroundColor() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.backgroundColor, typedValue, true);
        return typedValue.data;
    }

    public void goToSourceCode(View v) {
        String url = Constants.URL_REPO;
        changeBackgroundColor(v);
        openWebPage(url);
    }

    public void openEmail(View v) {
        String[] addresses = {Constants.CONTACT_EMAIL};
        changeBackgroundColor(v);
        composeEmail(addresses, Constants.SUBJECT_EMAIL);
    }

    private void changeBackgroundColor(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            v.setBackgroundColor(this.getColor(R.color.A_700));
        } else {
            //getResources().getColor(bgColor)) produces a warning in project build (deprecated)
            v.setBackgroundColor(getResources().getColor(R.color.A_700));
        }
    }

    private void launchActivity(Intent intent) {
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.no_intent_available), Toast.LENGTH_LONG).show();
        }
    }

    public void openWebPage(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        launchActivity(intent);
    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        launchActivity(intent);
    }
}