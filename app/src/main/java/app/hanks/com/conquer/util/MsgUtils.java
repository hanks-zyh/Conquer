package app.hanks.com.conquer.util;

import android.content.Context;

import java.util.List;

import app.hanks.com.conquer.CustomApplication;
import app.hanks.com.conquer.bean.User;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 处理BmobChat相关的消息管理</br> 包括0.搜索用户 1.发送加好友请求，2.同意加好友请求 3.给好友发送消息 4.删除好友 5.加入黑名单 6.移除黑名单
 * @author wmf
 */
public class MsgUtils {
	// /**
	// * 获取黑名单
	// *
	// * @param username
	// */
	// private void getBlacks() {
	// L.i("getFriends", "查询 的黑名单列表");
	// userManager.queryBlackList(new FindListener<BmobChatUser>() {
	// @Override
	// public void onError(int arg0, String arg1) {
	// if (arg0 == BmobConfig.CODE_COMMON_NONE) {
	// L.i(arg1);
	// } else {
	// L.i("查询好友列表失败：" + arg1);
	// }
	// }
	//
	// @Override
	// public void onSuccess(List<BmobChatUser> arg0) {
	// // 保存到application中方便比较
	// tv_content.setText("");
	// for (BmobChatUser u : arg0) {
	// tv_content.append(u.getUsername() + "\n");
	// }
	// BulaApplication.getInstance().setContactList(CollectionUtils.list2map(arg0));
	// }
	// });
	// }
	//
	// /**
	// * 获取好友列表
	// *
	// * @param username
	// */
	// private void getFriends() {
	// L.i("getFriends", "查询当前用户的好友列表");
	// //
	// 查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
	// // 这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
	// userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
	// @Override
	// public void onError(int arg0, String arg1) {
	// if (arg0 == BmobConfig.CODE_COMMON_NONE) {
	// L.i(arg1);
	// } else {
	// L.i("查询好友列表失败：" + arg1);
	// }
	// }
	//
	// @Override
	// public void onSuccess(List<BmobChatUser> arg0) {
	// tv_content.setText("");
	// for (BmobChatUser u : arg0) {
	// tv_content.append(u.getUsername() + "\n");
	// }
	// // 保存到application中方便比较
	// BulaApplication.getInstance().setContactList(CollectionUtils.list2map(arg0));
	// }
	// });
	//
	// }
	//
	// /**
	// * 移除黑名单
	// *
	// * @param username
	// */
	// private void removeBlack(String username) {
	// L.i("removeBlack", "移除黑名单的好友是" + username);
	// userManager.removeBlack(username, new UpdateListener() {
	// @Override
	// public void onSuccess() {
	// L.i("移出黑名单成功");
	// // 重新设置下内存中保存的好友列表
	// BulaApplication.getInstance().setContactList(
	// CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList()));
	// }
	//
	// @Override
	// public void onFailure(int arg0, String arg1) {
	// L.i("移出黑名单失败:" + arg1);
	// }
	// });
	// }

	//
	// /**
	// * 添加到黑名单列表
	// *
	// * @param username
	// */
	// private void addBlack(final String username) {
	// L.i("delFriend", "添加到黑名单的好友是" + username);
	// userManager.addBlack(username, new UpdateListener() {
	// @Override
	// public void onSuccess() {
	// L.i("黑名单添加成功!");
	// // 重新设置下内存中保存的好友列表
	// BulaApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));
	// BmobDB.create(context).addBlack(username);
	// }
	//
	// @Override
	// public void onFailure(int arg0, String arg1) {
	// L.i("黑名单添加失败:" + arg1);
	// }
	// });
	// }

	/**
	 * 收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
	 * @param username
	 */
	public static void agreenFriend(final Context context, BmobUserManager userManager, String username) {
		L.i("agreenFriend", "同意的好友是" + username);
		userManager.addContactAfterAgree(username, new FindListener<BmobChatUser>() {
			@Override
			public void onError(int arg0, final String arg1) {
				L.d("addContactAfterAgree", arg0 + "," + arg1);
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				L.d("addContactAfterAgree_onSuccess", arg0.get(0).getUsername());
				// 保存到内存中
				CustomApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));
			}
		});
	}

	/**
	 * 发送陪任务消息，沒有回調
	 * @param context
	 * @param manager
	 * @param targetUser 目标用户，可以是好友或者陌生人
	 */
	public static void sendMsg(Context context, BmobChatManager manager, User targetUser, String msg) {
		L.i("sendMsg2Friend", "发送的好友是" + targetUser + "，" + targetUser.getUsername());
		// 发送消息，同时系统会自动将数据保存到本地消息表和最近会话表中（第一个参数是接收方，第二个参数是消息对象）
		// 创建BmobMsg消息对象，第一个参数是上下文，第二个参数是消息类型，第三个参数是接收方的objectId，
		// 第四个参数是消息内容
		BmobMsg message = BmobMsg.createTextSendMsg(context, targetUser.getObjectId(), msg);
		// 发送消息，同时系统会自动将数据保存到本地消息表和最近会话表中（第一个参数是接收方，第二个参数是消息对象）
		manager.sendTextMessage(targetUser, message);
	}

	/**
	 * 删除好友
	 */
	public static void delFriend(BmobUserManager userManager, String userId, final DeleteUserListener delUserListener) {
		L.i("delFriend", "删除好友的id是" + userId);
		userManager.deleteContact(userId, new UpdateListener() {
			@Override
			public void onSuccess() {
				L.i("删除成功");
				delUserListener.onSuccess();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				L.i("发送请求失败，请重新添加!" + arg0 + arg1);
				delUserListener.onError(arg0, arg1);
			}
		});
	}

	/**
	 * 添加好友
	 */
	public static void addFriend(Context context, String userId, final AddUserListener addUserListener) {
		L.i("addFriend", "添加的好友id是" + userId);
		BmobChatManager.getInstance(context).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, userId, new PushListener() {
			@Override
			public void onSuccess() {
				L.i("发送请求成功，等待对方验证!");
				addUserListener.onSuccess();
			}

			@Override
			public void onFailure(int arg0, final String arg1) {
				L.i("发送请求失败，请重新添加!" + arg0 + arg1);
				addUserListener.onError(arg0, arg1);
			}
		});

	}

	/**
	 * 根据用户的8位数字串 （username） 搜索用戶
	 * @param searchName
	 */
	public static void searchUser(BmobUserManager userManager, String searchName, final SearchUserListener searchUserListener) {
		L.i("searchFriend", "搜索的用户名是" + searchName);
		userManager.queryUser(searchName, new FindListener<User>() {
			@Override
			public void onSuccess(List<User> arg0) {
				L.i("搜索用户成功:" + arg0.size());
				searchUserListener.onSuccess(arg0);
			}

			@Override
			public void onError(int arg0, String arg1) {
				L.i("搜索用户错误:" + arg1 + "," + arg1);
				searchUserListener.onError(arg0, arg1);
			}
		});
	}

	interface SearchUserListener {
		public void onSuccess(List<User> list);

		public void onError(int errorCode, String msg);
	}

	interface DeleteUserListener {
		public void onSuccess();

		public void onError(int errorCode, String msg);
	}

	interface AddUserListener {
		public void onSuccess();

		public void onError(int errorCode, String msg);
	}
}
