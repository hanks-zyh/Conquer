package app.hanks.com.conquer.util;


import android.content.Context;

import java.util.List;

import app.hanks.com.conquer.bean.User;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 用户资料的工具类
 * 
 * @author zyh
 */
public class UserDataUtils {

	/**
	 * 保存用户信息
	 * @param context
	 * @param user
	 */
	public static void UpdateUserData(Context context, User user, final UpdateUserDataListener updateListener) {
		user.update(context, new UpdateListener() {
			@Override
			public void onSuccess() {
				L.i("更新用户信息成功");
				updateListener.onSuccess();
			}
			@Override
			public void onFailure(int arg0, String arg1) {
				L.i("更新用户信息失败：" + arg0 + arg1);
				updateListener.onFailure(arg0, arg1);
			}
		});

	}

	/**
	 * 保存用户信息
	 * 
	 * @param context
	 * @param user
	 */
	public static void UpdateUserData(Context context, User user, boolean hasDialog,
			final UpdateUserDataListener updateListener) {
		if (hasDialog){
			ProgressUtil.showWaitting(context);
		}
		user.update(context, new UpdateListener() {

			@Override
			public void onSuccess() {
				L.i("更新用户信息成功");
				updateListener.onSuccess();
				ProgressUtil.dismiss();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				L.i("更新用户信息失败：" + arg0 + arg1);
				updateListener.onFailure(arg0, arg1);
				ProgressUtil.dismiss();
			}
		});

	}

	public static void queryUserByUsername(Context context, String Username,
			final QueryUserDataListener queryUserDataListener) {
		BmobUserManager.getInstance(context).queryUser(Username, new FindListener<User>() {
			@Override
			public void onError(int arg0, String arg1) {
				L.i("queryUser", "onError onError:" + arg1);
				queryUserDataListener.onFailure(arg0, arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				L.i("queryUser,onSuccess" + arg0.size());
				queryUserDataListener.onSuccess(arg0);
			}
		});
	}

	public interface QueryUserDataListener {
		public void onSuccess(List<User> arg0);

		public void onFailure(int errorCode, String msg);
	}

	public interface UpdateUserDataListener {
		public void onSuccess();

		public void onFailure(int errorCode, String msg);
	}
}
