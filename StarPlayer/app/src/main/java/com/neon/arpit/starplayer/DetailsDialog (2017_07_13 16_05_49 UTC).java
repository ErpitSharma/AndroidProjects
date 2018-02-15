package com.neon.arpit.starplayer;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by arpit on 18-03-2017.
 */
public class DetailsDialog extends DialogFragment {

    TextView name, title, size, location, type, album, artist;
    DatabaseAdapter database;
    ImageView icon;
    Cursor cursor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.details_dialog,null);

        name=(TextView)v.findViewById(R.id.name);
        title=(TextView)v.findViewById(R.id.title);
        size=(TextView)v.findViewById(R.id.size);
        artist=(TextView)v.findViewById(R.id.artist);
        album=(TextView)v.findViewById(R.id.album);
        icon=(ImageView)v.findViewById(R.id.icon);
        type=(TextView)v.findViewById(R.id.type);
        location=(TextView)v.findViewById(R.id.location);


        database=new DatabaseAdapter(getContext());
        database.open();
        File f= new File(StaticData.pathToFile);

        cursor=database.getSong(StaticData.rowId);

        cursor.moveToFirst();
        name.setText(f.getName());
        size.setText(f.length()+" bytes");

        title.setText(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.SongTitle)));
        artist.setText(database.getArtistName(cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.Artist))));
        album.setText(database.getAlbumName(cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.Album))));
        location.setText(f.getAbsolutePath());
        type.setText("MP3");

        setAlbumArt();
        database.close();
        cursor.close();

        return v;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void setAlbumArt()
    {


            byte art[];
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(getContext(), Uri.parse(StaticData.pathToFile));
            if (mmr.getEmbeddedPicture()!=null)
            {
                try
                {
                    art = mmr.getEmbeddedPicture();
                    Bitmap Albumart = BitmapFactory.decodeByteArray(art, 0, art.length);
                    icon.setImageBitmap(Albumart);
                }
                catch (Exception r)
                {
                    Log.e("setting AlbumArt" ,"caught exception :"+r.toString());
                    icon.setImageResource(R.drawable.minion);

                }
            }else
            {
                icon.setImageResource(R.drawable.minion);

            }



    }
}
