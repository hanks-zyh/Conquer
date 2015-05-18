package app.hanks.com.conquer.util;

import android.util.Log;

import com.orhanobut.logger.Logger;

/**
 * 日志工具类
 *
 * @author LeeLay
 *         <p/>
 *         2014-9-25
 */
public class L {
    public static        boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static final String  TAG     = "ZYH";

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug) {
//			Log.i(TAG, "........................" + msg);
            Logger.i(msg);
        }
    }

    public static void d(String msg) {
        if (isDebug) {
//			Log.d(TAG, "........................" + msg);
            Logger.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
//			Log.e(TAG, "........................" + msg);
            Logger.e(TAG, msg);
        }
    }

    public static void v(String msg) {
        if (isDebug) {
//			Log.v(TAG, "........................" + msg);
            Logger.v(TAG, msg);
        }
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug) {
            //Log.i(tag, "........................" + msg);
            Logger.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
//			Log.i(tag,"........................" + msg);
            Logger.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug)
            Log.i(tag, "........................" + msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug)
            Log.i(tag, "........................" + msg);
    }
}
