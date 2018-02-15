package com.neon.arpit.starplayer;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by arpit on 18-02-2017.
 */
public class PlayList_Fragment extends Fragment implements
        AdapterView.OnItemClickListener,
        View.OnClickListener,
        View.OnFocusChangeListener,
        AdapterView.OnItemLongClickListener

{


    static ListView List, songList;
    DatabaseAdapter Database;
    communicator comm;
    Button newPlaylist;
    BaseAdapter adapter;
    Cursor cursor,listCursor;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm=(communicator)activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container, savedInstanceState);
        final View v= inflater.inflate(R.layout.fragment_main, container,false);
        List=(ListView)v.findViewById(android.R.id.list);
        newPlaylist = (Button)v.findViewById(R.id.addNewPlaylist);
        songList=(ListView)v.findViewById(R.id.songList);
        newPlaylist.setVisibility(View.VISIBLE);

        PopulateplayList();

        newPlaylist.setOnClickListener(this);
        List.setOnItemClickListener(this);
        List.setOnFocusChangeListener(this);
        songList.setOnItemClickListener(this);


        return v;
    }

    void PopulateplayList()
    {
        Database=new DatabaseAdapter(getContext());
        Database.open();

        cursor=Database.getPlaylists();
        SimpleCursorAdapter cursorAdapter= null;
        try {
            cursorAdapter = new SimpleCursorAdapter(getContext(),R.layout.album_artist_list,
                    cursor,new String[]{DatabaseAdapter.PlaylistName},new int[]{R.id.ArtistName});
        } catch (Exception e) {
            Log.e("Adapter",e.toString());
        }
        List.setAdapter(cursorAdapter);

    }

    public void SongFastList(String playCode) {
        Cursor cursor1;
        cursor1 = Database.getPlaylistSongs(playCode);

        adapter = new ImageCursorAdapter(getActivity().getApplicationContext(), // Context.
                R.layout.custom_list,
                cursor1, new String[]{DatabaseAdapter.AlbumArt, DatabaseAdapter.SongTitle, DatabaseAdapter.Artist},
                new int[]{R.id.albumArt, R.id.ListTitle, R.id.ListArtist});

        songList.setAdapter(adapter);

        songList.setOnItemLongClickListener(this);
        getActivity().startManagingCursor(cursor1);

    }
    String PlayCode ="";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (songList.getVisibility()==View.VISIBLE) {
            comm.stop();
            if (StaticData.shuffleFlag==0) {
                StaticData.position =position;
            } else {
                StaticData.List.clear();
                for (int i=0;i<listCursor.getCount();i++)
                    StaticData.List.add(i,i);
                StaticData.Shuffle(StaticData.List);
                StaticData.swaper(StaticData.List,0,StaticData.List.indexOf(position));
                StaticData.position=0;
                Log.e("position of song","selected from Playlist "+ StaticData.position);
            }
            comm.play(listCursor,StaticData.position);
        }
        else
        {
            cursor.moveToPosition(position);
            PlayCode=cursor.getString(cursor.getColumnIndex(DatabaseAdapter.PlaylistCode));
            SongFastList(PlayCode);
            List.setVisibility(View.GONE);
            songList.setVisibility(View.VISIBLE);
            listCursor = Database.getPlaylistSongs(PlayCode);
        }
    }

    @Override
    public void onClick(View v) {
        StaticData.isNewPlaylistEmpty=true;
        FragmentManager FM =getFragmentManager();
        AddNewPlaylist addNewPlaylist=new AddNewPlaylist();
        addNewPlaylist.show(FM,"newPlaylist");
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus)
        {
            PopulateplayList();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        listCursor.moveToPosition(position);
        StaticData.hashToAdd=listCursor.getString(listCursor.getColumnIndex(DatabaseAdapter.HashValue));
        StaticData.pathToFile=listCursor.getString(listCursor.getColumnIndex(DatabaseAdapter.Path));
        StaticData.rowId=listCursor.getInt(listCursor.getColumnIndex(DatabaseAdapter.Row_ID));
        StaticData.isNewPlaylistEmpty=false;
        FragmentManager FM=getFragmentManager();
        PopUpMenuDialog SPD= new PopUpMenuDialog();
        SPD.show(FM,"SPD_Emergency");
        return true;
    }
}
