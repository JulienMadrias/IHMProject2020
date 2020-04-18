package com.example.ihmproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ModeDeDeplacementFragment extends Fragment implements View.OnClickListener {

    public ModeDeDeplacementFragment() {
        // Required empty public constructor
    }

    private IButtonClickListener mCallBack;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallBack = (IButtonClickListener)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mode_transport, container, false);
        ((Button) view.findViewById(R.id.close_mode_transport)).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_mode_transport:
                mCallBack.onCloseModeTransportButtonClicked(v);
                break;
        }
    }
}
