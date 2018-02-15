package com.neon.arpit.starplayer;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * Created by arpit on 12-03-2017.
 */
public class VideoList_Fragment extends Fragment implements AdapterView.OnItemClickListener {

    static ListView List, songList;
    DatabaseAdapter Database;
    communicator comm;
    BaseAdapter adapter;
    Handler handler;
    Cursor cursor;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (communicator) activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        handler = new Handler();
        Database = new DatabaseAdapter(getContext());
        Database.open();

        List = (ListView) v.findViewById(android.R.id.list);
        songList = (ListView) v.findViewById(R.id.songList);

        Runnable runnable = new Runnable() {
            public void run() {
                populateList();
            }
        };
        runnable.run();

        return v;
    }

    public void populateList() {
        Database=new DatabaseAdapter(getContext());
        Database.open();

        cursor=Database.getVideos();
        SimpleCursorAdapter cursorAdapter= null;
        try {
            cursorAdapter = new SimpleCursorAdapter(getContext(),R.layout.album_artist_list,
                    cursor,new String[]{DatabaseAdapter.SongTitle},new int[]{R.id.ArtistName});
        } catch (Exception e) {
            Log.e("Adapter",e.toString());
        }
        List.setAdapter(cursorAdapter);
        List.setOnItemClickListener(this);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        comm.stop();
        // cursor = Database.getSongs();
        if (StaticData.shuffleFlag == 0) {
            StaticData.position = position;
        } else {
            StaticData.List.clear();
            for (int i = 0; i < cursor.getCount(); i++)
                StaticData.List.add(i, i);
            StaticData.Shuffle(StaticData.List);
            StaticData.swaper(StaticData.List, 0, StaticData.List.indexOf(position));
            StaticData.position = 0;
        }
        comm.playVideo(cursor, StaticData.position);
    }


}