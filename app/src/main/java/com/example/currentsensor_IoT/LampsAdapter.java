package com.example.currentsensor_IoT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LampsAdapter extends ArrayAdapter<Lamps> {
    private LayoutInflater inflater;
    private int layout;
    private List<Lamps> lampss;

    public LampsAdapter(Context context, int resourse, List<Lamps> lampss) {
        super(context, resourse, lampss);
        this.lampss = lampss;
        this.layout = resourse;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Lamps lamp = lampss.get(position);

        viewHolder.nameView.setText(lamp.getName());
        viewHolder.condView.setText(lamp.getCond());

        return convertView;
    }


    private class ViewHolder {
        final TextView nameView, condView;

        ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.nameLamp);
            condView = (TextView) view.findViewById(R.id.condLamp);
        }
    }
}
