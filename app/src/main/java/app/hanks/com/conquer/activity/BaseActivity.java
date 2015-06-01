/*
 * Created by Hanks
 * Copyright (c) 2015 NaShangBan. All rights reserved
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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.List;

import app.hanks.com.conquer.CustomApplication;
import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.User;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.SP;
import app.hanks.com.conquer.util.SystemBarTintManager;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Activity的父类,做一些公共的初始化,变量,方法
 * Created by Hanks on 2015/5/17.
 */
public abstract class BaseActivity extends FragmentActivity {

    protected Context             context;
    protected BmobUserManager     userManager;
    protected BmobChatManager     manager;
    protected CustomApplication   mApplication;
    protected User                currentUser;
    protected int                 mScreenWidth;
    protected int                 mScreenHeight;
    protected ImageLoader         loader;
    protected DisplayImageOptions option_photo, option_pic;
    DbUtils dbUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initStatusBar();
        initTheme();
        initLayout();
        userManager = BmobUserManager.getInstance(getApplicationContext());
        manager = BmobChatManager.getInstance(getApplicationContext());
        mApplication = CustomApplication.getInstance();
        currentUser = BmobChatUser.getCurrentUser(context, User.class);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        loader = ImageLoader.getInstance();

        option_photo = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_photo)
                .showImageForEmptyUri(R.drawable.default_photo).showImageOnFail(R.drawable.default_photo)
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(800)).build();
        option_pic = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.pic_loading)
                .showImageForEmptyUri(R.drawable.pic_loading).showImageOnFail(R.drawable.pic_loading)
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(1000)).build();
        dbUtils = DbUtils.create(context);
    }

    /**
     * 设置主题
     */
    private void initTheme() {

        int theme = (Integer) SP.get(context, "theme", 0);
        switch (theme) {
            case 0:
                setTheme(R.style.SwitchTheme0);
                break;
            case 1:
                setTheme(R.style.SwitchTheme1);
                break;
            case 2:
                setTheme(R.style.SwitchTheme2);
                break;
            case 3:
                setTheme(R.style.SwitchTheme3);
                break;
        }
    }

    /**
     * 初始化布局
     */
    private void initLayout() {
        View v = getContentView();
        ViewGroup rl_title = (ViewGroup) v.findViewById(R.id.rl_title);// 标题总布局
        TextView tv_title = (TextView) v.findViewById(R.id.tv_title);// 标题文字
        ImageButton ib_back = (ImageButton) v.findViewById(R.id.ib_back);// 左边返回图标
        ImageButton ib_right = (ImageButton) v.findViewById(R.id.ib_right);// 右边图标
        View shadow = v.findViewById(R.id.shadow);// 标题阴影
//        if (rl_title != null && tv_title == null && ib_back == null && ib_right == null && shadow != null) {
        initTitleBar(rl_title, tv_title, ib_back, ib_right, shadow);
//        }
        setContentView(v);
    }

    /**
     * 获取布局View
     *
     * @return
     */
    protected abstract View getContentView();

    /**
     * 初始化title
     *
     * @param rl_title
     * @param tv_title
     * @param ib_back
     * @param ib_right
     * @param shadow
     */
    protected abstract void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow);

    /**
     * 沉浸状态栏
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initStatusBar() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        int theme = (Integer) SP.get(context, "theme", 0);
        int color = R.color.theme_0;
        switch (theme) {
            case 0:
                color = R.color.theme_0;
                break;
            case 1:
                color = R.color.theme_1;
                break;
            case 2:
                color = R.color.theme_2;
                break;
            case 3:
                color = R.color.theme_3;
                break;
        }
        tintManager.setStatusBarTintColor(getResources().getColor(color));
    }

    /**
     * 切换Fragment
     *
     * @param id       要切换的布局id
     * @param fragment 要切换的Fragment
     */
    protected void changeFramgnt(int id, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(id, fragment).commit();
    }

    /**
     * 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
     */
    public void updateUserInfos() {
        // 更新地理位置信息
        updateUserLocation();
        // 查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
        // 这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
        userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
            @Override
            public void onError(int arg0, String arg1) {
                L.i("查询好友列表失败：" + arg1);
            }

            @Override
            public void onSuccess(List<BmobChatUser> arg0) {
                // 保存到application中方便比较
                CustomApplication.getInstance().setContactList(CollectionUtils.list2map(arg0));
            }
        });
    }

    /**
     * 更新用户的经纬度信息
     */
    public void updateUserLocation() {
        if (CustomApplication.lastPoint != null) {
            String saveLatitude = CustomApplication.lastPoint.getLatitude() + "";
            String saveLongtitude = CustomApplication.lastPoint.getLongitude() + "";
            String newLat = String.valueOf(CustomApplication.lastPoint.getLatitude());
            String newLong = String.valueOf(CustomApplication.lastPoint.getLongitude());
            if (!saveLatitude.equals(newLat) || !saveLongtitude.equals(newLong)) {// 只有位置有变化就更新当前位置，达到实时更新的目的
                final User user = (User) userManager.getCurrentUser(User.class);
                user.setLocation(CustomApplication.lastPoint);
                user.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        CustomApplication.getInstance().setLocation(user.getLocation().getLongitude(), user.getLocation().getLatitude());
                        L.i("经纬度更新成功");
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        L.i("经纬度更新 失败:" + msg);
                    }
                });
            } else {
                L.i("用户位置未发生过变化");
            }
        }
    }

    /**
     * 隐藏软键盘 hideSoftInputView
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 返回按键
     */
    @Override
    public void onBackPressed() {
        A.finishSelf(context);
    }

    /**
     * 返回按钮
     *
     * @param v
     */
    public void back(View v) {
        onBackPressed();
    }
}
