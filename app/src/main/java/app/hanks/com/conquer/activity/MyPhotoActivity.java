package app.hanks.com.conquer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.AlbumAdapter;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.T;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MyPhotoActivity extends BaseActivity {

	private GridView gv_album;
	private ArrayList<String> list;
	private AlbumAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList<CharSequence> albums = getIntent().getCharSequenceArrayListExtra("album");
		list = new ArrayList<String>();
		for (CharSequence s : albums) {
			list.add(s.toString());
		}
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		gv_album = (GridView) findViewById(R.id.gv_album);
		adapter = new AlbumAdapter(context, list);
		gv_album.setAdapter(adapter);
	}

	protected void selectPic() {
		Intent intent = new Intent(context, SelectPicActivity.class);
		intent.putExtra("noCut", true);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		L.d(requestCode + "," + resultCode + "," + data);
		if (resultCode == RESULT_OK) {
			File f = new File(data.getStringExtra("photo_path"));
			L.e("照片路径：" + f.getAbsolutePath());
			if (f.exists()) {
				final BmobFile bf = new BmobFile(f);
				bf.uploadblock(context, new UploadFileListener() {
					@Override
					public void onSuccess() {
						list.add(0, bf.getFileUrl(getApplicationContext()));
						adapter.notifyDataSetChanged();
						updateAlbum();
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						T.show(context, "上传失败");
					}
				});
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 更新相册
	 */
	public void updateAlbum() {
		// 更新
		if (currentUser != null) {
			currentUser.setAlbum(list);
			currentUser.update(context, new UpdateListener() {
				@Override
				public void onSuccess() {
					L.d("更新相册成功" + list.size());
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					L.d("更新相册失败");
				}
			});
		}
	}

	@Override
	public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
		tv_title.setText("相册");
		ib_back.setImageResource(R.drawable.ic_clear_white_24dp);
		ib_right.setVisibility(View.VISIBLE);
		shadow.setVisibility(View.GONE);
		ib_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectPic();				
			}
		});
	}

	@Override
	public View getContentView() {
		return View.inflate(context, R.layout.activity_my_photo, null);
	}
}
