package app.hanks.com.conquer.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.activity.MyPhotoActivity;
import app.hanks.com.conquer.activity.PhotoGallery;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.AlertDialogUtils;

/**
 * 相册的适配器 2014-11-25 12:31:54
 * @author zyh
 */
public class AlbumAdapter extends MyBaseAdpter<String> {

	public AlbumAdapter(Context context, ArrayList<String> list) {
		super(context, list);
		loder = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.pic_loading).showImageForEmptyUri(R.drawable.pic_loading)
				.showImageOnFail(R.drawable.pic_loading).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	private ImageLoader loder;
	private DisplayImageOptions options;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_grid_image, null);
		}
		ImageView image = ViewHolder.get(convertView, R.id.image);
		final ProgressBar progress = ViewHolder.get(convertView, R.id.progress);
		loder.displayImage((String) list.get(position), image, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progress.setProgress(0);
				progress.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				progress.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				progress.setVisibility(View.GONE);
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
				progress.setProgress(Math.round(100.0f * current / total));
			}
		});
		//点击查看
		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, PhotoGallery.class);
				intent.putStringArrayListExtra("album", list);
				A.goOtherActivity(context, intent);
			}
		});
		
		//长按删除
		image.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				AlertDialogUtils.show(context, "是否删除", "确定要删除吗", "删除", "算了", new AlertDialogUtils.OkCallBack() {
					@Override
					public void onOkClick(DialogInterface dialog, int which) {
						list.remove(position);
						AlbumAdapter.this.notifyDataSetChanged();
						((MyPhotoActivity) context).updateAlbum();
					}
				}, null);
				return false;
			}
		});

		return convertView;
	}

}
