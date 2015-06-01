package app.hanks.com.conquer.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.util.T;

public class PhotoGallery extends BaseActivity {

    public static final String INTENT_ALBUM = "album";

    private ViewPager vp;
    private Button bt_save;
    private TextView tv_page;
    private ImageLoader loader;
    private DisplayImageOptions option;
    private ArrayList<ImageView> list;
    private PhotoAdapter adapter;
    private int cur = 0; // 当前page位置
    private ArrayList<String> albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
        albums = getIntent().getStringArrayListExtra(INTENT_ALBUM);
        loader = ImageLoader.getInstance();
        option = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.pic_loading)
                .showImageForEmptyUri(R.drawable.pic_loading)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true).cacheOnDisk(true).build();
        vp = (ViewPager) findViewById(R.id.vp);
        tv_page = (TextView) findViewById(R.id.tv_page);
        bt_save = (Button) findViewById(R.id.bt_save);
        tv_page.setText("1/" + albums.size());
        bt_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loader.loadImage(albums.get(cur).toString(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        String name = "" + System.currentTimeMillis();
                        String p = albums.get(cur).toString();
                        name += p.substring(p.lastIndexOf("."));
                        saveMyBitmap(name, loadedImage);
                    }
                });
            }
        });
        list = new ArrayList<ImageView>();
        for (CharSequence s : albums) {
            ImageView iv = new ImageView(this);
            loader.displayImage(s.toString(), iv, option);
            list.add(iv);
        }
        adapter = new PhotoAdapter();
        vp.setAdapter(adapter);
        vp.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                cur = arg0;
                tv_page.setText((arg0 + 1) + "/" + albums.size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    class PhotoAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView v = list.get(position);
            // PhotoViewAttacher mAttacher = new PhotoViewAttacher(v);
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }
    }

    public void saveMyBitmap(String bitName, Bitmap mBitmap) {
        File f = new File(Environment.getExternalStorageDirectory().getPath(), bitName);
        try {
            f.createNewFile();
        } catch (IOException e) {
            T.show(this, "在保存图片时出错：" + e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        T.show(this, "图片保存到" + f.getAbsolutePath());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back,
            ImageButton ib_right, View shadow) {
        shadow.setVisibility(View.VISIBLE);
    }

    @Override
    public View getContentView() {
        return View.inflate(context, R.layout.common_title, null);
    }
}
