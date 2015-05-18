package app.hanks.com.conquer.util;

import android.content.Context;
import android.widget.Toast;

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
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
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
