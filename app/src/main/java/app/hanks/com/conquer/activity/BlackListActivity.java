package app.hanks.com.conquer.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

import app.hanks.com.conquer.CustomApplication;
import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.BlackListAdapter;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.T;

public class BlackListActivity extends BaseActivity implements OnItemClickListener {

	ListView listview;
	BlackListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		// mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		// initTopBarForLeft("黑名单");
		adapter = new BlackListAdapter(this, BmobDB.create(this).getBlackList());
		listview = (ListView) findViewById(R.id.list_blacklist);
		listview.setOnItemClickListener(this);
		listview.setAdapter(adapter);
	}

	/**
	 * 显示移除黑名单对话框
	 * @Title: showRemoveBlackDialog
	 * @Description: TODO
	 * @param @param position
	 * @param @param invite
	 * @return void
	 * @throws
	 */
	public void showRemoveBlackDialog(final int position, final BmobChatUser user) {
		new Builder(context).setTitle("移除黑名单").setMessage("确实移出黑名单吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				adapter.remove(position);
				userManager.removeBlack(user.getUsername(), new UpdateListener() {
					@Override
					public void onSuccess() {
						T.show(context, "移出黑名单成功");
						// 重新设置下内存中保存的好友列表
						CustomApplication.getInstance().setContactList(
								CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList()));
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						T.show(context, "移出黑名单失败:" + arg1);
					}
				});

			}
		}).setNegativeButton("取消", null).show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		BmobChatUser invite = (BmobChatUser) adapter.getItem(arg2);
		showRemoveBlackDialog(arg2, invite);
	}

	@Override
	public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
		tv_title.setText("黑名单");
		ib_back.setImageResource(R.drawable.ic_clear_white_24dp);
	}
	@Override
	public View getContentView() {
		return View.inflate(context, R.layout.activity_blacklist, null);
	}
}
