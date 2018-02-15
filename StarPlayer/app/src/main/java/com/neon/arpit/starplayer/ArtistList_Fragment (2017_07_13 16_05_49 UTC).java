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
 * Created by arpit on 02-02-2017.
 */
public class ArtistList_Fragment extends Fragment implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener

{


    static ListView List, songList;
    DatabaseAdapter Database;
    communicator comm;
    Cursor cursor, listCursor;
    static Button Name;

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
        songList=(ListView)v.findViewById(R.id.songList);
        Name=(Button)v.findViewById(R.id.addNewPlaylist);
        Name.setClickable(false);
        Name.setVisibility(View.GONE);

        Database=new DatabaseAdapter(getContext());
        Database.open();

        cursor=Database.getArtists();
        SimpleCursorAdapter cursorAdapter= null;
        try {
            cursorAdapter = new SimpleCursorAdapter(getContext(),R.layout.album_artist_list,
                    cursor,new String[]{DatabaseAdapter.Artist},new int[]{R.id.ArtistName});
        } catch (Exception e) {
            Log.e("Adapter",e.toString());
        }
        List.setAdapter(cursorAdapter);

        List.setOnItemClickListener(this);
        songList.setOnItemClickListener(this);


        return v;
    }
    public void SongFastList(int position) {
        Cursor cursor1;
        cursor1 = Database.getArtistSongs(position);

        BaseAdapter adapter = new ImageCursorAdapter(getActivity().getApplicationContext(), // Context.
                R.layout.custom_list,
                cursor1, new String[]{DatabaseAdapter.AlbumArt, DatabaseAdapter.SongTitle, DatabaseAdapter.Artist},
                new int[]{R.id.albumArt, R.id.ListTitle, R.id.ListArtist});

        songList.setAdapter(adapter);

        getActivity().startManagingCursor(cursor1);

        songList.setOnItemLongClickListener(this);
    }
    int artCodeSelected =0;

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
                Log.e("position of song","selected from Artist "+ StaticData.position);
            }
            comm.play(listCursor,StaticData.position);
        }
        else
        {
            cursor.moveToPosition(position);
            artCodeSelected=Database.getArtCode(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.Artist)));
            Name.setText(Database.getArtistName(artCodeSelected));
            SongFastList(artCodeSelected);
            List.setVisibility(View.GONE);
            Name.setVisibility(View.VISIBLE);
            songList.setVisibility(View.VISIBLE);
            listCursor = Database.getArtistSongs(artCodeSelected);
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
