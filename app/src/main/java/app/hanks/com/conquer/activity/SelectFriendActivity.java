package app.hanks.com.conquer.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;

import app.hanks.com.conquer.CustomApplication;
import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.UserFriendAdapter;
import app.hanks.com.conquer.bean.User;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.CharacterParser;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.PinyinComparator;
import app.hanks.com.conquer.view.MyLetterView;
import app.hanks.com.conquer.view.MyLetterView.OnTouchingLetterChangedListener;

/**
 * 用来显示好友列表类
 * @author zyh
 */
public class SelectFriendActivity extends BaseActivity implements OnItemClickListener {
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
		userAdapter = new UserFriendAdapter(context, friends);
		list_friends.setAdapter(userAdapter);
		list_friends.setOnItemClickListener(this);
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
		// 重新设置下内存中保存的好友列表
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

	@Override
	public void onResume() {
		super.onResume();
		refresh();
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
		User user = (User) userAdapter.getItem(position);
		// 返回选择的User
		Intent data = new Intent();
		data.putExtra("selectUser", user);
		setResult(RESULT_OK, data);
		A.finishSelf(context);
	}

	@Override
	public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
		tv_title.setText("选择好友");
		ib_back.setImageResource(R.drawable.ic_clear_white_24dp);
	}

	@Override
	public View getContentView() {
		return View.inflate(context, R.layout.activity_select_friend, null);
	}

}
