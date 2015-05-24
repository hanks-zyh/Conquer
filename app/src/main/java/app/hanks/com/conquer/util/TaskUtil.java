package app.hanks.com.conquer.util;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.hanks.com.conquer.bean.Card;
import app.hanks.com.conquer.bean.Task;
import app.hanks.com.conquer.bean.User;
import app.hanks.com.conquer.config.Constants;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 自习的逻辑
 *
 * @author wmf
 */
public class TaskUtil {

    public final static String FORMAT_YEAR      = "yyyy";
    public final static String FORMAT_MONTH_DAY = "MM月dd日";

    public final static String FORMAT_DATE           = "yyyy-MM-dd";
    public final static String FORMAT_TIME           = "HH:mm";
    public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日  hh:mm";

    public final static String FORMAT_DATE_TIME        = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_DATE1_TIME       = "yyyy/MM/dd HH:mm";
    public final static String FORMAT_DATE_TIME_SECOND = "yyyy/MM/dd HH:mm:ss";

    private static SimpleDateFormat sdf     = new SimpleDateFormat();
    private static boolean          debugDB = false;

    /**
     * 返回大于当后时间的自习
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static List<Task> getAfterZixi(Context context) {
        List<Task> listTask = new ArrayList<Task>();
        DbUtils dbUtils = DbUtils.create(context);
        List<Task> temp = new ArrayList<Task>();
        try {
            // temp = dbUtils.findAll(Tasks.class);
            List<Task> findAll = dbUtils.findAll(Selector.from(Task.class).orderBy("time"));
            if (findAll != null && findAll.size() > 0) {
                temp.addAll(findAll);
                long curTime = System.currentTimeMillis();
                L.i("大小" + temp.size());
                for (Task task : temp) {
                    if (task.getTime() >= curTime) {
                        listTask.add(task);
                        if (listTask.size() > Constants.MAIN_MYZIXI_LIMIT) break;
                    }
                }
            }
        } catch (Exception e) {
            // if (debugDB) e.printStackTrace();
        }
        return listTask;
    }

    /**
     * 返回今天本地大于当前时间的自习,并且未被提醒的
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static List<Task> getTodayAfterZixi(Context context) {
        List<Task> listTask = new ArrayList<Task>();
        DbUtils dbUtils = DbUtils.create(context);
        Calendar cur = Calendar.getInstance();
        cur.setTimeInMillis(System.currentTimeMillis());
        Calendar c = Calendar.getInstance();
        try {
            // 获取没有提醒的
            List<Task> findAll = dbUtils.findAll(Selector.from(Task.class).where("hasAlerted", "=", false).orderBy("time"));
            if (findAll != null && findAll.size() > 0) {
                long curTime = System.currentTimeMillis();
                L.i("大小" + findAll.size());
                for (Task task : findAll) {
                    c.setTimeInMillis(task.getTime());
                    // 今天，大于当前时间的
                    if (task.getTime() >= curTime && c.get(Calendar.DAY_OF_YEAR) == cur.get(Calendar.DAY_OF_YEAR)) {
                        listTask.add(task);
                    }
                }
            }
        } catch (Exception e) {
            // if (debugDB) e.printStackTrace();
        }
        return listTask;
    }

    /**
     * 返回数据库用户所有的自习
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static List<Task> getAllZixi(Context context) {
        DbUtils dbUtils = DbUtils.create(context);
        List<Task> temp = new ArrayList<Task>();
        try {
            // temp = dbUtils.findAll(Tasks.class);
            List<Task> findAll = dbUtils.findAll(Selector.from(Task.class).orderBy("time"));
            if (findAll != null && findAll.size() > 0) {
                temp.addAll(findAll);
                L.i("大小" + temp.size());
            }
        } catch (Exception e) {
            // if (debugDB) e.printStackTrace();
        }
        return temp;
    }

    /**
     * 根据日期返回数据库用户某天的自习
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static List<Task> getZixiByDay(Context context, long time) {
        DbUtils dbUtils = DbUtils.create(context);
        List<Task> temp = new ArrayList<Task>();
        try {
            List<Task> findAll = dbUtils.findAll(Selector.from(Task.class).orderBy("time"));
            if (CollectionUtils.isNotNull(findAll)) {
                for (Task task : findAll) {
                    if (isToday(task.getTime(), time)) temp.add(task);
                }
                L.i("大小" + temp.size());
            }
        } catch (Exception e) {
            // if (debugDB) e.printStackTrace();
        }
        return temp;
    }

    /**
     * 判断两个时间是不是同一天
     *
     * @return
     */
    public static boolean isToday(long now, long timestamp) {
        Calendar curC = Calendar.getInstance();
        curC.setTimeInMillis(now);
        Calendar zixiC = Calendar.getInstance();
        zixiC.setTimeInMillis(timestamp);
        int curYear = curC.get(Calendar.YEAR);
        int curDay = curC.get(Calendar.DAY_OF_YEAR);// 这一年的第几天
        int zixiYear = zixiC.get(Calendar.YEAR);
        int zixiDay = zixiC.get(Calendar.DAY_OF_YEAR);// 这一年的第几天
        return curYear == zixiYear && curDay == zixiDay;
    }

