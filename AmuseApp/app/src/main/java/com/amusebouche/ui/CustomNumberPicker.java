package com.amusebouche.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import java.lang.reflect.Field;
import java.lang.NullPointerException;

/**
 *
 */

/**
 * Custom NumberPicker class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Duplicate user interface element.
 * Needed to change NumberPicker divider color.
 */
public class CustomNumberPicker extends NumberPicker {

    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        Class<?> numberPickerClass = null;
        try {
            numberPickerClass = Class.forName("android.widget.NumberPicker");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Field selectionDivider = null;
        try {
            selectionDivider = numberPickerClass.getDeclaredField("mSelectionDivider");
        } catch (NoSuchFieldException | NullPointerException e) {
            e.printStackTrace();
        }

        try {
            selectionDivider.setAccessible(true);
            selectionDivider.set(this, getResources()
                    .getDrawable(com.amusebouche.activities.R.drawable.divider));
        } catch (IllegalArgumentException | Resources.NotFoundException |
                IllegalAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}