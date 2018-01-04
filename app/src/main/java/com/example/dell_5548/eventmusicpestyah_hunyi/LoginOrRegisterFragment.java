package com.example.dell_5548.eventmusicpestyah_hunyi;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginOrRegisterFragment extends Fragment implements View.OnClickListener{

    Button loginButton;
    Button registerButton;
    OnButtonClickedListener mCallback;

    public interface OnButtonClickedListener{
        public void onButtonClicked(int buttonCode);
    }

    public LoginOrRegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login_or_register, container, false);
        // Inflate the layout for this fragment

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view){
        loginButton = (Button) view.findViewById(R.id.fragmentLoginButton);
        registerButton = (Button) view.findViewById(R.id.fragmentRegisterButton);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        /*Button loginButton = (Button) view.findViewById(R.id.fragmentLoginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Fucket",Toast.LENGTH_SHORT).show();
                changeFragment();
            }
        });*/
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnButtonClickedListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement OnButtonClickedListener");

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fragmentLoginButton:
                mCallback.onButtonClicked(1);
                break;

            case R.id.fragmentRegisterButton:
                mCallback.onButtonClicked(2);
                break;
            default:
        }
    }



}
