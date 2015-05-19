package app.hanks.com.conquer.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.MyMsgAdapter;
import app.hanks.com.conquer.bean.MyMsg;
import app.hanks.com.conquer.bean.User;

public class MyMsgFragment extends BaseFragment {

	private ListView lv_msg;
	private MyMsgAdapter<MyMsg> adapter;
	private ArrayList<MyMsg> list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_mymsg, container, false);
		init(v);
		return v;
	}

	/**
	 * 初始化
	 * @param v
	 */
	private void init(View v) {
		lv_msg = (ListView) v.findViewById(R.id.lv_msg);
		list = new ArrayList<MyMsg>();

		User u6 = new User();
		u6.setNick("花开花落");
		u6.setAvatar("http://att.bbs.duowan.com/forum/201304/25/045101b65ppp32kk8jzne5.png");
		u6.setMale(false);
		u6.setSortLetters("aq");

		User u7 = new User();
		u7.setNick("九月你好");
		u7.setAvatar("http://att.bbs.duowan.com/forum/201304/25/045104doj79pno7nvp7a7z.png");
		u7.setMale(false);
		u7.setSortLetters("bsq");

		User u8 = new User();
		u8.setNick("心随你远行");
		u8.setAvatar("http://att.bbs.duowan.com/forum/201304/25/045109czyubkby8b88ttz0.png");
		u8.setMale(true);
		u8.setSortLetters("oos");

		User u9 = new User();
		u9.setNick("幼稚范e");
		u9.setAvatar("http://att.bbs.duowan.com/forum/201304/25/045101s9usszwwak3wuana.png");
		u9.setMale(false);
		u9.setSortLetters("yq");

		list.add(new MyMsg(u6, " 有你在的每一天都很开心°"));
		list.add(new MyMsg(u7, "If you do not leave, I do not discard"));
		list.add(new MyMsg(u8, "旅行的意义在于找到自己，而非浏览他人,姑娘别放弃那个拿青春跟你赌未来的那个少"));
		list.add(new MyMsg(u9, "人生五十年，乃如梦如幻；有生斯有死，壮士复何憾。"));
		adapter = new MyMsgAdapter(context, list);
		lv_msg.setAdapter(adapter);
	}
}
