package app.hanks.com.conquer.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.User;
import app.hanks.com.conquer.fragment.MyAlbumFragment;
import app.hanks.com.conquer.fragment.MyDataFragment;
import app.hanks.com.conquer.fragment.MyMsgFragment;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.view.CircularImageView;
import app.hanks.com.conquer.view.PagerSlidingTabStrip;
import cn.bmob.v3.listener.FindListener;

/**
 * 用户资料界面，需要intent传过来用户的username
 * @author wmf
 */
public class FriendDataActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private MyDataFragment       myDataFragment;
	private MyMsgFragment        myMsgFragment;
	private MyAlbumFragment      myAlbumFragment;
	private DisplayMetrics       dm;
	private PagerSlidingTabStrip tabs;
	private CircularImageView    iv_photo;
	private TextView             tv_nickname;
	/**
	 * 查询到的好友，需要每次进到Activity时开始查询一下好友资料
	 */
	private User                 friend;
	private ImageView            iv_gender;

	/**
	 * 初始化
	 */
	private void init() {
		String friendName = getIntent().getStringExtra("friendName");
		initOtherData(friendName);
		iv_photo = (CircularImageView) findViewById(R.id.iv_photo);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		iv_gender = (ImageView) findViewById(R.id.iv_gender);
		// dm = getResources().getDisplayMetrics();
		// ViewPager pager = (ViewPager) findViewById(R.id.pager);
		// tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		// pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		// tabs.setViewPager(pager);
		// setTabsValue();
		// myDataFragment = new MyDataFragment();
		// myMsgFragment = new MyMsgFragment();
		// myAlbumFragment = new MyAlbumFragment();
	}

	/**
	 * 获取好友资料
	 * @param name
	 */
	private void initOtherData(String name) {
		userManager.queryUser(name, new FindListener<User>() {
			@Override
			public void onError(int arg0, String arg1) {
				T.show(context, "onError onError:" + arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				if (arg0 != null && arg0.size() > 0) {
					friend = arg0.get(0);
					initPhoto(friend);
					// btn_chat.setEnabled(true);
					// btn_back.setEnabled(true);
					// btn_add_friend.setEnabled(true);
					// updateUser(user);
				} else {
					T.show(context, "onSuccess 查无此人");
				}
			}
		});
	}

	/**
	 * 显示用户头像，昵称，性别
	 * @param user
	 */
	private void initPhoto(User user) {
		if (user != null) {
			loader.displayImage(user.getAvatar(), iv_photo, option_photo);
			tv_nickname.setText(user.getNick());
			iv_gender.setImageResource(user.isMale() ? R.drawable.ic_male : R.drawable.ic_female);
		}
	}

	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(getResources().getColor(R.color.title));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		tabs.setSelectedTextColor(getResources().getColor(R.color.title));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private final String[] titles = { "我", "留言板", "相册" };

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					if (myDataFragment == null) {
						myDataFragment = new MyDataFragment();
					}
					return myDataFragment;
				case 1:
					if (myMsgFragment == null) {
						myMsgFragment = new MyMsgFragment();
					}
					return myMsgFragment;
				case 2:
					if (myAlbumFragment == null) {
						myAlbumFragment = new MyAlbumFragment();
					}
					return myAlbumFragment;
				default:
					return null;
			}
		}

	}

	@Override
	public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
		tv_title.setText("好友资料");
		ib_back.setImageResource(R.drawable.ic_clear_white_24dp);
	}

	@Override
	public View getContentView() {
		return View.inflate(context, R.layout.activity_friend_data, null);
	}
}
