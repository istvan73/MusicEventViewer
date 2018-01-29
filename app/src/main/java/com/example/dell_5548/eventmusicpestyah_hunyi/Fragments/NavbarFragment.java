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
public class NavbarFragment extends Fragment implements View.OnClickListener{

    private Button subscribedButton;
    private Button activeButton;
    private Button archiveButton;
    private onNavbarButtonClickedListener listener;

    public NavbarFragment() {
        // Required empty public constructor
    }


    public interface onNavbarButtonClickedListener{
        public void onNavbarButtonClicked(int buttonCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_navbar, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view){
        subscribedButton = (Button) view.findViewById(R.id.subscribedButton);
        activeButton = (Button) view.findViewById(R.id.activesButton);
        archiveButton = (Button) view.findViewById(R.id.archiveButton);

        subscribedButton.setOnClickListener(this);
        activeButton.setOnClickListener(this);
        archiveButton.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (NavbarFragment.onNavbarButtonClickedListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement onNavbarButtonClickedListener");

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.subscribedButton:
                listener.onNavbarButtonClicked(0);
                break;

            case R.id.activesButton:
                listener.onNavbarButtonClicked(1);
                break;

            case R.id.archiveButton:
                listener.onNavbarButtonClicked(2);
                break;

            default:
        }
    }

}
