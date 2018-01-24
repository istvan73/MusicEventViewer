package com.example.dell_5548.eventmusicpestyah_hunyi.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dell_5548.eventmusicpestyah_hunyi.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavbarFragment extends Fragment{

    public NavbarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_navbar, container, false);
    }

}
