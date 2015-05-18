package app.hanks.com.conquer.location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.NetUtils;
import app.hanks.com.conquer.util.T;

/**
 * 用于获取定位相关信息
 *
 * @author zyh
 * @version 1.0
 * @date 2015年2月7日
 */
public class LocationService {

    public static final String TAG = "LocationService";
    private static LocationService sInstance;

    private LocationClient mLocationClient = null;
    private BDLocationListener        mListener;
    private Context                   mContext;
    private LocationInfo              lastPoint;
    private OnLocateCompletedListener listener;

    public static LocationService getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LocationService(context);
        }
        return sInstance;
    }

    private LocationService(Context context) {
        mContext = context;
        // 初始化定位sdk
        mListener = new mBDLocationListener();
        // 声明LocationClient类
        mLocationClient = new LocationClient(mContext.getApplicationContext());
        // 注册监听函数
        mLocationClient.registerLocationListener(mListener);
    }

    /**
     * 设置定位回调监听
     *
     * @param listener
     * @version 1.0
     * @author zyh
     * @date 2015年2月7日 下午8:03:27
     */
    public void setOnLocateCompletedListener(OnLocateCompletedListener listener) {
        this.listener = listener;
    }

    /**
     * 请求开启定位服务
     *
     * @version 1.0
     * @author zyh
     * @date 2015年2月7日 下午7:30:33
     */
    public void getMyLocation() {
        if (NetUtils.isNetworkAvailable(mContext)) {
            startLocate();
        } else {
            T.show(mContext, "当前无网络连接,请先开启网络");
            lastPoint = new LocationInfo(0, 0, "定位失败");
            listener.onLocateCompleted(lastPoint);
        }
    }

    /**
     * 开始定位
     *
     * @version 1.0
     * @author zyh
     * @date 2015年2月7日 下午7:31:36
     */
    public void startLocate() {
        InitLocation();
        L.i("准备开启");
        // 发起定位请求。请求过程是异步的，定位结果在上面的监听函数onReceiveLocation中获取。
        mLocationClient.start();
    }

    /**
     * 设置定位参数
     *
     * @version 1.0
     * @author zyh
     * @date 2015年2月7日 下午7:32:07
     */
    public void InitLocation() {
        try {
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationMode.Hight_Accuracy);
            option.setOpenGps(true);
            option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
            // option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms
            option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
            option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
            mLocationClient.setLocOption(option);
            L.i("初始化完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // BDLocationListener接口有2个方法需要实现：
    // 1.接收异步返回的定位结果，参数是BDLocation类型参数。
    // 2.接收异步返回的POI查询结果，参数是BDLocation类型参数。

    /**
     * 百度定位的回调监听类
     *
     * @author zyh
     * @version 1.0
     */
    public class mBDLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            L.i(location.getLocType() + "," + +location.getOperators() + "," + location.getLatitude() + ","
                    + location.getLongitude() + "," + location.getAddrStr());
            // 成功定位
            if (location.getLocType() == 161 || location.getLocType() == 61) {
                lastPoint = new LocationInfo(location.getLongitude(), location.getLatitude(),
                        location.getAddrStr());
            } else {// 定位失败
                lastPoint = new LocationInfo(0, 0, "定位失败");
            }
            // 停止定位
            mLocationClient.stop();
            if (listener != null) {
                listener.onLocateCompleted(lastPoint);
            }
        }
    }

    public void stop() {
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
    }

    public interface OnLocateCompletedListener {
        public void onLocateCompleted(LocationInfo locationInfo);

    }


}
