package com.example.arpit.toolkit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Arpit on 23-08-2017.
 */

public class Selection extends Fragment implements View.OnClickListener {

    Button toDoButton, calculatorButton, torchButton, notesButton, stopwatchButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.selection_layout,container,false);

        calculatorButton = (Button)v.findViewById(R.id.calC);
        calculatorButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.calC)
        {
            FragmentManager FM= getFragmentManager();
            FragmentTransaction transaction= FM.beginTransaction();
            transaction.replace(R.id.holder, new Calculator()  ,null);
            transaction.commit();
        }

    }
}
