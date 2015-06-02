package app.hanks.com.conquer.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.util.A;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.util.TaskUtil;
import app.hanks.com.conquer.util.TaskUtil.UpLoadListener;
import app.hanks.com.conquer.util.UserDataUtils;
import app.hanks.com.conquer.util.UserDataUtils.UpdateUserDataListener;
import app.hanks.com.conquer.view.CircularImageView;
import app.hanks.com.conquer.view.RevealBackgroundView;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuDrawable;
import app.hanks.com.conquer.view.materialmenu.MaterialMenuView;

public class UserDataActivity extends BaseActivity implements OnClickListener, ObservableScrollViewCallbacks, RevealBackgroundView.OnStateChangeListener {

    private static String photoUrl = "";
    private static String homeUrl  = "";

    private ObservableScrollView scrollView;
    private CircularImageView    iv_photo;
    private ImageView            iv_gender;

    private TextView tv_id, tv_nickname;
    private TextView et_city, et_phone;
    private ViewGroup ll_label, ll_album;
    private ImageView add_pic;
    private ImageView iv_home_bg;

    private int SCROLL_DIS = 180;// 头部滑动检测距离
    private int home_bg_height;

    private View title_bg;
    private View add;
    private View topView;
    private View iv_camera;
    private View data;
    private View album;
    private View bt_eidt;

    private RevealBackgroundView vRevealBackground;
    private MaterialMenuView     material_menu;

    private boolean finishRelav;
    private boolean isHide = false;

    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public static void startUserProfileFromLocation(int[] startingLocation, Activity mainActivity) {
        Intent intent = new Intent(mainActivity, UserDataActivity.class);
        intent.putExtra("startingLocation", startingLocation);
        mainActivity.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 每次进来更新信息
        bindViews();
        init();
        setScrollListen();
        setupRevealBackground(savedInstanceState);
    }

    private void bindViews() {
        scrollView = (ObservableScrollView) findViewById(R.id.sv);
        title_bg = findViewById(R.id.title_bg);
        iv_photo = (CircularImageView) findViewById(R.id.iv_photo);
        material_menu = (MaterialMenuView) findViewById(R.id.material_menu);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        iv_gender = (ImageView) findViewById(R.id.iv_gender);
        iv_home_bg = (ImageView) findViewById(R.id.iv_home_bg);
        album = findViewById(R.id.album);
        data = findViewById(R.id.data);
        tv_id = (TextView) findViewById(R.id.tv_id);
        et_city = (TextView) findViewById(R.id.et_city);
        et_phone = (TextView) findViewById(R.id.et_phone);
        topView = findViewById(R.id.topView);
        iv_camera = findViewById(R.id.iv_camera);
        ll_album = (ViewGroup) findViewById(R.id.ll_album);
        ll_label = (ViewGroup) findViewById(R.id.ll_label);
        bt_eidt = findViewById(R.id.bt_eidt);
        add = View.inflate(context, R.layout.item_album, null);
        add_pic = (ImageView) add.findViewById(R.id.iv_pic);
    }

    private void init() {

        iv_camera.setOnClickListener(this);
        bt_eidt.setOnClickListener(this);
        add_pic.setImageResource(R.drawable.ic_add_pic);
        ll_album.addView(add);

        SCROLL_DIS = getResources().getDimensionPixelSize(R.dimen.photo_top) - getResources().getDimensionPixelSize(R.dimen.title_height)
                / 2 + getResources().getDimensionPixelSize(R.dimen.photo_size) / 2;

        home_bg_height = getResources().getDimensionPixelSize(R.dimen.home_bg_height)
                - getResources().getDimensionPixelSize(R.dimen.title_height);
        material_menu.setState(MaterialMenuDrawable.IconState.X);


        iv_camera.setScaleX(0);
        iv_camera.setScaleY(0);
        isHide = true;
        finishRelav = true;
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
            if (home != null && !homeUrl.equals(home)) {
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

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            initUserData();
//            UserDataUtils.queryUserByUsername(context, currentUser.getUsername(), new QueryUserDataListener() {
//                @Override
//                public void onSuccess(List<User> arg0) {
//                    if (CollectionUtils.isNotNull(arg0)) {
//                        currentUser = arg0.get(0);
//                        initUserData();
//                    }
//                }
//
//                @Override
//                public void onFailure(int errorCode, String msg) {
//                }
//            });
        }
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
        Intent intent = new Intent(context, AlbumActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            List<String> images = data.getStringArrayListExtra(AlbumActivity.INTENT_SELECTED_PICTURE);
            if (images == null || images.size() <= 0) {
                return;
            }
            File f = new File(images.get(0));
            L.d("照片路径：" + f.getAbsolutePath());
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
            public void onSuccess(final String url) {
                currentUser.setHomeBg(url);
                loader.displayImage(url,iv_home_bg);
                UserDataUtils.UpdateUserData(context, currentUser, new UpdateUserDataListener() {
                    @Override
                    public void onSuccess() {
                        homeUrl = url;
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
        return View.inflate(context, R.layout.activity_user_data, null);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (scrollY > home_bg_height) {
            scrollY = home_bg_height;
        }
        float f = scrollY * 1.0f / home_bg_height; //当前滚动的距离占目标距离的百分比
        topView.setAlpha(f);
        topView.setTranslationY(-scrollY);
        if (finishRelav) {
            iv_photo.setTranslationY(-SCROLL_DIS * f);
            iv_photo.setScaleX(1 - 0.65f * f);
            iv_photo.setScaleY(1 - 0.65f * f);
            if (scrollY >= 5) {
                hideFabAnimate();
            } else {
                showFabAnimate();
            }
        }
    }

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


    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground = (RevealBackgroundView) findViewById(R.id.vRevealBackground);
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra("startingLocation");
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return false;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            titleAnim(); //开启一系列动画
        }
    }

    private void titleAnim() {

        bt_eidt.animate().alpha(1).setDuration(300).start(); //编辑信息的按钮
        material_menu.animateState(MaterialMenuDrawable.IconState.ARROW); //左边返回按钮

        //头像信息
        iv_photo.setTranslationY(-iv_photo.getHeight());
        iv_photo.animate().alpha(1).translationY(0).setStartDelay(100).start();

        //title的透明度
        title_bg.animate().alpha(0).setDuration(400).start();

        scrollView.setVisibility(View.VISIBLE);  //scrollview
        ValueAnimator valueAnimator = ValueAnimator.ofInt(iv_home_bg.getHeight(), 0);
        valueAnimator.setDuration(400);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int v = (int) animation.getAnimatedValue();
                scrollView.setScrollY(v);
            }
        });

        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final int[] location = new int[2];
        data.getLocationOnScreen(location);
        data.setTranslationY(metrics.heightPixels - location[1]);

        album.getLocationOnScreen(location);
        album.setTranslationY(metrics.heightPixels - location[1]);
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                data.setVisibility(View.VISIBLE);
                album.setVisibility(View.VISIBLE);
                data.animate().translationY(0).setStartDelay(450).start();
                album.animate().translationY(0).setStartDelay(500).start();
            }
        }, 400);

    }
}
