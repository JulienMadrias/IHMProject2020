package com.example.ihmproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ModeDeDeplacementFragment extends Fragment implements View.OnClickListener {
    private Button auto,motard,cycliste,passager,pieton,bus;

    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;
    public ModeDeDeplacementFragment() {
        // Required empty public constructor
    }

    private IButtonDrawerClickListener mCallBack;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallBack = (IButtonDrawerClickListener)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mode_transport, container, false);
        ((Button) view.findViewById(R.id.close_mode_transport)).setOnClickListener(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        auto = ((Button) view.findViewById(R.id.mode_automobiliste_button));
        auto.setOnClickListener(this);

        motard = ((Button) view.findViewById(R.id.mode_motard_button));
        motard.setOnClickListener(this);

        cycliste = ((Button) view.findViewById(R.id.mode_cycliste_button));
        cycliste.setOnClickListener(this);

        passager = ((Button) view.findViewById(R.id.mode_passager_button));
        passager.setOnClickListener(this);

        pieton = ((Button) view.findViewById(R.id.mode_pieton_button));
        pieton.setOnClickListener(this);

        bus = ((Button) view.findViewById(R.id.mode_bus_button));
        bus.setOnClickListener(this);


        if(preferences.getString("USER_MODE","automobile")=="automobile")
            editor.putString("USER_MODE", "automobile");
            editor.commit();

        initialiseMode();



        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_mode_transport:
                mCallBack.onCloseModeTransportButtonClicked(v);
                break;
            case R.id.mode_automobiliste_button:
                editor.putString("USER_MODE", "automobile");
                editor.commit();
                initialiseMode();
                break;
            case R.id.mode_bus_button:
                editor.putString("USER_MODE", "bus");
                editor.commit();
                initialiseMode();
                break;
            case R.id.mode_pieton_button:
                editor.putString("USER_MODE", "pieton");
                editor.commit();
                initialiseMode();
                break;
            case R.id.mode_motard_button:
                editor.putString("USER_MODE", "motard");
                editor.commit();
                initialiseMode();
                break;
            case R.id.mode_passager_button:
                editor.putString("USER_MODE", "passager");
                editor.commit();
                initialiseMode();
                break;
            case R.id.mode_cycliste_button:
                editor.putString("USER_MODE", "cycliste");
                editor.commit();
                initialiseMode();
                break;
        }
    }
    private void initialiseMode(){
        auto.setBackgroundColor(Color.WHITE);
        motard.setBackgroundColor(Color.WHITE);
        cycliste.setBackgroundColor(Color.WHITE);
        passager.setBackgroundColor(Color.WHITE);
        pieton.setBackgroundColor(Color.WHITE);
        bus.setBackgroundColor(Color.WHITE);
        switch (preferences.getString("USER_MODE","bus")){
            case "automobile":
                auto.setBackgroundColor(Color.GREEN);
                break;
            case "motard":
                motard.setBackgroundColor(Color.GREEN);
                break;
            case "cycliste":
                cycliste.setBackgroundColor(Color.GREEN);
                break;
            case "passager":
                passager.setBackgroundColor(Color.GREEN);
                break;
            case "pieton":
                pieton.setBackgroundColor(Color.GREEN);
                break;
            case "bus":
                bus.setBackgroundColor(Color.GREEN);
                break;
        }
    }
}
