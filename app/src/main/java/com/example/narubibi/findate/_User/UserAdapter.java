package com.example.narubibi.findate._User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.narubibi.findate.R;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    Context context;

    public UserAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        User item = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_cards, parent, false);

        TextView name = convertView.findViewById(R.id.user_name);
        ImageView imageView = convertView.findViewById(R.id.user_image);

        name.setText(item.getName());
        Glide.with(getContext()).load(item.getProfileImageUrl()).into(imageView);

        return convertView;
    }
}
