package com.example.socket_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class FriAdaopter extends ArrayAdapter<Fri> {
    private int resourceId;

    public FriAdaopter(Context context, int textViewResourceId, List<Fri> objects) {
        super(context, textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder.image = view.findViewById(R.id.list_image);
            viewHolder.name = view.findViewById(R.id.list_name);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        Fri fri = getItem(position);
        viewHolder.image.setImageResource(fri.getImageId());
        viewHolder.name.setText(fri.getName());
        return view;
    }
}

//
//class ViewHolder {
//    ImageView image;
//    TextView name;
//}