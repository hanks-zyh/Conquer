package app.hanks.com.conquer.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.AlbumAdapter;

public class MyAlbumFragment extends BaseFragment {

	private GridView gv_album;
	private ArrayList<String> list;
	private AlbumAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_myalbum, container, false);
		init(v);
		return v;
	}

	/**
	 * 初始化
	 * @param v
	 */
	private void init(View v) {
		gv_album = (GridView) v.findViewById(R.id.gv_album);
		list = new ArrayList<String>();
		list.add("http://img.chinaumu.org/img/attachement/jpg/site2/20090820/xin_1408062014122111312516.jpg");
		list.add("http://a1.att.hudong.com/78/56/01300000987765128330562430507.jpg");
		list.add("http://www.qdxw.com.cn/upf/Image/201203011620390373.jpg");
		list.add("http://lady.southcn.com/6/images/attachement/jpg/site4/20130220/75/2432185252249882947.jpg");
		list.add("http://news.cnhubei.com/todaynews/sfw/lskd/201010/W020101004471600765907.jpg");
		list.add("http://pic.jschina.com.cn/0/12/09/58/12095827_547265.jpg");
		list.add("http://file.bmob.cn/M00/D4/7A/oYYBAFR0BUyABXjUAAABZ8j9Gq4753.png");// 添加图片
		adapter = new AlbumAdapter(context, list);
		gv_album.setAdapter(adapter);
	}

}
