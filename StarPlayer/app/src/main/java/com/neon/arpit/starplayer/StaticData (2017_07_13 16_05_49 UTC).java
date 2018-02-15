package com.neon.arpit.starplayer;

import android.media.MediaPlayer;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by arpit on 02-02-2017.
 */
public class StaticData {
    static public String Title = "Select a Track";
    static public String Path = "unknown";
    static public int rowId = -1;
    static public String SortingBy = DatabaseAdapter.SongTitle;
    static public String order = "ASC";
    static public int position = 0, shuffleFlag = 0, repeatFlag = 0, currentId = -1;
    static public MediaPlayer Song;
    static public boolean isDBDone = false;
    static public ArrayList<Integer> List = new ArrayList<>();
    public static boolean isNewPlaylistEmpty=false, isVideoPlaying=false,isVideoPaused=true;
    static public String hashToAdd="", pathToFile="unknown";

    public static void Shuffle(ArrayList<Integer> a)
    {
        int n=a.size();
        Random random=new Random();
        random.nextInt();
        for (int i=0;i<n;i++)
        {
            int change =i+random.nextInt(n-i);
            swaper(a, i, change);
        }
        List=a;
        for (int i=0;i<List.size();i++) {
            Log.e("shuffled list "+i," "+List.get(i));
        }

    }

    public static void swaper(ArrayList<Integer> a,int i,int change)
    {
        int helpu=a.get(i);
        a.set(i,a.get(change));
        a.set(change,helpu);


    }

}
