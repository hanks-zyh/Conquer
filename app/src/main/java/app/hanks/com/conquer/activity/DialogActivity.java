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

/**
 * 登录过后的DialogActivity
 * Created by Hanks on 2015/5/17.
 */

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import app.hanks.com.conquer.CustomApplication;
import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.MaxNumber;
import app.hanks.com.conquer.otto.BusProvider;
import app.hanks.com.conquer.otto.FinishActivityEvent;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.T;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

public class DialogActivity extends BaseActivity {
    protected Integer temp;
    private   String  nickName;
    private   String  photoUrl;
    private   String  gender;
    private   String  city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nickName = getIntent().getStringExtra("nickName");
        photoUrl = getIntent().getStringExtra("photoUrl");
        gender = getIntent().getStringExtra("gender");
        city = getIntent().getStringExtra("city");
        L.i("nickName:" + nickName + ",photoUrl:" + photoUrl + ",gender:" + gender + ",city" + city);
        // 判断用户是否为空
        if (currentUser != null) {
            L.i("用户非空");
            if (currentUser.getUsername().length() >= 15) {
                // qq第一次注册，绑定username,和一大堆默认信息
                currentUser.setNick(nickName);
                currentUser.setAvatar(photoUrl);
                currentUser.setCity(city);
                currentUser.setHomeBg("http://file.bmob.cn/M00/D6/4C/oYYBAFR9X16AWtxmAABU3gzbPlk642.jpg");
                currentUser.setSchool("北京第一青年疗养院");
                currentUser.setDep("二里沟爬山学院");
                currentUser.setMajor("爬山减肥");
                currentUser.setYear("2015");
                currentUser.setLoveStatus("孤家寡人");
                currentUser.setPhoneNum("57575777");
                currentUser.setSign("树下斑驳的光影,那是那年盛夏我们零碎的流年.");
                ArrayList<String> list = new ArrayList<String>();
                list.add("http://file.bmob.cn/M00/D6/4E/oYYBAFR9ZMuAI5HVAAAG8DKoaHY038.png");
                currentUser.setAlbum(list);
                ArrayList<String> list2 = new ArrayList<String>();
                list2.add("单身贵族");
                currentUser.setLabel(list2);
                currentUser.setMale((gender.equals("男") || gender.equals("m")) ? true : false);

                L.i("用户绑定id");
                bindUserName();
            } else {
                // 已经注册过了，更新用户信息去主界面(换个手机登录)
                // 需要綁定设备Id
                currentUser.setDeviceType("android");
                currentUser.setInstallId(BmobInstallation.getInstallationId(context));
                currentUser.update(context, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        userManager.bindInstallationForRegister(currentUser.getUsername());
                        updateUserInfos();
//                      sendBroadcast(new Intent(Constants.ACTION_REGISTER_SUCCESS_FINISH));
                        BusProvider.getInstance().post(new FinishActivityEvent());
                        A.goOtherActivityFinishNoAnim(context, MainActivity.class);
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        L.i(arg0 + "更新用户信息去主界面失败" + arg1);
                        CustomApplication.getInstance().logout();
                        A.finishSelf(context);
                    }
                });
            }
        } else {
            L.i("用户为空");
            finish();
            T.show(context, "授权失败");
        }
    }


    private void bindUserName() {
        /** 从服务器获取一个可用的用户名 */
        BmobQuery<MaxNumber> query = new BmobQuery<MaxNumber>();
        query.getObject(this, "ZvX5LLLU", new GetListener<MaxNumber>() {
            @Override
            public void onSuccess(final MaxNumber arg0) {
                temp = arg0.getMaxNum();
                arg0.increment("maxNum");
                arg0.update(context, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        temp++;
                        L.i("temp=" + temp + ",用户名:" + currentUser.getUsername());
                        // 重新设置用户名（数字串）
                        currentUser.setUsername(temp + "");
                        currentUser.setDeviceType("android");
                        currentUser.setInstallId(BmobInstallation.getInstallationId(context));
                        /* 必须调用update，不能用save */
                        currentUser.update(DialogActivity.this, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                // 将设备与username进行绑定
                                userManager.bindInstallationForRegister(currentUser.getUsername());
                                // 更新用户信息
                                updateUserInfos();
                                // 发广播通知登陆页面退出
//                                sendBroadcast(new Intent(Constants.ACTION_REGISTER_SUCCESS_FINISH));
                                BusProvider.getInstance().post(new FinishActivityEvent());
                                // 去主界面
                                L.e(currentUser.toString());
                                // 去学校信息
                                A.goOtherActivityFinishNoAnim(context, SchoolInfoActivity.class);
                            }

                            @Override
                            public void onFailure(int arg0, String arg1) {
                                L.i(arg0 + "," + arg1);
                                CustomApplication.getInstance().logout();
                                A.finishSelf(context);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        L.i(arg0 + ",update," + arg1);
                        CustomApplication.getInstance().logout();
                        A.finishSelf(context);
                    }
                });
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                L.i(arg0 + ",update2," + arg1);
            }
        });
    }

    @Override
    public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
        tv_title.setText("完善信息");
    }

    public View getContentView() {
        return View.inflate(context, R.layout.activity_dialog, null);
    }
}