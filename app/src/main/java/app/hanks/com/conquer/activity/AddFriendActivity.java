package app.hanks.com.conquer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.AddFriendAdapter;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.view.xlist.XListView;
import app.hanks.com.conquer.view.xlist.XListView.IXListViewListener;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.task.BRequest;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * 添加好友
 * @ClassName: AddFriendActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-5 下午5:26:41
 */
public class AddFriendActivity extends BaseActivity implements OnClickListener, IXListViewListener, OnItemClickListener {

	EditText et_find_name;
	Button btn_search;

	List<BmobChatUser> users = new ArrayList<BmobChatUser>();
	XListView mListView;
	AddFriendAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		// initTopBarForLeft("查找好友");
		et_find_name = (EditText) findViewById(R.id.et_find_name);
		btn_search = (Button) findViewById(R.id.btn_search);
		btn_search.setOnClickListener(this);
		initXListView();
	}

	private void initXListView() {
		mListView = (XListView) findViewById(R.id.list_search);
		// 首先不允许加载更多
		mListView.setPullLoadEnable(false);
		// 不允许下拉
		mListView.setPullRefreshEnable(false);
		// 设置监听器
		mListView.setXListViewListener(this);
		//
		mListView.pullRefreshing();

		adapter = new AddFriendAdapter(this, users);
		mListView.setAdapter(adapter);

		mListView.setOnItemClickListener(this);
	}

	int curPage = 0;
	ProgressDialog progress;

	private void initSearchList(final boolean isUpdate) {
		if (!isUpdate) {
			progress = new ProgressDialog(AddFriendActivity.this);
			progress.setMessage("正在搜索...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}
		userManager.queryUserByPage(isUpdate, 0, searchName, new FindListener<BmobChatUser>() {
			@Override
			public void onError(int arg0, String arg1) {
				BmobLog.i("查询错误:" + arg1);
				if (users != null) {
					users.clear();
				}
				T.show(context, "用户不存在");
				mListView.setPullLoadEnable(false);
				refreshPull();
				// 这样能保证每次查询都是从头开始
				curPage = 0;
			}

			@Override
			public void onSuccess(final List<BmobChatUser> arg0) {
				if (CollectionUtils.isNotNull(arg0)) {
					if (isUpdate) {
						users.clear();
					}
					runOnUiThread(new Runnable() {
						public void run() {
							adapter.addAll(arg0);
							adapter.notifyDataSetChanged();
						}
					});
					if (arg0.size() < BRequest.QUERY_LIMIT_COUNT) {
						mListView.setPullLoadEnable(false);
						T.show(context, "用户搜索完成!");
					} else {
						mListView.setPullLoadEnable(true);
					}
				} else {
					BmobLog.i("查询成功:无返回值");
					if (users != null) {
						users.clear();
					}
					T.show(context, "用户不存在");
				}
				if (!isUpdate) {
					progress.dismiss();
				} else {
					refreshPull();
				}
				// 这样能保证每次查询都是从头开始
				curPage = 0;
			}
		});

	}

	/**
	 * 查询更多
	 * @Title: queryMoreNearList
	 * @Description: TODO
	 * @param @param page
	 * @return void
	 * @throws
	 */
	private void queryMoreSearchList(int page) {
		userManager.queryUserByPage(true, page, searchName, new FindListener<BmobChatUser>() {
			@Override
			public void onSuccess(List<BmobChatUser> arg0) {

				if (CollectionUtils.isNotNull(arg0)) {
					adapter.addAll(arg0);
				}
				refreshLoad();
			}

			@Override
			public void onError(int arg0, String arg1) {
				L.i("搜索更多用户出错:" + arg1);
				mListView.setPullLoadEnable(false);
				refreshLoad();
			}

		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		BmobChatUser user = (BmobChatUser) adapter.getItem(position - 1);
		Intent intent = new Intent(this, FriendDataActivity.class);
		intent.putExtra("from", "add");
		intent.putExtra("username", user.getUsername());
		A.goOtherActivity(context, intent);
	}

	String searchName = "";

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
			case R.id.btn_search:// 搜索
				users.clear();
				searchName = et_find_name.getText().toString();
				if (searchName != null && !searchName.equals("")) {
					initSearchList(false);
				} else {
					T.show(context, "请输入用户名");
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

		userManager.querySearchTotalCount(searchName, new CountListener() {

			@Override
			public void onSuccess(int arg0) {

				if (arg0 > users.size()) {
					curPage++;
					queryMoreSearchList(curPage);
				} else {
					T.show(context, "数据加载完成");
					mListView.setPullLoadEnable(false);
					refreshLoad();
				}
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				L.i("查询附近的人总数失败" + arg1);
				refreshLoad();
			}
		});
	}

	private void refreshLoad() {
		if (mListView.getPullLoading()) {
			mListView.stopLoadMore();
		}
	}

	private void refreshPull() {
		if (mListView.getPullRefreshing()) {
			mListView.stopRefresh();
		}
	}

	@Override
	public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
		tv_title.setText("添加好友");
	}

	@Override
	public View getContentView() {
		return View.inflate(context, R.layout.activity_add_contact, null);
	}

}
