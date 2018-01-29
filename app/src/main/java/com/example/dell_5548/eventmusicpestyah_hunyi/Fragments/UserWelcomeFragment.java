package com.example.dell_5548.eventmusicpestyah_hunyi.Fragments;


import android.content.Context;
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
public class UserWelcomeFragment extends Fragment implements View.OnClickListener{
    private Button profileButton;
    private Button logOutButton;
    private OnButtonClickedListener mCallback;

    public UserWelcomeFragment() {
        // Required empty public constructor
    }

    public interface OnButtonClickedListener{
        public void onButtonClickedTop(int buttonCode);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_welcome, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view){
        profileButton = (Button) view.findViewById(R.id.fragment_user_profile);
        logOutButton = (Button) view.findViewById(R.id.fragment_user_log_out);

        profileButton.setOnClickListener(this);
        logOutButton.setOnClickListener(this);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (UserWelcomeFragment.OnButtonClickedListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement OnButtonClickedListener");

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fragment_user_log_out:
                mCallback.onButtonClickedTop(1);
                break;

            case R.id.fragment_user_profile:
                mCallback.onButtonClickedTop(2);
                break;

            default:
        }
    }


}
