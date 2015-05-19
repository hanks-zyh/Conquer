package app.hanks.com.conquer.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import app.hanks.com.conquer.R;

public class SlideDelListview extends ListView {

	private static final String TAG = "QQlistView";

	// private static final int VELOCITY_SANP = 200;
	// private VelocityTracker mVelocityTracker;
	/**
	 * 用户滑动的最小距离
	 */
	private int touchSlop;

	/**
	 * 是否响应滑动
	 */
	private boolean isSliding;

	/**
	 * 手指按下时的x坐标
	 */
	private int xDown;
	/**
	 * 手指按下时的y坐标
	 */
	private int yDown;
	/**
	 * 手指移动时的x坐标
	 */
	private int xMove;
	/**
	 * 手指移动时的y坐标
	 */
	private int yMove;

	private LayoutInflater mInflater;

	private PopupWindow mPopupWindow;
	private int mPopupWindowHeight;
	private int mPopupWindowWidth;

	private Button mDelBtn;
	/**
	 * 为删除按钮提供一个回调接口
	 */
	private DelButtonClickListener mListener;

	/**
	 * 当前手指触摸的View
	 */
	private View mCurrentView;

	/**
	 * 当前手指触摸的位置
	 */
	private int mCurrentViewPos;

	public SlideDelListview(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public SlideDelListview(Context context) {
		super(context);
		init(context);
	}

	public SlideDelListview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public void init(Context context){
		mInflater = LayoutInflater.from(context);  
    touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();  
    View view = mInflater.inflate(R.layout.delete_btn, null);  
    mDelBtn = (Button) view.findViewById(R.id.id_item_btn);  
    mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,  
            LinearLayout.LayoutParams.WRAP_CONTENT);  
    /** 
     * 先调用下measure,否则拿不到宽和高 
     */  
    mPopupWindow.getContentView().measure(0, 0);  
    mPopupWindowHeight = mPopupWindow.getContentView().getMeasuredHeight();  
    mPopupWindowWidth = mPopupWindow.getContentView().getMeasuredWidth();  
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		switch (action) {

			case MotionEvent.ACTION_DOWN:
				xDown = x;
				yDown = y;
				/**
				 * 如果当前popupWindow显示，则直接隐藏，然后屏蔽ListView的touch事件的下传
				 */
				if (mPopupWindow.isShowing()) {
					dismissPopWindow();
					return false;
				}
				// 获得当前手指按下时的item的位置
				mCurrentViewPos = pointToPosition(xDown, yDown);
				// 获得当前手指按下时的item
				View view = getChildAt(mCurrentViewPos - getFirstVisiblePosition());
				mCurrentView = view;
				break;
			case MotionEvent.ACTION_MOVE:
				xMove = x;
				yMove = y;
				int dx = xMove - xDown;
				int dy = yMove - yDown;
				/**
				 * 判断是否是从右到左的滑动
				 */
				if (xMove < xDown && Math.abs(dx) > touchSlop && Math.abs(dy) < touchSlop) {
					// Log.e(TAG, "touchslop = " + touchSlop + " , dx = " + dx +
					// " , dy = " + dy);
					isSliding = true;
				}
				break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		/**
		 * 如果是从右到左的滑动才相应
		 */
		if (isSliding) {
			switch (action) {
				case MotionEvent.ACTION_MOVE:
					int[] location = new int[2];
					// 获得当前item的位置x与y
					mCurrentView.getLocationOnScreen(location);
					// 设置popupWindow的动画
					// mPopupWindow.setAnimationStyle(R.style.popwindow_delete_btn_anim_style);
					mPopupWindow.update();
					mPopupWindow.showAtLocation(mCurrentView, Gravity.LEFT | Gravity.TOP, location[0] + mCurrentView.getWidth(), location[1]
							+ mCurrentView.getHeight() / 2 - mPopupWindowHeight / 2);
					// 设置删除按钮的回调
					mDelBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (mListener != null) {
								mListener.clickHappend(mCurrentViewPos);
								mPopupWindow.dismiss();
							}
						}
					});
					// Log.e(TAG, "mPopupWindow.getHeight()=" + mPopupWindowHeight);

					break;
				case MotionEvent.ACTION_UP:
					isSliding = false;
			}
			// 相应滑动期间屏幕itemClick事件，避免发生冲突
			return true;
		}

		return super.onTouchEvent(ev);
	}

	/**
	 * 隐藏popupWindow
	 */
	private void dismissPopWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}

	public void setDelButtonClickListener(DelButtonClickListener listener) {
		mListener = listener;
	}

	interface DelButtonClickListener {
		public void clickHappend(int position);
	}

}
