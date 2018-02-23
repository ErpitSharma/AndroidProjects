package com.example.arpit.fanpick;

import java.util.ArrayList;
        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;
/**
 * Created by Arpit on 23-07-2017.
 */

public class customStackView extends BaseAdapter {

    ArrayList<StoryRoutes> arrayList;
    LayoutInflater inflater;
    ViewHolder holder = null;

    public customStackView(Context context, ArrayList<StoryRoutes> arrayList) {
        this.arrayList = arrayList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arrayList.size();
    }

    @Override
    public StoryRoutes getItem(int pos) {
        // TODO Auto-generated method stub
        return arrayList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        // TODO Auto-generated method stub
        return pos;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.story_page, parent, false);
            holder = new ViewHolder();

            holder.text = (TextView) view.findViewById(R.id.pageTitle);
            holder.image = (TextView) view.findViewById(R.id.storyContent);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.text.setText(arrayList.get(pos).pageTitle);
        holder.image.setText(arrayList.get(pos).storyContent);

        return view;
    }

    public class ViewHolder {
        TextView text;
        TextView image;
    }

}