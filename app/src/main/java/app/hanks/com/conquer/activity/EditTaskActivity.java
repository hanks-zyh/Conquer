/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Calendar;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.db.TaskDao;
import app.hanks.com.conquer.otto.BusProvider;
import app.hanks.com.conquer.otto.RefreshEvent;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.NotifyUtils;
import app.hanks.com.conquer.util.PixelUtil;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.util.TaskUtil;
import app.hanks.com.conquer.view.RoundProgressBar;
import app.hanks.com.conquer.view.datetime.timepicker.RadialPickerLayout;
import app.hanks.com.conquer.view.datetime.timepicker.TimePickerDialog;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuDrawable;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuView;

/**
 * Created by Hanks on 2015/6/3.
 */
public class EditTaskActivity extends BaseActivity {
    private Task task;

    private View layout_task;
    private View title_bg;
    private View ll_bottom;

    private EditText tv_name;
    private TextView tv_time;

    private ImageButton iv_search;
    private ImageButton iv_sort;

    private MaterialMenuView materialMenu;
    private RoundProgressBar pb;
    private SimpleDraweeView iv_home_bg;

    private View ll_audio;
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
        iv_search = (ImageButton) findViewById(R.id.iv_search);
        iv_home_bg = (SimpleDraweeView) findViewById(R.id.iv_home_bg);
        title_bg = findViewById(R.id.title_bg);
        ll_bottom = findViewById(R.id.ll_bottom);
        ll_audio = findViewById(R.id.ll_audio);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_name.requestFocus();
            }
        }, 450);

    }

    private void bindData() {
        task = (Task) getIntent().getSerializableExtra("task");
        tv_name.setText(task.getName());
        tv_name.setSelection(tv_name.length());
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
        pb.setProgressNoAnim(p);
        if (task.getImageUrl() != null) {
            iv_home_bg.setImageURI(Uri.parse(task.getImageUrl()));
        }


        materialMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        String path = task.getAudioUrl();
        if (path != null) {
            final ImageButton ib_play = (ImageButton) ll_audio.findViewById(R.id.ib_play);
            final ProgressBar pb = (ProgressBar) ll_audio.findViewById(R.id.pb2);
            ib_play.setTag("play");
            ib_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ib_play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ib_play.getTag().equals("play")) {
                                ib_play.setImageResource(R.drawable.pause_audio);
                                ib_play.setTag("pause");
                                NotifyUtils.palyAudio(context, ib_play, pb, task.getAudioUrl());
                            } else {
                                ib_play.setTag("play");
                                ib_play.setImageResource(R.drawable.play_audio);
                                NotifyUtils.pauseAudio(ib_play);
                            }
                        }
                    });
                }
            });
        }
        if (task.getNote() != null) {
            ((TextView) findViewById(R.id.tv_note)).setText(task.getNote());
        }
    }

    private void showAnim() {
        int y = getIntent().getIntExtra("y", 0);
        int[] loc = new int[2];
        layout_task.getLocationOnScreen(loc);
        L.d("y:" + y + "," + loc[1]);
        layout_task.setVisibility(View.VISIBLE);
        layout_task.setTranslationY(y - loc[1]);
        layout_task.animate().translationY(0).setDuration(400).start();
        iv_sort.animate().alpha(0).setDuration(400).start();
        iv_search.animate().alpha(0).setDuration(400).start();
        iv_home_bg.animate().alpha(1).setDuration(400).start();
        title_bg.animate().alpha(0).setDuration(400).start();
        ll_bottom.animate().alpha(1).setDuration(400).start();
        materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
        String path = task.getAudioUrl();
        if (path != null) {
            ll_audio.animate().alpha(1).setDuration(300).start();
        }
    }

    /**
     * 初始化日历时间的选择控件
     */
    public void showDatePicker(View v) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(task.getTime());


        String timeStr = tv_time.getText().toString().trim();
        int hour = Integer.parseInt(timeStr.substring(0, 2));
        int min = Integer.parseInt(timeStr.substring(3, 5));
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, min);

        TimePickerDialog timePickerDialog24h = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                tv_time.setText(
                        new StringBuilder().append(pad(hourOfDay)).append(":").append(pad(minute))
                                .toString());
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
        timePickerDialog24h.show(getFragmentManager(), "");
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    protected View getContentView() {
        return View.inflate(context, R.layout.activity_edit_task, null);
    }

    @Override
    protected void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {

    }


    @Override
    public void onBackPressed() {
        String name = tv_name.getText().toString();
        if (TextUtils.isEmpty(name) && task.getRepeat() == 0) {
            return;
        }
        task.setName(name);
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(task.getTime());

        String timeStr = tv_time.getText().toString().trim();
        int hour = Integer.parseInt(timeStr.substring(0, 2));
        int min = Integer.parseInt(timeStr.substring(3, 5));
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, min);
        task.setTime(mCalendar.getTimeInMillis());


        if (task.getTime() < System.currentTimeMillis() && task.getRepeat() == 0) {
            T.show(context, "时间已经过去了╮(-_-)╭");
            return;
        }

        new TaskDao(context).update(task);

        BusProvider.getInstance().post(new RefreshEvent());

        super.onBackPressed();
    }
}