    /**
     * 返回网络上大于当后时间的自习
     *
     * @param context
     * @param currentUser
     * @param getZixiCallBack 获取网络数据的回调
     * @throws Exception
     */
    public static void getNetAfterZixi(Context context, User currentUser, final int limit, final GetZixiCallBack getZixiCallBack) {
        final DbUtils dbUtils = DbUtils.create(context);
        BmobQuery<Task> query = new BmobQuery<Task>();
        // 设置查询条数
        query.setLimit(1000);
        query.addWhereEqualTo("user", currentUser);
        // 这个查询也包括了用户的已经过时的自习
        query.findObjects(context, new FindListener<Task>() {
            @Override
            public void onSuccess(List<Task> arg0) {
                try {
                    // 1.更新本地数据库
                    if (arg0.size() > 0) {
                        dbUtils.deleteAll(Task.class);
                        dbUtils.saveAll(arg0);
                    }
                } catch (DbException e) {
                    // if (debugDB) e.printStackTrace();
                }
                // 2.筛选大于当后时间的
                List<Task> listTask = new ArrayList<Task>();
                long curTime = System.currentTimeMillis();
                for (Task task : arg0) {
                    if (task.getTime() >= curTime) {
                        listTask.add(task);
                    }
                    if (listTask.size() > limit) break;
                }
                getZixiCallBack.onSuccess(listTask);
            }

            @Override
            public void onError(int arg0, String arg1) {
                getZixiCallBack.onError(arg0, arg1);
            }
        });
    }

    /**
     * 返回网络上用户所有的自习
     *
     * @param context
     * @param currentUser
     * @param getZixiCallBack 获取网络数据的回调
     * @throws Exception
     */
    public static void getNetAllZixi(Context context, User currentUser, final GetZixiCallBack getZixiCallBack) {
        final DbUtils dbUtils = DbUtils.create(context);
        BmobQuery<Task> query = new BmobQuery<Task>();
        query.addWhereEqualTo("user", currentUser);
        // 这个查询也包括了用户的已经过时的自习
        query.findObjects(context, new FindListener<Task>() {
            @Override
            public void onSuccess(List<Task> arg0) {
                try {
                    // 1.更新本地数据库
                    if (arg0.size() > 0) {
                        dbUtils.deleteAll(Task.class);
                        dbUtils.saveAll(arg0);
                    }
                } catch (DbException e) {
                    // if (debugDB) e.printStackTrace();
                }
                // 2.筛选大于当后时间的
                List<Task> listTask = new ArrayList<Task>();
                long curTime = System.currentTimeMillis();
                for (Task task : arg0) {
                    if (task.getTime() >= curTime) {
                        listTask.add(task);
                    }
                }
                getZixiCallBack.onSuccess(listTask);
            }

            @Override
            public void onError(int arg0, String arg1) {
                getZixiCallBack.onError(arg0, arg1);
            }
        });
    }

