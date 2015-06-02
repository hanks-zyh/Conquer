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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.config.Constants;
import app.hanks.com.conquer.fragment.MenuFragment;
import app.hanks.com.conquer.fragment.MyTaskFragment;
import app.hanks.com.conquer.fragment.OtherTaskFragment;
import app.hanks.com.conquer.otto.BusProvider;
import app.hanks.com.conquer.otto.MenuPhotoClickEvent;
import app.hanks.com.conquer.util.PixelUtil;
import app.hanks.com.conquer.util.SP;
import app.hanks.com.conquer.view.OpAnimationView;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuDrawable;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuView;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by Administrator on 2015/5/17.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private DrawerLayout       drawerLayout;
    private MaterialMenuView   materialMenu;
    private MenuFragment       menuFragment;// 侧滑菜单Fragment
    private ImageButton        iv_sort;
    private View               toolbar;
    private TextView           mTitle;
    private PopupWindow        popWin;
    private SwipeRefreshLayout refreshLayout;
    private OpAnimationView    addButtom;
    private MyTaskFragment     myTaskFragment;
    private OtherTaskFragment  otherTaskFragment;
    private Fragment           currentFragment;
    private boolean isFirst = true;
    private boolean userDataFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        materialMenu = (MaterialMenuView) findViewById(R.id.material_menu);
        toolbar = findViewById(R.id.title);
        mTitle = (TextView) findViewById(R.id.tv_title);
        iv_sort = (ImageButton) findViewById(R.id.iv_sort);
        addButtom = (OpAnimationView) findViewById(R.id.iv_add);
        materialMenu.setOnClickListener(this);
        iv_sort.setOnClickListener(this);
        addButtom.setOnClickListener(this);
        initUserLocation();
        initDrawerMenu();
        initMain();

        BusProvider.getInstance().register(this);
    }


    @Subscribe
    public void goUserData(MenuPhotoClickEvent event) {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
//            A.goOtherActivity(context, UserDataActivity.class);
            userDataFlag = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    private void initUserLocation() {
        double longitude = Double.parseDouble((String) SP.get(context.getApplicationContext(), "longitude", "0"));
        double latitude = Double.parseDouble((String) SP.get(context.getApplicationContext(), "latitude", "0"));
        currentUser.setLocation(new BmobGeoPoint(longitude, latitude));
        currentUser.update(context.getApplicationContext());
    }

    /**
     * 主界面
     */
    private void initMain() {
        myTaskFragment = new MyTaskFragment();
        otherTaskFragment = new OtherTaskFragment();
        currentFragment = myTaskFragment;
        if (isFirst) {
            startIntroAnimation();
            isFirst = false;
        }
    }

    /**
     * 侧滑抽屉界面
     */
    private void initDrawerMenu() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);// 侧滑控件
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int arg0) {
            }

            @Override
            public void onDrawerSlide(View arg0, float arg1) {
            }

            @Override
            public void onDrawerOpened(View arg0) {
                materialMenu.animatePressedState(MaterialMenuDrawable.IconState.X);
            }

            @Override
            public void onDrawerClosed(View arg0) {
                if (userDataFlag) {
                    int[] location = new int[] { PixelUtil.dp2px(20), PixelUtil.dp2px(56) };
                    UserDataActivity.startUserProfileFromLocation(location, MainActivity.this);
                    overridePendingTransition(0, 0);
                }
                materialMenu.animateState(MaterialMenuDrawable.IconState.BURGER);
                userDataFlag = false;
            }
        });

        // 侧滑菜单
        menuFragment = new MenuFragment();
        changeFramgnt(R.id.left_drawer, menuFragment);
    }

    /**
     * 切换侧滑菜单布局打开或关闭
     */
    public void toggle() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
            materialMenu.animatePressedState(MaterialMenuDrawable.IconState.BURGER);
        } else {
            materialMenu.animatePressedState(MaterialMenuDrawable.IconState.X);
            drawerLayout.openDrawer(Gravity.START);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.material_menu:
                toggle();
                break;
            case R.id.iv_add:
                launcherAddTaskActivity(v);
                break;
            case R.id.iv_sort:
                showSelectSort();
                break;
        }
    }

    public void launcherAddTaskActivity(View v) {
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        AddTaskActivity.startUserProfileFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }

    /**
     * 弹出选择排序的popupWindow
     */
    private void showSelectSort() {
        View v = View.inflate(context, R.layout.pop_sort, null);
        //刷新
        v.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWin.dismiss();
            }
        });
        //切换
        v.findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment == myTaskFragment) {
                    currentFragment = otherTaskFragment;
                } else {
                    currentFragment = myTaskFragment;
                }
                changeFramgnt(R.id.layout_content, currentFragment);
                popWin.dismiss();
            }
        });
        popWin = new PopupWindow(v, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popWin.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.card_bg));
        // popWin.setFocusable(true);
        popWin.setOutsideTouchable(true); // 点击popWin
        // 以处的区域，自动关闭
        // popWin.showAtLocation(iv_sort, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置在屏幕中的显示位置
        popWin.showAsDropDown(iv_sort, 0, -iv_sort.getHeight() + 10);
    }


    private void startIntroAnimation() {
        addButtom.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        int actionbarSize = PixelUtil.dp2px(57);
        toolbar.setTranslationY(-actionbarSize);
        mTitle.setTranslationY(-actionbarSize);
        iv_sort.setTranslationY(-actionbarSize);

        toolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        mTitle.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        iv_sort.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        addButtom.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(400)
                .start();
        changeFramgnt(R.id.layout_content, currentFragment);
    }

    @Override
    protected void onStop() {
        sendBroadcast(new Intent(Constants.ACTION_DESTORY_PLAYER));
        super.onStop();
    }

    @Override
    public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
    }

    @Override
    public View getContentView() {
        return View.inflate(context, R.layout.layout_main, null);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }
}
