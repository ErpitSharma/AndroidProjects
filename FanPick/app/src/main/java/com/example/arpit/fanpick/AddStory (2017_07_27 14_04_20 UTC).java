package com.example.arpit.fanpick;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Arpit on 17-07-2017.
 */

public class AddStory extends Fragment implements View.OnClickListener{

    Button cancel, save, addOption;
    AutoCompleteTextView titleBox,optionBox;
    AppCompatEditText description, workSpace;
    ArrayList<String> options;
    ListView optionList;
    Communicator communicator;
    int height=50;
    private final String TAG="AddStory";
    StoryMetaData data;
    FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    FirebaseDatabase database;
    private String title;
    private Boolean isFileUploaded=false, isMetaUploaded=false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator=(Communicator)activity;
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.add_story,container,false);

        cancel=(Button)v.findViewById(R.id.cancel);
        save=(Button)v.findViewById(R.id.save);
        addOption=(Button)v.findViewById(R.id.addOptionButton);
        titleBox=(AutoCompleteTextView)v.findViewById(R.id.title);
        optionBox=(AutoCompleteTextView)v.findViewById(R.id.addOptionTextView);
        description=(AppCompatEditText)v.findViewById(R.id.description);
        workSpace=(AppCompatEditText)v.findViewById(R.id.workSpace);
        optionList=(ListView)v.findViewById(R.id.optionsList);

        options=new ArrayList<>();
        options.add("");
        optionList.setNestedScrollingEnabled(true);


        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        addOption.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();



        setTargetFragment(this,0);

        return v;
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.cancel)
        {
            FragmentManager fragmentManager=getFragmentManager();
            FragmentTransaction FT=fragmentManager.beginTransaction();
            FT.detach(this);
            FT.remove(this);
            FT.commit();
            communicator.Showfab(true);
        }
        if (v.getId()==R.id.addOptionButton)
        {
            String option=optionBox.getText().toString();
            if (!option.isEmpty())
            options.add(option);
            Log.e("option added",option+" "+options.size());
            optionList.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,options));
        }
        if (v.getId()==R.id.save)
        {
            title=titleBox.getText().toString();
            if(attemptSave())
           {
               SharedPreferences preferences=getActivity().getSharedPreferences("way-word_Preference",getActivity().MODE_PRIVATE);
               data=new StoryMetaData(title,description.getText().toString(),preferences.getString("userName","New User"),2.0f,1);
               data.options=this.options;
               DatabaseReference myRef = database.getReference("Stories").child(title);
               myRef.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                      isMetaUploaded=true;

                       if (isFileUploaded && isMetaUploaded) {
                           Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();
                           FragmentManager fragmentManager=getFragmentManager();
                           FragmentTransaction FT=fragmentManager.beginTransaction();
                           FT.remove(getTargetFragment());
                           FT.commit();
                           communicator.Showfab(true);
                           isMetaUploaded=false;
                       }
                   }

                   @Override
                   public void onCancelled(DatabaseError error) {
                       // Failed to read value
                       Log.w(TAG, "Failed to read value.", error.toException());
                   }
               });

               Uri file = null;
               StorageReference riversRef = null;
               try {
                   String filepath=Environment.getExternalStorageDirectory().toString()+"//wayword//"+title+".txt";
                   file = Uri.fromFile(new File(filepath));
                   riversRef = mStorageRef.child("Stories//"+mAuth.getCurrentUser().getUid()+"//"+title+".txt");
                   FileWriter writer = new FileWriter(filepath);
                   writer.append(workSpace.getText().toString());
                   writer.flush();
                   writer.close();

               } catch (Exception e) {
                   Log.e("creating file",e.toString());
               }

               riversRef.putFile(file)
                       .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                               isFileUploaded=true;

                               if (isFileUploaded && isMetaUploaded) {
                                   FragmentManager fragmentManager=getFragmentManager();
                                   FragmentTransaction FT=fragmentManager.beginTransaction();
                                   FT.remove(getTargetFragment());
                                   FT.commit();
                                   communicator.Showfab(true);
                                   isFileUploaded=false;
                               }
                           }

                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception exception) {
                               Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                           }
                       });
               myRef.setValue(data);

           }

        }

    }

    private Boolean attemptSave()
    {
        titleBox.setError(null);
        description.setError(null);

        Boolean cancel=false;
        View focus=null;
        if (title.trim().isEmpty())
        {
            titleBox.setError("Enter Title");
            focus=titleBox;
            cancel=true;
        }
        if (description.getText().toString().trim().isEmpty())
        {
            description.setError("Enter a Short Description");
            focus=description;
            cancel=true;
        }
        if (cancel) {
            focus.requestFocus();
            return false;
        }

        return true;
    }


}
