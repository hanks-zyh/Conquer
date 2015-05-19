package app.hanks.com.conquer.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.util.ImageLoadOptions;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.PushListener;

/**
 * 查找好友
 * 
 * @ClassName: AddFriendAdapter
 * @Description: TODO
 * @author smile
 * @date 2014-6-25 上午10:56:33
 */
public class AddFriendAdapter extends BaseListAdapter<BmobChatUser> {
	public AddFriendAdapter(Context context, List<BmobChatUser> list) {
		super(context, list);
	}
	@Override
	public View bindView(int arg0, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_add_friend, null);
		}
		final BmobChatUser contract = getList().get(arg0);
		TextView name = ViewHolder.get(convertView, R.id.tv_name);
		TextView nick = ViewHolder.get(convertView, R.id.tv_nick);
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
		Button btn_add = ViewHolder.get(convertView, R.id.btn_add);
		String avatar = contract.getAvatar();
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
		} else {
			iv_avatar.setImageResource(R.drawable.default_photo);
		}
		name.setText("ID:" + contract.getUsername());
		nick.setText(contract.getNick());
		btn_add.setText("添加");
		btn_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final ProgressDialog progress = new ProgressDialog(mContext);
				progress.setMessage("正在添加...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				// 发送tag请求
				BmobChatManager.getInstance(mContext).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, contract.getObjectId(),
						new PushListener() {
							@Override
							public void onSuccess() {
								progress.dismiss();
								ShowToast("发送请求成功，等待对方验证!");
							}

							@Override
							public void onFailure(int arg0, final String arg1) {
								progress.dismiss();
								ShowToast("发送请求失败，请重新添加!");
								ShowLog("发送请求失败:" + arg1);
							}
						});
			}
		});
		return convertView;
	}
}
