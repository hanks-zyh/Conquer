package app.hanks.com.conquer.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.NewFriendAdapter;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.AlertDialogUtils;
import app.hanks.com.conquer.util.AlertDialogUtils.OkCallBack;

/**
 * 新朋友
 * @ClassName: NewFriendActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-6 下午4:28:09
 */
public class NewFriendActivity extends BaseActivity implements OnItemLongClickListener {

	ListView listview;

	NewFriendAdapter adapter;

	String from = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		from = getIntent().getStringExtra("from");
		initView();
	}

	private void initView() {
		listview = (ListView) findViewById(R.id.list_newfriend);
		listview.setOnItemLongClickListener(this);
		adapter = new NewFriendAdapter(this, BmobDB.create(this).queryBmobInviteList());
		listview.setAdapter(adapter);
		if (from == null) {// 若来自通知栏的点击，则定位到最后一条
			listview.setSelection(adapter.getCount());
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		BmobInvitation invite = (BmobInvitation) adapter.getItem(position);
		showDeleteDialog(position, invite);
		return true;
	}

	public void showDeleteDialog(final int position, final BmobInvitation invite) {
		AlertDialogUtils.show(context, invite.getFromname(), "删除好友请求", "确定", "取消", new OkCallBack() {
			@Override
			public void onOkClick(DialogInterface dialog, int which) {
				deleteInvite(position, invite);

			}
		}, null);
	}

	/**
	 * 删除请求 deleteRecent
	 * @param @param recent
	 * @return void
	 * @throws
	 */
	private void deleteInvite(int position, BmobInvitation invite) {
		adapter.remove(position);
		BmobDB.create(this).deleteInviteMsg(invite.getFromid(), Long.toString(invite.getTime()));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (from == null) {
			A.goOtherActivity(context, MainActivity.class);
		}
	}

	@Override
	public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
		tv_title.setText("新朋友");
		ib_back.setImageResource(R.drawable.ic_clear_white_24dp);
	}

	@Override
	public View getContentView() {
		return View.inflate(context, R.layout.activity_new_friend, null);
	}
}
