package app.hanks.com.conquer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.utils.L;

import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.util.PixelUtil;
import app.hanks.com.conquer.util.ProgressUtil;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.util.ZixiUtil;
import app.hanks.com.conquer.util.ZixiUtil.DeleteZixiListener;
import app.hanks.com.conquer.view.RoundProgressBar;


public class MyZixiAdapter extends RecyclerView.Adapter<MyZixiAdapter.ZixiViewHolder> {


    private final List<Task> list;
    private final Context    context;

    public MyZixiAdapter(Context context, List<Task> list) {
        this.context = context;
        this.list = list;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_myzix, parent, false);
        }
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
        TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
        RoundProgressBar pb = ViewHolder.get(convertView, R.id.pb);// pd最大值为60*24*3，3天以后的为最大值
        Task task = list.get(position);
        if (tv_name == null) {
            L.d("tv_name空了空空了空空了空空了空空了空空了空空了空");
        }
        if (task == null) {
            L.d("zixi空了空空了空空了空空了空空了空空了空空了空");
        }
        if (ZixiUtil.getZixiTimeS(task) == null) {
            L.d("getZixiTimeS空了空空了空空了空空了空空了空空了空空了空");
        }
        tv_time.setText(ZixiUtil.getZixiTimeS(task.getTime()));
        tv_name.setText(task.getName());
        int p = 4320 - ZixiUtil.getDurationFromNow(task.getTime());
		if (p <= 0) p = 1;
		L.d("自习进度:" + p);
		pb.setText(ZixiUtil.getDescriptionTimeFromTimestamp(task.getTime()));
		if (task.getTime() <= System.currentTimeMillis()) {
			pb.setRoundWidth(0);
			pb.setText(ZixiUtil.getZixiDateS(task.getTime()));
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

	@Override
	public ZixiViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_myzix, viewGroup, false);
        return new ZixiViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ZixiViewHolder zixiViewHolder, final int position) {
        Task task = list.get(position);
        if (zixiViewHolder.tv_name == null) {
            L.d("tv_name空了空空了空空了空空了空空了空空了空空了空");
        }
        if (task == null) {
            L.d("zixi空了空空了空空了空空了空空了空空了空空了空");
        }
        if (ZixiUtil.getZixiTimeS(task) == null) {
            L.d("getZixiTimeS空了空空了空空了空空了空空了空空了空空了空");
        }
        zixiViewHolder. tv_time.setText(ZixiUtil.getZixiTimeS(task.getTime()));
        zixiViewHolder. tv_name.setText(task.getName());
        int p = 4320 - ZixiUtil.getDurationFromNow(task.getTime());
        if (p <= 0) p = 1;
        L.d("自习进度:" + p);
        zixiViewHolder. pb.setText(ZixiUtil.getDescriptionTimeFromTimestamp(task.getTime()));
        if (task.getTime() <= System.currentTimeMillis()) {
            zixiViewHolder. pb.setRoundWidth(0);
            zixiViewHolder. pb.setText(ZixiUtil.getZixiDateS(task.getTime()));
        }else{
            zixiViewHolder.  pb.setRoundWidth(PixelUtil.dp2px(6));
        }
        if (p < 100) p = 100;// 防止太小了
        zixiViewHolder. pb.setProgress(p);
        zixiViewHolder.itemView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteZixi(position);
                return false;
            }
        });
	}

	@Override
	public int getItemCount() {
		return list ==null ? 0 : list.size();
	}

	class ZixiViewHolder extends RecyclerView.ViewHolder{
        TextView tv_time ;
        TextView tv_name ;
        RoundProgressBar pb;

		public ZixiViewHolder(View itemView) {
			super(itemView);
              tv_time = (TextView) itemView.findViewById(R.id.tv_time);
              tv_name =  (TextView) itemView.findViewById(R.id.tv_name);
              pb = (RoundProgressBar) itemView.findViewById( R.id.pb);
		}

	}
}
