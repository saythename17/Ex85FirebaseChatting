package com.icandothisallday2020.ex85firebasechatting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends BaseAdapter {
    Context context;
    ArrayList<MItem> items;

    public ChatAdapter(Context context, ArrayList<MItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MItem item=items.get(position);//현재만들 순서의 뷰
        //1. Create View(New View)[ mymsgbox or othermsgbox ]
        View view=null;
        if(G.nickName.equals(item.name))
            view= LayoutInflater.from(context).inflate(R.layout.my_msgbox,parent,false);
        else view=LayoutInflater.from(context).inflate(R.layout.other_msgbox,parent,false);

        //2. Bind View(값을 연결)
        CircleImageView iv=view.findViewById(R.id.iv);
        TextView name=view.findViewById(R.id.tv_name);
        TextView msg=view.findViewById(R.id.tv_msg);
        TextView time=view.findViewById(R.id.tv_time);

        Glide.with(context).load(item.profileUrl).into(iv);

        name.setText(item.name);
        msg.setText(item.message);
        time.setText(item.time);




        return view;
    }
}
