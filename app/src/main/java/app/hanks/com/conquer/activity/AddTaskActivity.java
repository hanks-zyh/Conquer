package app.hanks.com.conquer.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.Card;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.bean.User;
import app.hanks.com.conquer.config.Constants;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.AlertDialogUtils;
import app.hanks.com.conquer.util.AlertDialogUtils.EtOkCallBack;
import app.hanks.com.conquer.util.AlertDialogUtils.OkCallBack;
import app.hanks.com.conquer.util.AudioUtils;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.MsgUtils;
import app.hanks.com.conquer.util.PixelUtil;
import app.hanks.com.conquer.util.RecordUtil;
import app.hanks.com.conquer.util.SP;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.util.TaskUtil;
import app.hanks.com.conquer.util.TaskUtil.UpLoadListener;
import app.hanks.com.conquer.util.Tasks;
import app.hanks.com.conquer.util.TimeUtil;
import app.hanks.com.conquer.view.AutoCompleteArrayAdapter;
import app.hanks.com.conquer.view.FlowLayout;
import app.hanks.com.conquer.view.RevealBackgroundView;
import app.hanks.com.conquer.view.datetime.datepicker.DatePickerDialog;
import app.hanks.com.conquer.view.datetime.datepicker.DatePickerDialog.OnDateSetListener;
import app.hanks.com.conquer.view.datetime.timepicker.RadialPickerLayout;
import app.hanks.com.conquer.view.datetime.timepicker.TimePickerDialog;
import app.hanks.com.conquer.view.datetime.timepicker.TimePickerDialog.OnTimeSetListener;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuDrawable;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.v3.listener.SaveListener;

public class AddTaskActivity extends BaseActivity implements OnClickListener, RevealBackgroundView.OnStateChangeListener {

    private static final int      REQUES_IMG    = 0;
    private static final int      REQUES_FRIEND = 1;
    private final        Calendar mCalendar     = Calendar.getInstance();
    boolean isFirst = true;
    private String               tag;
    private AutoCompleteTextView et_name;
    private TextView             tv_time, tv_time_tip;
    private TimePickerDialog timePickerDialog24h;
    private DatePickerDialog datePickerDialog;
    private TextView         tv_date;
    private TextView         wk_0, wk_1, wk_2, wk_3, wk_4, wk_5, wk_6;
    private TextView day_0, day_1, day_2, day_3, day_4, day_5, day_6;
    private String imgUrl   = null;
    private String audioUrl = null;
    private ImageView  iv;// 添加的图片
    private View       ll_audio;
    private FlowLayout ll_at_friend;
    private List<String> atFriends = new ArrayList<String>();
    private List<User>   at        = new ArrayList<User>();
    private AudioUtils aUtils;
    // 标记第一个的时间基准
    private long       headTime;
    private String note = null;
    private RevealBackgroundView vRevealBackground;

    private MaterialMenuView material_menu;
    private View             iv_sort; //title上面的


    private View ll_bottom; //底部操作
    private View layout_date;//日期选择
    private View currentTime; //显示选择的时间
    private View ib_audio, ib_theme, ib_img, ib_at, ib_save;

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public static void startUserProfileFromLocation(int[] startingLocation, Activity mainActivity) {
        Intent intent = new Intent(mainActivity, AddTaskActivity.class);
        intent.putExtra("startingLocation", startingLocation);
        mainActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setupRevealBackground(savedInstanceState);
    }

