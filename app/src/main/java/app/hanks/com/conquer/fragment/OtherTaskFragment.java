/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.OtherTaskAdapter;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.util.AudioUtils;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.PixelUtil;
import app.hanks.com.conquer.util.TaskUtil;

/**
 * Created by Hanks on 2015/5/21.
 */
public class OtherTaskFragment extends BaseFragment{
    private RecyclerView        mRecylerView;
    private LinearLayoutManager mLayoutManager;
    private List<Task>          list = new ArrayList<>();
    private OtherTaskAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_other_task, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecylerView = (RecyclerView) getView().findViewById(R.id.recylerView);
        mLayoutManager = new LinearLayoutManager(context);
        mRecylerView.setLayoutManager(mLayoutManager);
        adapter = new OtherTaskAdapter(context, list);
        mRecylerView.setAdapter(adapter);
        getOtherZixi();
    }


    /**
     * 获取好友或者其他人的自习，让用户自习设置选择优先级</br> 0.优先时间近 1.优先好友的 2.优先本学院 3.优先本学校的 4.优先位置近的 5.其他
     */
    private void getOtherZixi() {
        TaskUtil.getNetZixiNotUser(context, currentUser, new TaskUtil.GetZixiCallBack() {
            @Override
            public void onSuccess(List<Task> list) {
                if (CollectionUtils.isNotNull(list)) {
                    setListData(list);
                }
            }

            @Override
            public void onError(int errorCode, String msg) {
            }
        });
    }


    /**
     * 设置list数据
     */
    private void setListData(List<Task> newList) {
        list.clear();
        list.addAll(newList);
        adapter.notifyDataSetChanged();
    }

    public void animToOther() {
       /* final Task task = list.get(mViewPager.getCurrentItem());
        L.d("lv_friend大小：" + mViewPager.getChildCount() + "，listZixi2大小：" + listTask2.size() + "当前item：" + mViewPager.getCurrentItem() + ","
                + task.toString());
        View fragment = ((FriendZixiFragment) friendAdapter.getFragment(mViewPager.getCurrentItem())).getRootView();
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
        visToInvis.start();*/
    }


    private static final int        DRAG_MINHEIGHT = PixelUtil.dp2px(50);
    private              AudioUtils aUtils         = AudioUtils.getInstance();
    private int downY, dy;
    private boolean animing = false;

    class ArrowTouch implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent ev) {
          /*  if (animing) return false;
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
            }*/
            return true;
        }
    }

}
