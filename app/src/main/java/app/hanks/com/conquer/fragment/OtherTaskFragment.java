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
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.OtherTaskAdapter;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.util.CollectionUtils;
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

}
