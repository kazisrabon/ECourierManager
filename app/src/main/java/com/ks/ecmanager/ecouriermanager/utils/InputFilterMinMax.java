/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {
    private int min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    public InputFilterMinMax(String min, int offsetMin, String max, int offsetMax) {
        this.min = Integer.parseInt(min) + offsetMin;
        this.max = Integer.parseInt(max) - offsetMax;
    }

    public InputFilterMinMax(String min, String max, int offsetMax) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max) - offsetMax;
    }

    public InputFilterMinMax(String min, int offsetMin, String max) {
        this.min = Integer.parseInt(min) + offsetMin;
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
