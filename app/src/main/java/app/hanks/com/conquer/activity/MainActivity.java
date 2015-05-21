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
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.config.Constants;
import app.hanks.com.conquer.fragment.MenuFragment;
import app.hanks.com.conquer.fragment.MyTaskFragment;
import app.hanks.com.conquer.fragment.OtherTaskFragment;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.SP;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuDrawable;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuView;

/**
 * Created by Administrator on 2015/5/17.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private DrawerLayout     drawerLayout;
    private MaterialMenuView materialMenu;
    private MenuFragment     menuFragment;// 侧滑菜单Fragment
    private ImageButton      iv_sort;

    private PopupWindow        popWin;
    private SwipeRefreshLayout refreshLayout;
    private ImageView          addButtom;
    private MyTaskFragment     myTaskFragment;
    private OtherTaskFragment  otherTaskFragment;
    private Fragment           currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        materialMenu = (MaterialMenuView) findViewById(R.id.material_menu);
        iv_sort = (ImageButton) findViewById(R.id.iv_sort);
        addButtom = (ImageView) findViewById(R.id.iv_add);
        materialMenu.setOnClickListener(this);
        iv_sort.setOnClickListener(this);
        addButtom.setOnClickListener(this);

        initDrawerMenu();

        myTaskFragment = new MyTaskFragment();
        otherTaskFragment = new OtherTaskFragment();
        currentFragment = myTaskFragment;
        changeFramgnt(R.id.layout_content, currentFragment);
    }

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
                materialMenu.animatePressedState(MaterialMenuDrawable.IconState.BURGER);
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
                A.goOtherActivity(context, AddTaskActivity.class);
                break;
            case R.id.iv_sort:
                showSelectSort();
                break;
        }
    }

    /**
     * 弹出选择排序的popupWindow
     */
    private void showSelectSort() {
        int defaultSort = (Integer) SP.get(context, Constants.SP_SORT, 0);
        View v = View.inflate(context, R.layout.pop_sort, null);
        //刷新
         v.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

             }
         });
        //切换
          v.findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if(currentFragment == myTaskFragment){
                      currentFragment = otherTaskFragment;
                  }else{
                      currentFragment = myTaskFragment;
                  }
                  changeFramgnt(R.id.layout_content,currentFragment);
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
        return View.inflate(context, R.layout.common_title, null);
    }

}
