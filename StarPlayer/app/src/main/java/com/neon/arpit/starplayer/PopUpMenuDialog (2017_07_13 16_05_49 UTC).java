package com.neon.arpit.starplayer;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

/**
 * Created by arpit on 18-03-2017.
 */
public class PopUpMenuDialog extends DialogFragment implements View.OnClickListener{

    Button details, delete, addPlaylist, favourite;
    DatabaseAdapter database;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.main_dialog,null);
        details=(Button)v.findViewById(R.id.detailsButton);
        delete=(Button)v.findViewById(R.id.deleteButton);
        addPlaylist=(Button)v.findViewById(R.id.playlistButton);
        favourite=(Button)v.findViewById(R.id.favouriteShortcut);


        details.setOnClickListener(this);
        delete.setOnClickListener(this);
        addPlaylist.setOnClickListener(this);
        favourite.setOnClickListener(this);

        database=new DatabaseAdapter(getContext());
        database.open();
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.detailsButton)
        {
            FragmentManager FM=getFragmentManager();
            DetailsDialog DD= new DetailsDialog();
            DD.show(FM,"Double D");
            dismiss();
        }
        if (v.getId()==R.id.playlistButton)
        {
            FragmentManager FM=getFragmentManager();
            SelectPlaylistDialog SPD= new SelectPlaylistDialog();
            SPD.show(FM,"SPD_Emergency");
            dismiss();
        }
        if (v.getId()==R.id.deleteButton)
        {
            File f= new File(StaticData.pathToFile);
            if(database.deleteRow(StaticData.rowId) && f.delete()  )
            {
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "error deleting", Toast.LENGTH_SHORT).show();
            }
            database.close();
            SongList_Fragment.List.setVisibility(View.GONE);
            SongList_Fragment.List.setVisibility(View.VISIBLE);
            SongList_Fragment.List.requestFocus();
            dismiss();
        }
        if (v.getId()==R.id.favouriteShortcut)
        {
            int p=database.IsPlaylistnameNew("Favourites");
            if (p<=0) {
                database.insertPlaylist("Favourites","");
                Toast.makeText(getActivity(), "Created 'Favourites' Playlist", Toast.LENGTH_SHORT).show();
            }

           database.AddToPlaylist(StaticData.hashToAdd,database.getPlayCode("Favourites"));
            database.close();

            dismiss();
        }
    }

}
