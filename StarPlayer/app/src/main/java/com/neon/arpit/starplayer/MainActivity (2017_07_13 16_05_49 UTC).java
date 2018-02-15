package com.neon.arpit.starplayer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements communicator, View.OnClickListener, AdapterView.OnItemClickListener, ViewPager.OnPageChangeListener{


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    CreateDataBase CDB;
    static Controls controller;
    ImageButton details,currentButton,favourIt;
    RelativeLayout quickMenu;
    static VideoView video;
//    TabLayout tabLayout;
    PagerTabStrip tabLayout;
    DatabaseAdapter Database;
    ListView currentQ;
    Boolean isVideoFS=false;
    static ImageView mainArt;
    Toolbar toolbar;
    View Vu;
    Boolean aBoolean = Boolean.FALSE;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    static public ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Vu=findViewById(R.id.clicker);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        currentQ=(ListView)findViewById(R.id.currentQ);
        currentButton=(ImageButton)findViewById(R.id.currentQButton);
        favourIt=(ImageButton)findViewById(R.id.favourIt);
        details=(ImageButton)findViewById(R.id.details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        quickMenu= (RelativeLayout) findViewById(R.id.quickMenu);
        mainArt=(ImageView)findViewById(R.id.mainArt);
        Log.e("The OnCreate"," is executing");
        video=(VideoView) findViewById(R.id.videoView);
        Database = new DatabaseAdapter(getApplicationContext());
        Database.open();

        StaticData.isDBDone=Database.isDBDone();
        if (!StaticData.isDBDone) {
            CDB = new CreateDataBase(getApplicationContext());
            CDB.execute();
        }else {
            StaticData.SortingBy = Database.getSortingBy();
            StaticData.order=Database.getSortOrder();
            StaticData.repeatFlag=Database.getRepeatState();
            StaticData.shuffleFlag=Database.getShuffleState();
            Log.e("Sorting is set",StaticData.SortingBy+" "+StaticData.order);
        }
        Database.close();

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        controller =new Controls();
        tabLayout = (PagerTabStrip) findViewById(R.id.tabs);
        mViewPager.setCurrentItem(1);
        mViewPager.addOnPageChangeListener(this);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.ControlSpace, controller, "Controlid");
        fragmentTransaction.commit();
        details.setOnClickListener(this);
        favourIt.setOnClickListener(this);
        currentButton.setOnClickListener(this);
        Vu.setOnClickListener(this);

//        AudioManager mAudioManager =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        ComponentName mReceiverComponent = new ComponentName(this,YourBroadcastReceiver.class);
//        mAudioManager.registerMediaButtonEventReceiver(mReceiverComponent);
// somewhere else
        //mAudioManager.unregisterMediaButtonEventReceiver(mReceiverComponent);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            Intent intent=new Intent(this,SettingsActivity.class);
//            startActivity(intent);
            if (StaticData.isDBDone) {
                CDB = new CreateDataBase(getApplicationContext());
                CDB.execute();
                SongList_Fragment.List.setVisibility(View.GONE);
                SongList_Fragment.List.setVisibility(View.VISIBLE);
                SongList_Fragment.List.requestFocus();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void play(Cursor cursor, int position) {

            StaticData.isVideoPlaying=false;
            controller.Play(cursor, position);
//        } catch (Exception e) {
//            Log.e("main activity via list",e.toString());
//
//        }
    }

    @Override
    public void showAlbumArt() {
        favourIt.setBackgroundResource(android.R.drawable.star_big_off);
        mViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        quickMenu.setVisibility(View.VISIBLE);
        mainArt.setVisibility(View.VISIBLE);
    }

    @Override
    public void showVideo() {
        mViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        video.setVisibility(View.VISIBLE);
        quickMenu.setVisibility(View.GONE);
        mainArt.setVisibility(View.GONE);
        Vu.setVisibility(View.VISIBLE);
    }

    @Override
    public void playVideo(Cursor cursor, int position) {
        StaticData.isVideoPlaying=true;

        controller.playVideo(cursor, position);
        showVideo();
    }



    @Override
    public void stop() {
        controller.Stop();
    }



    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.clicker)
        {
            Log.e("video","full screen");
            if (!isVideoFS)
            {
                Log.e("video","full screen on");
                isVideoFS=true;
                toolbar.setVisibility(View.GONE);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.hide(controller);
                fragmentTransaction.commit();            }
            else
            {
               fullScreenOff();
            }
        }
        if (v.getId()==R.id.details)
        {
            StaticData.pathToFile=StaticData.Path;
            android.support.v4.app.FragmentManager FM=getSupportFragmentManager();
            DetailsDialog DD= new DetailsDialog();
            DD.show(FM,"Double D");
        }

        if (v.getId()==R.id.currentQButton){
            if (currentQ.getVisibility()==View.GONE)
            {
                setTitle("Songs Queue");
                populateList();
                currentQ.setVisibility(View.VISIBLE);
            }
            else
            {
                setTitle("Star Player");
                currentQ.setVisibility(View.GONE);
            }
        }

        if (v.getId()==R.id.favourIt)
        {
            DatabaseAdapter Database=new DatabaseAdapter(this);
            Database.open();
            int p=IsPlaylistnameNew("Favourites",Database);
            if (p<=0) {
                Database.insertPlaylist("Favourites","");
                Toast.makeText(this, "Created Favourites Playlist", Toast.LENGTH_SHORT).show();
            }

            Cursor c=Database.getSong(StaticData.rowId);
            c.moveToFirst();
            String s=c.getString(c.getColumnIndex(DatabaseAdapter.HashValue));
            Database.AddToPlaylist(s,Database.getPlayCode("Favourites"));
            Database.close();
            favourIt.setBackgroundResource(android.R.drawable.star_big_on);

        }
    }

    public void fullScreenOff()
    {
        Log.e("video","full screen off");
        isVideoFS=false;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(controller);
        fragmentTransaction.commit();
        toolbar.setVisibility(View.VISIBLE);
    }


    public int IsPlaylistnameNew(String str, DatabaseAdapter database)
    {
        int temp = database.getPlayCode(str);
        if (temp >= 0)
            return temp;
        return -1;
    }


    public void populateList() {
        BaseAdapter adapter = new ImageCursorAdapter(this, // Context.
                R.layout.custom_list,
                Controls.cursor, new String[]{DatabaseAdapter.AlbumArt, DatabaseAdapter.SongTitle, DatabaseAdapter.Artist},
                new int[]{R.id.albumArt, R.id.ListTitle, R.id.ListArtist});
        currentQ.setAdapter(adapter);
        currentQ.setTextFilterEnabled(true);
        currentQ.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        stop();
        if (StaticData.shuffleFlag == 0) {
            StaticData.position = position;
        } else {
            StaticData.swaper(StaticData.List, 0, StaticData.List.indexOf(position));
            StaticData.position = 0;
        }
        play(null, StaticData.position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if (position==1)
        {
            SongList_Fragment.List.requestFocus();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new PlayList_Fragment();
            if (position == 1)
                return new SongList_Fragment();
            if (position == 2)
                return new AlbumList_Fragment();
            if (position == 3)
                return new ArtistList_Fragment();
            else
                return  new VideoList_Fragment();
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Playlist";
                case 1:
                    return "Songs";
                case 2:
                    return "Albums";
                case 3:
                    return "Artists";
                case 4:
                    return "Videos";
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {

        if (mainArt.getVisibility() == View.VISIBLE || video.getVisibility()==View.VISIBLE)
        {
            if (isVideoFS)
            {
                fullScreenOff();
            }
            else if (currentQ.getVisibility()==View.GONE)
            {
                if (video.isPlaying())
                {
                    video.pause();
                    StaticData.isVideoPaused=true;
                    Controls.PlayPause.setBackgroundResource(R.drawable.play);
                }
                Vu.setVisibility(View.GONE);
                video.setVisibility(View.INVISIBLE);
                mainArt.setVisibility(View.INVISIBLE);
                quickMenu.setVisibility(View.GONE);
                mViewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                setTitle("Star Player");
                currentQ.setVisibility(View.GONE);
            }


        }
        else if (mViewPager.getCurrentItem() == 1) {
            if (SongList_Fragment.LA.getVisibility() == View.GONE) {
                if (aBoolean) {
                    super.onBackPressed();
                    return;
                }

                this.aBoolean = true;

                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        aBoolean = false;
                    }
                }, 2000);
            } else
            {
                SongList_Fragment.List.setVisibility(View.VISIBLE);
                SongList_Fragment.LA.setVisibility(View.GONE);
                setTitle("Star Player");
                SongList_Fragment.searchView.setQuery("",true);
            }
        }
        else
        {
            if (PlayList_Fragment.songList.getVisibility()==View.VISIBLE && mViewPager.getCurrentItem()==0)
            {
                PlayList_Fragment.songList.setVisibility(View.GONE);
                PlayList_Fragment.List.setVisibility(View.VISIBLE);
            }
            else if (ArtistList_Fragment.songList.getVisibility()==View.VISIBLE && mViewPager.getCurrentItem()==3)
            {
                ArtistList_Fragment.songList.setVisibility(View.GONE);
                ArtistList_Fragment.List.setVisibility(View.VISIBLE);
                ArtistList_Fragment.Name.setVisibility(View.GONE);
            }
            else if (AlbumList_Fragment.songList.getVisibility() == View.VISIBLE && mViewPager.getCurrentItem()==2)
            {
                AlbumList_Fragment.songList.setVisibility(View.GONE);
                AlbumList_Fragment.List.setVisibility(View.VISIBLE);
                AlbumList_Fragment.Name.setVisibility(View.GONE);

            }
            else
                mViewPager.setCurrentItem(1);
        }

    }
}

interface communicator
{
    void play(Cursor cursor, int position);
    void stop();
    void showAlbumArt();
    void showVideo();
    void playVideo(Cursor cursor, int position);
}


