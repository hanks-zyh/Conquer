package app.hanks.com.conquer.util;

import android.content.Context;

/**
 * App
 */
public class AppInfoUtils {

	private static String getAppName(Context context,int pID) {
		return context.getApplicationInfo().name;
	}
}
