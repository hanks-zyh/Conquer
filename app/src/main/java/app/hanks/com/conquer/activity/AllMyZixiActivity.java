package app.hanks.com.conquer.activity;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.DayAdapter;
import app.hanks.com.conquer.adapter.MyTaskAdapter;
import app.hanks.com.conquer.bean.Day;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.TaskUtil;
import app.hanks.com.conquer.view.xlist.XListView.IXListViewListener;

public class AllMyZixiActivity extends BaseActivity implements IXListViewListener {


    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
    private RecyclerViewDragDropManager         mRecyclerViewDragDropManager;
    private RecyclerViewSwipeManager            mRecyclerViewSwipeManager;
    private RecyclerView.Adapter                mWrappedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private RecyclerView    mRecyclerView;
    private ArrayList<Task> listTask;
    private ArrayList<Day>  listDay;
    private MyTaskAdapter   mAdapter;
    private DayAdapter      adapterDay;
    private ListView        lv_day;
    private TextView        tv_day;

    /**
     * 初始化
     */
    private void init() {
        listTask = new ArrayList<Task>();
        listDay = new ArrayList<Day>();
        adapterDay = new DayAdapter(context, listDay);

        mRecyclerView = (RecyclerView) findViewById(R.id.lv_all_zixi);
        lv_day = (ListView) findViewById(R.id.lv_day);
        tv_day = (TextView) findViewById(R.id.tv_day);


        // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        // drag & drop manager 拖拽排序的manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z3_xxhdpi));

        // swipe manager 滑动item的manager
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        //adapter
        mAdapter = new MyTaskAdapter(context, listTask);
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mAdapter);      // wrap for swiping
        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mWrappedAdapter);      // wrap for swiping
        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        animator.setSupportsChangeAnimations(false);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);


        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z1_xxhdpi)));
        }
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        // priority: TouchActionGuard > Swipe > DragAndDrop
        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);


        lv_day.setAdapter(adapterDay);

        // // 允许加载更多
//        lv_day.setPullLoadEnable(true);
//        // 允许下拉
//        lv_day.setPullRefreshEnable(true);
//        // 设置监听器
//        lv_day.setXListViewListener(this);
//        lv_day.pullRefreshing();

        lv_day.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Day d = listDay.get(position);
                Calendar tmp = Calendar.getInstance();
                tmp.set(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 1);
                tv_day.setText(TaskUtil.getZixiDateS(tmp.getTimeInMillis()));
                getZixiByDay(tmp);
            }
        });
        // 加载数据
        initData();
    }

    private List<Task> allTask;
    private int posotion = 0;

    private void initData() {


        allTask = TaskUtil.getAllZixi(context);
        Calendar c = Calendar.getInstance();
        Calendar c1 = Calendar.getInstance();
        final long curTime = System.currentTimeMillis();
        int len = allTask.size();

        L.d("查询到任务个数：" + len);
        // 循环每个任务
        for (int i = 0; i < len; i++) {
            int j = listDay.size();
            long zixiTime = allTask.get(i).getTime();
            if (j > 0) {
                // 得到上一天
                Day d = listDay.get(j - 1);
                c.set(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 1);
                if (!TaskUtil.isToday(c.getTimeInMillis(), zixiTime)) {
                    // 不是同一天 ，加到daylist
                    c1.setTimeInMillis(zixiTime);
                    boolean isToday = TaskUtil.isToday(curTime, zixiTime);
                    if (isToday) posotion = i;
                    listDay.add(new Day(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH), isToday ? true : false));
                }
            } else {
                c1.setTimeInMillis(zixiTime);
                boolean isToday = TaskUtil.isToday(curTime, zixiTime);
                if (isToday) posotion = i;
                listDay.add(new Day(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH), isToday ? true : false));
            }
        }

        listTask.clear();
        listTask.addAll(TaskUtil.getZixiByDay(context, curTime));
        L.d("今天任务：" + listTask.size());
        runOnUiThread(new Runnable() {
            public void run() {
                tv_day.setText(TaskUtil.getZixiDateS(curTime));
                mAdapter.notifyDataSetChanged();
                adapterDay.notifyDataSetChanged();
                if (posotion > 0 && posotion < listDay.size()) {
                    View v = lv_day.getChildAt(posotion);
                    if (v != null) lv_day.scrollBy(0, v.getTop());
                }
            }
        });
    }

    private void getZixiByDay(final Calendar c) {
        new Thread() {
            public void run() {
                listTask.clear();
                listTask.addAll((ArrayList<Task>) TaskUtil.getZixiByDay(context, c.getTimeInMillis()));
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
        tv_title.setText("我的任务");
        ib_back.setImageResource(R.drawable.ic_clear_white_24dp);
    }

    @Override
    public View getContentView() {
        return View.inflate(context, R.layout.activity_all_myzixi, null);
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }
}
