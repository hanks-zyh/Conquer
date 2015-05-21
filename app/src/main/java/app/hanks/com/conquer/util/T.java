package app.hanks.com.conquer.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import app.hanks.com.conquer.R;

/**
 * Toast常用类
 * @author LeeLay 2014-9-24
 */
public class T {

	/**
	 * 默认注释
	 * @param context
	 * @param content
	 */
	public static void show(Context context, String content) {
		View v = View.inflate(context.getApplicationContext(), R.layout.layout_toast, null);
		((TextView)v.findViewById(R.id.text)).setText(content);
		Toast t = new Toast(context.getApplicationContext());
		t.setView(v);
		t.setGravity(Gravity.TOP,0,0);
		t.show();
	}

	/**
	 * 长显示
	 * @param context
	 * @param content
	 */
	public static void showL(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	/**
	 * 网络错误Toast
	 * @param context
	 */
	public static void showNetErr(Context context) {
		Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
	}

}
