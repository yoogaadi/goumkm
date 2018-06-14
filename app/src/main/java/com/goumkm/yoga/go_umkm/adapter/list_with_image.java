package com.goumkm.yoga.go_umkm.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.goumkm.yoga.go_umkm.R;

public class list_with_image extends ArrayAdapter {
    String[] string1;
    int[] images;

    public list_with_image(Context context, String[] string1, int[] images){
        super(context, R.layout.list_with_image,R.id.text_item,string1);
        this.string1 = string1;
        this.images = images;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_with_image,parent,false);
        TextView string1_ = (TextView) row.findViewById(R.id.text_item);
        ImageView image1_ = (ImageView) row.findViewById(R.id.icon_item);
        string1_.setText(string1[position]);
        image1_.setImageResource(images[position]);

        return row;
    }
}