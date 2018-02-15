package com.neon.arpit.starplayer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ImageCursorAdapter extends SimpleCursorAdapter {

    private Cursor c;
    DatabaseAdapter database;
    private Context context;
    public ImageCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
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
            v = inflater.inflate(R.layout.custom_list,null,false);
        }
        this.c.moveToPosition(pos);

        String firstName = null;
        String lastName = null;
        lastName = database.getArtistName(this.c.getInt(this.c.getColumnIndex(DatabaseAdapter.Artist)));
        firstName = this.c.getString(this.c.getColumnIndex(DatabaseAdapter.SongTitle));
        byte[] image = this.c.getBlob(this.c.getColumnIndex(DatabaseAdapter.AlbumArt));
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


      if (StaticData.currentId==c.getInt(c.getColumnIndex(DatabaseAdapter.Row_ID)))
      {
          RelativeLayout RL=(RelativeLayout)v.findViewById(R.id.Back);
          RL.setBackgroundResource(R.color.shade);
      }
        TextView fname = (TextView) v.findViewById(R.id.ListTitle);
        fname.setText(firstName);

        TextView lname = (TextView) v.findViewById(R.id.ListArtist);
        lname.setText(lastName);


        return(v);
    }


    void setSelectged()
    {

    }


}