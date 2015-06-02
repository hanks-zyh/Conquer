package app.hanks.com.conquer.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.view.RippleBackground;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.inteface.OnRecordChangeListener;


/**
 * 弹出录音对话框并录制声音返回路径
 * @author zyh
 */
public class RecordUtil {

	private Context context;
	private View    view;
	private String  userObjectId;

	private BmobRecordManager           recordManager;
	private AlertDialog                 dialog;
	private ImageButton                 ib_recoder;
	private TextView                    tv_second;
	private RippleBackground            rippleBackground;
	private ProgressBar                 pb;
	private ViewGroup                   ll_bottom;
	private ImageButton                 ib_play;
	private int                         curPosition;
	private String                      recorderPath;
	private int                         recordSecond;
	private TextView                    tv_time;
	private RecordStatusChangedListener listener;
	private MediaPlayer                 player;
	private Timer                       timer_play;

	public RecordUtil(Context context, View view, String userObjectId, RecordStatusChangedListener recordStatusChangedListener) {
		this.context = context;
		this.view = view;
		this.userObjectId = userObjectId;
		this.listener = recordStatusChangedListener;
		init();
	}

	private void init() {
		// 布局
		dialog = new AlertDialog.Builder(context).create();
		dialog.setCanceledOnTouchOutside(false);
		initView(view);

		recordManager = BmobRecordManager.getInstance(context.getApplicationContext());
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
				if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束
					ib_recoder.setTag("off");
					// 动画，秒数消失
					// 停止录音
					recordManager.stopRecording();
					tv_second.setText("准备录音");
					pb.setMax(recordTime * 1000);
					pb.setProgress(0);
					ll_bottom.setVisibility(View.VISIBLE);
				}
			}
		});

		pauseAudio();
		// 录音按钮
		ib_recoder.setImageResource(R.drawable.record_off);
		ib_recoder.setTag("off");
		ib_recoder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ib_recoder.getTag().equals("off")) {
					ll_bottom.setVisibility(View.GONE);
					ib_recoder.setTag("on");
					// 开始录音动画
					startRecordAnim();
					// 开始录音
					recordSecond = 0;
					recordManager.startRecording(userObjectId);
				} else {
					ib_recoder.setTag("off");
					// 动画，秒数消失
					stopRecordAnim();
					// 停止录音
					recordManager.stopRecording();
					tv_second.setText("准备录音");
					pb.setMax(recordSecond * 1000);
					pb.setProgress(0);
					ll_bottom.setVisibility(View.VISIBLE);
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
					palyAudio();
				} else {
					tv_time.setText(recordSecond + "秒");
					ib_play.setTag("play");
					ib_play.setImageResource(R.drawable.play_audio);
					// 停止/暂停播放
					pauseAudio();
				}
			}
		});

		// 添加按钮
		view.findViewById(R.id.tv_ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseAudio();
				dialog.dismiss();
				curPosition = 0;// 初始化播放进度
				listener.onRecordCompleled(recorderPath);
			}
		});
		// 删除按钮
		view.findViewById(R.id.iv_del).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseAudio();
				recorderPath = null;
				ll_bottom.setVisibility(View.GONE);
				listener.onRecordCompleled(null);//空路径
			}
		});
		// 取消按钮按钮
		view.findViewById(R.id.tv_cancle).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseAudio();
				listener.onRecordCancel();
				dialog.dismiss();
				listener.onRecordCompleled(null);
			}
		});

		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				recordManager.stopRecording();
				pauseAudio();
				stopRecordAnim();
			}
		});
	}

	private void initView(View v) {
		ib_recoder = (ImageButton) v.findViewById(R.id.ib_recoder);
		tv_second = (TextView) v.findViewById(R.id.tv_second);
		rippleBackground = (RippleBackground) v.findViewById(R.id.content);
		// 底部按钮
		ib_play = (ImageButton) v.findViewById(R.id.ib_play);
		pb = (ProgressBar) v.findViewById(R.id.pb);
		tv_time = (TextView) v.findViewById(R.id.tv_time);
		// 底部布局
		ll_bottom = (ViewGroup) v.findViewById(R.id.ll_bottom);
		ll_bottom.setVisibility(View.GONE);

		dialog.setView(v, 0, 0, 0, 0);
		dialog.show();
		
		
	}

	/**
	 * 录音动画
	 */
	private void startRecordAnim() {
		if (rippleBackground != null) rippleBackground.startRippleAnimation();
	}

	/**
	 * 停止录音的动画
	 */
	private void stopRecordAnim() {
		if (rippleBackground.isRippleAnimationRunning()) {
			rippleBackground.stopRippleAnimation();
		}
	}

	protected void palyAudio() {
		// 播放录音
		if (recorderPath == null) {
			T.show(context, "找不到录音文件");
			return;
		}
		if (player == null) player = new MediaPlayer();
		player.reset();
		try {
			player.setDataSource(recorderPath);
			player.prepare();
			player.seekTo(curPosition);
			player.start();
			pb.setMax(player.getDuration());
			// 秒++
			if (timer_play == null) timer_play = new Timer();
			timer_play.schedule(new TimerTask() {
				@Override
				public void run() {
					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							Log.e("	timer_play.schedule", curPosition + "");
							if(tv_time==null) L.e("tv_time空空空空");
							tv_time.setText((player.getCurrentPosition() / 1000) + "秒");
							pb.setProgress(player.getCurrentPosition());
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
	 * 暂停播放,保存播放进度
	 */
	private void pauseAudio() {
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

	/** 布局控件已准备 */
	public static final int STATUS_PERPARED = 0;
	/** 正在播放 **/
	public static final int STATUS_RECORDING = 1;
	/** 录音完成 */
	public static final int STATUS_COMPLETED = 2;
	/** 录音取消，释放资源 */
	public static final int STATUS_CANCEL = 3;
	/** 录音窗口关闭 */
	public static final int STATUS_DISSMISS = 3;

	public interface RecordStatusChangedListener {
		public void onRecordCompleled(String path);
		public void onRecordCancel();
	}
}