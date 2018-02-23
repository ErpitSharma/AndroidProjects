package com.example.arpit.fanpick;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.google.firebase.FirebaseApp;

/**
 * Created by Arpit on 17-07-2017.
 */

public class AddStory extends Fragment implements View.OnClickListener{

    Button cancel;
    Communicator communicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator=(Communicator)activity;
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.add_story,container,false);

        cancel=(Button)v.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);


        return v;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction FT=fragmentManager.beginTransaction();
        FT.remove(this);
        FT.commit();
        communicator.Showfab(true);

    }
}
