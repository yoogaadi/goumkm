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
import com.squareup.picasso.Picasso;

public class list_promosi extends ArrayAdapter {
    String[] string1;
    String[] string2;
    String[] image1;


    public list_promosi(Context context, String[] string1, String[] string2, String[] image1){
        super(context, R.layout.list_promosi,R.id.text_nama,string1);
        this.string1 = string1;
        this.string2 = string2;
        this.image1 = image1;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_promosi,parent,false);

        TextView string1_ = (TextView) row.findViewById(R.id.text_nama);
        TextView string2_ = (TextView) row.findViewById(R.id.text_deskripsi);
        ImageView image1_ = (ImageView) row.findViewById(R.id.image_promosi);
        string1_.setText(string1[position]);
        string2_.setText(string2[position]);
        try{
            Picasso.get()
                    .load(image1[position])
                    .placeholder(R.drawable.baseline_photo_camera_black_36dp)
                    .error(R.drawable.baseline_photo_camera_black_36dp)
                    .resize(400, 250)
                    .centerCrop()
                    .into(image1_);
        }catch (Exception e){
            System.out.println("ada error :"+e.toString());
        }
        return row;
    }
}
