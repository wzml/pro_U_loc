package com.example.login_server.UserInfo.fri_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.login_server.R;

import java.util.ArrayList;
import java.util.List;

public class FriAdaopter extends ArrayAdapter<Fri> {
    private int resourceId;
    private List<Fri> mfriList = new ArrayList<>();

    public FriAdaopter(Context context, int textViewResourceId, List<Fri> objects) {
        super(context, textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    public void refresh(List<Fri> friList) {
        if(friList != null && friList.size() >= 0){
            mfriList.clear();
            mfriList.addAll(friList);
            //通知刷新页面
            notifyDataSetChanged();
        }
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