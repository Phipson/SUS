package com.esri.arcgisruntime.sample.arrayadapter;

import java.util.ArrayList;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private final ArrayList<String> attachmentName;
    public CustomList(Activity context,
                      ArrayList<String> attachmentList) {
        super(context, ecomap.a2017.lahacks.sus.R.layout.attachment_entry, attachmentList);
        this.context = context;
        this.attachmentName = attachmentList;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(ecomap.a2017.lahacks.sus.R.layout.attachment_entry, null, true);

            holder = new ViewHolder();
            holder.textTitle = (TextView) convertView.findViewById(ecomap.a2017.lahacks.sus.R.id.attachment_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textTitle.setText(attachmentName.get(position));

        return convertView;
    }

    private static class ViewHolder {
        public TextView textTitle;
    }

}
