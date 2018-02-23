package com.example.arpit.fanpick;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Arpit on 22-07-2017.
 */

public class CustomStoryAdapter extends ArrayAdapter {


    Context context;
    ArrayList<StoryMetaData> stories;

    public CustomStoryAdapter(Context context,ArrayList<StoryMetaData> stories) {
        super(context,R.layout.story_view,stories);
        this.stories = stories;
        this.context=context;
    }
    private static class ViewHolder {
        TextView title, description, author;
        RatingBar rating;

    }



    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        final View result;

        if (convertView==null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.story_view, parent, false);

            viewHolder.title = (TextView) convertView.findViewById(R.id.storyTitleView);
            viewHolder.description = (TextView) convertView.findViewById(R.id.storyDescriptionView);
            viewHolder.author = (TextView) convertView.findViewById(R.id.storyAuthorView);
            viewHolder.rating = (RatingBar) convertView.findViewById(R.id.storyRatingView);

            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;


        viewHolder.title.setText(stories.get(position).title);
        viewHolder.description.setText(stories.get(position).description);
        viewHolder.author.setText(stories.get(position).author);
        viewHolder.rating.setMax(5);
        viewHolder.rating.setRating(stories.get(position).rating);



        return convertView;
    }
}
