package com.example.arpit.fanpick;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;

import java.util.ArrayList;

/**
 * Created by Arpit on 22-07-2017.
 */

public class StoryGallery extends Fragment implements AdapterView.OnItemClickListener {

    ListView storyGrid;
    ProgressBar mProgressView;
    FirebaseDatabase database;
    ArrayList<StoryMetaData> stories;
    ArrayAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.story_gallery,container,false);
        storyGrid=(ListView)v.findViewById(R.id.storyGallery);

        mProgressView=(ProgressBar) v.findViewById(R.id.gallery_Progress);
        showProgress(true);

        stories=new ArrayList<>();


        database=FirebaseDatabase.getInstance();
        SharedPreferences preferences=getActivity().getSharedPreferences("way-word_Preference",getActivity().MODE_PRIVATE);

        final DatabaseReference myRef = database.getReference("Stories//");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                StoryMetaData data=snapshot.getValue(StoryMetaData.class);

                    stories.add(data);
                }
                adapter=new CustomStoryAdapter(getActivity(),stories);
                storyGrid.setDividerHeight(30);
                storyGrid.setAdapter(adapter);
                showProgress(false);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.e("setting","on click");

        storyGrid.setOnItemClickListener(this);

        return v;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        StoryMetaData selecteddata=stories.get(position);
        Bundle bundle= new Bundle();
        bundle.putString("title",selecteddata.title);

        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction FT=fragmentManager.beginTransaction();
        StoryReadMode frag=new StoryReadMode();
        frag.setArguments(bundle);
        FT.replace(R.id.holder,frag,null);
        FT.commit();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            storyGrid.setVisibility(show ? View.GONE : View.VISIBLE);
            storyGrid.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    storyGrid.setVisibility(show ? View.GONE : View.VISIBLE);
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
            storyGrid.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
