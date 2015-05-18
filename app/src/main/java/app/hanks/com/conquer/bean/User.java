package app.hanks.com.conquer.bean;

import java.util.ArrayList;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 用户实体类
 * @author LeeLay 2014年11月22日 20:05:51
 */
public class User extends BmobChatUser {
	private boolean isMale;// 性别
	private String sign;// 个性签名
	private String city;// 所在城市
	private String school;// 学校
	private String dep;// 学院
	private String major;// 专业
	private String year;// 入学年份
	private String phoneNum;// 电话号码
	private String loveStatus;// 恋爱状况
	private ArrayList<String> label;// 标签
	private ArrayList<String> album;// 相册
	private String sortLetters;// 显示数据拼音的首字母
	private BmobGeoPoint location;// 地理坐标
	private String homeBg;// 资料的背景图片

	@Override
	public String toString() {
		return "User [isMale=" + isMale + ", username=" + getUsername() + ", nick=" + getNick() + ", sign=" + sign + ", city=" + city + ", school=" + school + ", dep=" + dep + ", major=" + major
				+ ", year=" + year + ", phoneNum=" + phoneNum + ", loveStatus=" + loveStatus + ", label=" + label + ", album=" + album
				+ ", sortLetters=" + sortLetters + ", location=" + location + ", homeBg=" + homeBg + "]";
	}

	public boolean isMale() {
		return isMale;
	}

	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getDep() {
		return dep;
	}

	public void setDep(String dep) {
		this.dep = dep;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getLoveStatus() {
		return loveStatus;
	}

	public void setLoveStatus(String loveStatus) {
		this.loveStatus = loveStatus;
	}

	public ArrayList<String> getLabel() {
		return label;
	}

	public void setLabel(ArrayList<String> label) {
		this.label = label;
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
