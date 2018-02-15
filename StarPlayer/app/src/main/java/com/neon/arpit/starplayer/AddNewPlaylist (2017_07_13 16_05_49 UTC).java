package com.neon.arpit.starplayer;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by arpit on 18-02-2017.
 */
public class AddNewPlaylist extends DialogFragment implements View.OnClickListener {
    EditText Ed;
    Button ok,cancel;
    DatabaseAdapter database;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.add_new_playlist_dialog,null);
        ok=(Button)v.findViewById(R.id.Accept);
        Ed=(EditText)v.findViewById(R.id.newName);
        cancel=(Button)v.findViewById(R.id.Decline);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        setCancelable(false);
        database =new DatabaseAdapter(getActivity());
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.Accept)
        {
            String str=Ed.getText().toString().trim();
            if (!str.isEmpty())
            {
                database.open();

                if (!StaticData.isNewPlaylistEmpty)
                {
                    try
                    {
                        int p=database.IsPlaylistnameNew(str);
                        if (p<=0) {
                            database.insertPlaylist(str,StaticData.hashToAdd);
                            Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
                            database.close();
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), "PlayLeest Allrady egg sist", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e("HAHA",e.toString().trim());
                        Toast.makeText(getActivity(), "Playlist ka naam badal ___", Toast.LENGTH_SHORT).show();
                                                                            // ^ Beeeep
                    }
                } else {
                    try
                    {
                        int p=database.IsPlaylistnameNew(str);
                        if (p<=0) {
                            database.insertPlaylist(str,"");
                            Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
                            database.close();
                            PlayList_Fragment.List.setVisibility(View.GONE);
                            PlayList_Fragment.List.setVisibility(View.VISIBLE);
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Playlist already exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e("HAHA",e.toString().trim());
                        Toast.makeText(getActivity(), "Enter valid name", Toast.LENGTH_SHORT).show();

                    }
                }

            }
            else
            {
                Toast.makeText(getActivity(), "Enter a valid naam", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            dismiss();
        }
    }

}

