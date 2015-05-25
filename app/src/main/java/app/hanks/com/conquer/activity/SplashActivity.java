package app.hanks.com.conquer.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import app.hanks.com.conquer.CustomApplication;
import app.hanks.com.conquer.R;
import app.hanks.com.conquer.config.Constants;
import app.hanks.com.conquer.location.LocationInfo;
import app.hanks.com.conquer.location.LocationService;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.NetUtils;
import app.hanks.com.conquer.util.T;
import cn.bmob.im.BmobChat;
import cn.bmob.v3.Bmob;

public class SplashActivity extends BaseActivity {

    private final int GO_HOME  = 100;
    private final int GO_LOGIN = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();                          //设置全屏
        logoAnim();                               //logo动画
        checkNetWrok();                           //检测网络
        startPollService();                       //开启后台服务检测到期的任务
        initBmob();                               //初始化bmob
        goMainActivity();                         //界面跳转
    }

    /**
     * 设置为全屏显示
     */
    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * logo的动画
     */
    private void logoAnim() {
        View iv_photo2 = findViewById(R.id.iv_photo2);
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(iv_photo2, "Alpha", 0f, 1f).setDuration(800);
        oa1.start();
        oa1.setStartDelay(500);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(iv_photo2, "TranslationY", 0f, -100f).setDuration(800);
        oa2.setInterpolator(new OvershootInterpolator());
        oa2.start();
        oa2.setStartDelay(500);
    }

    /**
     * 检查网络
     */
    private void checkNetWrok() {
        // 检查网络
        if (!NetUtils.isNetworkAvailable(context)) {
            T.showNetErr(context);
        } else {
            //定位
            initLocClient();
        }
    }

    private void startPollService() {
        // 开启后台任务检测服务,60s
        // PollingUtils.startPollingService(this, 60, AlertService.class, AlertService.ACTION);
    }

    private void initBmob() {
        // 可设置调试模式，当为true的时候，会在logcat的BmobChat下输出一些日志，包括推送服务是否正常运行，如果服务端返回错误，也会一并打印出来。方便开发者调试
        BmobChat.DEBUG_MODE = true;
        Bmob.initialize(getApplicationContext(), Constants.BMOB_KEY);
        BmobChat.getInstance(getApplicationContext()).init(Constants.BMOB_KEY);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    A.goOtherActivityFinish(context, MainActivity.class);
                    break;
                case GO_LOGIN:
                    A.goOtherActivityFinish(context, LoginActivity.class);
                    break;
            }
        }
    };

    /**
     * 开启定位，更新当前用户的经纬度坐标
     */
    private void initLocClient() {
        LocationService locationService = LocationService.getInstance(getApplicationContext());
        locationService.getMyLocation();
        locationService.setOnLocateCompletedListener(new LocationService.OnLocateCompletedListener() {
            @Override
            public void onLocateCompleted(LocationInfo locationInfo) {
                CustomApplication.getInstance().setLocation(locationInfo.getLongitude(), locationInfo.getLatitude());
            }
        });
    }

    /**
     * 去主界面
     */
    private void goMainActivity() {
        if (currentUser != null) {
            // 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
            String username = currentUser.getUsername();
            L.i("登录用户：" + username);
            updateUserInfos();
            mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //停止定位
        LocationService.getInstance(getApplicationContext()).stop();
    }

    @Override
    protected View getContentView() {//设置界面
        return View.inflate(context,R.layout.activity_splash,null);
    }

    @Override
    protected void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {

    }
}
