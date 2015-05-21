/*
 * Created by Hanks
 * Copyright (c) 2015 Hanks. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.hanks.com.conquer.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.FriendZixiFragAapter;
import app.hanks.com.conquer.adapter.MyZixiAdapter;
import app.hanks.com.conquer.adapter.ZixiAdapterItemAnimator;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.config.Constants;
import app.hanks.com.conquer.fragment.FriendZixiFragment;
import app.hanks.com.conquer.fragment.MenuFragment;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.AudioUtils;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.PixelUtil;
import app.hanks.com.conquer.util.SP;
import app.hanks.com.conquer.util.ZixiUtil;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuDrawable;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuView;

/**
 * Created by Administrator on 2015/5/17.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final int NOTIFY_MY     = 0;
    private static final int NOTIFY_FRIEND = 1;
    private DrawerLayout     drawerLayout;
    private MaterialMenuView materialMenu;
    private ViewPager        lv_friend;
    private RecyclerView     mRecyclerView;
    private MyZixiAdapter    myAdapter;
    private ArrayList<Task>  listTask, listTask2;
    private MenuFragment         menuFragment;// 侧滑菜单Fragment
    private FriendZixiFragAapter friendAdapter;
    private ImageView            iv_arraw;
    private ImageButton          iv_sort;
    private AudioUtils aUtils = AudioUtils.getInstance();

    private int downY, dy;
    private static final int     DRAG_MINHEIGHT = PixelUtil.dp2px(50);
    private              boolean animing        = false;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            @SuppressWarnings("unchecked")
            List<Task> list = (List<Task>) msg.obj;
            switch (msg.what) {
                case NOTIFY_MY:
                    listTask.clear();
                    listTask.addAll(list);
                    myAdapter.notifyDataSetChanged();
                    break;
                case NOTIFY_FRIEND:
                    listTask2.clear();
                    listTask2.addAll(list);
                    friendAdapter.notifyDataSetChanged();
                    break;
            }
        }

        ;
    };
    private PopupWindow popWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        materialMenu = (MaterialMenuView) findViewById(R.id.material_menu);
        materialMenu.setOnClickListener(this);
        findViewById(R.id.iv_add).setOnClickListener(this);
        iv_arraw = (ImageView) findViewById(R.id.iv_arraw);
        iv_sort = (ImageButton) findViewById(R.id.iv_sort);
        iv_sort.setOnClickListener(this);
        iv_arraw.setVisibility(View.GONE);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);// 侧滑控件
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int arg0) {
            }

            @Override
            public void onDrawerSlide(View arg0, float arg1) {
            }

            @Override
            public void onDrawerOpened(View arg0) {
                materialMenu.animatePressedState(MaterialMenuDrawable.IconState.X);
            }

            @Override
            public void onDrawerClosed(View arg0) {
                materialMenu.animatePressedState(MaterialMenuDrawable.IconState.BURGER);
            }
        });
        // 侧滑菜单
        menuFragment = new MenuFragment();
        changeFramgnt(R.id.left_drawer, menuFragment);

        // 主视图
        lv_friend = (ViewPager) findViewById(R.id.lv_friend);
        mRecyclerView = (RecyclerView) findViewById(R.id.lv_my);
//        View footerView = View.inflate(context, R.layout.layout_myzix_footer, null);
//        footerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                A.goOtherActivity(context, AllMyZixiActivity.class);
//            }
//        });
//        lv_my.addFooterView(footerView);

        listTask = new ArrayList<Task>();
        myAdapter = new MyZixiAdapter(this, listTask);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setItemAnimator(new ZixiAdapterItemAnimator());
        mRecyclerView.setAdapter(myAdapter);

        listTask2 = new ArrayList<Task>();
        friendAdapter = new FriendZixiFragAapter(getSupportFragmentManager(), listTask2);
        lv_friend.setAdapter(friendAdapter);
        iv_arraw.setOnTouchListener(new ArrowTouch());
    }

    class ArrowTouch implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            if (animing) return false;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downY = (int) ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    dy = (int) ev.getRawY() - downY;
                    if (dy > 0 && dy < DRAG_MINHEIGHT) ViewHelper.setTranslationY(iv_arraw, dy);
                    break;
                case MotionEvent.ACTION_UP:
                    dy = (int) ev.getRawY() - downY;
                    if (dy != 0) iv_arraw.animate().translationY(0).setDuration(400).start();
                    if (dy >= DRAG_MINHEIGHT - 10) {
                        animing = true;
                        animToOther();
                    }
                    break;
            }
            return true;
        }
    }

    /**
     * 获取好友或者其他人的自习，让用户自习设置选择优先级</br> 0.优先时间近 1.优先好友的 2.优先本学院 3.优先本学校的 4.优先位置近的 5.其他
     */
    private void getOtherZixi() {
        ZixiUtil.getNetZixiNotUser(context, currentUser, new ZixiUtil.GetZixiCallBack() {
            @Override
            public void onSuccess(List<Task> list) {
                if (CollectionUtils.isNotNull(list)) {
                    iv_arraw.setVisibility(View.VISIBLE);
                    Message.obtain(handler, NOTIFY_FRIEND, list).sendToTarget();
                } else iv_arraw.setVisibility(View.GONE);
            }

            @Override
            public void onError(int errorCode, String msg) {
            }
        });
    }


    public void animToOther() {
        final Task task = listTask2.get(lv_friend.getCurrentItem());
        L.d("lv_friend大小：" + lv_friend.getChildCount() + "，listZixi2大小：" + listTask2.size() + "当前item：" + lv_friend.getCurrentItem() + ","
                + task.toString());
        View fragment = ((FriendZixiFragment) friendAdapter.getFragment(lv_friend.getCurrentItem())).getRootView();
        if (fragment == null) L.d("	View fragment 获取ViewPager当前视图为空");
        ImageView iv_card_bg = (ImageView) fragment.findViewById(R.id.iv_card_bg);
        final View ll_audio = fragment.findViewById(R.id.ll_audio);
        fragment.findViewById(R.id.iv_del).setVisibility(View.GONE);
        ViewGroup rl_1 = (ViewGroup) fragment.findViewById(R.id.rl_1);
        ViewGroup rl_2 = (ViewGroup) fragment.findViewById(R.id.rl_2);
        if (null != task.getAudioUrl()) {
            ImageButton ib_play = (ImageButton) ll_audio.findViewById(R.id.ib_play);
            // 播放按钮
            ib_play.setImageResource(R.drawable.play_audio);
            ib_play.setTag("play");
            ib_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aUtils.play(context, ll_audio, task.getAudioUrl());
                }
            });
        }
        if (null != task.getCardBgUrl()) {
            loader.displayImage(task.getCardBgUrl(), iv_card_bg, option_pic);
        }

        final ViewGroup visibleList;
        final ViewGroup invisibleList;
        if (rl_2.getVisibility() == View.GONE) {
            visibleList = rl_1;
            invisibleList = rl_2;
        } else {
            invisibleList = rl_1;
            visibleList = rl_2;
        }
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(visibleList, "rotationY", 0f, 90f);
        visToInvis.setDuration(200);
        visToInvis.setInterpolator(new AccelerateInterpolator());
        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(invisibleList, "rotationY", -90f, 0f);
        invisToVis.setDuration(200);
        invisToVis.setInterpolator(new AccelerateInterpolator());
        visToInvis.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator arg0) {
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                visibleList.setVisibility(View.GONE);
                invisToVis.start();
                invisibleList.setVisibility(View.VISIBLE);
                animing = false;
            }

            @Override
            public void onAnimationCancel(Animator arg0) {
            }
        });
        visToInvis.start();
    }

    /**
     * 获取我的自习
     */
    private void getMyZixi() {
        // 访问数据库在子线程中进行
        new Thread() {
            public void run() {
                // 1.获取本地数据库
                List<Task> list = ZixiUtil.getAfterZixi(context);
                if (CollectionUtils.isNotNull(list)) {
                    Message.obtain(handler, NOTIFY_MY, list).sendToTarget();
                }
                L.i("我的本地自习长度" + listTask.size());
                // 2.获取网络，可能是换手机了，或者是没有添加过自习，或者是当前时间以后没有自习
                if (listTask.size() <= 0) {
                    ZixiUtil.getNetAfterZixi(context, currentUser, Constants.MAIN_MYZIXI_LIMIT, new ZixiUtil.GetZixiCallBack() {
                        @Override
                        public void onSuccess(List<Task> list) {
                            if (CollectionUtils.isNotNull(list)) {
                                Message.obtain(handler, NOTIFY_MY, list).sendToTarget();
                            }
                        }

                        @Override
                        public void onError(int errorCode, String msg) {
                        }
                    });
                }
            }

            ;
        }.start();
    }

    /**
     * 切换侧滑菜单布局打开或关闭
     */
    public void toggle() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
            materialMenu.animatePressedState(MaterialMenuDrawable.IconState.BURGER);
        } else {
            materialMenu.animatePressedState(MaterialMenuDrawable.IconState.X);
            drawerLayout.openDrawer(Gravity.START);
        }
    }

    /**
     * 当MainAtivity恢复时，获取一次更新
     */
    @Override
    protected void onStart() {
        getMyZixi();
        getOtherZixi();
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.material_menu:
                toggle();
                break;
            case R.id.iv_add:
                A.goOtherActivity(context, AddTaskActivity.class);
                break;
            case R.id.iv_sort:
                showSelectSort();
                break;
        }
    }

    /**
     * 弹出选择排序的popupWindow
     */
    private void showSelectSort() {
        int defaultSort = (Integer) SP.get(context, Constants.SP_SORT, 0);
        View v = View.inflate(context, R.layout.pop_sort, null);
        RadioGroup rg = (RadioGroup) v.findViewById(R.id.rg);
        rg.check(defaultSort);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SP.put(context, Constants.SP_SORT, checkedId);
                if (popWin != null && popWin.isShowing()) popWin.dismiss();
            }
        });
        popWin = new PopupWindow(v, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popWin.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.card_bg));
        // popWin.setFocusable(true);
        popWin.setOutsideTouchable(true); // 点击popWin
        // 以处的区域，自动关闭
        // popWin.showAtLocation(iv_sort, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置在屏幕中的显示位置
        popWin.showAsDropDown(iv_sort, 0, -iv_sort.getHeight() + 10);
    }

    @Override
    protected void onStop() {
        sendBroadcast(new Intent(Constants.ACTION_DESTORY_PLAYER));
        super.onStop();
    }

    @Override
    public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
    }

    @Override
    public View getContentView() {
        return View.inflate(context, R.layout.common_title, null);
    }
}
