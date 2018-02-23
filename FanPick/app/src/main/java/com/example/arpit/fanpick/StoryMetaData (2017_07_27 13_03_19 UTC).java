package com.example.arpit.fanpick;

import java.util.List;

/**
 * Created by Arpit on 19-07-2017.
 */

public class StoryMetaData {

    String title;
    String description="adadaasd";
    String author;
    float rating = 5.0f;
    List<String> options;
    int noOfPages;

    public StoryMetaData()
    {

    }

    public StoryMetaData(String title,
                         String description,
                         String author,
                         float rating,
                         int noOfPages
    ) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.rating = rating;
        this.noOfPages = noOfPages;
    }
}
