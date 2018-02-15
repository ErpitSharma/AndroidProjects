package com.neon.arpit.starplayer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by arpit on 26-04-2017.
 */
public class PopulateAlbums extends SimpleCursorAdapter {

    private Cursor c;
    DatabaseAdapter database;
    private Context context;
    public PopulateAlbums(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        this.c = c;
        this.context = context;

    }

    public View getView(int pos, View inView, ViewGroup parent) {
        View v = inView;
        database=new DatabaseAdapter(context);
        database.open();
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.album,null,false);
        }
        this.c.moveToPosition(pos);


        String lastName = null;
        lastName = c.getString(c.getColumnIndex(DatabaseAdapter.Album));
        Cursor cc=database.getAlbumSongs(c.getInt(c.getColumnIndex(DatabaseAdapter.Row_ID)));
        byte[] image = cc.getBlob(cc.getColumnIndex(DatabaseAdapter.AlbumArt));
        ImageView iv = (ImageView)v.findViewById(R.id.albumArt);
        if (image != null) {
            if(image.length > 3)
            {

                iv.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }
            else
            {
                iv.setImageResource(R.drawable.bc);
            }
        }
        else
        {
            iv.setImageResource(R.drawable.bc);
        }

        TextView lname = (TextView) v.findViewById(R.id.albumName);
        lname.setText(lastName);


        return(v);
    }


}
