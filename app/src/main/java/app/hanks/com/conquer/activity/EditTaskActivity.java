/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.PixelUtil;
import app.hanks.com.conquer.util.TaskUtil;
import app.hanks.com.conquer.view.RoundProgressBar;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuDrawable;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuView;

/**
 * Created by Hanks on 2015/6/3.
 */
public class EditTaskActivity extends BaseActivity {
    private Task task;

    private EditText         tv_name;
    private TextView         tv_time;
    private View             layout_task;
    private RoundProgressBar pb;

    private ImageButton      iv_sort;
    private MaterialMenuView materialMenu;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViews();
        bindData();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showAnim();
            }
        }, 50);
    }

    private void bindViews() {
        layout_task = findViewById(R.id.layout_task);
        tv_name = (EditText) findViewById(R.id.tv_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        pb = (RoundProgressBar) findViewById(R.id.pb);
        materialMenu = (MaterialMenuView) findViewById(R.id.material_menu);
        iv_sort = (ImageButton) findViewById(R.id.iv_sort);
    }

    private void bindData() {
        task = (Task) getIntent().getSerializableExtra("task");
        tv_name.setText(task.getName());
        tv_time.setText(TaskUtil.getZixiTimeS(task.getTime()));
        int p = 4320 - TaskUtil.getDurationFromNow(task.getTime());
        if (p <= 0) p = 1;
        pb.setText(TaskUtil.getDescriptionTimeFromTimestamp(task.getTime()));
        if (task.getTime() <= System.currentTimeMillis()) {
            pb.setRoundWidth(0);
            pb.setText(TaskUtil.getZixiDateS(task.getTime()));
        } else {
            pb.setRoundWidth(PixelUtil.dp2px(6));
        }
        if (p < 100) p = 100;// 防止太小了
        pb.setProgress(p);

    }

    private void showAnim() {
        int y = getIntent().getIntExtra("y", 0);
        int[] loc = new int[2];
        layout_task.getLocationOnScreen(loc);
        L.d("y:" + y + "," + loc[1]);
        layout_task.setVisibility(View.VISIBLE);
        layout_task.setTranslationY(y - loc[1]);
        layout_task.animate().translationY(0).setDuration(400).start();
        iv_sort.animate().alpha(0).setDuration(400);
        materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
    }

    @Override
    protected View getContentView() {
        return View.inflate(context, R.layout.activity_edit_task, null);
    }

    @Override
    protected void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {

    }
}
