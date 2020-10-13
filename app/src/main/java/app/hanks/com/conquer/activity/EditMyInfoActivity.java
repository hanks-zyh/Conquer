package app.hanks.com.conquer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.User;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.AlertDialogUtils;
import app.hanks.com.conquer.util.AlertDialogUtils.EtOkCallBack;
import app.hanks.com.conquer.util.AlertDialogUtils.OkCallBack;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.util.UserDataUtils;
import app.hanks.com.conquer.util.UserDataUtils.QueryUserDataListener;
import app.hanks.com.conquer.util.UserDataUtils.UpdateUserDataListener;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;

public class EditMyInfoActivity extends BaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private ImageView iv_photo;
    private TextView  tv_nick, tv_gender, tv_school, tv_dep, tv_year, tv_phone, tv_city;

    /**
     * 初始化
     */
    private void init() {
        findViewById(R.id.rl_photo).setOnClickListener(this);
        findViewById(R.id.rl_nick).setOnClickListener(this);
        findViewById(R.id.rl_gender).setOnClickListener(this);
        findViewById(R.id.rl_city).setOnClickListener(this);
        findViewById(R.id.rl_phone).setOnClickListener(this);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        tv_nick = (TextView) findViewById(R.id.tv_nick);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_school = (TextView) findViewById(R.id.tv_school);
        tv_dep = (TextView) findViewById(R.id.tv_dep);
        tv_year = (TextView) findViewById(R.id.tv_year);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_city = (TextView) findViewById(R.id.tv_city);
    }

    private void initUserData() {
        if (currentUser != null) {
            loader.displayImage(currentUser.getAvatar(), iv_photo, option_photo);
            tv_nick.setText(currentUser.getNick());
            tv_gender.setText(currentUser.isMale() ? "男" : "女");
            tv_city.setText(currentUser.getCity());
            tv_phone.setText(currentUser.getPhoneNum());
            tv_city.setText(currentUser.getCity());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_photo:
                changePhoto();
                break;
            case R.id.rl_nick:
                AlertDialogUtils.showEditDialog(context, "输入昵称", "确定", "取消", new EtOkCallBack() {
                    @Override
                    public void onOkClick(String s) {
                        tv_nick.setText(s);
                        currentUser.setNick(s);
                    }
                });
                break;
            case R.id.rl_gender:
                AlertDialogUtils.showChiceGender(context, new String[] { "男", "女" }, currentUser.isMale() ? 0 : 1, new OkCallBack() {
                    @Override
                    public void onOkClick(DialogInterface dialog, int which) {
                        currentUser.setMale(which == 0 ? true : false);
                        tv_gender.setText(which == 0 ? "男" : "女");
                    }
                });
                break;
            case R.id.rl_city:
                AlertDialogUtils.showEditDialog(context, "输入所在城市", "确定", "取消", new EtOkCallBack() {
                    @Override
                    public void onOkClick(String s) {
                        currentUser.setCity(s);
                        tv_city.setText(s);
                    }
                });
                break;
            case R.id.rl_phone:
                AlertDialogUtils.showEditDialog(context, "输入电话号码", "确定", "取消", new EtOkCallBack() {
                    @Override
                    public void onOkClick(String s) {
                        currentUser.setPhoneNum(s);
                        tv_phone.setText(s);
                    }
                });
                break;
        }
    }

    private void updateUserInfo() {
        UserDataUtils.UpdateUserData(context, currentUser, new UpdateUserDataListener() {
            @Override
            public void onSuccess() {
                A.finishSelf(context);
            }

            @Override
            public void onFailure(int errorCode, String msg) {
            }
        });
    }

    private void changePhoto() {
        Intent intent = new Intent(context, AlbumActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onStart() {
        initUserData();
        UserDataUtils.queryUserByUsername(context, currentUser.getUsername(), new QueryUserDataListener() {
            @Override
            public void onSuccess(List<User> arg0) {
                if (CollectionUtils.isNotNull(arg0)) {
                    currentUser = arg0.get(0);
                    initUserData();
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
            }
        });
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.d(requestCode + "," + resultCode + "," + data);
        if (resultCode == RESULT_OK && data != null) {
            List<String> images = data.getStringArrayListExtra(AlbumActivity.INTENT_SELECTED_PICTURE);
            if (images == null || images.size() <= 0) {
                return;
            }
            File f = new File(images.get(0));
            L.e("照片路径：" + f.getAbsolutePath());
            if (f.exists()) uploadPic(f);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 上传图片更新网络信息
     *
     * @param f
     */
    private void uploadPic(File f) {
        loader.displayImage("file://" + f.getAbsolutePath(), iv_photo, option_pic);
        final BmobFile bf = new BmobFile(f);
        bf.uploadblock(context, new UploadFileListener() {
            @Override
            public void onSuccess() {
                currentUser.setAvatar(bf.getFileUrl(getApplicationContext()));
                loader.displayImage(bf.getFileUrl(getApplicationContext()), iv_photo, option_pic);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                T.show(context, "上传失败");
            }
        });
    }

    @Override
    public void onBackPressed() {
        updateUserInfo();
//        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.activity_right_exit);
    }

    @Override
    public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
        tv_title.setText("我的信息");
    }

    @Override
    public View getContentView() {
        return View.inflate(context, R.layout.activity_edit_myinfo, null);
    }
}
