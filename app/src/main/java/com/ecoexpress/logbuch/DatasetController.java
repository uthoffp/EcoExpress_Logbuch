package com.ecoexpress.logbuch;

import android.content.Context;
import android.content.ContextWrapper;

import java.util.Calendar;

public class DatasetController extends ContextWrapper {
    private int[] userId;
    private Calendar calendar;

    public DatasetController(Context context, int[] userId) {
        super(context);
        this.userId = userId;
        this.calendar = Calendar.getInstance();
    }
}
