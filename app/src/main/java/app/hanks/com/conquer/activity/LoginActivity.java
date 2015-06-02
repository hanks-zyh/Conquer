/*
 * Created by Hanks
 * Copyright (c) 2015 Hanks. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.hanks.com.conquer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.config.Constants;
import app.hanks.com.conquer.otto.BusProvider;
import app.hanks.com.conquer.otto.FinishActivityEvent;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.NetUtils;
import app.hanks.com.conquer.util.PixelUtil;
import app.hanks.com.conquer.util.T;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.listener.OtherLoginListener;

/**
 * 第三方登录
 * Created by Hanks on 2015/5/17.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private String nickName;
    private String photoUrl;
    private String gender;
    private String city;

    private View welcome;
    private View line;
    private View bottom;
    private View bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        findViewById(R.id.bt_qq).setOnClickListener(this);
        findViewById(R.id.bt_sina).setOnClickListener(this);
        BusProvider.getInstance().register(this); //registe Bus
        bindViews();
        showAnim();
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

    private void bindViews() {
        bg = findViewById(R.id.bg);
        welcome = findViewById(R.id.welcome);
        line = findViewById(R.id.line);
        bottom = findViewById(R.id.bottom);
    }

    private void showAnim() {
        int DURATION = 400;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //首先是背景
        bg.animate().translationY(-metrics.heightPixels + PixelUtil.dp2px(220)).setDuration(DURATION).start();

        line.animate().scaleX(1).setDuration(DURATION).setStartDelay(DURATION).start();

        welcome.animate().alpha(1).setDuration(DURATION).setStartDelay(DURATION).start();

        bottom.animate().alpha(1).setDuration(DURATION).setStartDelay(DURATION).start();

    }

    @Override
    protected View getContentView() {
        return View.inflate(context, R.layout.activity_login, null);
    }

    @Override
    protected void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
        tv_title.setText(getString(R.string.login));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onFinishListen(FinishActivityEvent event) {
        L.i("finish");
        event.finish(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_qq:
                LoginQQ();
                break;
            case R.id.bt_sina:
                LoginSina();
                break;
        }
    }

    /**
     * QQ授权登录
     */
    private void LoginQQ() {
        BmobChatUser.qqLogin(context, Constants.QQ_KEY, new OtherLoginListener() {
            @Override
            public void onSuccess(JSONObject userAuth) {
                L.i("QQ授权成功去校验" + userAuth.toString());
                getQQInfo(userAuth);
            }

            @Override
            public void onFailure(int code, String msg) {
                L.i("QQ第三方登陆失败：" + msg);
                T.show(context, "QQ授权失败");
            }

            @Override
            public void onCancel() {
                L.i("QQ第三方登陆取消");
                T.show(context, "QQ授权取消");
            }
        });
    }

    /**
     * 新浪授权登录
     */
    private void LoginSina() {
        BmobChatUser.weiboLogin(context, Constants.Weibo_KEY, "https://api.weibo.com/oauth2/default.html", new OtherLoginListener() {
            @Override
            public void onSuccess(JSONObject userAuth) {
                L.i("weibo授权成功去校验" + userAuth.toString());
                getWeiboInfo(userAuth);
            }

            @Override
            public void onFailure(int code, String msg) {
                L.i("weibo第三方登陆失败：" + msg);
                T.show(context, "weibo授权失败");
            }

            @Override
            public void onCancel() {
                L.i("weibo第三方登陆取消");
                T.show(context, "weibo授权取消");
            }
        });
    }


    /**
     * 获取微博的资料
     */
    public void getWeiboInfo(final JSONObject obj) {
        // 根据http://open.weibo.com/wiki/2/users/show提供的API文档
        new Thread() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    if (obj != null) {
                        params.put("access_token", obj.getJSONObject("weibo").getString("access_token"));// 此为微博登陆成功之后返回的access_token
                        params.put("uid", obj.getJSONObject("weibo").getString("uid"));// 此为微博登陆成功之后返回的uid
                    }
                    String result = NetUtils.getRequest("https://api.weibo.com/2/users/show.json", params);
                    L.i("微博的个人信息：" + result);
                    JSONObject json = new JSONObject(result);
                    nickName = json.getString("screen_name");
                    gender = json.getString("gender");
                    photoUrl = json.getString("avatar_large").replace("\\", "");
                    city = json.getString("location");
                    goDialogActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    /**
     * 获取QQ的信息
     */
    public void getQQInfo(final JSONObject obj) {
        // 若更换为自己的APPID后，仍然获取不到自己的用户信息，则需要
        // 根据http://wiki.connect.qq.com/get_user_info提供的API文档，想要获取QQ用户的信息，则需要自己调用接口，传入对应的参数
        new Thread() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    // 下面则是返回的json字符
                    // {
                    // "qq": {
                    // "openid": "B4F5ABAD717CCC93ABF3BF28D4BCB03A",
                    // "access_token": "05636ED97BAB7F173CB237BA143AF7C9",
                    // "expires_in": 7776000
                    // }
                    // }
                    if (obj != null) {
                        // params.put("access_token", obj.getJSONObject("qq")
                        // .getString("access_token"));//
                        // 此为微博登陆成功之后返回的access_token
                        // params.put("uid",
                        // obj.getJSONObject("weibo").getString("uid"));//
                        // 此为微博登陆成功之后返回的uid
                        params.put("access_token", obj.getJSONObject("qq").getString("access_token"));// 此为QQ登陆成功之后返回access_token
                        params.put("openid", obj.getJSONObject("qq").getString("openid"));
                        params.put("oauth_consumer_key", Constants.QQ_KEY);// oauth_consumer_key为申请QQ登录成功后，分配给应用的appid
                        params.put("format", "json");// 格式--非必填项
                    }
                    String result = NetUtils.getRequest("https://graph.qq.com/user/get_user_info", params);
                    L.i("login", "QQ的个人信息：" + result);
                    JSONObject json = new JSONObject(result);
                    nickName = json.getString("nickname");
                    gender = json.getString("gender");
                    photoUrl = json.getString("figureurl_qq_2").replace("\\", "");
                    city = json.getString("province") + " " + json.getString("city");
                    goDialogActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 必须跳到一个界面处理一下获取到的信息
     */
    protected void goDialogActivity() {
        Intent i = new Intent(context, DialogActivity.class);
        i.putExtra("nickName", nickName);
        i.putExtra("gender", gender);
        i.putExtra("photoUrl", photoUrl);
        i.putExtra("city", city);
        A.goOtherActivityNoAnim(context, i);
    }
}
