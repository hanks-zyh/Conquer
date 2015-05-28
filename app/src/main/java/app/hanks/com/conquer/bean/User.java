package app.hanks.com.conquer.bean;

import java.util.ArrayList;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 用户实体类
 *
 * @author Hanks 2014年11月22日 20:05:51
 */
public class User extends BmobChatUser {
    private boolean           isMale;// 性别
    private String            city;// 所在城市
    private String            phoneNum;// 电话号码
    private ArrayList<String> album;// 相册
    private String            sortLetters;// 显示数据拼音的首字母
    private BmobGeoPoint      location;// 地理坐标
    private String            homeBg;// 资料的背景图片

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean isMale) {
        this.isMale = isMale;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }


    public ArrayList<String> getAlbum() {
        return album;
    }

    public void setAlbum(ArrayList<String> album) {
        this.album = album;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public String getHomeBg() {
        return homeBg;
    }

    public void setHomeBg(String homeBg) {
        this.homeBg = homeBg;
    }

}
