package com.debranax.linearsystem.utils;

import android.text.*;
import android.text.method.*;

import androidx.annotation.*;

import java.util.*;

public class FractionalNumberKeyListener extends NumberKeyListener {
    private final char[] mCharacters;

    private static final Object sLock = new Object();

    @GuardedBy("sLock")
    private static final HashMap<Locale, FractionalNumberKeyListener> sInstanceCache = new HashMap<>();

    private static final char[] CHARACTERS = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '/', '-', '.', ' '
    };

    public int getInputType() {
        return InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE;
    }

    @Override
    @NonNull
    protected char[] getAcceptedChars() {
        return mCharacters;
    }

    public FractionalNumberKeyListener() {
        this(null);
    }

    public FractionalNumberKeyListener(@Nullable Locale locale) {
        //TODO Locale for future use, e.g. can be useful for locales that use comma instead period
        mCharacters = CHARACTERS;
    }

    @NonNull
    public static FractionalNumberKeyListener getInstance() {
        return getInstance(null);
    }

    /**
     * Returns an instance of FractionalNumberKeyListener (locale is not used).
     */
    @NonNull
    public static FractionalNumberKeyListener getInstance(@Nullable Locale locale) {
        FractionalNumberKeyListener instance;
        synchronized (sLock) {
            instance = sInstanceCache.get(locale);
            if (instance == null) {
                instance = new FractionalNumberKeyListener(locale);
                sInstanceCache.put(locale, instance);
            }
        }
        return instance;
    }
}
