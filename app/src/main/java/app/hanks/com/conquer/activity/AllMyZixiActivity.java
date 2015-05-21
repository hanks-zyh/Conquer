package app.hanks.com.conquer.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.DayAdapter;
import app.hanks.com.conquer.adapter.MyZixiAdapter;
import app.hanks.com.conquer.bean.Day;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.TaskUtil;
import app.hanks.com.conquer.view.xlist.XListView.IXListViewListener;

public class AllMyZixiActivity extends BaseActivity implements IXListViewListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private RecyclerView    lv_all_zixi;
	private ArrayList<Task> listTask;
	private ArrayList<Day>  listDay;
	private MyZixiAdapter   adapter;
	private DayAdapter      adapterDay;
	private ListView        lv_day;
	private TextView        tv_day;

	/**
	 * 初始化
	 */
	private void init() {
		listTask = new ArrayList<Task>();
		listDay = new ArrayList<Day>();
		adapter = new MyZixiAdapter(context, listTask);
		adapterDay = new DayAdapter(context, listDay);

		lv_all_zixi = (RecyclerView) findViewById(R.id.lv_all_zixi);
		lv_day = (ListView) findViewById(R.id.lv_day);
		tv_day = (TextView) findViewById(R.id.tv_day);

		lv_all_zixi.setLayoutManager(new LinearLayoutManager(context));
		lv_all_zixi.setItemAnimator(new DefaultItemAnimator());
		lv_all_zixi.setAdapter(adapter);
		lv_day.setAdapter(adapterDay);

		// // 允许加载更多
		// lv_day.setPullLoadEnable(true);
		// // 允许下拉
		// lv_day.setPullRefreshEnable(true);
		// // 设置监听器
		// lv_day.setXListViewListener(this);
		// lv_day.pullRefreshing();

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
		new Thread() {
			public void run() {
				allTask = TaskUtil.getAllZixi(context);
				Calendar c = Calendar.getInstance();
				Calendar c1 = Calendar.getInstance();
				final long curTime = System.currentTimeMillis();
				int len = allTask.size();

				L.d("查询到自习个数：" + len);
				// 循环每个自习
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
							listDay.add(new Day(c1.get(1), c1.get(2), c1.get(5), isToday ? true : false));
						}
					} else {
						c1.setTimeInMillis(zixiTime);
						boolean isToday = TaskUtil.isToday(curTime, zixiTime);
						if (isToday) posotion = i;
						listDay.add(new Day(c1.get(1), c1.get(2), c1.get(5), isToday ? true : false));
					}
				}

				listTask.clear();
				listTask.addAll(TaskUtil.getZixiByDay(context, curTime));
				L.d("今天自习：" + listTask.size());
				runOnUiThread(new Runnable() {
					public void run() {
						L.d("更新日期，自习");
						tv_day.setText(TaskUtil.getZixiDateS(curTime));
						adapter.notifyDataSetChanged();
						adapterDay.notifyDataSetChanged();
						if (posotion > 0 && posotion < listDay.size()) {
							View v = lv_day.getChildAt(posotion);
							if (v != null) lv_day.scrollBy(0, v.getTop());
						}
					}
				});
			}

			;
		}.start();
	}

	private void getZixiByDay(final Calendar c) {
		new Thread() {
			public void run() {
				listTask.clear();
				listTask.addAll((ArrayList<Task>) TaskUtil.getZixiByDay(context, c.getTimeInMillis()));
				L.d(c.get(5) + "的自习：" + listTask.size());
				runOnUiThread(new Runnable() {
					public void run() {
						adapter.notifyDataSetChanged();
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
		tv_title.setText("我的自习");
		ib_back.setImageResource(R.drawable.ic_clear_white_24dp);
	}

	@Override
	public View getContentView() {
		return View.inflate(context,R.layout.activity_all_myzixi, null);
	}
}
