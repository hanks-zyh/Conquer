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
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

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

    private EditText    tv_name;
    private TextView    tv_time;
    private ImageButton iv_sort;

    private RoundProgressBar pb;
    private SimpleDraweeView iv_home_bg;
    private MaterialMenuView materialMenu;

    private View layout_task;
    private View title_bg;
    private View ll_bottom;

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
        iv_home_bg = (SimpleDraweeView) findViewById(R.id.iv_home_bg);
        title_bg = findViewById(R.id.title_bg);
        ll_bottom = findViewById(R.id.ll_bottom);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_name.requestFocus();
            }
        },450);

    }

    private void bindData() {
        task = (Task) getIntent().getSerializableExtra("task");
        tv_name.setText(task.getName());
        tv_name.moveCursorToVisibleOffset();
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
        iv_sort.animate().alpha(0).setDuration(400).start();
        iv_home_bg.animate().alpha(1).setDuration(400).start();
        title_bg.animate().alpha(0).setDuration(400).start();
        ll_bottom.animate().alpha(1).setDuration(400).start();
        materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
    }

    /**
     * 初始化日历时间的选择控件
     */
    private void initDatePicker() {
//        mCalendar.add(Calendar.MINUTE, 60);// 设成60分钟后
//        setTimeAndTip(new SimpleDateFormat("yyyy/MM/dd").format(mCalendar.getTime()) + " "
//                + pad(mCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + pad(mCalendar.get(Calendar.MINUTE)));
//
//        timePickerDialog24h = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
//                setTimeAndTip(tv_date.getText()
//                        + " "
//                        + new StringBuilder().append(pad(hourOfDay)).append(":").append(pad(minute))
//                        .toString());
//            }
//        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
//
//        datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
//                Calendar c = Calendar.getInstance();
//                c.set(year, month, day);
//                headTime = c.getTimeInMillis();
//                initWeekday(c);
//                tv_date.setText(new StringBuilder().append(pad(year)).append("/").append(pad(month + 1))
//                        .append("/").append(pad(day)));
//                setTimeAndTip(tv_date.getText() + " " + tv_time.getText());
//            }
//        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
//        int curYear = mCalendar.get(Calendar.YEAR);
//        datePickerDialog.setYearRange(curYear, mCalendar.get(Calendar.MONTH) >= 11 ? curYear + 1 : curYear);
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
        if(TextUtils.isEmpty(name)){
            return;
        }
        task.setName(name);
//        task.setTime();

        super.onBackPressed();
    }
}
