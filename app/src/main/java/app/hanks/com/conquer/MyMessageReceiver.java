package app.hanks.com.conquer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.hanks.com.conquer.activity.ChatActivity;
import app.hanks.com.conquer.activity.MainActivity;
import app.hanks.com.conquer.bean.Card;
import app.hanks.com.conquer.bean.User;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.NetUtils;
import app.hanks.com.conquer.util.NotifyUtils;
import app.hanks.com.conquer.util.SP;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;


/**
 * 推送消息接收器
 * 
 * @ClassName: MyMessageReceiver
 * @Description: TODO
 * @author smile
 * @date 2014-5-30 下午4:01:13
 */
public class MyMessageReceiver extends BroadcastReceiver {

	// 事件监听
	public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();

	public static final int NOTIFY_ID = 0x000;
	public static int mNewNum = 0;//
	BmobUserManager userManager;
	BmobChatUser currentUser;

	private SoundPool pool;

	private int id;

	// 如果你想发送自定义格式的消息，请使用sendJsonMessage方法来发送Json格式的字符串，然后你按照格式自己解析并处理
	@Override
	public void onReceive(Context context, Intent intent) {
		if (pool == null) {
			pool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
			String defaultAudioUri = (String) SP.get(context, "alert_audio", "");
			L.d("默认铃声：" + defaultAudioUri);
			if(!TextUtils.isEmpty(defaultAudioUri)){
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = context.getContentResolver().query(Uri.parse(defaultAudioUri),proj,null,null,null);
				int actual_image_column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				String path = cursor.getString(actual_image_column_index);
				id = pool.load(path,  1);
			}else{
				id = pool.load(context, R.raw.notify, 1);
			}
		}
		String json = intent.getStringExtra("msg");
		BmobLog.i("收到的message = " + json);
		L.i("接收消息Action：" + intent.getAction());
		userManager = BmobUserManager.getInstance(context);
		currentUser = userManager.getCurrentUser();
		boolean isNetConnected = NetUtils.isNetworkAvailable(context);
		if (isNetConnected) {
			L.i("网络正常");
			parseMessage(context, json);
		} else {
			L.i("网络无连接");
			for (int i = 0; i < ehList.size(); i++)
				((EventListener) ehList.get(i)).onNetChange(isNetConnected);
		}
	}

