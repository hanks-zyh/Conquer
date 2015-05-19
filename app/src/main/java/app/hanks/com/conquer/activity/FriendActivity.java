package app.hanks.com.conquer.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

import app.hanks.com.conquer.CustomApplication;
import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.UserFriendAdapter;
import app.hanks.com.conquer.bean.User;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.CharacterParser;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.PinyinComparator;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.view.MyLetterView;
import app.hanks.com.conquer.view.MyLetterView.OnTouchingLetterChangedListener;

/**
 * 用来显示好友列表类
 * @author zyh
 */
public class FriendActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener, OnClickListener {
	TextView dialog;
	ListView list_friends;
	MyLetterView right_letter;
	List<User> friends = new ArrayList<User>();
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		initListView();
		initRightLetterView();
	}

	ImageView iv_msg_tips;
	TextView tv_new_name;
	private UserFriendAdapter userAdapter;

	private void initListView() {
		list_friends = (ListView) findViewById(R.id.list_friends);
		RelativeLayout headView = (RelativeLayout) View.inflate(context, R.layout.layout_new_friend, null);
		iv_msg_tips = (ImageView) headView.findViewById(R.id.iv_msg_tips);
		headView.findViewById(R.id.layout_new).setOnClickListener(this);
		headView.findViewById(R.id.layout_near).setOnClickListener(this);
		headView.findViewById(R.id.layout_add).setOnClickListener(this);
		list_friends.addHeaderView(headView);
		userAdapter = new UserFriendAdapter(context, friends);
		list_friends.setAdapter(userAdapter);
		list_friends.setOnItemClickListener(this);
		list_friends.setOnItemLongClickListener(this);
	}

	private void initRightLetterView() {
		right_letter = (MyLetterView) findViewById(R.id.right_letter);
		dialog = (TextView) findViewById(R.id.dialog);
		right_letter.setTextView(dialog);
		right_letter.setOnTouchingLetterChangedListener(new LetterListViewListener());
	}

	private class LetterListViewListener implements OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(String s) {
			// 该字母首次出现的位置
			int position = userAdapter.getPositionForSection(s.charAt(0));
			if (position != -1) {
				list_friends.setSelection(position);
			}
		}
	}

	/**
	 * 获取好友列表 queryMyfriends
	 * @return void
	 * @throws
	 */
	private void queryMyfriends() {
		// 是否有新的好友请求
		if (BmobDB.create(context).hasNewInvite()) {
			iv_msg_tips.setVisibility(View.VISIBLE);
		} else {
			iv_msg_tips.setVisibility(View.GONE);
		}
		// 在这里再做一次本地的好友数据库的检查，是为了本地好友数据库中已经添加了对方，但是界面却没有显示出来的问题
		// 重新设置下内存中保存的好友列表
		CustomApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));

		Map<String, BmobChatUser> users = CustomApplication.getInstance().getContactList();
		// 组装新的User
		filledData(CollectionUtils.map2list(users));
		if (userAdapter == null) {
			userAdapter = new UserFriendAdapter(context, friends);
			list_friends.setAdapter(userAdapter);
		} else {
			userAdapter.notifyDataSetChanged();
		}

	}

	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private void filledData(List<BmobChatUser> datas) {
		friends.clear();
		int total = datas.size();
		L.i(datas.size() + "好友数");
		for (int i = 0; i < total; i++) {
			BmobChatUser user = datas.get(i);
			User sortModel = new User();
			sortModel.setAvatar(user.getAvatar());
			sortModel.setNick(user.getNick());
			sortModel.setUsername(user.getUsername());
			sortModel.setObjectId(user.getObjectId());
			sortModel.setContacts(user.getContacts());
			// 汉字转换成拼音
			String username = sortModel.getNick();
			// 若没有username
			if (username != null) {
				String pinyin = characterParser.getSelling(sortModel.getNick());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
			} else {
				sortModel.setSortLetters("#");
			}
			friends.add(sortModel);
		}
		// 根据a-z进行排序
		Collections.sort(friends, pinyinComparator);
	}

	private boolean hidden;

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refresh();
		}
	}

	public void refresh() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					queryMyfriends();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		User user = (User) userAdapter.getItem(position - 1);
		// 开始聊天
		Intent intent = new Intent(context, ChatActivity.class);
		intent.putExtra("user", user);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		A.goOtherActivity(context, intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		User user = (User) userAdapter.getItem(position - 1);
		showDeleteDialog(user);
		return true;
	}

	public void showDeleteDialog(final User user) {
		Builder builder = new Builder(context);
		builder.setTitle("是否删除好友").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteContact(user);
			}
		}).setNegativeButton("取消", null).show();

	}

	/**
	 * 删除联系人 deleteContact
	 * @return void
	 * @throws
	 */
	private void deleteContact(final User user) {
		final ProgressDialog progress = new ProgressDialog(context);
		progress.setMessage("正在删除...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		userManager.deleteContact(user.getObjectId(), new UpdateListener() {

			@Override
			public void onSuccess() {
				T.show(context, "删除成功");
				// 删除内存
				CustomApplication.getInstance().getContactList().remove(user.getUsername());
				// 更新界面
				((Activity) context).runOnUiThread(new Runnable() {
					public void run() {
						progress.dismiss();
						userAdapter.remove(user);
					}
				});
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				T.show(context, "删除失败：" + arg1);
				progress.dismiss();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.layout_add:
				A.goOtherActivity(context, AddFriendActivity.class);
				break;
			case R.id.layout_near:
				A.goOtherActivity(context, NearPeopleActivity.class);
				break;
			case R.id.layout_new:
				Intent intent = new Intent(context, NewFriendActivity.class);
				intent.putExtra("from", "contact");
				A.goOtherActivity(context, intent);
				break;
		}
	}

	@Override
	public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
		tv_title.setText("好友列表");
	}
	@Override
	public View getContentView() {
		return View.inflate(context, R.layout.activity_friend, null);
	}
}