    /**
     * 返回网络上其他人的大于当后时间的自习
     *
     * @param context
     * @param currentUser
     * @param getZixiCallBack 获取网络数据的回调
     * @throws Exception
     */
    public static void getNetZixiNotUser(Context context, User currentUser, final GetZixiCallBack getZixiCallBack) {

        // 这个先不用缓存
        // final DbUtils dbUtils = DbUtils.create(context);
        BmobQuery<Task> query = new BmobQuery<Task>();
        query.include("user");
        query.addWhereNotEqualTo("user", currentUser);
        query.addWhereGreaterThanOrEqualTo("time", System.currentTimeMillis()); // 设置大于当后系统时间的
        query.findObjects(context, new FindListener<Task>() {
            @Override
            public void onSuccess(List<Task> arg0) {
                L.i("getNetZixiNotUser,查询成功" + arg0.size());
                // try {
                // // 1.更新本地数据库
                // if (arg0.size() > 0) {
                // dbUtils.deleteAll(Tasks.class);
                // dbUtils.saveAll(arg0);
                // }
                // } catch (DbException e) {
                // if(debugDB ) e.printStackTrace();
                // }
                // 2.筛选大于当后时间的
                getZixiCallBack.onSuccess(arg0);
            }

            @Override
            public void onError(int arg0, String arg1) {
                L.i("getNetZixiNotUser：查询失败" + arg0 + arg1);
                getZixiCallBack.onError(arg0, arg1);
            }
        });
    }

    /**
     * 返回自习的时间字符串 如 14:12
     *
     * @param task
     * @return
     */
    public static String getZixiTimeS(Task task) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(task.getTime());
        DecimalFormat df = new DecimalFormat("00");
        return df.format(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(calendar.get(Calendar.MINUTE));
    }

