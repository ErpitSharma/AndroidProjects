package com.neon.arpit.starplayer;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * Created by arpit on 02-02-2017.
 */

public class SongList_Fragment extends Fragment implements AdapterView.OnItemClickListener, Runnable, View.OnFocusChangeListener ,SearchView.OnQueryTextListener, AdapterView.OnItemLongClickListener
 {

     static ListView List;
     ListView songList,artistList,albumList;
     TextView songLabel,artistLabel,albumLabel,result;
     DatabaseAdapter Database;
     static RelativeLayout LA;
     communicator comm;
     Handler handler;
     Cursor cursor;
     static SearchView searchView;
     Cursor songCursor;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (communicator) activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.main_list, container, false);
        handler = new Handler();
        Database = new DatabaseAdapter(getContext());
        Database.open();

        List = (ListView) v.findViewById(android.R.id.list);
        songList = (ListView) v.findViewById(R.id.songList);
        artistList = (ListView) v.findViewById(R.id.artistList);
        LA=(RelativeLayout)v.findViewById(R.id.searchList);
        albumList = (ListView) v.findViewById(R.id.albumList);
        songLabel=(TextView)v.findViewById(R.id.songLabel);
        artistLabel=(TextView)v.findViewById(R.id.artistLabel);
        result=(TextView)v.findViewById(R.id.result);
        albumLabel=(TextView)v.findViewById(R.id.albumLabel);


        searchView=(SearchView) v.findViewById(R.id.searchView);
        searchView.setVisibility(View.VISIBLE);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);
        searchView.clearFocus();

        handler.postDelayed(this,0);
        List.setOnFocusChangeListener(this);
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId()==android.R.id.list) {
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
            comm.play(cursor, StaticData.position);
        }
        if (parent.getId()==R.id.songList) {
            comm.stop();
            songCursor.moveToPosition(position);
            int Rid=songCursor.getInt(songCursor.getColumnIndex(DatabaseAdapter.Row_ID));
            cursor = Database.getSong(Rid);
            StaticData.position = 0;
            comm.play(cursor, StaticData.position);
        }
        if (parent.getId()==R.id.artistList) {
            MainActivity.mViewPager.setCurrentItem(3);

        }
        if (parent.getId()==R.id.albumList) {
            MainActivity.mViewPager.setCurrentItem(2);

        }
    }

    @Override
    public void run() {
        populateList();
        if (!StaticData.isDBDone) {
            handler.postDelayed(this,100);
        }

    }


    public void populateList() {
        cursor = Database.getSongs();
        BaseAdapter adapter = new ImageCursorAdapter(getActivity(), // Context.
                R.layout.custom_list,
                cursor, new String[]{DatabaseAdapter.AlbumArt, DatabaseAdapter.SongTitle, DatabaseAdapter.Artist},
                new int[]{R.id.albumArt, R.id.ListTitle, R.id.ListArtist});
        List.setAdapter(adapter);
        List.setTextFilterEnabled(true);
        List.setOnItemClickListener(this);
        List.setOnItemLongClickListener(this);
       
    }
     public void populateList(Cursor cursor) {
         BaseAdapter adapter = new ImageCursorAdapter(getActivity(), // Context.
                 R.layout.custom_list,
                 cursor, new String[]{DatabaseAdapter.AlbumArt, DatabaseAdapter.SongTitle, DatabaseAdapter.Artist},
                 new int[]{R.id.albumArt, R.id.ListTitle, R.id.ListArtist});
         songList.setAdapter(adapter);
         songList.setTextFilterEnabled(true);
         handler.postDelayed(this, 3000);
         songList.setOnItemClickListener(this);
     }

     public void populateArtist(Cursor cursor) {
         SimpleCursorAdapter cursorAdapter= null;
         try {
             cursorAdapter = new SimpleCursorAdapter(getContext(),android.R.layout.simple_list_item_1,
                     cursor,new String[]{DatabaseAdapter.Artist},new int[]{android.R.id.text1});
         } catch (Exception e) {
             Log.e("Adapter",e.toString());
         }
         artistList.setAdapter(cursorAdapter);
         artistList.setTextFilterEnabled(true);
         handler.postDelayed(this, 3000);
         artistList.setOnItemClickListener(this);
     }
     public void populateAlbum(Cursor cursor) {
         SimpleCursorAdapter cursorAdapter= null;
         try {
             cursorAdapter = new SimpleCursorAdapter(getContext(),android.R.layout.simple_list_item_1,
                     cursor,new String[]{DatabaseAdapter.Album},new int[]{android.R.id.text1});
         } catch (Exception e) {
             Log.e("Adapter",e.toString());
         }
         albumList.setAdapter(cursorAdapter);
         albumList.setTextFilterEnabled(true);
         handler.postDelayed(this, 3000);
         albumList.setOnItemClickListener(this);
     }


     @Override
    public void onFocusChange(View v, boolean hasFocus) {
         if (hasFocus)
        {
            Log.e("focus","changed");
            handler.postDelayed(this,0);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText.isEmpty()) {
           List.setVisibility(View.VISIBLE);
            LA.setVisibility(View.GONE);
            getActivity().setTitle("Star Player");
        }
        else
        {
            getActivity().setTitle("Search");
            LA.setVisibility(View.VISIBLE);
            List.setVisibility(View.GONE);

            songCursor=Database.searchSong(newText);
            Boolean sResult=songCursor.getCount()>0;
            if (sResult) {
                songLabel.setVisibility(View.VISIBLE);
                songList.setVisibility(View.VISIBLE);
                populateList(songCursor);
            }else
            {
                songList.setVisibility(View.GONE);
                songLabel.setVisibility(View.GONE);
            }


            cursor=Database.searchArtists(newText);
            Boolean arResult=cursor.getCount()>0;
            if (arResult) {
                artistList.setVisibility(View.VISIBLE);
                artistLabel.setVisibility(View.VISIBLE);
                populateArtist(cursor);
            }else {
                artistList.setVisibility(View.GONE);
                artistLabel.setVisibility(View.GONE);
            }


            cursor=Database.searchAlbums(newText);
            Boolean abResult=cursor.getCount()>0;
            if (cursor.getCount()>0) {
                albumLabel.setVisibility(View.VISIBLE);
                albumList.setVisibility(View.VISIBLE);
                populateAlbum(cursor);
            }else {
                albumLabel.setVisibility(View.GONE);
                albumList.setVisibility(View.GONE);
            }
            if (!sResult && !arResult && !abResult)
            {
                result.setVisibility(View.VISIBLE);
            }
            else
                result.setVisibility(View.GONE);
        }

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String newText) {
        return false;
    }

     @Override
     public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

         cursor.moveToPosition(position);
         StaticData.hashToAdd=cursor.getString(cursor.getColumnIndex(DatabaseAdapter.HashValue));
         StaticData.pathToFile=cursor.getString(cursor.getColumnIndex(DatabaseAdapter.Path));
         StaticData.rowId=cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.Row_ID));
         StaticData.isNewPlaylistEmpty=false;
         FragmentManager FM=getFragmentManager();
         PopUpMenuDialog SPD= new PopUpMenuDialog();
         SPD.show(FM,"SPD_Emergency");
         return true;
     }


 }
