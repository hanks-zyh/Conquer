package app.hanks.com.conquer.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.util.ProgressUtil;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.util.TaskUtil;
import app.hanks.com.conquer.util.TaskUtil.DeleteZixiListener;


public class MyZixiAdapter extends RecyclerView.Adapter<MyZixiAdapter.ZixiViewHolder>
        implements SwipeableItemAdapter<MyZixiAdapter.ZixiViewHolder>, DraggableItemAdapter<MyZixiAdapter.ZixiViewHolder> {


    private final List<Task> list;
    private final Context    context;


    private EventListener mEventListener;
    private View.OnClickListener mItemViewOnClickListener;
    private View.OnClickListener mSwipeableViewContainerOnClickListener;

    public interface EventListener {
        void onItemRemoved(int position);

        void onItemPinned(int position);

        void onItemViewClicked(View v, boolean pinned);
    }


    public MyZixiAdapter(Context context, List<Task> list) {
        this.context = context;
        this.list = list;
        setHasStableIds(true);

        mItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(v);
            }
        };
        mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeableViewContainerClick(v);
            }
        };
    }

    private void onItemViewClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(v, true); // true --- pinned
        }
    }

    private void onSwipeableViewContainerClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v), false);  // false --- not pinned
        }
    }



    @Override
    public long getItemId(int position) {
        return list.get(position).getObjectId().hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    /**
     * 删除我的自习
     *
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
//                list.remove(position);
//                MyZixiAdapter.this.notifyDataSetChanged();
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
        View v = LayoutInflater.from(context).inflate(R.layout.item_myzixi, viewGroup, false);
        return new ZixiViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ZixiViewHolder holder, final int position) {

        // set listeners
        // (if the item is *not pinned*, click event comes to the itemView)
        holder.itemView.setOnClickListener(mItemViewOnClickListener);
        // (if the item is *pinned*, click event comes to the mContainer)
        holder.mContainer.setOnClickListener(mSwipeableViewContainerOnClickListener);



        Task item = list.get(position);
        holder.mTextView.setText(item.getName());
//        if (zixiViewHolder.tv_name == null) {
//            L.d("tv_name空了空空了空空了空空了空空了空空了空空了空");
//        }
//        if (task == null) {
//            L.d("zixi空了空空了空空了空空了空空了空空了空空了空");
//        }
//        if (TaskUtil.getZixiTimeS(task) == null) {
//            L.d("getZixiTimeS空了空空了空空了空空了空空了空空了空空了空");
//        }
//        zixiViewHolder. tv_time.setText(TaskUtil.getZixiTimeS(task.getTime()));
//        zixiViewHolder. tv_name.setText(task.getName());
//        int p = 4320 - TaskUtil.getDurationFromNow(task.getTime());
//        if (p <= 0) p = 1;
//        L.d("自习进度:" + p);
//        zixiViewHolder. pb.setText(TaskUtil.getDescriptionTimeFromTimestamp(task.getTime()));
//        if (task.getTime() <= System.currentTimeMillis()) {
//            zixiViewHolder. pb.setRoundWidth(0);
//            zixiViewHolder. pb.setText(TaskUtil.getZixiDateS(task.getTime()));
//        }else{
//            zixiViewHolder.  pb.setRoundWidth(PixelUtil.dp2px(6));
//        }
//        if (p < 100) p = 100;// 防止太小了
//        zixiViewHolder. pb.setProgress(p);
////        zixiViewHolder.itemView.setOnLongClickListener(new OnLongClickListener() {
////            @Override
////            public boolean onLongClick(View v) {
////                deleteZixi(position);
////                return false;
////            }
////        });
//
//        zixiViewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
//            @Override
//            public void onClose(SwipeLayout layout) {
//                Log.d("SwipeLayout", "onClose:" + layout.getDragDistance());
//                delete = false;
//                finish = false;
//            }
//
//            @Override
//            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
//                //you are swiping.
//                Log.d("SwipeLayout","onUpdate:"+"  "+leftOffset+"    "+topOffset);
//                if(Math.abs(leftOffset)>layout.getDragDistance()/4){
//                    if(leftOffset>0){
//                        delete = true;
//                    }else {
//                        finish = true;
//                    }
//                }
//            }
//
//            @Override
//            public void onStartOpen(SwipeLayout layout) {
//                Log.d("SwipeLayout","onStartOpen:"+layout.getDragDistance());
//            }
//
//            @Override
//            public void onOpen(SwipeLayout layout) {
//                //when the BottomView totally show.
//                Log.d("SwipeLayout","onOpen:"+layout.getDragDistance());
//                if(delete){
//                    deleteZixi(position);
//                    L.d("position:"+position);
//                }
//                delete = false;
//                finish = false;
//            }
//
//            @Override
//            public void onStartClose(SwipeLayout layout) {
//
//                Log.d("SwipeLayout","onStartClose:"+layout.getDragDistance());
//            }
//
//            @Override
//            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
//                //when user's hand released.
//                Log.d("SwipeLayout","onHandRelease:"+layout.getDragDistance()+","+xvel+","+yvel);
//
//
//            }
//        });

        // set background resource (target view ID: mContainer)
        final int dragState = holder.getDragStateFlags();
        final int swipeState = holder.getSwipeStateFlags();

        if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0) ||
                ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;
            } else if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_swiping_active_state;
            } else if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_SWIPING) != 0) {
                bgResId = R.drawable.bg_item_swiping_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }

        // set swiping properties
        holder.setSwipeItemSlideAmount(
                item.isPinedToSwipeLeft() ? RecyclerViewSwipeManager.OUTSIDE_OF_THE_WINDOW_LEFT : 0);
    }

    //交换位置
    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }
        moveItem(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }
        final Task item = list.remove(fromPosition);
        list.add(toPosition, item);
    }

    @Override
    public int onGetSwipeReactionType(ZixiViewHolder holder, int position, int x, int y) {
        if (onCheckCanStartDrag(holder, position, x, y)) {
            return RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_BOTH;
        } else {
            return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_BOTH;
        }
    }

    @Override
    public void onSetSwipeBackground(ZixiViewHolder holder, int position, int type) {
        int bgRes = 0;
        switch (type) {
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_neutral;
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_left;  //左边滑动出现的布局,应该算是一个drawable
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_right; //右边滑动出现的布局
                break;
        }
        holder.itemView.setBackgroundResource(bgRes);
    }

    @Override
    public int onSwipeItem(ZixiViewHolder holder, int position, int result) {
        switch (result) {
            // swipe right
            case RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT:
                if (list.get(position).isPinedToSwipeLeft()) {
                    // pinned --- back to default position
                    return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
                } else {
                    // not pinned --- remove
                    deleteZixi(position);
                    return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;
                }
                // swipe left -- pin
            case RecyclerViewSwipeManager.RESULT_SWIPED_LEFT:

                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION;
            // other --- do nothing
            case RecyclerViewSwipeManager.RESULT_CANCELED:
            default:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
        }
    }

    @Override
    public void onPerformAfterSwipeReaction(ZixiViewHolder holder, int position, int result, int reaction) {
        final Task item = list.get(position);
        if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM) {
            list.remove(position);
            notifyItemRemoved(position);
            if (mEventListener != null) {
                mEventListener.onItemRemoved(position);
            }
        } else if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION) {
            item.setPinedToSwipeLeft(true);
            notifyItemChanged(position);
            if (mEventListener != null) {
                mEventListener.onItemPinned(position);
            }
        } else {
            item.setPinedToSwipeLeft(false);
        }
    }


    @Override
    public boolean onCheckCanStartDrag(ZixiViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View mContainerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = mContainerView.getLeft() + (int) (ViewCompat.getTranslationX(mContainerView) + 0.5f);
        final int offsetY = mContainerView.getTop() + (int) (ViewCompat.getTranslationY(mContainerView) + 0.5f);

        return hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(ZixiViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }


    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    public static boolean hitTest(View v, int x, int y) {
        final int tx = (int) (ViewCompat.getTranslationX(v) + 0.5f);
        final int ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
        final int left = v.getLeft() + tx;
        final int right = v.getRight() + tx;
        final int top = v.getTop() + ty;
        final int bottom = v.getBottom() + ty;

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ZixiViewHolder extends AbstractDraggableSwipeableItemViewHolder {
        public ViewGroup mContainer;
        public View      mDragHandle;
        public TextView  mTextView;

        public ZixiViewHolder(View itemView) {
            super(itemView);

            mContainer = (ViewGroup) itemView.findViewById(R.id.container);
            mDragHandle = itemView.findViewById(R.id.drag_handle);
            mTextView = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }

}
