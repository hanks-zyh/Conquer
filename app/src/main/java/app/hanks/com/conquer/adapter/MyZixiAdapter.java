package app.hanks.com.conquer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.nostra13.universalimageloader.utils.L;

import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.util.PixelUtil;
import app.hanks.com.conquer.util.ProgressUtil;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.util.TaskUtil;
import app.hanks.com.conquer.util.TaskUtil.DeleteZixiListener;
import app.hanks.com.conquer.view.RoundProgressBar;


public class MyZixiAdapter extends RecyclerView.Adapter<MyZixiAdapter.ZixiViewHolder> {


    private final List<Task> list;
    private final Context    context;
    private boolean finish = false;
    private boolean delete = false;

    public MyZixiAdapter(Context context, List<Task> list) {
        this.context = context;
        this.list = list;
    }


	/**
	 * 删除我的自习
	 * @param position
	 */
	private void deleteZixi(final int position) {
//		new AlertDialog.Builder(context).setTitle("是否删除该条自习").setMessage("").setPositiveButton("删除", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				ProgressUtil.showWaitting(context);
				TaskUtil.DeleteZixi(context, list.get(position), new DeleteZixiListener() {
                    @Override
                    public void onSuccess() {
//						ProgressUtil.dismiss();


                        list.remove(position);
                        MyZixiAdapter.this.notifyDataSetChanged();


                    }

                    @Override
                    public void onError(int errorCord, String msg) {
                        T.show(context, "删除失败，请检查网络");
                        ProgressUtil.dismiss();
                    }
                });
//			}
//		}).setNegativeButton("算了", null).show();
	}

	@Override
	public ZixiViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_myzix, viewGroup, false);
        final SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(R.id.swipe);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, v.findViewById(R.id.bottom_right));
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, v.findViewById(R.id.bottom_left));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
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
        if (TaskUtil.getZixiTimeS(task) == null) {
            L.d("getZixiTimeS空了空空了空空了空空了空空了空空了空空了空");
        }
        zixiViewHolder. tv_time.setText(TaskUtil.getZixiTimeS(task.getTime()));
        zixiViewHolder. tv_name.setText(task.getName());
        int p = 4320 - TaskUtil.getDurationFromNow(task.getTime());
        if (p <= 0) p = 1;
        L.d("自习进度:" + p);
        zixiViewHolder. pb.setText(TaskUtil.getDescriptionTimeFromTimestamp(task.getTime()));
        if (task.getTime() <= System.currentTimeMillis()) {
            zixiViewHolder. pb.setRoundWidth(0);
            zixiViewHolder. pb.setText(TaskUtil.getZixiDateS(task.getTime()));
        }else{
            zixiViewHolder.  pb.setRoundWidth(PixelUtil.dp2px(6));
        }
        if (p < 100) p = 100;// 防止太小了
        zixiViewHolder. pb.setProgress(p);
//        zixiViewHolder.itemView.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                deleteZixi(position);
//                return false;
//            }
//        });

        zixiViewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                Log.d("SwipeLayout", "onClose:" + layout.getDragDistance());
                delete = false;
                finish = false;
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
                Log.d("SwipeLayout","onUpdate:"+"  "+leftOffset+"    "+topOffset);
                if(Math.abs(leftOffset)>layout.getDragDistance()/4){
                    if(leftOffset>0){
                        delete = true;
                    }else {
                        finish = true;
                    }
                }
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                Log.d("SwipeLayout","onStartOpen:"+layout.getDragDistance());
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
                Log.d("SwipeLayout","onOpen:"+layout.getDragDistance());
                if(delete){
                    deleteZixi(position);
                    L.d("position:"+position);
                }
                delete = false;
                finish = false;
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

                Log.d("SwipeLayout","onStartClose:"+layout.getDragDistance());
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
                Log.d("SwipeLayout","onHandRelease:"+layout.getDragDistance()+","+xvel+","+yvel);


            }
        });
	}

	@Override
	public int getItemCount() {
		return list ==null ? 0 : list.size();
	}

	class ZixiViewHolder extends RecyclerView.ViewHolder{
        SwipeLayout swipeLayout;
        TextView         tv_time;
        TextView         tv_name;
        RoundProgressBar pb;

        public ZixiViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            pb = (RoundProgressBar) itemView.findViewById(R.id.pb);
        }

    }
}
