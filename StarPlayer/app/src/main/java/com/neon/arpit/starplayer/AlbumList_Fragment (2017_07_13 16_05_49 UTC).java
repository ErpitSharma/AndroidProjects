package com.neon.arpit.starplayer;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.GridView;
import android.widget.ListView;

/**
 * Created by arpit on 02-02-2017.
 */
public class AlbumList_Fragment extends Fragment implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener
{

    static ListView songList;
    static GridView List;
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
        View v= inflater.inflate(R.layout.album_list, container,false);


        Database=new DatabaseAdapter(getContext());
        Database.open();

        Name=(Button)v.findViewById(R.id.addNewPlaylist);
        Name.setClickable(false);
        Name.setVisibility(View.GONE);
        List=(GridView) v.findViewById(android.R.id.list);
        songList=(ListView)v.findViewById(R.id.songList);

        cursor=Database.getAlbums();
        BaseAdapter adapter = new PopulateAlbums(getActivity().getApplicationContext(), // Context.
                R.layout.custom_list,
                cursor, new String[]{ DatabaseAdapter.Album},
                new int[]{R.id.albumName});

        List.setAdapter(adapter);
        List.setOnItemClickListener(this);
        songList.setOnItemClickListener(this);



        return v;
    }

    public void SongFastList(int position) {
        Cursor cursor1;
        cursor1 = Database.getAlbumSongs(position);

        BaseAdapter adapter = new ImageCursorAdapter(getActivity().getApplicationContext(), // Context.
                R.layout.custom_list,
                cursor1, new String[]{DatabaseAdapter.AlbumArt, DatabaseAdapter.SongTitle, DatabaseAdapter.Artist},
                new int[]{R.id.albumArt, R.id.ListTitle, R.id.ListArtist});

        songList.setAdapter(adapter);
        songList.setOnItemLongClickListener(this);
        getActivity().startManagingCursor(cursor1);

    }
    int albCodeSelected =0;

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
                Log.e("position of song","selected from list "+ StaticData.position);
            }
            comm.play(listCursor,StaticData.position);
        }
        else
        {
            cursor.moveToPosition(position);
            albCodeSelected=Database.getAlbCode(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.Album)));
            Name.setText(Database.getAlbumName(albCodeSelected));
            SongFastList(albCodeSelected);
            Name.setVisibility(View.VISIBLE);
            List.setVisibility(View.GONE);
            songList.setVisibility(View.VISIBLE);
            listCursor = Database.getAlbumSongs(albCodeSelected);
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
