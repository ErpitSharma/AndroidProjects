package com.neon.arpit.starplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Delayed;

/**
 * Created by arpit on 02-02-2017.
 */
public class Controls extends Fragment implements
        View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        View.OnLongClickListener
//        ,TextureView.SurfaceTextureListener
{

    static RelativeLayout bottomBack;
    SeekBar seekBar;
    TextView TimePlayed, TimeLeft, SongTitle;
    static ImageButton PlayPause;
    ImageButton Previous, Next,Shuffle,Repeat;
    int min, sec, totsec;
    int cumin, cusec, totcusec;
    Handler handler;
    static Cursor cursor;
    DatabaseAdapter Database;
    communicator comm;
    View gestureView;
    Context context;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm=(communicator)activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container, savedInstanceState);
        View v= inflater.inflate(R.layout.control_fragment, container,false);
        seekBar=(SeekBar)v.findViewById(R.id.seeku);
        PlayPause=(ImageButton)v.findViewById(R.id.playPause);
        Next=(ImageButton)v.findViewById(R.id.bottomNext);
        Previous=(ImageButton)v.findViewById(R.id.bottomPrevious);
        SongTitle=(TextView)v.findViewById(R.id.bottomSongname);
        TimeLeft=(TextView)v.findViewById(R.id.timeLeft);
        TimePlayed=(TextView)v.findViewById(R.id.timePlayed);
        Shuffle=(ImageButton) v.findViewById(R.id.shuffle);
        Repeat=(ImageButton) v.findViewById(R.id.repeat);
        bottomBack=(RelativeLayout)v.findViewById(R.id.bottomBack);


        Database= new DatabaseAdapter(getContext());
        Database.open();
        cursor= Database.getSongs();

        final String DEBUG_TAG="touch";


