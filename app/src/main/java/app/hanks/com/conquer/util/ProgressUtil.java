package app.hanks.com.conquer.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * 进度条工具类
 * @author LeeLay 2014-9-24
 */
public class ProgressUtil {

	private static Dialog progressDialog;

	// public static void show(Context context, String message) {
	//
	// LayoutInflater inflater = LayoutInflater.from(context);
	// View v = inflater.inflate(R.layout.dialog_progress, null);// 得到加载view
	// LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
	// // main.xml中的ImageView
	// ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
	// TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
	// // 加载动画
	// // Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
	// // context, R.anim.loading);
	// // 使用ImageView显示动画
	// // spaceshipImage.startAnimation(hyperspaceJumpAnimation);
	// spaceshipImage.setImageResource(R.drawable.dialog_loading);
	// AnimationDrawable animationDrawable = (AnimationDrawable) spaceshipImage
	// .getDrawable();
	// animationDrawable.start();
	// tipTextView.setText(message);// 设置加载信息
	//
	// progressDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
	//
	// progressDialog.setCancelable(false);// 不可以用“返回键”取消
	// progressDialog.setContentView(layout, new LinearLayout.LayoutParams(
	// LinearLayout.LayoutParams.MATCH_PARENT,
	// LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
	//
	// progressDialog.show();
	// }

	public static void showWaitting(Context context) {
		if (progressDialog == null) progressDialog = new ProgressDialog(context);// 创建自定义样式dialog
		progressDialog.setTitle("请稍后...");
		progressDialog.setCancelable(false);// 不可以用“返回键”取消
		progressDialog.show();
	}

	public static void dismiss() {
		if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
	}
}
