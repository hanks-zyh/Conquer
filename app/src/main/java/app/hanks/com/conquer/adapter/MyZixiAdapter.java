package app.hanks.com.conquer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.Zixi;
import app.hanks.com.conquer.util.PixelUtil;
import app.hanks.com.conquer.util.ProgressUtil;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.util.ZixiUtil;
import app.hanks.com.conquer.util.ZixiUtil.DeleteZixiListener;
import app.hanks.com.conquer.view.RoundProgressBar;


public class MyZixiAdapter extends MyBaseAdpter<Zixi> {

	public MyZixiAdapter(Context context, ArrayList<Zixi> list) {
		super(context, list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_myzix, parent, false);
		}
		TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
		TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
		RoundProgressBar pb = ViewHolder.get(convertView, R.id.pb);// pd最大值为60*24*3，3天以后的为最大值
		Zixi zixi = list.get(position);
		if (tv_name == null) {
			L.d("tv_name空了空空了空空了空空了空空了空空了空空了空");
		}
		if (zixi == null) {
			L.d("zixi空了空空了空空了空空了空空了空空了空空了空");
		}
		if (ZixiUtil.getZixiTimeS(zixi) == null) {
			L.d("getZixiTimeS空了空空了空空了空空了空空了空空了空空了空");
		}
		tv_time.setText(ZixiUtil.getZixiTimeS(zixi.getTime()));
		tv_name.setText(zixi.getName());
		int p = 4320 - ZixiUtil.getDurationFromNow(zixi.getTime());
		if (p <= 0) p = 1;
		L.d("自习进度:" + p);
		pb.setText(ZixiUtil.getDescriptionTimeFromTimestamp(zixi.getTime()));
		if (zixi.getTime() <= System.currentTimeMillis()) {
			pb.setRoundWidth(0);
			pb.setText(ZixiUtil.getZixiDateS(zixi.getTime()));
		}else{
			pb.setRoundWidth(PixelUtil.dp2px(6));
		}
		if (p < 100) p = 100;// 防止太小了
		pb.setProgress(p);
		convertView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				deleteZixi(position);
				return false;
			}
		});
		return convertView;
	}

	/**
	 * 删除我的自习
	 * @param position
	 */
	private void deleteZixi(final int position) {

		new AlertDialog.Builder(context).setTitle("是否删除该条自习").setMessage("").setPositiveButton("删除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ProgressUtil.showWaitting(context);
				ZixiUtil.DeleteZixi(context, list.get(position), new DeleteZixiListener() {
					@Override
					public void onSuccess() {
						ProgressUtil.dismiss();
						list.remove(position);
						MyZixiAdapter.this.notifyDataSetChanged();
					}
					@Override
					public void onError(int errorCord, String msg) {
						T.show(context, "删除失败，请检查网络");
						ProgressUtil.dismiss();
					}
				});
			}
		}).setNegativeButton("算了", null).show();
	}
}
