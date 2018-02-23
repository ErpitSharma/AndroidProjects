package com.example.arpit.fanpick;

import android.media.Image;

/**
 * Created by Arpit on 21-07-2017.
 */

public class UserMetaData {

     String userName;
     String userQuote;
     int numberOfStories;

    public UserMetaData(){

    }
    public UserMetaData(String userName, String userQuote, int numberOfStories) {
        this.userName = userName;
        this.userQuote = userQuote;
        this.numberOfStories = numberOfStories;
    }


}
