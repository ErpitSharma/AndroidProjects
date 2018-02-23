package com.example.arpit.fanpick;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.StackView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Arpit on 23-07-2017.
 */

public class StoryReadMode extends Fragment implements AdapterView.OnItemSelectedListener{
    StoryRoutes story;
    TextView title;
    TextView storyContent;
    String name;
    ProgressBar mProgressView;
    DatabaseReference database;
    LinearLayout page;
    Spinner optionSpinner;
    Uri file;
    File f;
    Bundle bundle;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.story_page,container,false);
        page=(LinearLayout)v.findViewById(R.id.pageLayout);
        mProgressView=(ProgressBar) v.findViewById(R.id.page_Progress);
        showProgress(true);
        storyContent=(TextView)v.findViewById(R.id.storyContent);
        title=(TextView)v.findViewById(R.id.pageTitle);
        optionSpinner=(Spinner)v.findViewById(R.id.optionSpinner);

        bundle=getArguments();

        name=bundle.getString("title");

        database=FirebaseDatabase.getInstance().getReference("Stories").child(name);

        SharedPreferences preferences=getActivity().getSharedPreferences("way-word_Preference",getActivity().MODE_PRIVATE);
        final StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Stories//"+preferences.getString("userId",null)+"//"+name+".txt");

        try {

            f=File.createTempFile("tempFile","txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file","to be fetched");

        storageReference.getFile(f).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("file","fetched");

                if (f!=null)
                {
                    try {
                        StringBuilder stringBuilder=new StringBuilder();
                        BufferedReader bufferedReader= new BufferedReader(new FileReader(f));
                        String temp;
                        while ((temp = bufferedReader.readLine()) != null) {
                            stringBuilder.append(temp);
                            stringBuilder.append('\n');
                        }
                        bufferedReader.close();

                        title.setText(name);
                        storyContent.setText(stringBuilder.toString());

                        showProgress(false);




                    } catch (Exception e) {

                        Log.e("file",e.toString());}
                }
            }
        });
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StoryMetaData storydata=dataSnapshot.getValue(StoryMetaData.class);

//                Log.e("Spinner","uploading"+storydata.options.get(1));

                    optionSpinner.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,storydata.options));

                optionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        TextView text=(TextView) view;
                        String selected=text.getText().toString();
                        Log.e("spinner selected",selected);
                        if (position>0) {
                            Bundle bundle= new Bundle();
                            bundle.putString("title",selected);
                            FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                            FragmentTransaction FT=fragmentManager.beginTransaction();
                            StoryReadMode frag=new StoryReadMode();
                            frag.setArguments(bundle);
                            FT.add(R.id.holder,frag,null);
                            FT.commit();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            page.setVisibility(show ? View.GONE : View.VISIBLE);
            page.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    page.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            page.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
