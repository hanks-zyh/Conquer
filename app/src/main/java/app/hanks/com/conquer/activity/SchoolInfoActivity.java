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

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.utils.L;

import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.User;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.AlertDialogUtils;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.util.UserDataUtils;

/**
 * Created by Hanks on 2015/5/17.
 */

public class SchoolInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView year;
    private TextView department;
    private TextView school;
    private Button   bt_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        school = (TextView) findViewById(R.id.school);
        department = (TextView) findViewById(R.id.department);
        year = (TextView) findViewById(R.id.year);
        bt_save = (Button) findViewById(R.id.bt_save);
        bt_save.setOnClickListener(this);
        school.setEnabled(false);
        department.setEnabled(false);
        year.setEnabled(false);
        findViewById(R.id.ll_school).setOnClickListener(this);
        findViewById(R.id.ll_department).setOnClickListener(this);
        findViewById(R.id.ll_year).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (currentUser == null) {
            T.show(context, "请先登录");
            return;
        }
        switch (v.getId()) {
            case R.id.ll_school:
            case R.id.ll_department:
                A.goOtherActivity(context, SchoolActivity.class);
                break;
            case R.id.ll_year:
                final String[] years = new String[] { "2012年", "2012年", "2013年", "2014年" };
                AlertDialogUtils.showChiceGender(context, years, 0, new AlertDialogUtils.OkCallBack() {
                    @Override
                    public void onOkClick(DialogInterface dialog, int which) {
                        currentUser.setYear(years[which]);
                        year.setText(years[which]);
                        if (!TextUtils.isEmpty(school.getText().toString().trim())
                                && !TextUtils.isEmpty(department.getText().toString().trim())
                                && !TextUtils.isEmpty(year.getText().toString().trim())) {
                            bt_save.setVisibility(View.VISIBLE);
                        }
                    }
                });
                break;
            case R.id.bt_save:
                saveInfo();
                break;
        }
    }

    private void saveInfo() {
        UserDataUtils.UpdateUserData(context, currentUser, new UserDataUtils.UpdateUserDataListener() {
            @Override
            public void onSuccess() {
                bt_save.setEnabled(false);
                A.goOtherActivityFinish(context, MainActivity.class);
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                bt_save.setEnabled(true);
                T.show(context, "保存失败，请重试");
            }
        });
    }

    @Override
    protected void onStart() {
        L.d("SchoolInfo---->>>>onStart");
        UserDataUtils.queryUserByUsername(context, currentUser.getUsername(), new UserDataUtils.QueryUserDataListener() {
            @Override
            public void onSuccess(List<User> arg0) {
                if (CollectionUtils.isNotNull(arg0)) {
                    currentUser = arg0.get(0);
                    L.e(currentUser.toString());
                    school.setText(currentUser.getSchool().equals("北京第一青年疗养院") ? "" : currentUser.getSchool());
                    department.setText(currentUser.getDep().equals("二里沟爬山学院") ? "" : currentUser.getDep());
                    if (!TextUtils.isEmpty(school.getText().toString().trim())
                            && !TextUtils.isEmpty(department.getText().toString().trim())
                            && !TextUtils.isEmpty(year.getText().toString().trim())) {
                        bt_save.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(int errorCode, String msg) {

            }
        });
        super.onStart();
    }
    @Override
    public View getContentView() {
        return View.inflate(context, R.layout.activity_schoolinfo, null);
    }

    @Override
    public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back,
                             ImageButton ib_right, View shadow) {
        tv_title.setText("完善信息");
        ib_back.setVisibility(View.GONE);
    }
}
