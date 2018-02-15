package com.neon.arpit.starplayer;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by arpit on 18-02-2017.
 */
public class SelectPlaylistDialog extends DialogFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    ListView list;
    Button newPlaylist;
    Cursor cursor;
    DatabaseAdapter Database;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.select_playlist,null);

        newPlaylist = (Button)v.findViewById(R.id.addNewPlaylist);
        list=(ListView) v.findViewById(R.id.listView);
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
        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(this);
        newPlaylist.setOnClickListener(this);
        return v;

    }

    @Override
    public void onClick(View v) {
        StaticData.isNewPlaylistEmpty=false;
        FragmentManager FM =getFragmentManager();
        AddNewPlaylist addNewPlaylist=new AddNewPlaylist();
        addNewPlaylist.show(FM,"newPlaylist");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            cursor.moveToPosition(position);
            String str=cursor.getString(cursor.getColumnIndex(DatabaseAdapter.PlaylistName));
            Database.AddToPlaylist(StaticData.hashToAdd,Database.getPlayCode(str));
        } catch (SQLException e) {
            Log.e("SQL wali",e.toString());
        }
        Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
        Database.close();

        dismiss();

    }
}
