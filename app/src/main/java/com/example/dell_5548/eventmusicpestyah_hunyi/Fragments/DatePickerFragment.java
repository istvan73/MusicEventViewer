package com.example.dell_5548.eventmusicpestyah_hunyi.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.dell_5548.eventmusicpestyah_hunyi.Activities.CreateEventActivity;

import java.util.Calendar;

/**
 * Created by Pista on 2018.01.27..
 */

public class DatePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), (CreateEventActivity)getActivity(),year,month,day);

    }
}
