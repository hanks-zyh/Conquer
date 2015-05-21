/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.MyZixiAdapter;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.config.Constants;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.TaskUtil;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by Hanks on 2015/5/22.
 */
public class MyTaskFragment extends BaseFragment {

    private RecyclerView        mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout  refreshLayout;
    private List<Task>          list = new ArrayList<>();
    private MyZixiAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_task, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recylerView);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyZixiAdapter(context, list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(1000);
        mRecyclerView.setAdapter(mAdapter);
        initRefreshLayout();
        //        View footerView = View.inflate(context, R.layout.layout_myzix_footer, null);
//        footerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                A.goOtherActivity(context, AllMyZixiActivity.class);
//            }
//        });
//        lv_my.addFooterView(footerView);
    }


    /**
     * 设置RecyclerView
     */
    private void initRefreshLayout() {
        refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_0));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyZixi();
            }
        });
    }


    /**
     * 获取我的自习
     */
    private void getMyZixi() {
        // 访问数据库在子线程中进行
        // 1.首先获取本地数据库
        List<Task> list = TaskUtil.getAfterZixi(context);
        if (CollectionUtils.isNotNull(list)) {
            setListData(list);
        }
        L.i("我的本地自习长度" + list.size());
        // 2.获取网络，可能是换手机了，或者是没有添加过自习，或者是当前时间以后没有自习
        if (list.size() <= 0) {
            TaskUtil.getNetAfterZixi(context, currentUser, Constants.MAIN_MYZIXI_LIMIT, new TaskUtil.GetZixiCallBack() {
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
}


    /**
     * 设置list数据
     */
    private void setListData(List<Task> newList) {
        list.clear();
        list.addAll(newList);
        mAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

}
