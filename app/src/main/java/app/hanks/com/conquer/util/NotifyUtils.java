package app.hanks.com.conquer.util;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.hanks.com.conquer.CustomApplication;
import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.Card;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class NotifyUtils {

	private static MediaPlayer player;
	private static Timer timer_play;
	private static int curPosition = 0;

	/**
	 * 有人提醒任务时弹出的卡片
	 * @param context
	 * @param card
	 */
	public static void showZixiAlertToast(final Context context, final Card card) {
		final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		final View view = View.inflate(context, R.layout.toast_alert_notify, null);
		ImageView iv_bell = (ImageView) view.findViewById(R.id.iv_bell);
		ImageView iv_photo = (ImageView) view.findViewById(R.id.iv_photo);
		TextView tv_type = (TextView) view.findViewById(R.id.tv_type);
		TextView tv_from = (TextView) view.findViewById(R.id.tv_from);
		TextView tv_zixitime = (TextView) view.findViewById(R.id.tv_zixitime);
		TextView tv_zixiname = (TextView) view.findViewById(R.id.tv_zixiname);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		ViewGroup ll_audio = (ViewGroup) view.findViewById(R.id.ll_audio);
		final ImageButton ib_play = (ImageButton) view.findViewById(R.id.ib_play);
		final ProgressBar pb = (ProgressBar) view.findViewById(R.id.pb);
		ImageLoader.getInstance().displayImage(card.getFavatar(), iv_photo, ImageLoadOptions.getOptions());
		tv_type.setText("任务提醒");
		tv_from.setText("来自：" + card.getFnick());
		tv_zixitime.setText(TaskUtil.getZixiTimeS(card.getTime()) + " " + TaskUtil.getZixiDateS(card.getTime()));
		tv_zixiname.setText(card.getZixiName());
		tv_content.setText(card.getContent());
		ll_audio.setVisibility(card.getAudioUrl() != null ? View.VISIBLE : View.GONE);
		// 设置任务已提醒，不需要本地系统提醒了
		TaskUtil.setZixiHasAlerted(context, card.getZixiId());
		ib_play.setTag("play");
		ib_play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ib_play.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (ib_play.getTag().equals("play")) {
							ib_play.setImageResource(R.drawable.pause_audio);
							ib_play.setTag("pause");
							palyAudio(context, ib_play, pb, card.getAudioUrl());
						} else {
							ib_play.setTag("play");
							ib_play.setImageResource(R.drawable.play_audio);
							pauseAudio(ib_play);
						}
					}
				});
			}
		});

		// 铃铛动画
		iv_bell.setBackgroundResource(R.drawable.alert_bell_anim);
		AnimationDrawable draw = (AnimationDrawable) iv_bell.getBackground();
		draw.start();

		final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		params.height = WindowManager.LayoutParams.MATCH_PARENT;
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.gravity = Gravity.BOTTOM;
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		// params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
		// WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		// | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.windowAnimations = android.R.style.Animation_InputMethod;
		wm.addView(view, params);

		// 知道了按钮
		view.findViewById(R.id.ll_save).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeMyToast(wm, view);
				saveCard(context, card);
			}
		});
	}

	/**
	 * 播放音频
	 * @param ib_play 播放按钮
	 * @param pb 进度条
	 * @param path 音频路径
	 */
	public static void palyAudio(final Context context, final ImageButton ib_play, final ProgressBar pb, String path) {
		// 播放录音
		if (path == null) {
			T.show(context, "找不到录音文件");
			return;
		}
		if (player == null) player = new MediaPlayer();
		player.reset();
		try {
			player.setDataSource(path);
			player.prepareAsync();
			player.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					player.seekTo(curPosition);
					pb.setMax(player.getDuration());
					player.start();
					// 秒++
					if (timer_play == null) timer_play = new Timer();
					timer_play.schedule(new TimerTask() {
						@Override
						public void run() {
							curPosition += 1000;
							Log.e("	timer_play.schedule", curPosition + "");
							pb.setProgress(curPosition);
						}
					}, new Date(), 1000);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			T.show(context, "播放出错");
		}
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.i("player", "OnCompletionListener");
				if (timer_play != null) {
					timer_play.cancel();
					timer_play = null;
				}
				ib_play.setImageResource(R.drawable.play_audio);
				ib_play.setTag("play");
				pb.setProgress(0);
				curPosition = 0;
			}
		});
	}

	public static void pauseAudio(ImageButton ib_play) {
		// 暂停播放,保存播放进度
		if (player != null && player.isPlaying()) {
			curPosition = player.getCurrentPosition();
			player.pause();
			if (timer_play != null) {
				timer_play.cancel();
				timer_play = null;
			}
			if (ib_play != null) {
				ib_play.setImageResource(R.drawable.play_audio);
				ib_play.setTag("play");
			}
		}
	}

	/**
	 * 有人看到你的任务时，想与你一起上任务是发过来的卡片
	 * @param context
	 * @param card
	 */
	public static void showGoudaToast(final Context context, final Card card) {

		/** 接受到该类消息本地用户就会就由User变为BmobChatUser，丢失一下信息，还没解决 */
		final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		final View view = View.inflate(context, R.layout.toast_gouda_notify, null);
		ImageView iv_bell = (ImageView) view.findViewById(R.id.iv_bell);
		TextView tv_type = (TextView) view.findViewById(R.id.tv_type);
		TextView tv_from = (TextView) view.findViewById(R.id.tv_from);
		SimpleDraweeView iv_avatar = (SimpleDraweeView) view.findViewById(R.id.iv_avatar);
		TextView tv_zixitime = (TextView) view.findViewById(R.id.tv_zixitime);
		TextView tv_zixiname = (TextView) view.findViewById(R.id.tv_zixiname);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_type.setText("勾搭任务");
		tv_from.setText("来自:"+card.getFnick());
		tv_zixitime.setText(TaskUtil.getZixiDateS(card.getTime()) + " " + TaskUtil.getZixiTimeS(card.getTime()));
		tv_zixiname.setText(card.getZixiName());
		tv_content.setText(card.getContent());
		iv_avatar.setImageURI(Uri.parse(card.getFavatar()));
		// 铃铛动画
		iv_bell.setBackgroundResource(R.drawable.alert_bell_anim);
		AnimationDrawable draw = (AnimationDrawable) iv_bell.getBackground();
		draw.start();

		final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		params.height = metrics.heightPixels / 2;
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.gravity = Gravity.BOTTOM;
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		// params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
		// WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		// | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.windowAnimations = android.R.style.Animation_InputMethod;
		wm.addView(view, params);
		// 监听点击事件
		// 忽略按钮
		view.findViewById(R.id.ib_decline).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeMyToast(wm, view);
			}
		});
		// 拉黑按钮
		view.findViewById(R.id.iv_add_black).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeMyToast(wm, view);
				BmobUserManager.getInstance(context).addBlack(card.getFusername(), new UpdateListener() {
					@Override
					public void onSuccess() {
						T.show(context, "黑名单添加成功!");
						// 重新设置下内存中保存的好友列表
						CustomApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));
						BmobDB.create(context).addBlack(card.getFusername());
					}
					@Override
					public void onFailure(int arg0, String arg1) {
						T.show(context, "黑名单添加失败:" + arg1);
					}
				});
			}
		});

		// 同意按钮
		view.findViewById(R.id.iv_accept).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeMyToast(wm, view);
				final BmobInvitation invitation = new BmobInvitation(card.getFid(), card.getFusername(), "", "", System.currentTimeMillis(), 1);
				BmobUserManager.getInstance(context).agreeAddContact(invitation, new UpdateListener() {
					@Override
					public void onSuccess() {
						saveCard(context, card);
						BmobUserManager.getInstance(context).queryCurrentContactList(new FindListener<BmobChatUser>() {
							@Override
							public void onError(int arg0, String arg1) {
								L.i("查询好友列表失败：" + arg1);
							}

							@Override
							public void onSuccess(List<BmobChatUser> arg0) {
								T.show(context, "已将" + card.getFnick() + "添加为陪友");
								// 保存到application中方便比较
								CustomApplication.getInstance().setContactList(CollectionUtils.list2map(arg0));
							}
						});
						// BmobDB.create(context).saveContact(invitation);
						// CustomApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));

					}

					@Override
					public void onFailure(int arg0, String arg1) {
						T.show(context, "同意添加好友失敗:" + arg1);
					}
				});
			}
		});
	}

	/**
	 * 保存卡片到云
	 * @param context
	 * @param card
	 */
	private static void saveCard(Context context, Card card) {
		card.save(context, new SaveListener() {
			@Override
			public void onSuccess() {
				L.i("Card保存成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				L.i("Card保存失败" + arg0 + arg1);
			}
		});
	}

	/**
	 * 移除卡片
	 * @param wm
	 * @param view
	 */
	private static void removeMyToast(final WindowManager wm, final View view) {
		// params.windowAnimations = android.R.style.Animation_Toast;
		// wm.updateViewLayout(view, params);
		pauseAudio(null);
		if (view != null) wm.removeView(view);
	}
}
