package app.hanks.com.conquer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import app.hanks.com.conquer.bean.User;

import cn.bmob.im.bean.BmobChatUser;


public class BaseFragment extends Fragment {

	protected Context context;
	protected User    currentUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		currentUser = BmobChatUser.getCurrentUser(context, User.class);
	}
}
