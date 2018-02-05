package com.example.dell_5548.eventmusicpestyah_hunyi.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.dell_5548.eventmusicpestyah_hunyi.Activities.CreateEventActivity;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Pista on 2018.01.28..
 */

public class TimePickerDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new android.app.TimePickerDialog(getActivity(),(android.app.TimePickerDialog.OnTimeSetListener)getActivity(),hour,minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
    }
}
