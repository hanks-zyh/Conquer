/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.Tag;

/**
 * Created by Hanks on 2015/5/29.
 */
public class TagAdapter extends MyBaseAdpter<Tag> {

    public TagAdapter(Context context, List<Tag> list) {
        super(context, (ArrayList)list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_list_tag, null);
        }
        TextView text = ViewHolder.get(convertView, R.id.text);
        text.setText(list.get(position).getName());
        return convertView;
    }
}