	/**
	 * 解析Json字符串
	 * 
	 * @Title: parseMessage
	 * 
	 * @Description: TODO
	 * @param @param context
	 * @param @param json
	 * @return void
	 * @throws
	 */
	private void parseMessage(final Context context, String json) {
		JSONObject jo;
		try {
			jo = new JSONObject(json);
			String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
			L.i("tag", tag);
			if (tag.equals(BmobConfig.TAG_OFFLINE)) {// 下线通知
				if (currentUser != null) {
					if (ehList.size() > 0) {// 有监听的时候，传递下去
						for (EventListener handler : ehList)
							handler.onOffline();
					} else {
						// 清空数据
						CustomApplication.getInstance().logout();
					}
				}
			} else {
				String fromId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETID);
				// 增加消息接收方的ObjectId--目的是解决多账户登陆同一设备时，无法接收到非当前登陆用户的消息。
				final String toId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TOID);
				L.i("FromId", fromId);
				L.i("toId", toId);

				String msgTime = BmobJsonUtil.getString(jo, BmobConstant.PUSH_READED_MSGTIME);
				L.i("msgTime", msgTime);
				if (fromId != null && !BmobDB.create(context, toId).isBlackUser(fromId)) {// 该消息发送方不为黑名单用户
					if (TextUtils.isEmpty(tag)) {// 不携带tag标签--此可接收陌生人的消息
						L.i("isEmpty(tag)");
						BmobChatManager.getInstance(context).createReceiveMsg(json, new OnReceiveListener() {
							@Override
							public void onSuccess(BmobMsg msg) {
								L.i("createReceiveMsg" + msg.getBelongUsername() + "," + msg.getBelongId() + "," + msg.getBelongNick());
								if (ehList.size() > 0) {// 有监听的时候，传递下去
									for (int i = 0; i < ehList.size(); i++) {
										((EventListener) ehList.get(i)).onMessage(msg);
									}
								} else {
									L.i("showMsgNotify");
									boolean isAllow = SP.isAllowPushNotify(context);
									if (isAllow && currentUser != null && currentUser.getObjectId().equals(toId)) {// 当前登陆用户存在并且也等于接收方id
										mNewNum++;
										showMsgNotify(context, msg);
									}
								}
							}

							@Override
							public void onFailure(int code, String arg1) {
								BmobLog.i("获取接收的消息失败：" + arg1);
							}
						});

					} else {// 带tag标签
						if (tag.equals(BmobConfig.TAG_ADD_CONTACT)) {
							// 保存好友请求道本地，并更新后台的未读字段
							BmobInvitation message = BmobChatManager.getInstance(context).saveReceiveInvite(json, toId);
							if (currentUser != null) {// 有登陆用户
								if (toId.equals(currentUser.getObjectId())) {
									if (ehList.size() > 0) {// 有监听的时候，传递下去
										for (EventListener handler : ehList)
											handler.onAddUser(message);
									} else {
										// showOtherNotify(context,
										// message.getFromname(), toId,
										// message.getFromname() + "请求添加好友",
										// NewFriendActivity.class);
										showOtherNotify(context, message.getFromname(), toId, message.getFromname() + "请求添加好友",
												MainActivity.class);
									}
								}
							}
						} else if (tag.equals(BmobConfig.TAG_ADD_AGREE)) {

							String username = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETUSERNAME);
							// 收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
							BmobUserManager.getInstance(context).addContactAfterAgree(username, new FindListener<BmobChatUser>() {

								@Override
								public void onError(int arg0, final String arg1) {

								}

								@Override
								public void onSuccess(List<BmobChatUser> arg0) {
									// 保存到内存中
									CustomApplication.getInstance().setContactList(
											CollectionUtils.list2map(BmobDB.create(context).getContactList()));
								}
							});
							// 显示通知
							showOtherNotify(context, username, toId, username + "同意添加您为好友", MainActivity.class);
							// 创建一个临时验证会话--用于在会话界面形成初始会话
							BmobMsg.createAndSaveRecentAfterAgree(context, json);
						} else if (tag.equals(BmobConfig.TAG_READED)) {// 已读回执
							String conversionId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_READED_CONVERSIONID);
							if (currentUser != null) {
								// 更改某条消息的状态
								BmobChatManager.getInstance(context).updateMsgStatus(conversionId, msgTime);
								if (toId.equals(currentUser.getObjectId())) {
									if (ehList.size() > 0) {// 有监听的时候，传递下去--便于修改界面
										for (EventListener handler : ehList)
											handler.onReaded(conversionId, msgTime);
									}
								}
							}
						}
					}
				} else {// 在黑名单期间所有的消息都应该置为已读，不然等取消黑名单之后又可以查询的到
					BmobChatManager.getInstance(context).updateMsgReaded(true, fromId, msgTime);
					BmobLog.i("该消息发送方为黑名单用户");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			// 这里截取到的有可能是web后台推送给客户端的消息，也有可能是开发者自定义发送的消息，需要开发者自行解析和处理
			BmobLog.i("parseMessage错误：" + e.getMessage());
		}
	}

	/**
	 * 显示与聊天消息的通知
	 * 
	 * @Title: showNotify
	 * @return void
	 * @throws
	 */
	public void showMsgNotify(Context context, BmobMsg msg) {
		// 更新通知栏
		String trueMsg = "";
		if (msg.getMsgType() == BmobConfig.TYPE_TEXT && msg.getContent().contains("\\ue")) {
			trueMsg = "[表情]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
			trueMsg = "[图片]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
			trueMsg = "[语音]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
			trueMsg = "[位置]";
		} else {
			trueMsg = msg.getContent();
		}
		// CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
		// String contentTitle = msg.getBelongUsername() + " (" + mNewNum +
		// "条新消息)";
		// 解析trueMsg是自习提醒卡，或者勾搭卡
		try {
			parseCard(context, trueMsg);
		} catch (Exception e) {
			e.printStackTrace();
			CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
			String contentTitle = msg.getBelongUsername() + " (" + mNewNum + "条新消息)";
			Intent intent = new Intent(context, ChatActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			User fuser = new User();
			fuser.setObjectId(msg.getBelongId());
			fuser.setUsername(msg.getBelongUsername());
			fuser.setNick(msg.getBelongNick());
			intent.putExtra("user", fuser);
			boolean isAllowVoice = SP.isAllowVoice(context);
			boolean isAllowVibrate = SP.isAllowVibrate(context);
			BmobNotifyManager.getInstance(context).showNotifyWithExtras(isAllowVoice, isAllowVibrate, R.drawable.ic_launcher,
					tickerText.toString(), contentTitle, tickerText.toString(), intent);
		}
	}

	/**
	 * 解析trueMsg是自习提醒卡，或者勾搭卡
	 * 
	 * @param json
	 */
	private void parseCard(Context context, String json) {
		Card card = new Gson().fromJson(json, Card.class);
		L.e(card.toString());
		if (card != null) {
			int streamID = pool.play(id, 1, 1, 0, 0, 1);
			pool.setVolume(streamID, 1, 1);
			// 通知栏内容
			// CharSequence tickerText = "";
			if (card.getType() == 0) {// 提醒卡
				NotifyUtils.showZixiAlertToast(context, card);
				// tickerText = card.getFnick() + "提醒你在" +
				// ZixiUtil.getZixiDateS(card.getTime()) + " " +
				// ZixiUtil.getZixiTimeS(card.getTime())
				// + "有自习要上哦";
			} else {
				NotifyUtils.showGoudaToast(context, card);
				// tickerText = card.getFnick() + "想与你在" +
				// ZixiUtil.getZixiDateS(card.getTime()) + " " +
				// ZixiUtil.getZixiTimeS(card.getTime())
				// + "上自习";
			}
			// String contentTitle = card.getFnick() + "(" + mNewNum + "条新消息)";
			// Intent intent = new Intent(context, MainActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// boolean isAllowVoice = SP.isAllowVoice(context);
			// boolean isAllowVibrate = SP.isAllowVibrate(context);
			//
			// BmobNotifyManager.getInstance(context).showNotifyWithExtras(isAllowVoice,
			// isAllowVibrate, R.drawable.ic_launcher,
			// tickerText.toString(), contentTitle, tickerText.toString(),
			// intent);
		}
	}

	/**
	 * 显示其他Tag的通知 showOtherNotify
	 */
	public void showOtherNotify(Context context, String username, String toId, String ticker, Class<?> cls) {
		boolean isAllow = SP.isAllowPushNotify(context);
		boolean isAllowVoice = SP.isAllowVoice(context);
		boolean isAllowVibrate = SP.isAllowVibrate(context);
		if (isAllow && currentUser != null && currentUser.getObjectId().equals(toId)) {
			// 同时提醒通知
			// BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,
			// isAllowVibrate, R.drawable.ic_launcher, ticker, username,
			// ticker.toString(), NewFriendActivity.class);
			BmobNotifyManager.getInstance(context).showNotify(isAllowVoice, isAllowVibrate, R.drawable.ic_launcher, ticker, username,
					ticker.toString(), MainActivity.class);
		}
	}

}
