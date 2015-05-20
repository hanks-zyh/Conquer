package app.hanks.com.conquer.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import app.hanks.com.conquer.bean.Task;
import cn.bmob.im.BmobChatManager;

import com.google.gson.Gson;
import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.Card;
import app.hanks.com.conquer.bean.User;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.MsgUtils;

public class GoudaCradActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gouda_card);
		init();
	}

	private User      targetUser;
	private TextView  tv_title;
	private ImageView iv_record;
	private EditText  et_note;
	private ImageView iv_card_bg;
	private Button    bt_send;
	private Task      task;

	private void init() {
		task = (Task) getIntent().getSerializableExtra("task");
		targetUser = task.getUser();
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_record = (ImageView) findViewById(R.id.iv_record);
		iv_card_bg = (ImageView) findViewById(R.id.iv_card_bg);
		et_note = (EditText) findViewById(R.id.et_note);
		tv_title = (TextView) findViewById(R.id.tv_title);
		bt_send = (Button) findViewById(R.id.bt_send);
		bt_send.setOnClickListener(this);
		et_note.setText("亲爱的" + targetUser.getNick() + ":");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_send:
				sendGoudaCard();
				break;
		}
	}

	/**
	 * 发送勾搭卡
	 */
	private void sendGoudaCard() {
		if (currentUser != null) {
			String text = et_note.getText().toString();
			Card card = new Card();
			card.setType(1);// 1。勾搭卡
			card.setFid(currentUser.getObjectId());
			card.setFusername(currentUser.getUsername());
			card.setFnick(currentUser.getNick());
			card.setFavatar(currentUser.getAvatar());
			card.setContent(text);
			card.setZixiName(task.getName());
			card.setTime(task.getTime());
			card.settId(task.getUser().getObjectId());
			// card.setAudioUrl("");
			// card.setImgUrl("");
			L.e(card.toString());
			String json = new Gson().toJson(card);
			MsgUtils.sendMsg(context, BmobChatManager.getInstance(context), task.getUser(), json);
		} else {
			// 登录对话框
		}
	}

	@Override
	public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {

	}

	@Override
	public View getContentView() {
		return  null;
	}
}
