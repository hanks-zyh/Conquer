package app.hanks.com.conquer.activity;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;

import com.google.gson.Gson;
import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.Card;
import app.hanks.com.conquer.bean.Zixi;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.MsgUtils;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.util.ZixiUtil;
import app.hanks.com.conquer.view.CircularImageView;
import app.hanks.com.conquer.view.RippleBackground;

public class AlertActivity extends BaseActivity implements OnClickListener {

	private Zixi zixi;
	private EditText et;
	private RadioGroup rg;
	private AlertDialog dialog;
	private ImageButton ib_recoder;
	private TextView tv_second;
	private ViewGroup ll_bottom;
	private BmobRecordManager recordManager;
	private ViewGroup ll_audio;
	private ProgressBar loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		zixi = (Zixi) getIntent().getSerializableExtra("zixi");
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		et = (EditText) findViewById(R.id.et);

		CircularImageView iv_photo = (CircularImageView) findViewById(R.id.iv_photo);
		ImageView iv_gender = (ImageView) findViewById(R.id.iv_gender);
		TextView tv_Nick = (TextView) findViewById(R.id.tv_nickname);
		TextView tv_name = (TextView) findViewById(R.id.tv_name);
		TextView tv_time = (TextView) findViewById(R.id.tv_time);
		TextView tv_dis = (TextView) findViewById(R.id.tv_dis);
		tv_time.setText(ZixiUtil.getZixiTimeS(zixi));
		tv_name.setText(zixi.getName());
		tv_Nick.setText(zixi.getUser().getNick());
		tv_dis.setText(ZixiUtil.getDistance(currentUser, zixi.getUser().getLocation()));
		loader.displayImage(zixi.getUser().getAvatar(), iv_photo);
		iv_gender.setImageResource(zixi.getUser().isMale() ? R.drawable.ic_male : R.drawable.ic_female);
		rg = (RadioGroup) findViewById(R.id.rg);
		// 语音按钮
		findViewById(R.id.bt_audio).setOnClickListener(this);
		// 确定
		findViewById(R.id.tv_ok).setOnClickListener(this);