//        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);//"android.intent.action.MEDIA_BUTTON"
//        MediaButtonIntentReceiver r = new MediaButtonIntentReceiver();
//        filter.setPriority(1000); //this line sets receiver priority
//        registerReceiver(r, filter);




        PlayPause.setOnClickListener(this);
        Next.setOnClickListener(this);
        Previous.setOnClickListener(this);
        Shuffle.setOnClickListener(this);
        Repeat.setOnClickListener(this);
        bottomBack.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        handler = new Handler();



        SongTitle.setText(StaticData.Title);
        if (StaticData.Song!=null) {
            totsec = StaticData.Song.getDuration() / 1000;
            min = totsec / 60;
            sec = totsec % 60;
            seekBar.setMax(totsec);
            handler.postDelayed(run,0);
            setAlbumArt();
        }

        Shuffle.setBackgroundResource(R.drawable.shuffle_off);
                if (StaticData.repeatFlag==0)
                {
                    Repeat.setBackgroundResource(R.drawable.repeat_off);
                }
                else if (StaticData.repeatFlag==1)
                {
                    Repeat.setBackgroundResource(R.drawable.repeat_one);
                }
                else
                {
                    Repeat.setBackgroundResource(R.drawable.repeat_all);
                }

                if (StaticData.shuffleFlag==0)
                {
                    Shuffle.setBackgroundResource(R.drawable.shuffle_off);
                }else
                {
                    if (StaticData.List.isEmpty()) {
                        StaticData.List=new ArrayList<>();
                        for (int i=0; i<cursor.getCount();i++) {
                            StaticData.List.add(i);
                            Log.e("array at "+i,StaticData.List.get(i)+"");
                        }
                        StaticData.Shuffle(StaticData.List);
                        StaticData.position=0;
                    }
                    Shuffle.setBackgroundResource(R.drawable.shuffle_on);
                }


        return v;
    }


    Runnable run=new Runnable()
    {
        @Override
        public void run()
        {
            if (StaticData.Song!=null)
            {
                if (StaticData.Song.isPlaying())
                {
                    updateTime();
                    handler.postDelayed(run,500);
                }
            }
        }
    };

    Runnable vidRun=new Runnable()
    {
        @Override
        public void run()
        {
            if (!StaticData.isVideoPaused)
            {
                if (MainActivity.video.isPlaying())
                {
                    updateTime();
                    handler.postDelayed(vidRun,500);
                }
            }
        }
    };

    void updateTime() {
        if (!StaticData.isVideoPlaying && StaticData.Song.isPlaying()) {
            totcusec = StaticData.Song.getCurrentPosition() / 1000;
        }else {
            totcusec = MainActivity.video.getCurrentPosition() / 1000;

        }
        seekBar.setProgress(totcusec);
        cumin = totcusec / 60;
        cusec = totcusec % 60;
        String str=cumin + ":" + cusec;
        DateFormat sdf=new SimpleDateFormat("mm:ss");
        Date date=null;
        try {
            date=sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TimePlayed.setText(sdf.format(date));

        int remmin = (totsec - totcusec) / 60;
        int remsec = (totsec - totcusec) % 60;
        String str2=remmin + ":" + remsec;
        DateFormat sdf1=new SimpleDateFormat("mm:ss");
        Date date1=null;
        try {
            date1=sdf1.parse(str2);
        } catch (ParseException e) {
            Log.e("Update time",e.toString());
        }
        TimeLeft.setText("-"+sdf.format(date1));

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser)
        {
            if(StaticData.Song !=null)
            {
                int seek = (seekBar.getProgress() * 1000)-100;
                StaticData.Song.seekTo(seek);
                updateTime();
            }
            if(!StaticData.isVideoPaused)
            {
                int seek = (seekBar.getProgress() * 1000)-100;
                MainActivity.video.seekTo(seek);
                updateTime();
            }

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar ProgressBar) {
        if (StaticData.Song != null) {
            int seek = (ProgressBar.getProgress() * 1000)-100;
            StaticData.Song.seekTo(seek);
            updateTime();
        }
        if(!StaticData.isVideoPaused)
        {
            int seek = (seekBar.getProgress() * 1000)-100;
            MainActivity.video.seekTo(seek);
            updateTime();
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.playPause)
        {
            if (!StaticData.isVideoPlaying) {
                if (StaticData.Song != null) {
                    if (!StaticData.Song.isPlaying()) {
                        try {
                            StaticData.isVideoPlaying=false;
                            Play(cursor, StaticData.position);
                        } catch (Exception e) {
                            Stop();
                            Log.e("on press of play",e.toString());

                        }
                    } else {
                        Pause();
                    }
                }
                else
                {
                    try {
                        Play(cursor,StaticData.position);

                    } catch (Exception e) {
                        Log.e("on press of play",e.toString());
                    }
                }
            } else {
                    if (!MainActivity.video.isPlaying()) {
                        try {
                            playVideo(cursor, StaticData.position);
                        } catch (Exception e) {
                            Stop();
                            Log.e("on press of play",e.toString());

                        }
                    } else {
                        Pause();
                    }

            }
        }



        if (v.getId()==R.id.bottomNext)
        {
            Stop();
            try {
                Next(cursor);
            } catch (Exception e) {
                Log.e("next",e.toString());
            }
        }
        if (v.getId()==R.id.bottomPrevious)
        {
            Stop();
            try {
                Previous(cursor);
            } catch (Exception e){
                Log.e("Previous",e.toString());
            }
        }

        if (v.getId()==R.id.shuffle)
        {
            if (StaticData.shuffleFlag==0)
            {
                StaticData.shuffleFlag=1;

                StaticData.List=new ArrayList<>();
                for (int i=0; i<cursor.getCount();i++) {
                    StaticData.List.add(i,i);
                }
                StaticData.Shuffle(StaticData.List);
                StaticData.swaper(StaticData.List,0,StaticData.List.indexOf(StaticData.position));
                StaticData.position=0;

                Shuffle.setBackgroundResource(R.drawable.shuffle_on);
            }
            else{
                StaticData.shuffleFlag=0;
                StaticData.position=StaticData.List.get(StaticData.position);
                StaticData.List.clear();
                Shuffle.setBackgroundResource(R.drawable.shuffle_off);
            }
            Database.saveShuffleState(StaticData.shuffleFlag);
        }
        if (v.getId()==R.id.repeat)
        {
            if (StaticData.repeatFlag==2)
            {
                StaticData.repeatFlag=0;
                    Repeat.setBackgroundResource(R.drawable.repeat_off);
            }
            else if (StaticData.repeatFlag==0)
            {
                StaticData.repeatFlag=1;
                Repeat.setBackgroundResource(R.drawable.repeat_one);
            }
            else
            {
                StaticData.repeatFlag=2;
                Repeat.setBackgroundResource(R.drawable.repeat_all);
            }
            Database.saveRepeatState(StaticData.repeatFlag);
        }

        if (v.getId()==R.id.bottomBack)
        {
            if (!StaticData.isVideoPlaying)
            {
                if (StaticData.Song!=null)
                {
                    StaticData.rowId=cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.Row_ID));
                    comm.showAlbumArt();
                }
            }else
            {
                comm.showVideo();
                MainActivity.video.seekTo(totcusec*1000);
               playVideo(cursor, StaticData.position);
            }
        }

    }

    public void playVideo(Cursor curse, int position) {
        if (StaticData.isVideoPaused)
        {
            Log.e("video","Resumed");
            if (!MainActivity.video.isPlaying())
            {
                MainActivity.video.start();
                StaticData.isVideoPaused=false;
                totsec = MainActivity.video.getDuration() / 1000;
                min = totsec / 60;
                sec = totsec % 60;
                seekBar.setMax(totsec);
                Log.e("totsec",totsec+"");
                PlayPause.setBackgroundResource(R.drawable.pause);
                handler.postDelayed(vidRun,500);
            }
        }
        else
        {
            Log.e("video","preparing");
            if (curse!=null) {
                this.cursor=curse;
            }

            MediaPlayer.OnCompletionListener object= new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (StaticData.repeatFlag==2) {
                        try {
                            Stop();
                            Next(cursor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (StaticData.repeatFlag==1)
                    {
                        MainActivity.video.start();
                    }
                    else
                    {
                        if (StaticData.position ==cursor.getCount()-1)
                        {
                            Stop();
                        }
                        else
                        {
                            try {
                                Stop();
                                Next(cursor);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            MainActivity.video.setOnCompletionListener(object);


            if (StaticData.shuffleFlag==0) {
                cursor.moveToPosition(position);
            } else {
                Log.e("array value at",position+" is "+StaticData.List.get(position));
                cursor.moveToPosition(StaticData.List.get(position));
            }
            StaticData.Path=cursor.getString(cursor.getColumnIndex(DatabaseAdapter.Path));
            StaticData.rowId=cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.Row_ID));
            StaticData.hashToAdd=cursor.getString(cursor.getColumnIndex(DatabaseAdapter.HashValue));


            MainActivity.video.setVideoPath(StaticData.Path);
            MainActivity.video.start();
            StaticData.isVideoPaused=false;
            StaticData.isVideoPlaying=true;
            Log.e("video","is playing");

            PlayPause.setBackgroundResource(R.drawable.pause);
            StaticData.Title=cursor.getString(cursor.getColumnIndex(DatabaseAdapter.SongTitle));
            StaticData.currentId=cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.Row_ID));


            if(StaticData.Title.length()>=50)
            {
                SongTitle.setText(StaticData.Title.substring(0,45)+"...");
            }
            else
            {
                SongTitle.setText(StaticData.Title);
            }

            MainActivity.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    totsec = MainActivity.video.getDuration() / 1000;
                    min = totsec / 60;
                    sec = totsec % 60;
                    seekBar.setMax(totsec);
                    Log.e("totsec",MainActivity.video.getDuration()+"");

                    handler.postDelayed(vidRun,1000);
                }
            });


        }
    }


    void Play(final Cursor curse, int position) {
        if (StaticData.Song != null)
        {
            if (!StaticData.Song.isPlaying())
            {
                StaticData.Song.start();
                totsec = StaticData.Song.getDuration() / 1000;
                min = totsec / 60;
                sec = totsec % 60;
                seekBar.setMax(totsec);
                PlayPause.setBackgroundResource(R.drawable.pause);
                handler.postDelayed(run,500);
            }
        }
        else
        {
            StaticData.Song = new MediaPlayer();
            if (curse!=null) {
                this.cursor=curse;
            }

            MediaPlayer.OnCompletionListener object= new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (StaticData.repeatFlag==2) {
                        try {
                            Stop();
                            Next(cursor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (StaticData.repeatFlag==1)
                    {
                            StaticData.Song.start();
                    }
                    else
                    {
                        if (StaticData.position ==cursor.getCount()-1)
                        {
                            Stop();
                        }
                        else
                        {
                            try {
                                Stop();
                                Next(cursor);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            StaticData.Song.setOnCompletionListener(object);


            if (StaticData.shuffleFlag==0) {
                cursor.moveToPosition(position);
            } else {
                Log.e("array value at",position+" is "+StaticData.List.get(position));
                cursor.moveToPosition(StaticData.List.get(position));
            }
            StaticData.Path=cursor.getString(cursor.getColumnIndex(DatabaseAdapter.Path));
            StaticData.rowId=cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.Row_ID));
            StaticData.hashToAdd=cursor.getString(cursor.getColumnIndex(DatabaseAdapter.HashValue));

            File f = new File(StaticData.Path);
            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                StaticData.Song.setDataSource(fis.getFD());
                StaticData.Song.prepare();
                StaticData.Song.setAudioStreamType(AudioManager.STREAM_MUSIC);
                StaticData.Song.start();
                StaticData.isVideoPlaying=false;
                PlayPause.setBackgroundResource(R.drawable.pause);
                StaticData.Title=cursor.getString(cursor.getColumnIndex(DatabaseAdapter.SongTitle));
                StaticData.currentId=cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.Row_ID));

                totsec = StaticData.Song.getDuration() / 1000;
                min = totsec / 60;
                sec = totsec % 60;
                seekBar.setMax(totsec);
                handler.postDelayed(run,400);

                if(StaticData.Title.length()>=50)
                {
                    SongTitle.setText(StaticData.Title.substring(0,50)+"...");
                }
                else
                {
                    SongTitle.setText(StaticData.Title);
                }


                SongList_Fragment.List.setVisibility(View.GONE);
                SongList_Fragment.List.setVisibility(View.VISIBLE);
                SongList_Fragment.List.requestFocus();


                setAlbumArt();
            } catch (Exception e) {
                Toast.makeText(getContext(), "oops. something is wrong..", Toast.LENGTH_SHORT).show();
                Log.e("Playing","error"+e.toString());
                try {
                    Next(cursor);
                } catch (Exception e1)
                {
                    Log.e("catch in catch","error"+e.toString());
                }
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void setAlbumArt() {

        if (!StaticData.isVideoPlaying) {
            byte art[];
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, Uri.parse(StaticData.Path));
            if (mmr.getEmbeddedPicture() != null) {
                try {
                    art = mmr.getEmbeddedPicture();
                    Bitmap Albumart = null;
                    if (art.length>800000) {
                        BitmapFactory.Options option= new BitmapFactory.Options();
                        option.inSampleSize=2;
                        Albumart = BitmapFactory.decodeByteArray(art, 0, art.length,option);
                    }else
                        Albumart = BitmapFactory.decodeByteArray(art, 0, art.length);

                    MainActivity.mainArt.setImageBitmap(Albumart);
                    Drawable d = new BitmapDrawable(getResources(), Albumart);
                    MainActivity.mViewPager.setBackground(d);
                } catch (Exception r) {
                    Log.e("setting AlbumArt", "caught exception :" + r.toString());
                    MainActivity.mainArt.setImageResource(R.drawable.minion);
                    MainActivity.mViewPager.setBackgroundResource(R.drawable.background);

                }
            } else {
                MainActivity.mainArt.setImageResource(R.drawable.minion);
                MainActivity.mViewPager.setBackgroundResource(R.drawable.background);

            }
        } else {
            MainActivity.mViewPager.setBackgroundResource(R.drawable.background);
            StaticData.Song.setScreenOnWhilePlaying(true);
//            if (MainActivity.video.isAvailable()) {
//
//                MainActivity.video.setSurfaceTextureListener(this);
//
//            }
        }


    }


    void Pause() {
        if (StaticData.Song!=null) {
            StaticData.Song.pause();
        }
        if (MainActivity.video!=null) {
            MainActivity.video.pause();
            StaticData.isVideoPaused=true;
        }
        PlayPause.setBackgroundResource(R.drawable.play);
    }


    void Stop()
    {
        if (StaticData.Song !=null) {
            StaticData.Song.stop();
            StaticData.Song = null;
        }
        if (MainActivity.video !=null) {
            MainActivity.video.stopPlayback();
            StaticData.isVideoPaused=false;
        }
        TimeLeft.setText("00:00");
        TimePlayed.setText("00:00");
        SongTitle.setText("Select a Track");
    }

    private void Next(Cursor cursor) throws Exception {

            if (StaticData.position <cursor.getCount()-1) {
                StaticData.position++;
            }
            else
            {
                StaticData.position =0;
            }
        if (!StaticData.isVideoPlaying) {
            Play(cursor, StaticData.position);
        } else {
            playVideo(cursor,StaticData.position);
        }

    }


    private void Previous(Cursor cursor) throws Exception {

            if (StaticData.position ==0)
            {
                StaticData.position =(cursor.getCount()-1);

            }else
            {
                StaticData.position--;
            }
        if (!StaticData.isVideoPlaying) {
            Play(cursor, StaticData.position);
        } else {
            playVideo(cursor,StaticData.position);
        }    }




    @Override
    public boolean onLongClick(View v) {
        if (v.getId()==R.id.bottomNext)
        {
        }
        return false;
    }

}
