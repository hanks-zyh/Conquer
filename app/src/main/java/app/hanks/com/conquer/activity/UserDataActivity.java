package app.hanks.com.conquer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.User;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.util.TaskUtil;
import app.hanks.com.conquer.util.TaskUtil.UpLoadListener;
import app.hanks.com.conquer.util.UserDataUtils;
import app.hanks.com.conquer.util.UserDataUtils.QueryUserDataListener;
import app.hanks.com.conquer.util.UserDataUtils.UpdateUserDataListener;
import app.hanks.com.conquer.view.CircularImageView;

public class UserDataActivity extends BaseActivity implements OnClickListener, ObservableScrollViewCallbacks {

    private ObservableScrollView scrollView;
    private CircularImageView    iv_photo;
    private ImageView            iv_gender;
    private TextView             tv_id, tv_nickname;
    private TextView et_school, et_dep, et_year, et_city, et_phone;
    private ViewGroup ll_label, ll_album;
    private View title_bg;
    private int SCROLL_DIS = 180;// 头部滑动检测距离
    private ImageView add_pic;
    private ImageView iv_home_bg;
    private View      add;
    private static String photoUrl = "";
    private static String homeUrl  = "";
    private float photoScale;
    private int   home_bg_height;
    private View  topView;
    private View  iv_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        // 每次进来更新信息
        init();
    }

    private void init() {
        scrollView = (ObservableScrollView) findViewById(R.id.sv);
        title_bg = findViewById(R.id.title_bg);
        iv_photo = (CircularImageView) findViewById(R.id.iv_photo);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        iv_gender = (ImageView) findViewById(R.id.iv_gender);
        iv_home_bg = (ImageView) findViewById(R.id.iv_home_bg);
        tv_id = (TextView) findViewById(R.id.tv_id);
        et_city = (TextView) findViewById(R.id.et_city);
//        et_school = (TextView) findViewById(R.id.et_school);
//        et_dep = (TextView) findViewById(R.id.et_dep);
//        et_year = (TextView) findViewById(R.id.et_year);
        et_phone = (TextView) findViewById(R.id.et_phone);
        topView = findViewById(R.id.topView);
        // et_love_status = (EditText) findViewById(R.id.et_love_status);
        iv_camera = findViewById(R.id.iv_camera);
        iv_camera.setOnClickListener(this);

        ll_album = (ViewGroup) findViewById(R.id.ll_album);
        ll_label = (ViewGroup) findViewById(R.id.ll_label);

        add = View.inflate(context, R.layout.item_album, null);
        add_pic = (ImageView) add.findViewById(R.id.iv_pic);
        add_pic.setImageResource(R.drawable.ic_add_pic);
        ll_album.addView(add);
        findViewById(R.id.bt_eidt).setOnClickListener(this);
        // initUserData();//resume中调用
        SCROLL_DIS = getResources().getDimensionPixelSize(R.dimen.photo_top) - getResources().getDimensionPixelSize(R.dimen.title_height)
                / 2 + getResources().getDimensionPixelSize(R.dimen.photo_size) / 2;

        photoScale = getResources().getDimensionPixelSize(R.dimen.photo_size) * 1.8f;
        home_bg_height = getResources().getDimensionPixelSize(R.dimen.home_bg_height)
                - getResources().getDimensionPixelSize(R.dimen.title_height);


        setScrollListen();

    }

    /**
     * 监听ScrollView滚动
     */
    private void setScrollListen() {
        scrollView.setScrollViewCallbacks(this);
    }

    /**
     * 初始化用户资料
     */
    private void initUserData() {
        if (currentUser != null) {
            L.e("initUserData", currentUser.toString());
            String avatar = currentUser.getAvatar();
            if (avatar != null && !photoUrl.equals(avatar)) {
                loader.displayImage(avatar, iv_photo, option_photo);
                photoUrl = avatar;
            }
            String home = currentUser.getHomeBg();
            if (home != null && !homeUrl.equals(avatar)) {
                loader.displayImage(home, iv_home_bg, option_pic);
                homeUrl = home;
            }
            tv_nickname.setText(currentUser.getNick());
            iv_gender.setImageResource(currentUser.isMale() ? R.drawable.ic_male : R.drawable.ic_female);
            tv_id.setText(currentUser.getUsername());
            et_city.setText(currentUser.getCity());
            et_phone.setText(currentUser.getPhoneNum());
            et_city.setText(currentUser.getCity());
            // et_love_status.setText(currentUser.getLoveStatus());
            initAlbum(currentUser.getAlbum());
            add_pic.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MyPhotoActivity.class);
                    intent.putCharSequenceArrayListExtra("album", new ArrayList<CharSequence>(currentUser.getAlbum()));
                    A.goOtherActivity(context, intent);
                }
            });
        }

    }

    /**
     * 相册
     *
     * @param list
     */
    private void initAlbum(final ArrayList<String> list) {
        if (list == null) return;
        ll_album.removeAllViews();
        ll_album.addView(add);
        for (int i = 0; i < list.size(); i++) {
            View v = View.inflate(context, R.layout.item_album, null);
            ImageView iv = (ImageView) v.findViewById(R.id.iv_pic);
            loader.displayImage(list.get(i), iv, option_pic);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PhotoGallery.class);
                    intent.putStringArrayListExtra("album", list);
                    A.goOtherActivity(context, intent);
                }
            });
            ll_album.addView(v, 0);
            if (i > 3) break;
        }
    }

    /**
     * 动态添加text
     */
    private void initLabel(ArrayList<String> list) {
        if (list == null) return;
        ll_label.removeAllViews();
        LinearLayout ll = null;
        LayoutParams pa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
        param.setMargins(5, 5, 5, 5);
        for (int i = 0; i < list.size(); i++) {
            TextView tv = (TextView) View.inflate(context, R.layout.item_label, null);
            tv.setLayoutParams(param);
            tv.setText(list.get(i));
            if (i % 4 == 0) {
                ll = new LinearLayout(context);
                ll.setLayoutParams(pa);
                ll_label.addView(ll);
            }
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // goDiscussActivity(list.get(p));
                }
            });
            ll.addView(tv);
            if (i > 4) break;
        }
    }

    @Override
    protected void onStart() {
        if (currentUser != null) {
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
        }
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_camera:
                changeHomeBg();
                break;
            case R.id.bt_eidt:
                A.goOtherActivity(context, EditMyInfoActivity.class);
                break;
        }
    }

    /**
     * 改变用户背景
     */
    private void changeHomeBg() {
        Intent intent = new Intent(context, SelectPicActivity.class);
        intent.putExtra("noCut", true);
        // intent.putExtra("cutW", 720);
        // intent.putExtra("cutH", 400);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.d(requestCode + "," + resultCode + "," + data);
        if (resultCode == RESULT_OK) {
            File f = new File(data.getStringExtra("photo_path"));
            L.e("照片路径：" + f.getAbsolutePath());
            if (f.exists()) {
                loader.displayImage("file://" + f.getAbsolutePath(), iv_home_bg, option_pic);
                uploadPic(f);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 上传图片更新网络信息
     *
     * @param f
     */
    private void uploadPic(File f) {
        TaskUtil.upLoadFile(context, f, new UpLoadListener() {
            @Override
            public void onSuccess(String url) {
                currentUser.setHomeBg(url);
                UserDataUtils.UpdateUserData(context, currentUser, new UpdateUserDataListener() {
                    @Override
                    public void onSuccess() {
                        initUserData();
                    }

                    @Override
                    public void onFailure(int errorCode, String msg) {
                    }
                });
            }

            @Override
            public void onFailure(int error, String msg) {
                T.show(context, "上传失败");
            }
        });

    }

    @Override
    public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {

    }

    @Override
    public View getContentView() {
        return View.inflate(context, R.layout.common_title, null);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//        if (scrollY <= home_bg_height) {
//            ViewHelper.setAlpha(title_bg, (float) (scrollY * 1.0 / home_bg_height));
//            ViewHelper.setTranslationY(iv_home_bg, scrollY * 0.2f);
//            if (scrollY <= SCROLL_DIS) {
//                ViewHelper.setTranslationY(iv_photo, -scrollY);
//                ViewHelper.setScaleX(iv_photo, (photoScale - scrollY) / photoScale);
//                ViewHelper.setScaleY(iv_photo, (photoScale - scrollY) / photoScale);
//            }
//        }

        int height = iv_home_bg.getHeight() - title_bg.getHeight();
        if (scrollY > height) {
            scrollY = height;
        }
        float f = scrollY * 1.0f / height; //当前滚动的距离占目标距离的百分比
        topView.setAlpha(f);
        topView.setTranslationY(-scrollY);
        iv_photo.setTranslationY(-SCROLL_DIS * f);
        iv_photo.setScaleX(1 - 0.65f * f);
        iv_photo.setScaleY(1 - 0.65f * f);
        if (scrollY >= 5) {
            hideFabAnimate();
        } else {
            showFabAnimate();
        }
    }

    private boolean isHide = false;

    private void hideFabAnimate() {
        if (isHide) {
            return;
        }
        isHide = true;
        iv_camera.animate().scaleX(0).scaleY(0).setDuration(200).start();
    }

    private void showFabAnimate() {
        if (!isHide) {
            return;
        }
        isHide = false;
        iv_camera.animate().scaleX(1).scaleY(1).setDuration(200).start();
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }


}
