/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.activity.AlertActivity;
import app.hanks.com.conquer.activity.FriendDataActivity;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.TaskUtil;
import app.hanks.com.conquer.util.TimeUtil;
import app.hanks.com.conquer.view.CircularImageView;

/**
 * Created by Hanks on 2015/5/21.
 */
public class OtherTaskAdapter extends RecyclerView.Adapter<OtherTaskAdapter.TaskViewHolder> {

    private final List<Task> list;
    private final Context    context;

    public OtherTaskAdapter(Context context, List<Task> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public OtherTaskAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = View.inflate(context, R.layout.item_friendzixi, null);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OtherTaskAdapter.TaskViewHolder holder, int position) {
        final Task task = list.get(position);
        try {
            holder.tv_created_time.setText("("
                    + TimeUtil.getDescriptionTimeFromTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(task.getCreatedAt())
                    .getTime()) + ")");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tv_note.setText(task.getNote());
        holder.tv_zixitime.setText(TaskUtil.getDescriptionTimeFromTimestamp(task.getTime()));
        holder.tv_time.setText(TaskUtil.getZixiTimeS(task));
        holder.tv_name.setText(task.getName());
        holder.tv_Nick.setText(task.getUser().getNick());
//        holder.tv_dis.setText(TaskUtil.getDistance(currentUser, task.getUser().getLocation()));
//        loader.displayImage(task.getUser().getAvatar(), iv_photo);
        holder.iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, FriendDataActivity.class);
                i.putExtra("friendName", task.getUser().getUsername());
                A.goOtherActivity(context, i);
            }
        });
        holder.iv_gender.setImageResource(task.getUser().isMale() ? R.drawable.ic_male : R.drawable.ic_female);
        // 陪她按钮
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, AlertActivity.class);
                intent.putExtra("task", task);
                A.goOtherActivity(context, intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    class TaskViewHolder extends RecyclerView.ViewHolder {
        CircularImageView iv_photo;
        ImageView         iv_gender;
        TextView          tv_Nick;
        TextView          tv_name;
        TextView          tv_time;
        TextView          tv_dis;
        TextView          tv_created_time;
        TextView          tv_zixitime;
        TextView          tv_note;

        public TaskViewHolder(View view) {
            super(view);

            iv_photo = (CircularImageView) view.findViewById(R.id.iv_photo);
            iv_gender = (ImageView) view.findViewById(R.id.iv_gender);
            tv_Nick = (TextView) view.findViewById(R.id.tv_nickname);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_dis = (TextView) view.findViewById(R.id.tv_dis);
            tv_created_time = (TextView) view.findViewById(R.id.tv_created_time);
            tv_zixitime = (TextView) view.findViewById(R.id.tv_zixitime);
            tv_note = (TextView) view.findViewById(R.id.tv_note);

        }


    }
}
