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
package app.hanks.com.conquer;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.lidroid.xutils.DbUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.squareup.leakcanary.LeakCanary;

import java.util.HashMap;
import java.util.Map;

import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.SP;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 应用的入口
 * Created by Hanks on 2015/5/17.
 */
public class CustomApplication extends Application {

    public static CustomApplication mInstance;
    public static BmobGeoPoint lastPoint = new BmobGeoPoint();// 上一次定位到的经纬度

    public static CustomApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //捕获系统异常
        //		MyCrashHandler myHandler = MyCrashHandler.getInstance();
        //		myHandler.init(this);
        //		Thread.currentThread().setUncaughtExceptionHandler(myHandler);
        // 是否开启debug模式--默认开启状态

        mInstance = this;

        BmobChat.DEBUG_MODE = true;
        DbUtils.create(getApplicationContext()).configDebug(false);
        Fresco.initialize(getApplicationContext());
        LeakCanary.install(this);
        // 将“12345678”替换成您申请的APPID，申请地址：http://open.voicecloud.cn
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=556fce70");
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        initImageLoader(getApplicationContext());

        // 若用户登陆过，则先从好友数据库中取出好友list存入内存中
        if (BmobUserManager.getInstance(getApplicationContext()).getCurrentUser() != null) {
            // 获取本地好友user list到内存,方便以后获取好友list
            contactList = CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList());
        }

    }

    /**
     * 初始化ImageLoader
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(720, 1280).diskCacheExtraOptions(720, 1280, null)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache()).diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(70 * 1024 * 1024).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    //通知栏
    private NotificationManager mNotificationManager;

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    //播放
    private MediaPlayer mMediaPlayer;

    public synchronized MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        return mMediaPlayer;
    }


    /**
     * 设置位置信息
     */
    public void setLocation(double lon, double lat) {
        SP.put(this, "longitude", lon);
        SP.put(this, "latitude", lat);
        lastPoint.setLongitude(lon);
        lastPoint.setLatitude(lat);
    }


    private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();

    /**
     * 获取内存中好友user list
     */
    public Map<String, BmobChatUser> getContactList() {
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, BmobChatUser> contactList) {
        if (this.contactList != null) {
            this.contactList.clear();
        }
        this.contactList = contactList;
    }

    /**
     * 退出登录,清空缓存数据
     */
    public void logout() {
        BmobUserManager.getInstance(getApplicationContext()).logout();
        setContactList(null);
    }
}