    /**
     * 返回自习的时间字符串 如 14:12
     *
     * @return
     */
    public static String getZixiTimeS(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        DecimalFormat df = new DecimalFormat("00");
        return df.format(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(calendar.get(Calendar.MINUTE));
    }

    /**
     * 返回自习的时间字符串 如 2014-11-20
     */
    public static String getZixiDateS(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        DecimalFormat df = new DecimalFormat("00");
        return calendar.get(Calendar.YEAR) + "-" + df.format(calendar.get(Calendar.MONTH)) + "-"
                + df.format(calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 返回自习的时间字符串 如 2014-11-20
     *
     * @param task
     * @return
     */
    public static String getZixiDateS(Task task) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(task.getTime());
        DecimalFormat df = new DecimalFormat("00");
        return calendar.get(Calendar.YEAR) + "-" + df.format(calendar.get(Calendar.MONTH)) + "-"
                + df.format(calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 返回zixi的时间距现在还有几分钟
     *
     * @return
     */
    public static int getDurationFromNow(long time) {
        int result = (int) ((time - System.currentTimeMillis()) / 1000 / 60);
        return result;
    }

    public interface GetZixiCallBack {
        void onSuccess(List<Task> list);

        void onError(int errorCode, String msg);
    }

    /**
     * 根据时间戳获取描述性时间，如3分钟后，1天后
     *
     * @param timestamp 时间戳 单位为毫秒
     * @return 时间字符串
     */
    public static String getDescriptionTimeFromTimestamp(long timestamp) {

        if (timestamp <= System.currentTimeMillis()) {
            return TimeUtil.getDescriptionTimeFromTimestamp(timestamp);
        }

        Calendar curC = Calendar.getInstance();
        Calendar zixiC = Calendar.getInstance();
        zixiC.setTimeInMillis(timestamp);

        int curYear = curC.get(Calendar.YEAR);
        int curMonth = curC.get(Calendar.MONTH);
        int curDay = curC.get(Calendar.DAY_OF_YEAR);// 这一年的第几天
        int curHour = curC.get(Calendar.HOUR_OF_DAY);
        int curMin = curC.get(Calendar.MINUTE);

        int zixiYear = zixiC.get(Calendar.YEAR);
        int zixiMonth = zixiC.get(Calendar.MONTH);
        int zixiDay = zixiC.get(Calendar.DAY_OF_YEAR);// 这一年的第几天
        int zixiHour = zixiC.get(Calendar.HOUR_OF_DAY);
        int zixiMin = zixiC.get(Calendar.MINUTE);

        String result = "未知";
        if (curYear == zixiYear) {
            if (curMonth == zixiMonth) {
                if (curDay == zixiDay) {
                    if (zixiHour - curHour <= 1) {// 小于120分钟
                        if (zixiMin + 60 * (zixiHour - curHour) - curMin <= 3) {// 小于3分钟
                            result = "马上";
                        } else result = (zixiMin + 60 * (zixiHour - curHour) - curMin) + "分钟后";
                    } else result = (zixiHour - curHour) + "小时后";
                } else result = (zixiDay - curDay) + "天后";
            } else result = (zixiMonth - curMonth) + "个月后";
        } else result = (zixiYear - curYear) + "年后";
        curC = null;
        zixiC = null;
        return result;
        //
        // long currentTime = System.currentTimeMillis();
        // long timeGap = (timestamp - currentTime) / 1000;// 与现在时间相差秒数
        // System.out.println("timeGap: " + timeGap);
        // String timeStr = null;
        // if (timeGap > YEAR) {
        // timeStr = timeGap / YEAR + "年后";
        // } else if (timeGap > MONTH) {
        // timeStr = timeGap / MONTH + "个月后";
        // } else if (timeGap > DAY) {// 1天以上
        // timeStr = timeGap / DAY + "天后";
        // } else if (timeGap > HOUR) {// 1小时-24小时
        // timeStr = timeGap / HOUR + "小时后";
        // } else if (timeGap > MINUTE) {// 1分钟-59分钟
        // timeStr = timeGap / MINUTE + "分钟后";
        // } else {// 1秒钟-59秒钟
        // timeStr = "刚刚";
        // }
        // return timeStr;
    }

    /**
     * 获取提醒和勾搭我的Card
     *
     * @param context
     * @param getCardListener
     */
    public static void getAllMyCard(Context context, String tId, final GetCardListener getCardListener) {
        BmobQuery<Card> query = new BmobQuery<Card>();
        query.addWhereEqualTo("tId", tId);
        query.order("-createdAt");
        query.findObjects(context, new FindListener<Card>() {
            @Override
            public void onSuccess(List<Card> arg0) {
                L.i("getAllMyCard成功：" + arg0.size());
                getCardListener.onSuccess(arg0);
            }

            @Override
            public void onError(int arg0, String arg1) {
                L.i("getAllMyCard失败：" + arg0 + arg1);
                getCardListener.onError(arg0, arg1);
            }
        });
    }

    /**
     * 删除提醒和勾搭我的Card
     *
     * @param context
     */
    public static void deleteMyCard(final Context context, String cardId, final DeleteCardistener deleteCardistener) {
        BmobQuery<Card> query = new BmobQuery<Card>();
        query.addWhereEqualTo("objectId", cardId);
        query.findObjects(context, new FindListener<Card>() {
            @Override
            public void onSuccess(List<Card> arg0) {
                if (CollectionUtils.isNotNull(arg0)) {
                    arg0.get(0).delete(context, new DeleteListener() {
                        @Override
                        public void onSuccess() {
                            L.i("删除Card成功");
                            deleteCardistener.onSuccess();

                        }

                        @Override
                        public void onFailure(int arg0, String arg1) {
                            L.i("删除MyCard失败：" + arg0 + arg1);
                            deleteCardistener.onError(arg0, arg1);
                        }
                    });
                }

            }

            @Override
            public void onError(int arg0, String arg1) {
                L.i("删除MyCard失败：" + arg0 + arg1);
            }
        });
    }

    /**
     * 删除自习
     *
     * @param context
     * @param task
     * @param deleteZixiListener
     */
    public static void DeleteZixi(Context context, Task task, final DeleteZixiListener deleteZixiListener) {
        final DbUtils dbUtils = DbUtils.create(context);
        try {
            dbUtils.delete(task);
        } catch (DbException e) {
            // if (debugDB) e.printStackTrace();
        }
        task.delete(context, new DeleteListener() {
            @Override
            public void onSuccess() {
                L.i("删除自习成功");
                deleteZixiListener.onSuccess();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                L.i("删除自习失败：" + arg0 + arg1);
                deleteZixiListener.onError(arg0, arg1);
            }
        });
    }

    public interface DeleteZixiListener {
          void onSuccess();

          void onError(int errorCord, String msg);
    }

    public interface DeleteCardistener {
          void onSuccess();

          void onError(int errorCord, String msg);
    }

    public interface GetCardListener {
          void onSuccess(List<Card> list);
          void onError(int errorCord, String msg);
    }

    /**
     * 计算给定点距离本机的距离
     *
     * @param location
     * @return
     */
    public static String getDistance(User currentUser, BmobGeoPoint location) {
        String dis = "";
        if (currentUser != null && currentUser.getLocation() != null && location != null) {
            BmobGeoPoint loc = currentUser.getLocation();
            dis = (int) distance(loc.getLongitude(), loc.getLatitude(), location.getLongitude(), location.getLatitude()) + "米";
        }
        return dis;
    }

    // /**
    // * 计算地球上任意两点(经纬度)距离
    // * @param long1 第一点经度
    // * @param lat1 第一点纬度
    // * @param long2 第二点经度
    // * @param lat2 第二点纬度
    // * @return 返回距离 单位：米
    // */
    // public static double distance(double long1, double lat1, double long2, double lat2) {
    // double a, b, R;
    // R = 6378137; // 地球半径
    // lat1 = lat1 * Math.PI / 180.0;
    // lat2 = lat2 * Math.PI / 180.0;
    // a = lat1 - lat2;
    // b = (long1 - long2) * Math.PI / 180.0;
    // double d;
    // double sa2, sb2;
    // sa2 = Math.sin(a / 2.0);
    // sb2 = Math.sin(b / 2.0);
    // d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
    // return d;
    // }
    //
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
     */
    public static double distance(double lng1, double lat1, double lng2, double lat2) {
        double EARTH_RADIUS = 6378.137;
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math
                .asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        // s = Math.round(s * 10000) / 10000;
        double result = s * 1000;
        if (result > 1000) result /= 1000;
        return result;
    }

    /**
     * 将改自习设置为已被提醒
     */
    public static void setZixiHasAlerted(Context context, int zixiId) {
        final DbUtils dbUtils = DbUtils.create(context);
        try {
            L.e("设置自习为已提醒" + zixiId);
            Task task = dbUtils.findFirst(Selector.from(Task.class).where("id", "=", zixiId));
            if (task != null) {
                task.setHasAlerted(true);
                dbUtils.update(task, "hasAlerted");
            }
        } catch (DbException e) {
            // if (debugDB) e.printStackTrace();
        }
    }

    /**
     * 上传一个文件
     *
     * @param context
     * @param f
     * @param upLoadListener
     */
    public static void upLoadFile(final Context context, File f, final UpLoadListener upLoadListener) {
        final BmobFile bf = new BmobFile(f);
        bf.uploadblock(context, new UploadFileListener() {
            @Override
            public void onSuccess() {
                L.d("上传文件成功" + bf.getFileUrl(context));
                upLoadListener.onSuccess(bf.getFileUrl(context));
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                L.d("上传文件失败" + arg0 + arg1);
                upLoadListener.onFailure(arg0, arg1);
            }
        });
    }

    public interface UpLoadListener {
        void onSuccess(String url);

        void onFailure(int error, String msg);
    }

}
