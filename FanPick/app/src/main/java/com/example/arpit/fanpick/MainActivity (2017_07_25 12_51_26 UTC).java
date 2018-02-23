package com.example.arpit.fanpick;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , Communicator
{

    TextView userName,userQuote;
    private FirebaseAuth mAuth=null;
    FloatingActionButton fab=null;
    DrawerLayout drawer;
    ImageView profilePic;
    StorageReference storage;
    File localFile;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                fab.setClickable(false);
                fab.hide();
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction FT=fragmentManager.beginTransaction();
                FT.add(R.id.holder,new AddStory(),null);
                FT.commit();
            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View navHeaderView=navigationView.getHeaderView(0);

        profilePic=(ImageView)navHeaderView.findViewById(R.id.profilePic);
        userName=(TextView)navHeaderView.findViewById(R.id.userName);
        userQuote=(TextView)navHeaderView.findViewById(R.id.userQuote);

        updateNavHeader();

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction FT=fragmentManager.beginTransaction();
                FT.replace(R.id.holder,new UpdateUserInfo(),null);
                FT.commit();
                fab.hide();
                drawer.closeDrawer(Gravity.LEFT);

            }
        });





        navigationView.setNavigationItemSelectedListener(this);
    }

    public void updateNavHeader()
    {
        storage=FirebaseStorage.getInstance().getReference();
        SharedPreferences preferences=getSharedPreferences("way-word_Preference",MODE_PRIVATE);
        userName.setText(preferences.getString("userName","New User"));
        String quote=preferences.getString("userQuote","No Quote");
        if(quote.isEmpty())
            userQuote.setText("No quote");
        else userQuote.setText(quote);

        localFile=null;
        try {
            localFile = File.createTempFile("images", "png");
        } catch (IOException e) {
            Log.e("setting pic",e.toString());
        }
        storage=storage.child("Images//"+mAuth.getCurrentUser().getUid());

        storage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                Log.e("setting pic","done");
                Bitmap pic=BitmapFactory.decodeFile(localFile.getPath());
                profilePic.setImageBitmap(pic);
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            FragmentManager fragmentManager=getSupportFragmentManager();
            FragmentTransaction FT=fragmentManager.beginTransaction();
            FT.replace(R.id.holder,new StoryGallery(),null);
            FT.commit();
            drawer.closeDrawer(Gravity.LEFT);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {


        } else if (id == R.id.nav_send) {

            mAuth.signOut();
            Intent intent=new Intent(this, LoginActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void Showfab(Boolean show) {
        if (show)
        {
        fab.setClickable(true);
            fab.show();
        }
        else
        {
            fab.setClickable(false);
            fab.hide();
        }
    }
}

interface Communicator
{
    public void Showfab(Boolean show);
    public void updateNavHeader();
}
