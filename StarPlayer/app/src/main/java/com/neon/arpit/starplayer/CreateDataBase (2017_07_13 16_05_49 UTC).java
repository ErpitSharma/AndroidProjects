package com.neon.arpit.starplayer;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by arpit on 02-02-2017.
 */

public class CreateDataBase extends AsyncTask {
    File file;
    Context context;
    Bitmap bitmap;
    byte[] AlbumArt;
    MediaMetadataRetriever meta;
    String Title, Album, Artist,Path;
    DatabaseAdapter Database;
    Boolean added=false;

    CreateDataBase(Context context)
    {
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        Database = new DatabaseAdapter(context);
        Database.open();

        StaticData.isDBDone=false;
        Database.InsertStatus(0);
        Database.DeleteMainTable();
        Database.SaveSortingBy(DatabaseAdapter.SongTitle, StaticData.order);
        Database.saveRepeatState(0);
        Database.saveShuffleState(0);
    }

    @Override
    protected void onPostExecute(Object o) {

        if (added) {
            Database.InsertStatus(1);
            if (Database.IsPlaylistnameNew("Favourites")<=0 ){
                Database.insertPlaylist("Favourites","");
            }
        }
        else
        {
            Toast.makeText(context, "No files found or no permission to access data", Toast.LENGTH_SHORT).show();
        }
        StaticData.isDBDone=true;
        Database.close();
    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {
            file = Environment.getExternalStorageDirectory();
            if (file.canRead()) {
                Traverse(file);
            }
            File ff = new File("/storage/sdcard1");
            if (ff.exists() && ff.canRead()) {
                Log.e("sdCard : ", " scanning");
                Traverse(ff);

            }
        } catch (Exception e) {
            Log.e("looking for songs","Failed");
        }

        return null;
    }
    void Traverse(File f) {
        File AllFiles[] = f.listFiles();
        int length=0;
        if (AllFiles != null) {
            length= AllFiles.length;
        }
        for (int i = 0; i < length; i++)
        {
            if (AllFiles[i].isDirectory())
            {
                Traverse(AllFiles[i]);
            }
            else if (AllFiles[i].isFile())
            {
                String name = AllFiles[i].getName();

                if (AllFiles[i].toString().toLowerCase().endsWith("mp3")   )
                {
//                    StaticData.SongPath.add(AllFiles[i].getAbsolutePath());
                    Path=AllFiles[i].getAbsolutePath();
                    meta = new MediaMetadataRetriever();
                    meta.setDataSource(Path);
                    Title =meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    if ((Title!= null))
                    {
                        if (Title.trim().isEmpty())
                        {
                            Title =name;
                        }
                    }else {
                        Title=name;
                    }
                    Artist =meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    if ((Artist!= null))
                    {
                        if (Artist.trim().isEmpty())
                        {
                            Artist ="unknown";
                        }
                    }else {
                        Artist="unknown";
                    }
                    Album =meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                    if ((Album!= null))
                    {
                        if (Album.trim().isEmpty())
                        {
                            Album ="unknown";
                        }
                    }else {
                        Album="Unknown";
                    }

                    AlbumArt= meta.getEmbeddedPicture();
                    if (AlbumArt!=null)
                    {
                        BitmapFactory.Options option= new BitmapFactory.Options();
                        option.inSampleSize=8;
                        bitmap = BitmapFactory.decodeByteArray(AlbumArt,0,AlbumArt.length,option);
                        ByteArrayOutputStream stream= new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                        AlbumArt=stream.toByteArray();
                    }
                    else
                    {
                        bitmap=null;
                        AlbumArt=null;
                    }

                    int a = IsArtistNew(Artist);
                    int b = IsAlbummNew(Album);
//                        StaticData.SongList.add(DB_Title);
                    Database.insertRow(Path,Title,b+"",a+"",AlbumArt,0);
                    added=true;
                }
                if (AllFiles[i].toString().toLowerCase().endsWith("mp4")   )
                {
                    Path=AllFiles[i].getAbsolutePath();
                    Log.e("found : ",Path);
                    Database.insertRow(Path,name,"","",null,1);
                    added=true;
                }

            }

        }


    }
    int Artcount = 0;
    int Albcount = 0;

    public int IsArtistNew(String artist) {


        int temp = Database.getArtCode(artist);

        if (temp > 0)
            return temp;

        Database.insertArtist(artist);
        Artcount++;
        return Artcount;
    }

    public int IsAlbummNew(String album)
    {
        int temp = Database.getAlbCode(album);
        if (temp > 0)
            return temp;
        Database.insertAlbum(album);
        Albcount++;
        return Albcount;
    }

}
