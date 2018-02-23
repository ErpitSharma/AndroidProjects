package com.example.arpit.fanpick;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by Arpit on 21-07-2017.
 */

public class UpdateUserInfo extends Fragment implements View.OnClickListener{

    Button cancel, save;
    AutoCompleteTextView userName, quote;
    Communicator communicator;
    private final String TAG="AddStory";
    StoryMetaData data;
    FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    FirebaseDatabase database;
    ImageButton previewImage;
    SharedPreferences preferences;
    Bitmap bitmap;
    InputStream stream;
    byte[] by=null;
    File localFile;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator=(Communicator)activity;
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.edit_user_info,container,false);

        cancel=(Button)v.findViewById(R.id.userEditCancel);
        save=(Button)v.findViewById(R.id.userEditSave);
        userName=(AutoCompleteTextView)v.findViewById(R.id.nameEdit);
        quote=(AutoCompleteTextView)v.findViewById(R.id.quoteEdit);
        previewImage =(ImageButton)v.findViewById(R.id.previewImage);


        previewImage.setOnClickListener(this);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        SharedPreferences preferences=getActivity().getSharedPreferences("way-word_Preference",getActivity().MODE_PRIVATE);
        userName.setText(preferences.getString("userName","New User"));
        quote.setText(preferences.getString("userQuote","No Quote"));
        localFile=null;
        try {
            localFile = File.createTempFile("images", "png");
        } catch (IOException e) {
            Log.e("setting pic",e.toString());
        }
        StorageReference storage=FirebaseStorage.getInstance().getReference().child("Images//"+mAuth.getCurrentUser().getUid());

        storage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                Log.e("setting pic","done");
                Bitmap pic=BitmapFactory.decodeFile(localFile.getPath());
                previewImage.setImageBitmap(pic);
            }
        });
        setTargetFragment(this,0);
        return v;
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK)
            try {
                // We need to recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                stream = getActivity().getContentResolver().openInputStream(
                        data.getData());
                Rect rec=new Rect(2,2,2,2);
                BitmapFactory.Options option=new BitmapFactory.Options();
                option.inSampleSize=2;
                bitmap = BitmapFactory.decodeStream(stream,rec,option);
                previewImage.setImageBitmap(bitmap);
                ByteArrayOutputStream oStream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,oStream);
                by=oStream.toByteArray();
                stream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.previewImage)
        {
            pickImage();
        }
        if (v.getId()==R.id.userEditCancel)
        {
            FragmentManager fragmentManager=getFragmentManager();
            FragmentTransaction FT=fragmentManager.beginTransaction();
            FT.remove(this);
            FT.commit();
            communicator.Showfab(true);
        }

        if (v.getId()==R.id.userEditSave)
        {
            if(attemptSave())
            {
                UserMetaData userMetaData=new UserMetaData(userName.getText().toString(),quote.getText().toString(),1);
                DatabaseReference myRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid());

                myRef.setValue(userMetaData);

                if (by!=null)
                {
                   addNewPicture();
                }

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            preferences=getActivity().getSharedPreferences("way-word_Preference",getActivity().MODE_PRIVATE);
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putString("userName",userName.getText().toString());
                            editor.putString("userQuote",quote.getText().toString().trim());
                            editor.apply();
                            editor.commit();

                        communicator.updateNavHeader();


                            FragmentManager fragmentManager=getFragmentManager();
                            FragmentTransaction FT=fragmentManager.beginTransaction();
                            FT.remove(getTargetFragment());
                            FT.commit();
                            communicator.Showfab(true);
                        }


                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });


            }

        }

    }

    void addNewPicture()
    {
        mStorageRef=mStorageRef.child("Images//"+mAuth.getCurrentUser().getUid());
        mStorageRef.putBytes(by).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("Profile pic","updated");
                communicator.updateNavHeader();
            }
        });
    }

    private Boolean attemptSave()
    {
        userName.setError(null);

        Boolean cancel=false;
        View focus=null;
        if (userName.getText().toString().trim().isEmpty())
        {
            userName.setError("Enter a Name");
            focus=userName;
            cancel=true;
        }

        if (cancel) {
            focus.requestFocus();
            return false;
        }

        return true;
    }


}