		initRecode();
		// 添加的音频布局
		ll_audio = (ViewGroup) findViewById(R.id.ll_audio);
		ll_audio.setVisibility(View.GONE);
		final ImageButton ib_play = (ImageButton) findViewById(R.id.ib_play);
		final ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
		loading = (ProgressBar) findViewById(R.id.loading);
		final TextView tv_duration = (TextView) findViewById(R.id.tv_duration);
		loading.setVisibility(View.GONE);
		// 播放按钮
		ib_play.setImageResource(R.drawable.play_audio);
		ib_play.setTag("play");
		ib_play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ib_play.getTag().equals("play")) {
					ib_play.setImageResource(R.drawable.pause_audio);
					ib_play.setTag("pause");
					palyAudio(ib_play, pb, tv_duration, recorderPath);
					// 进度++，数字++
					// 开始播放
				} else {
					ib_play.setTag("play");
					ib_play.setImageResource(R.drawable.play_audio);
					pauseAudio(ib_play);
					// 动画，秒数消失
					// 停止录音
				}
			}
		});
		// 删除已录制的声音
		findViewById(R.id.iv_del).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(context).setTitle("删除录音").setMessage("确定要删除吗?")
						.setPositiveButton("删除", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								audioUrl = null;
								ll_audio.setVisibility(View.GONE);
							}
						}).setNegativeButton("算了", null).show();
			}
		});
	}

	private int recordSecond = 0; // 录音的长度
	private String recorderPath = null;// 录音路径
	private MediaPlayer player;
	private int curPosition = 0;// 当前播放进度
	private Timer timer_play;
	private ProgressBar pb;// 进度条

	private String audioUrl = null;
	private RippleBackground rippleBackground;

	/**
	 * 初始化录音组件
	 */
	private void initRecode() {
		recordManager = BmobRecordManager.getInstance(context);
		// 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
		recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {
			@Override
			public void onVolumnChanged(int value) {
				L.d("录音音量大小：" + value);
			}

			@Override
			public void onTimeChanged(int recordTime, String localPath) {
				L.d("已录音长度:" + recordTime);
				tv_second.setText((60 - recordTime) + "秒");
				recordSecond = recordTime;
				recorderPath = localPath;
				if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
					ib_recoder.setTag("off");
					// 动画，秒数消失
					// 停止录音
					recordManager.stopRecording();
					tv_second.setText("准备录音");
					pb.setMax(recordSecond * 1000);
					pb.setProgress(0);
					ll_bottom.setVisibility(0);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_audio:
				showRecodDialog();
				break;
			case R.id.tv_ok:
				if (currentUser != null) {
					sendAlertCard(rg.getCheckedRadioButtonId());
				} else {
					// 登录对话框
				}
				break;
		}
	}

	/**
	 * 弹出录音的对话框
	 */
	private void showRecodDialog() {
		// 布局
		dialog = new AlertDialog.Builder(context).create();
		dialog.setCanceledOnTouchOutside(false);
		View v = View.inflate(context, R.layout.dialog_recorder, null);
		ib_recoder = (ImageButton) v.findViewById(R.id.ib_recoder);
		tv_second = (TextView) v.findViewById(R.id.tv_second);
		rippleBackground = (RippleBackground) v.findViewById(R.id.content);
		// 底部按钮
		final ImageButton ib_play = (ImageButton) v.findViewById(R.id.ib_play);
		pb = (ProgressBar) v.findViewById(R.id.pb);
		final TextView tv_time = (TextView) v.findViewById(R.id.tv_time);

		// 底部布局
		ll_bottom = (ViewGroup) v.findViewById(R.id.ll_bottom);
		ll_bottom.setVisibility(View.GONE);

		pauseAudio(ib_play);
		// 录音按钮
		// ib_recoder.setImageResource(R.drawable.record_off);
		ib_recoder.setTag("off");
		ib_recoder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ib_recoder.getTag().equals("off")) {
					ll_bottom.setVisibility(View.GONE);
					ib_recoder.setTag("on");
					// 开始录音动画
					startRecordAnim(ib_recoder);
					// 开始录音
					recordSecond = 0;
					recordManager.startRecording(currentUser.getObjectId());
				} else {
					ib_recoder.setTag("off");
					// 动画，秒数消失
					stopRecordAnim();
					// 停止录音
					recordManager.stopRecording();
					tv_second.setText("准备录音");
					pb.setMax(recordSecond * 1000);
					pb.setProgress(0);
					ll_bottom.setVisibility(0);
				}
			}
		});

		// 播放按钮
		ib_play.setImageResource(R.drawable.play_audio);
		ib_play.setTag("play");
		ib_play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ib_play.getTag().equals("play")) {
					ib_play.setImageResource(R.drawable.pause_audio);
					ib_play.setTag("pause");
					// 开始播放
					palyAudio(ib_play, pb, tv_time, recorderPath);
				} else {
					tv_time.setText(recordSecond + "秒");
					ib_play.setTag("play");
					ib_play.setImageResource(R.drawable.play_audio);
					// 停止/暂停播放
					pauseAudio(ib_play);
				}
			}
		});

		// 添加按钮
		v.findViewById(R.id.tv_ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseAudio(ib_play);
				dialog.dismiss();
				curPosition = 0;// 初始化播放进度
				ll_audio.setVisibility(0);// 设置录音布局可见
				upLoadAudio();
			}
		});
		// 删除按钮
		v.findViewById(R.id.iv_del).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseAudio(ib_play);
				ll_bottom.setVisibility(View.GONE);
			}
		});
		// 取消按钮按钮
		v.findViewById(R.id.tv_cancle).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseAudio(ib_play);
				dialog.dismiss();
			}
		});
		dialog.setView(v, 0, 0, 0, 0);
		dialog.show();
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				recordManager.stopRecording();
				pauseAudio(ib_play);
				stopRecordAnim();
			}
		});
	}

	/**
	 * 上传录音
	 */
	private void upLoadAudio() {
		File f = new File(recorderPath);
		if (!f.exists()) {
			T.show(context, "文件出错");
		}
		L.i("录音文件路径" + recorderPath);
		final BmobFile bf = new BmobFile(f);
		loading.setVisibility(0);
		bf.uploadblock(context, new UploadFileListener() {
			@Override
			public void onSuccess() {
				audioUrl = bf.getFileUrl(context);
				loading.setVisibility(View.GONE);
				L.e("录音上传成功：" + audioUrl);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// loading.setVisibility(View.GONE);
				L.e("录音上传失败" + arg0 + arg1);
			}
		});
	}

	private void pauseAudio(ImageButton ib_play) {
		// 暂停播放,保存播放进度
		if (player != null && player.isPlaying()) {
			curPosition = player.getCurrentPosition();
			player.pause();
			if (timer_play != null) {
				timer_play.cancel();
				timer_play = null;
			}
			ib_play.setImageResource(R.drawable.play_audio);
			ib_play.setTag("play");
		}
	}

	/**
	 * 播放音频
	 * @param ib_play 播放按钮
	 * @param pb 进度条
	 * @param tv_time 播放进度显示的字
	 * @param path 音频路径
	 */
	protected void palyAudio(final ImageButton ib_play, final ProgressBar pb, final TextView tv_time, String path) {
		// 播放录音
		if (recorderPath == null) {
			T.show(context, "找不到录音文件");
			return;
		}
		if (player == null) player = new MediaPlayer();
		player.reset();
		try {
			player.setDataSource(path);
			player.prepare();
			player.seekTo(curPosition);
			player.start();
			pb.setMax(player.getDuration());
			// 秒++
			if (timer_play == null) timer_play = new Timer();
			timer_play.schedule(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						public void run() {
							curPosition += 1000;
							Log.e("	timer_play.schedule", curPosition + "");
							tv_time.setText((curPosition / 1000) + "秒");
							pb.setProgress(curPosition);
						}
					});
				}
			}, new Date(), 1000);
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

	/**
	 * 发送
	 * @param rbId
	 */
	private void sendAlertCard(final int rbId) {
		if (loading.getVisibility() == 0) {
			new AlertDialog.Builder(context).setTitle("录音上传中").setMessage("是否等待上传完成？")
					.setNegativeButton("先不传了", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							send(rbId);
						}
					}).setPositiveButton("再等等", null).show();
		} else {
			send(rbId);
		}
	}

	protected void send(final int rbId) {
		String text = et.getText().toString();
		Card card = new Card();
		card.setType(rbId == R.id.rb0 ? 0 : 1);// 0。提醒卡
		card.setFid(currentUser.getObjectId());
		card.setFusername(currentUser.getUsername());
		card.setFnick(currentUser.getNick());
		card.setZixiId(zixi.getId());
		card.setZixiName(zixi.getName());
		card.setTime(zixi.getTime());

		card.settId(zixi.getUser().getObjectId());
		card.setFavatar(currentUser.getAvatar());
		if (audioUrl != null) card.setAudioUrl(audioUrl);
		card.setContent(text);
		L.e(card.toString());
		String json = new Gson().toJson(card);
		MsgUtils.sendMsg(context, BmobChatManager.getInstance(context), zixi.getUser(), json);
		T.show(context, "信息已发送，等待对方回应");
		A.finishSelf(context);
	}

	@Override
	protected void onDestroy() {
		if (player != null) {
			player.release();
			player = null;
		}
		super.onDestroy();
	}

	/**
	 * 录音动画
	 * @param ib_recoder2
	 */
	private void startRecordAnim(final ImageButton ib_recoder2) {
		if (rippleBackground != null) rippleBackground.startRippleAnimation();
	}

	/**
	 * 录音动画
	 */
	private void stopRecordAnim() {
		if (rippleBackground.isRippleAnimationRunning()) {
			rippleBackground.stopRippleAnimation();
		}
	}

	@Override
	public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
		tv_title.setText("提醒好友");
		ib_back.setImageResource(R.drawable.ic_clear_white_24dp);
		shadow.setVisibility(View.GONE);
	}

	@Override
	public View getContentView() {
		return View.inflate(context, R.layout.dialog_alert, null);
	}
}
