package com.example.arpit.fanpick;

import java.util.List;

/**
 * Created by Arpit on 23-07-2017.
 */

public class StoryRoutes {
    String pageTitle;
    String storyContent;
    List<String> choices;

    public StoryRoutes(String pageTitle, String storyContent) {
        this.pageTitle = pageTitle;
        this.storyContent = storyContent;
    }
}