    private void init() {
        // 播放音频的
        aUtils = AudioUtils.getInstance();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_time_tip = (TextView) findViewById(R.id.tv_time_tip);
        tv_date = (TextView) findViewById(R.id.tv_title);
        material_menu = (MaterialMenuView) findViewById(R.id.material_menu);
        iv_sort = findViewById(R.id.iv_sort);
        et_name = (AutoCompleteTextView) findViewById(R.id.et_name);

        ll_audio = findViewById(R.id.ll_audio);
        ll_audio.setVisibility(View.GONE);
        iv = (ImageView) findViewById(R.id.iv);
        iv.setOnClickListener(this);
        material_menu.setState(MaterialMenuDrawable.IconState.BURGER);
        material_menu.setOnClickListener(this);
        ll_at_friend = (FlowLayout) findViewById(R.id.ll_at_friend);


        ll_bottom = findViewById(R.id.ll_bottom);
        layout_date = findViewById(R.id.layout_date);
        currentTime = findViewById(R.id.currentTime);

        ib_at = findViewById(R.id.ib_at);
        ib_img = findViewById(R.id.ib_img);
        ib_audio = findViewById(R.id.ib_audio);
        ib_theme = findViewById(R.id.ib_theme);
        ib_save = findViewById(R.id.ib_save);

        ib_theme.setScaleX(0.8f);
        ib_theme.setScaleY(0.8f);
        ib_at.setScaleX(0.8f);
        ib_at.setScaleY(0.8f);
        ib_audio.setScaleX(0.8f);
        ib_audio.setScaleY(0.8f);
        ib_img.setScaleX(0.8f);
        ib_img.setScaleY(0.8f);

        ib_at.setOnClickListener(this);
        ib_img.setOnClickListener(this);
        ib_audio.setOnClickListener(this);
        ib_theme.setOnClickListener(this);
        ib_save.setOnClickListener(this);

        wk_0 = (TextView) findViewById(R.id.wk_0);
        wk_1 = (TextView) findViewById(R.id.wk_1);
        wk_2 = (TextView) findViewById(R.id.wk_2);
        wk_3 = (TextView) findViewById(R.id.wk_3);
        wk_4 = (TextView) findViewById(R.id.wk_4);
        wk_5 = (TextView) findViewById(R.id.wk_5);
        wk_6 = (TextView) findViewById(R.id.wk_6);

        day_0 = (TextView) findViewById(R.id.day_0);
        day_1 = (TextView) findViewById(R.id.day_1);
        day_2 = (TextView) findViewById(R.id.day_2);
        day_3 = (TextView) findViewById(R.id.day_3);
        day_4 = (TextView) findViewById(R.id.day_4);
        day_5 = (TextView) findViewById(R.id.day_5);
        day_6 = (TextView) findViewById(R.id.day_6);

        wk_0.setOnClickListener(this);
        wk_1.setOnClickListener(this);
        wk_2.setOnClickListener(this);
        wk_3.setOnClickListener(this);
        wk_4.setOnClickListener(this);
        wk_5.setOnClickListener(this);
        wk_6.setOnClickListener(this);

        day_0.setOnClickListener(this);
        day_1.setOnClickListener(this);
        day_2.setOnClickListener(this);
        day_3.setOnClickListener(this);
        day_4.setOnClickListener(this);
        day_5.setOnClickListener(this);
        day_6.setOnClickListener(this);

        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        headTime = d.getTime();
        initWeekday(c);

        tv_date.setText(new SimpleDateFormat("yyyy/MM/dd").format(d));
        initDatePicker();
        tv_date.setOnClickListener(this);
        tv_time.setOnClickListener(this);

        //自动补全
        String[] course = Tasks.tasks;
        AutoCompleteArrayAdapter<String> adapter = new AutoCompleteArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, course);
        et_name.setAdapter(adapter);
        et_name.setDropDownHeight(metrics.heightPixels / 2);
        et_name.setThreshold(1);
        et_name.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (view.getText().length() > 0 && hasFocus) {
                    view.showDropDown();
                }
            }
        });
        et_name.setOnEditorActionListener(new SaveEditActionListener());

        Task task = (Task) getIntent().getSerializableExtra("task");
        if (task != null) {
            et_name.setText(task.getName());
        }

    }

    /**
     * title的动画
     */
    private void titleAnim() {
        layout_date.setTranslationY(-layout_date.getHeight());




        currentTime.setAlpha(0);
        et_name.setVisibility(View.VISIBLE);
        ll_bottom.setVisibility(View.VISIBLE);
        layout_date.setVisibility(View.VISIBLE);
        currentTime.setVisibility(View.VISIBLE);
        material_menu.animateState(MaterialMenuDrawable.IconState.ARROW);
        iv_sort.animate().rotation(90).setDuration(300).start();
        layout_date.animate().translationY(-PixelUtil.dp2px(3)).setDuration(300).start();
        currentTime.animate().alpha(1).setDuration(300).start();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int w = ib_save.getWidth() * 2;
        ib_theme.animate().translationY(-w).rotation(360).setDuration(300).setStartDelay(150).start();
        ib_at.animate().translationY((float) (-w *Math.sqrt(3)/ 2)).translationX(-w /2).rotation(360).setDuration(300).setStartDelay(100).start();
        ib_audio.animate().translationY(-w / 2).translationX((float) (-w * Math.sqrt(3) / 2)).rotation(360).setDuration(300).setStartDelay(50).start();
        ib_img.animate().translationX(-w).rotation(360).setDuration(300).start();
    }

    /**
     * 初始化头部
     */
    private void initWeekday(Calendar c) {

        Calendar tmp = Calendar.getInstance(Locale.CHINA);
        tmp.setTimeInMillis(c.getTimeInMillis());

        wk_0.setText(TimeUtil.getWeekDayD(tmp.get(Calendar.DAY_OF_WEEK)));
        day_0.setText(tmp.get(Calendar.DAY_OF_MONTH) + "");
        tmp.add(Calendar.DAY_OF_YEAR, 1);
        wk_1.setText(TimeUtil.getWeekDayD(tmp.get(Calendar.DAY_OF_WEEK)));
        day_1.setText(tmp.get(Calendar.DAY_OF_MONTH) + "");
        tmp.add(Calendar.DAY_OF_YEAR, 1);
        wk_2.setText(TimeUtil.getWeekDayD(tmp.get(Calendar.DAY_OF_WEEK)));
        day_2.setText(tmp.get(Calendar.DAY_OF_MONTH) + "");
        tmp.add(Calendar.DAY_OF_YEAR, 1);
        wk_3.setText(TimeUtil.getWeekDayD(tmp.get(Calendar.DAY_OF_WEEK)));
        day_3.setText(tmp.get(Calendar.DAY_OF_MONTH) + "");
        tmp.add(Calendar.DAY_OF_YEAR, 1);
        wk_4.setText(TimeUtil.getWeekDayD(tmp.get(Calendar.DAY_OF_WEEK)));
        day_4.setText(tmp.get(Calendar.DAY_OF_MONTH) + "");
        tmp.add(Calendar.DAY_OF_YEAR, 1);
        wk_5.setText(TimeUtil.getWeekDayD(tmp.get(Calendar.DAY_OF_WEEK)));
        day_5.setText(tmp.get(Calendar.DAY_OF_MONTH) + "");
        tmp.add(Calendar.DAY_OF_YEAR, 1);
        wk_6.setText(TimeUtil.getWeekDayD(tmp.get(Calendar.DAY_OF_WEEK)));
        day_6.setText(tmp.get(Calendar.DAY_OF_MONTH) + "");

        int t = (Integer) SP.get(context, "theme", 0);
        int color = getResources().getColor(R.color.theme_0);
        if (t == 1)
            color = getResources().getColor(R.color.theme_1);
        else if (t == 2)
            color = getResources().getColor(R.color.theme_2);
        else if (t == 3)
            color = getResources().getColor(R.color.theme_3);
        day_0.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_bg));
        day_1.setBackgroundColor(color);
        day_2.setBackgroundColor(color);
        day_3.setBackgroundColor(color);
        day_4.setBackgroundColor(color);
        day_5.setBackgroundColor(color);
        day_6.setBackgroundColor(color);
    }

    /**
     * 初始化日历时间的选择控件
     */
    private void initDatePicker() {
        mCalendar.add(Calendar.MINUTE, 10);// 设成10分钟后
        setTimeAndTip(new SimpleDateFormat("yyyy/MM/dd").format(mCalendar.getTime()) + " "
                + pad(mCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + pad(mCalendar.get(Calendar.MINUTE)));

        timePickerDialog24h = TimePickerDialog.newInstance(new OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                setTimeAndTip(tv_date.getText()
                        + " "
                        + new StringBuilder().append(pad(hourOfDay)).append(":").append(pad(minute))
                        .toString());
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);

        datePickerDialog = DatePickerDialog.newInstance(new OnDateSetListener() {
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                headTime = c.getTimeInMillis();
                initWeekday(c);
                tv_date.setText(new StringBuilder().append(pad(year)).append("/").append(pad(month + 1))
                        .append("/").append(pad(day)));
                setTimeAndTip(tv_date.getText() + " " + tv_time.getText());
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        int curYear = mCalendar.get(Calendar.YEAR);
        datePickerDialog.setYearRange(curYear, mCalendar.get(Calendar.MONTH) >= 11 ? curYear + 1 : curYear);
    }

    /**
     * 设置tv_time 和 tv_time_tip的文本内容
     *
     * @param string 类似yyyy/MM/dd HH:mm
     */
    private void setTimeAndTip(String string) {
        tv_time.setText(string.substring(string.length() - 5, string.length()));
        try {
            Date d = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(string);
            tv_time_tip.setTextColor(d.getTime() > System.currentTimeMillis() ? Color.GRAY : Color.RED);
            tv_time_tip.setText("(" + TaskUtil.getDescriptionTimeFromTimestamp(d.getTime()) + ")");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存任务
     */
    private void saveTask() {
        /** 1.彈出进度条dialog 或者 设置 保存按钮不可用 */
        /** 2.获取内容 */
        /** 3.提交服务器，成功finish ，失败关闭dialog或者设置保存按钮可以使用 */
        L.i("日期" + tv_date.getText());
        L.i("时间" + tv_time.getText());
        L.i("科目" + et_name.getText());
        L.i("分享" + true);
        String name = et_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            T.show(context, "请添加任务名称");
            return;
        }

        final Task task = new Task();
        task.setUser(currentUser);
        task.setName(name);
        try {
            Date time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(tv_date.getText().toString().trim()
                    .substring(0, 10)
                    + " " + tv_time.getText().toString().trim() + ":00");
            task.setTime(time.getTime());
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        if (task.getTime() < System.currentTimeMillis()) {
            T.show(context, "时间已经过去了 T_T ");
            return;
        }
        task.setShare(true);
        task.setHasAlerted(false);
        task.setCardBgUrl("http://file.bmob.cn/M00/D6/51/oYYBAFR9b8WAFsBlAAAqS_L5sFI605.jpg");
        task.setAudioUrl("http://file.bmob.cn/M00/D6/50/oYYBAFR9bguAMz02AACdR5Xly68154.amr");
        task.setNote("单身*一只，求任务陪同 ● v ● ");
        if (imgUrl != null)
            task.setCardBgUrl(imgUrl);
        if (audioUrl != null)
            task.setAudioUrl(audioUrl);
        if (note != null)
            task.setNote(note);
        if (atFriends.size() > 0) {
            task.setAtFriends(atFriends);
        }
        task.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                // 1.本地数据库存储
                try {
                    dbUtils.save(task);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (CollectionUtils.isNotNull(at)) {
                    sendInvite(task);
                }
                // 2.finish
                A.finishSelf(context);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                L.i(arg0 + "，task.save，" + arg1);
            }
        });
    }

    /**
     * 发送好友邀请
     */
    private void sendInvite(Task task) {
        for (User user : at) {
            Card card = new Card();
            card.setType(1);// 0。提醒卡
            card.setFid(currentUser.getObjectId());
            card.setFusername(currentUser.getUsername());
            card.setFnick(currentUser.getNick());
            card.setFavatar(currentUser.getAvatar());
            card.setZixiName(task.getName());
            card.setTime(task.getTime());
            card.settId(user.getObjectId());
            if (audioUrl != null)
                card.setAudioUrl(audioUrl);
            if (imgUrl != null)
                card.setImgUrl(imgUrl);
            card.setContent("来和我一块任务吧!");
            L.e(card.toString());
            String json = new Gson().toJson(card);
            L.d("发送邀请：" + user.getNick());
            MsgUtils.sendMsg(context, BmobChatManager.getInstance(context), user, json);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv:
                AlertDialogUtils.show(context, "删除图片", "真的要删除么？", "删除", "算了", new OkCallBack() {
                    @Override
                    public void onOkClick(DialogInterface dialog, int which) {
                        iv.setVisibility(View.GONE);
                        imgUrl = null;
                    }
                }, null);
                break;
            case R.id.ib_at:
                goSelectFriends();
                break;
            case R.id.ib_audio:
                showRecoder();
                break;
            case R.id.ib_img:
                selectPic();
                break;
            case R.id.ib_theme:
                editNote();
                break;
            case R.id.ib_save:
                if (currentUser != null) {
                    saveTask();
                } else {
                    // 登录对话框
                    T.show(context, "请先登录");
                }
                break;
            case R.id.tv_title:
                datePickerDialog.show(getFragmentManager(), tag);
                break;
            case R.id.tv_time:
                timePickerDialog24h.show(getFragmentManager(), tag);
                break;
            case R.id.material_menu:
                onBackPressed();
                break;
            case R.id.wk_0:
            case R.id.day_0:
                setCheckedDay(day_0, 0);
                break;
            case R.id.wk_1:
            case R.id.day_1:
                setCheckedDay(day_1, 1);
                break;
            case R.id.wk_2:
            case R.id.day_2:
                setCheckedDay(day_2, 2);
                break;
            case R.id.wk_3:
            case R.id.day_3:
                setCheckedDay(day_3, 3);
                break;
            case R.id.wk_4:
            case R.id.day_4:
                setCheckedDay(day_4, 4);
                break;
            case R.id.wk_5:
            case R.id.day_5:
                setCheckedDay(day_5, 5);
                break;
            case R.id.wk_6:
            case R.id.day_6:
                setCheckedDay(day_6, 6);
                break;
        }
    }

    /**
     * 备注
     */
    private void editNote() {
        AlertDialogUtils.showEditDialog(context, "输入悄悄话", "写好了", "算了", new EtOkCallBack() {
            @Override
            public void onOkClick(String s) {
                note = s;
            }
        });
    }

    /**
     * at好友
     */
    private void goSelectFriends() {
        if (currentUser != null) {
            Intent intent = new Intent(context, SelectFriendActivity.class);
            startActivityForResult(intent, REQUES_FRIEND);
        }
    }

    /**
     * 添加好友布局
     *
     * @param toUsers
     */
    private void addFriend(final User toUsers) {
        if (!atFriends.contains(toUsers.getObjectId())) {// 防止重复
            atFriends.add(toUsers.getObjectId());
            at.add(toUsers);
            final TextView tv = new TextView(context);
            // 必须
            MarginLayoutParams lp = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT,
                    MarginLayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(lp);
            tv.setBackgroundResource(R.drawable.btn_little_grey_f);
            if (atFriends.size() > 0)
                tv.setText("@" + toUsers.getNick() + "  ");
            ll_at_friend.addView(tv);
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_at_friend.removeView(tv);
                    atFriends.remove(toUsers);
                }
            });
        }
    }

    /**
     * 添加录音
     */
    private void showRecoder() {
        View v = View.inflate(context, R.layout.dialog_recorder, null);
        new RecordUtil(context, v, currentUser.getObjectId(), new RecordUtil.RecordStatusChangedListener() {
            @Override
            public void onRecordCompleled(String path) {
                if (path == null)
                    return;
                final File f = new File(path);
                if (f.exists()) {
                    ll_audio.setVisibility(View.VISIBLE);
                    // 为播放按钮设置点击事件
                    final ImageButton ib_play = (ImageButton) ll_audio.findViewById(R.id.ib_play);
                    ProgressBar pb = (ProgressBar) ll_audio.findViewById(R.id.pb);
                    pb.setProgress(0);
                    // 播放按钮
                    ib_play.setImageResource(R.drawable.play_audio);
                    ib_play.setTag("play");
                    ib_play.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            aUtils.play(context, ll_audio, f.getAbsolutePath());
                        }
                    });
                    // 删除布局
                    ll_audio.findViewById(R.id.iv_del).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialogUtils.show(context, "删除录音", "确定要删除录音吗？", "删除", "算了", new OkCallBack() {
                                @Override
                                public void onOkClick(DialogInterface dialog, int which) {
                                    ll_audio.setVisibility(View.GONE);
                                    audioUrl = null;
                                }
                            }, null);
                        }
                    });

                    TaskUtil.upLoadFile(context, f, new UpLoadListener() {
                        @Override
                        public void onSuccess(String url) {
                            audioUrl = url;
                        }

                        @Override
                        public void onFailure(int error, String msg) {
                        }
                    });
                }
            }

            @Override
            public void onRecordCancel() {
            }
        });
    }

    /**
     * 添加图片
     */
    private void selectPic() {
        Intent intent = new Intent(context, SelectPicActivity.class);
        intent.putExtra("noCut", true);
        startActivityForResult(intent, REQUES_IMG);
    }

    private void setCheckedDay(TextView day, int gap) {

        int t = (Integer) SP.get(context, "theme", 0);
        int color = getResources().getColor(R.color.theme_0);
        if (t == 1)
            color = getResources().getColor(R.color.theme_1);
        else if (t == 2)
            color = getResources().getColor(R.color.theme_2);
        else if (t == 3)
            color = getResources().getColor(R.color.theme_3);
        day_0.setBackgroundColor(color);
        day_1.setBackgroundColor(color);
        day_2.setBackgroundColor(color);
        day_3.setBackgroundColor(color);
        day_4.setBackgroundColor(color);
        day_5.setBackgroundColor(color);
        day_6.setBackgroundColor(color);
//		day.setBackgroundColor(getResources().getColor(R.color.red_button));
        day.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_bg));
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(headTime);
        c.add(Calendar.DAY_OF_YEAR, gap);
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.getText().toString()));
        tv_date.setText(new StringBuilder().append(pad(c.get(Calendar.YEAR))).append("/")
                .append(pad(c.get(Calendar.MONTH) + 1)).append("/").append(pad(c.get(Calendar.DAY_OF_MONTH))));
        setTimeAndTip(tv_date.getText() + " " + tv_time.getText());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.d(requestCode + "," + resultCode + "," + data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUES_IMG) {
                File f = new File(data.getStringExtra("photo_path"));
                L.e("照片路径：" + f.getAbsolutePath());
                if (f.exists()) {
                    iv.setVisibility(View.VISIBLE);
                    loader.displayImage("file://" + f.getAbsolutePath(), iv, option_pic);
                    uploadPic(f);
                }
            } else if (REQUES_FRIEND == requestCode) {
                User user = (User) data.getSerializableExtra("selectUser");
                if (user != null)
                    addFriend(user);
                else
                    L.e("返回的好友空空");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 上传
     *
     * @param f
     */
    private void uploadPic(File f) {
        TaskUtil.upLoadFile(context, f, new UpLoadListener() {
            @Override
            public void onSuccess(String url) {
                imgUrl = url;
            }

            @Override
            public void onFailure(int error, String msg) {
                T.show(context, "上传失败");
            }
        });
    }

    @Override
    protected void onDestroy() {
        sendBroadcast(new Intent(Constants.ACTION_DESTORY_PLAYER));
        super.onDestroy();
    }

    @Override
    public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back,
                             ImageButton ib_right, View shadow) {
        if (shadow != null) shadow.setVisibility(View.GONE);
    }

    @Override
    public View getContentView() {
        return View.inflate(context, R.layout.activity_add_zixi, null);
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground = (RevealBackgroundView) findViewById(R.id.vRevealBackground);
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra("startingLocation");
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return false;
                }
            });
        } else {
//            userPhotosAdapter.setLockedAnimations(true);
            vRevealBackground.setToFinishedFrame();
        }
    }

    private void setupUserProfileGrid() {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
//        rvUserProfile.setLayoutManager(layoutManager);
//        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                userPhotosAdapter.setLockedAnimations(true);
//            }
//        });
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
//            rvUserProfile.setVisibility(View.VISIBLE);
//            userPhotosAdapter = new UserProfileAdapter(this);
//            rvUserProfile.setAdapter(userPhotosAdapter);
            titleAnim();
        } else {
//            rvUserProfile.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 回车保存
     */
    private class SaveEditActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveTask();
            }
            return true;
        }
    }
}
