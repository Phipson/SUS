package com.esri.arcgisruntime.sample.spinner;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 14leec1 on 2/4/2017.
 */

public class SpinnerAdapter extends ArrayAdapter<ItemData> {
    private final int groupid;
    private final ArrayList<ItemData> list;
    private final LayoutInflater inflater;

    public SpinnerAdapter(Activity context, int groupid, int id, ArrayList<ItemData>
            list) {
        super(context, id, list);
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid = groupid;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = inflater.inflate(groupid, parent, false);
        ImageView imageView = (ImageView) itemView.findViewById(ecomap.a2017.lahacks.sus.R.id.img);
        imageView.setImageResource(list.get(position).getImageId());
        TextView textView = (TextView) itemView.findViewById(ecomap.a2017.lahacks.sus.R.id.txt);
        textView.setText(list.get(position).getText());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup
            parent) {
        return getView(position, convertView, parent);

    }
}